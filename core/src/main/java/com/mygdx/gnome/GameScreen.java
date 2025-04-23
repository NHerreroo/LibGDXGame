package com.mygdx.gnome;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;
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

    private int round = 1;

    List<Bullet> bullets = new ArrayList<>();
    float shootCooldown = 0f;
    float shootInterval = 0.5f; // dispara cada 1 segundo


    public GameScreen(final Main gam) {
        this.game = gam;
        this.batch = game.batch;

        mapTexture = game.assetManager.get("GNOME/Map/Map.png", Texture.class);
        Texture playerTexture = game.assetManager.get("GNOME/Player/1.png", Texture.class);
        Texture snailTexture = game.assetManager.get("GNOME/Snail/1.png", Texture.class);

        touchBg = game.assetManager.get("GNOME/joysk1.png", Texture.class);
        touchKnob = game.assetManager.get("GNOME/joysk2.png", Texture.class);
        bulletTexture = new Texture("GNOME/bullet.png");


        float mapAspectRatio = (float) mapTexture.getWidth() / mapTexture.getHeight();
        virtualWidth = VIRTUAL_HEIGHT * mapAspectRatio;

        camera = new OrthographicCamera();
        viewport = new FillViewport(virtualWidth, VIRTUAL_HEIGHT, camera);
        camera.position.set(virtualWidth / 2f, VIRTUAL_HEIGHT / 2f, 0);
        camera.zoom = 0.75f;
        camera.update();

        hudCamera = new OrthographicCamera();
        hudViewport = new FillViewport(virtualWidth, VIRTUAL_HEIGHT, hudCamera);
        hudCamera.position.set(virtualWidth / 2f, VIRTUAL_HEIGHT / 2f, 0);
        hudCamera.update();

        player = new Player(playerTexture, virtualWidth / 2f, VIRTUAL_HEIGHT / 2f, this);
        hud = new HUD(virtualWidth, VIRTUAL_HEIGHT, player);
        tienda = new Tienda(virtualWidth, VIRTUAL_HEIGHT, player); // MODIFICADO
        spawner = new Spawner(snailTexture, virtualWidth, VIRTUAL_HEIGHT);
        spawner.setRound(round);
    }
    public void nextRound() {
        round++;
        spawner.setRound(round); // actualizar spawner con la nueva ronda
    }

    public int getRound() {
        return round;
    }

    @Override
    public void render(float delta) {
        shootInterval = player.getCadencia();

        Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        Vector2 direction = new Vector2();
        if (isTouching) {
            direction.set(touchCurrent).sub(touchOrigin);
            if (direction.len() > 10f) {
                direction.nor();
                player.update(delta, direction);
            }
        }

        shootCooldown -= delta;
        if (shootCooldown <= 0f && !spawner.getSnails().isEmpty()) {
            Snail closest = findClosestEnemy();
            if (closest != null && player.getPosition().dst(closest.getPosition()) < 200f) {
                bullets.add(player.shootAt(closest.getPosition(), bulletTexture));
                shootCooldown = shootInterval;
            }
        }

        spawner.update(delta, player.getPosition(), tienda.activa);
        for (Bullet bullet : bullets) {
            bullet.update(delta);
        }
        player.actualizarHabilidades(delta);

        camera.position.set(player.getPosition(), 0);
        camera.update();

        // === RENDER ENTIDADES ===
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(mapTexture, 0, 0, virtualWidth, VIRTUAL_HEIGHT);
        spawner.render(batch);
        player.render(batch);

        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }

        for (EquipableItem item : player.getHabilidadesPermanentes()) {
            if (item instanceof AK47) {
                for (Bullet bullet : ((AK47)item).getBullets()) {
                    bullet.render(batch);
                }
            }
        }

        // Render de habilidades (LANZA, HALO, etc.)
        player.renderizarHabilidades(batch);

        batch.end();

        // === RENDER HUD Y TIENDA ===
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        hud.update(delta);

        if (hud.getTimeLeft() <= 0 && !tienda.activa) {
            tienda.show();
            spawner.eliminarTodos();
        }

        if (isTouching) {
            float bgSize = 100f;
            float knobSize = 50f;

            batch.draw(touchBg, touchOrigin.x - bgSize/2f, touchOrigin.y - bgSize/2f, bgSize, bgSize);

            Vector2 knobPos = new Vector2(touchCurrent);
            if (knobPos.dst(touchOrigin) > bgSize/2f) {
                knobPos.sub(touchOrigin).nor().scl(bgSize/2f).add(touchOrigin);
            }

            batch.draw(touchKnob, knobPos.x - knobSize/2f, knobPos.y - knobSize/2f, knobSize, knobSize);
        }

        batch.end();

        hud.render(batch);
        tienda.render(batch);

        handleCollisions();
    }



    private void handleCollisions() {
        // Colisiones balas del jugador
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();

            Iterator<Snail> snailIt = spawner.getSnails().iterator();
            while (snailIt.hasNext()) {
                Snail snail = snailIt.next();

                if (bullet.getPosition().dst(snail.getPosition()) < 20f) {
                    snail.recibirDaño(player.getAtaque());
                    bulletIt.remove();
                    if (snail.estaMuerto()) {
                        snailIt.remove();
                    }
                    break;
                }
            }
        }

        // Colisiones balas de AK47
        for (EquipableItem item : player.getHabilidadesPermanentes()) {
            if (item instanceof AK47) {
                Iterator<Bullet> akBulletIt = ((AK47)item).getBullets().iterator();
                while (akBulletIt.hasNext()) {
                    Bullet bullet = akBulletIt.next();

                    Iterator<Snail> snailIt = spawner.getSnails().iterator();
                    while (snailIt.hasNext()) {
                        Snail snail = snailIt.next();

                        if (bullet.getPosition().dst(snail.getPosition()) < 20f) {
                            snail.recibirDaño(((AK47)item).getDamage());
                            akBulletIt.remove();
                            if (snail.estaMuerto()) {
                                snailIt.remove();
                            }
                            break;
                        }
                    }
                }
            }
        }

        // Colisiones misiles del Robot
        for (EquipableItem item : player.getHabilidadesPermanentes()) {
            if (item instanceof Robot) {
                for (Robot.Missile missile : ((Robot)item).getMissiles()) {
                    if (missile.hasReachedTarget()) {
                        // El daño ya se aplicó en el update del misil
                        continue;
                    }

                    Iterator<Snail> snailIt = spawner.getSnails().iterator();
                    while (snailIt.hasNext()) {
                        Snail snail = snailIt.next();

                        if (missile.getPosition().dst(snail.getPosition()) < 20f) {
                            missile.applyDamage();
                            if (snail.estaMuerto()) {
                                snailIt.remove();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private Snail findClosestEnemy() {
        Snail closest = null;
        float minDist = Float.MAX_VALUE;

        for (Snail snail : spawner.getSnails()) {
            float dist = player.getPosition().dst2(snail.getPosition());
            if (dist < minDist) {
                minDist = dist;
                closest = snail;
            }
        }

        return closest;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudViewport.update(width, height);
        hud.resize(width,height);
        tienda.resize(width, height);
    }



    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}

    public HUD getHUD() {
        return hud;
    }

    public Spawner getSpawner() {
        return spawner;
    }
}
