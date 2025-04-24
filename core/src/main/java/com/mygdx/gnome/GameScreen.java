package com.mygdx.gnome;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen implements Screen {
    final Main game;
    OrthographicCamera camera;
    Viewport viewport;
    SpriteBatch batch;

    Texture mapTexture;
    Texture touchBg;
    Texture touchKnob;

    Player player;
    Spawner spawner;
    HUD hud;
    Tienda tienda;

    public static final float VIRTUAL_HEIGHT = 720f;
    public float virtualWidth;

    Vector2 touchOrigin = new Vector2();
    Vector2 touchCurrent = new Vector2();
    boolean isTouching = false;

    OrthographicCamera hudCamera;
    Viewport hudViewport;

    Texture bulletTexture;
    private Texture coinTexture;
    private List<Moneda> coins = new ArrayList<>();

    private int round = 1;
    List<Bullet> bullets = new ArrayList<>();
    float shootCooldown = 0f;
    float shootInterval = 0.5f; // dispara cada intervalo según cadencia

    public GameScreen(final Main gam) {
        this.game = gam;
        this.batch = game.getBatch();

        // Carga de texturas
        mapTexture    = game.assetManager.get("GNOME/Map/Map.png", Texture.class);
        Texture playerTexture = game.assetManager.get("GNOME/Player/1.png", Texture.class);
        Texture snailTexture  = game.assetManager.get("GNOME/Snail/1.png", Texture.class);

        touchBg      = game.assetManager.get("GNOME/joysk1.png", Texture.class);
        touchKnob    = game.assetManager.get("GNOME/joysk2.png", Texture.class);
        bulletTexture = new Texture("GNOME/bullet.png");
        coinTexture   = new Texture("GNOME/Coin.png");

        // Configuración de cámara y viewport de juego
        float mapAspectRatio = (float) mapTexture.getWidth() / mapTexture.getHeight();
        virtualWidth         = VIRTUAL_HEIGHT * mapAspectRatio;

        camera   = new OrthographicCamera();
        viewport = new FillViewport(virtualWidth, VIRTUAL_HEIGHT, camera);
        camera.position.set(virtualWidth / 2f, VIRTUAL_HEIGHT / 2f, 0);
        camera.zoom = 0.75f;
        camera.update();

        // Cámara y viewport del HUD
        hudCamera  = new OrthographicCamera();
        hudViewport= new FillViewport(virtualWidth, VIRTUAL_HEIGHT, hudCamera);
        hudCamera.position.set(virtualWidth / 2f, VIRTUAL_HEIGHT / 2f, 0);
        hudCamera.update();

        // Inicialización de entidades
        player  = new Player(playerTexture, virtualWidth / 2f, VIRTUAL_HEIGHT / 2f, this);
        hud     = new HUD(virtualWidth, VIRTUAL_HEIGHT, player, game.getSmallFont());
        tienda  = new Tienda(
            virtualWidth, VIRTUAL_HEIGHT,
            player,
            game.getBigFont(),    // fuente para título de la tienda
            game.getSmallFont()   // fuente para texto de ítems
        );
        spawner = new Spawner(snailTexture, virtualWidth, VIRTUAL_HEIGHT);
        spawner.setRound(round);
    }

    public void nextRound() {
        round++;
        spawner.setRound(round);
    }

    public int getRound() {
        return round;
    }

    @Override
    public void render(float delta) {
        // Actualización de cadencia de disparo
        shootInterval = player.getCadencia();

        // Limpieza de pantalla
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Manejo de entrada táctil
        if (Gdx.input.isTouched()) {
            if (!isTouching) {
                isTouching = true;
                touchOrigin.set(Gdx.input.getX(), Gdx.input.getY());
                hudViewport.unproject(touchOrigin);
            }
            touchCurrent.set(Gdx.input.getX(), Gdx.input.getY());
            hudViewport.unproject(touchCurrent);
        } else {
            isTouching = false;
        }

        // Cálculo de dirección de movimiento
        Vector2 direction = new Vector2();
        if (isTouching) {
            direction.set(touchCurrent).sub(touchOrigin);
            if (direction.len() > 10f) direction.nor();
            else direction.set(0, 0);
        }

        // Actualización de jugador y disparos automáticos
        player.update(delta, direction);
        shootCooldown -= delta;
        if (shootCooldown <= 0f && !spawner.getSnails().isEmpty()) {
            Snail closest = findClosestEnemy();
            if (closest != null && player.getPosition().dst(closest.getPosition()) < 200f) {
                bullets.add(player.shootAt(closest.getPosition(), bulletTexture));
                shootCooldown = shootInterval;
            }
        }

        // Actualización de spawner, balas y habilidades
        spawner.update(delta, player.getPosition(), tienda.activa);
        for (Bullet b : bullets) b.update(delta);
        player.actualizarHabilidades(delta);

        // Cámara sigue al jugador
        camera.position.set(player.getPosition(), 0);
        camera.update();

        // Render entidades en mundo
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(mapTexture, 0, 0, virtualWidth, VIRTUAL_HEIGHT);
        spawner.render(batch);
        player.render(batch);
        for (Bullet b : bullets) b.render(batch);
        for (Moneda m : coins) m.render(batch);
        for (EquipableItem item : player.getHabilidadesPermanentes()) {
            if (item instanceof AK47) {
                for (Bullet b2 : ((AK47) item).getBullets()) b2.render(batch);
            }
        }
        player.renderizarHabilidades(batch);
        batch.end();

        // Render HUD y tienda
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        hud.update(delta);
        if (hud.getTimeLeft() <= 0 && !tienda.activa) {
            tienda.show();
            spawner.eliminarTodos();
        }
        if (isTouching) {
            float bgSize = 100f, knobSize = 50f;
            batch.draw(touchBg, touchOrigin.x - bgSize/2f, touchOrigin.y - bgSize/2f, bgSize, bgSize);
            Vector2 knobPos = new Vector2(touchCurrent);
            if (knobPos.dst(touchOrigin) > bgSize/2f)
                knobPos.sub(touchOrigin).nor().scl(bgSize/2f).add(touchOrigin);
            batch.draw(touchKnob, knobPos.x - knobSize/2f, knobPos.y - knobSize/2f, knobSize, knobSize);
        }
        batch.end();

        hud.render(batch);
        tienda.render(batch);

        // Colisiones y recogida de monedas
        handleCollisions();
    }

    private void handleCollisions() {
        // Balas del jugador
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            Iterator<Snail> snailIt = spawner.getSnails().iterator();
            while (snailIt.hasNext()) {
                Snail s = snailIt.next();
                if (b.getPosition().dst(s.getPosition()) < 20f) {
                    s.recibirDaño(player.getAtaque());
                    bulletIt.remove();
                    if (s.estaMuerto()) {
                        coins.add(new Moneda(coinTexture, s.getPosition().x, s.getPosition().y));
                        snailIt.remove();
                    }
                    break;
                }
            }
        }

        // Balas de AK47
        for (EquipableItem item : player.getHabilidadesPermanentes()) {
            if (item instanceof AK47) {
                Iterator<Bullet> it2 = ((AK47)item).getBullets().iterator();
                while (it2.hasNext()) {
                    Bullet b = it2.next();
                    Iterator<Snail> snailIt = spawner.getSnails().iterator();
                    while (snailIt.hasNext()) {
                        Snail s = snailIt.next();
                        if (b.getPosition().dst(s.getPosition()) < 20f) {
                            s.recibirDaño(((AK47)item).getDamage());
                            it2.remove();
                            if (s.estaMuerto()) {
                                coins.add(new Moneda(coinTexture, s.getPosition().x, s.getPosition().y));
                                snailIt.remove();
                            }
                            break;
                        }
                    }
                }
            }
        }

        // Misiles de Robot
        for (EquipableItem item : player.getHabilidadesPermanentes()) {
            if (item instanceof Robot) {
                for (Robot.Missile m : ((Robot)item).getMissiles()) {
                    if (m.hasReachedTarget()) continue;
                    Iterator<Snail> snailIt = spawner.getSnails().iterator();
                    while (snailIt.hasNext()) {
                        Snail s = snailIt.next();
                        if (m.getPosition().dst(s.getPosition()) < 20f) {
                            m.applyDamage();
                            if (s.estaMuerto()) {
                                coins.add(new Moneda(coinTexture, s.getPosition().x, s.getPosition().y));
                                snailIt.remove();
                            }
                            break;
                        }
                    }
                }
            }
        }

        // Ataque de Halo
        for (EquipableItem item : player.getHabilidadesPermanentes()) {
            if (item instanceof Halo) {
                Halo h = (Halo)item;
                Iterator<Snail> snailIt = spawner.getSnails().iterator();
                while (snailIt.hasNext()) {
                    Snail s = snailIt.next();
                    if (player.getPosition().dst(s.getPosition()) <= h.getRadius()) {
                        s.recibirDaño(h.getDamage());
                        if (s.estaMuerto()) {
                            coins.add(new Moneda(coinTexture, s.getPosition().x, s.getPosition().y));
                            snailIt.remove();
                        }
                    }
                }
            }
        }

        // Ataque de Lanza
        for (EquipableItem item : player.getHabilidadesPermanentes()) {
            if (item instanceof Lanza) {
                Lanza l = (Lanza)item;
                for (Vector2 hit : l.getHitPoints()) {
                    Iterator<Snail> snailIt = spawner.getSnails().iterator();
                    while (snailIt.hasNext()) {
                        Snail s = snailIt.next();
                        if (hit.dst(s.getPosition()) < 20f) {
                            s.recibirDaño(l.getDamage());
                            if (s.estaMuerto()) {
                                coins.add(new Moneda(coinTexture, s.getPosition().x, s.getPosition().y));
                                snailIt.remove();
                            }
                            break;
                        }
                    }
                }
            }
        }

        // Colisión jugador-caracol
        Iterator<Snail> snailIt2 = spawner.getSnails().iterator();
        while (snailIt2.hasNext()) {
            Snail s = snailIt2.next();
            if (player.getPosition().dst(s.getPosition()) < 20f) {
                player.incrementarVidas(-1);
                snailIt2.remove();
                if (player.getVidas() <= 0) {
                    game.setScreen(new GameOverScreen(game));
                    dispose();
                }
                break;
            }
        }

        // Recogida de monedas
        Iterator<Moneda> coinIt = coins.iterator();
        while (coinIt.hasNext()) {
            Moneda m = coinIt.next();
            if (player.getPosition().dst(m.getPosition()) <= m.getPickupRadius()) {
                player.sumarDinero(m.getValue());
                coinIt.remove();
            }
        }
    }

    private Snail findClosestEnemy() {
        Snail closest = null;
        float minDist = Float.MAX_VALUE;
        for (Snail s : spawner.getSnails()) {
            float d = player.getPosition().dst2(s.getPosition());
            if (d < minDist) {
                minDist = d;
                closest = s;
            }
        }
        return closest;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudViewport.update(width, height);
        hud.resize(width, height);
        tienda.resize(width, height);
    }

    @Override public void show()    {}
    @Override public void hide()    {}
    @Override public void pause()   {}
    @Override public void resume()  {}
    @Override public void dispose() {}

    public HUD getHUD() {
        return hud;
    }

    public Spawner getSpawner() {
        return spawner;
    }
}
