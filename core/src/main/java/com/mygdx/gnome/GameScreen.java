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

    public static final float VIRTUAL_HEIGHT = 720f;
    public float virtualWidth;

    Vector2 touchOrigin = new Vector2();
    Vector2 touchCurrent = new Vector2();
    boolean isTouching = false;

    OrthographicCamera hudCamera;
    Viewport hudViewport;

    Texture bulletTexture;
    List<Bullet> bullets = new ArrayList<>();
    float shootCooldown = 0f;
    float shootInterval = 0.5f; // dispara cada 1 segundo


    public GameScreen(final Main gam) {
        this.game = gam;
        this.batch = game.batch;

        mapTexture = game.assetManager.get("GNOME/Map/Map.png", Texture.class);
        Texture playerTexture = game.assetManager.get("GNOME/Player/1.png", Texture.class);
        Texture snailTexture = game.assetManager.get("GNOME/Snail/1.png", Texture.class);

        touchBg = game.assetManager.get("GNOME/Player/1.png", Texture.class);
        touchKnob = game.assetManager.get("GNOME/Player/1.png", Texture.class);
        bulletTexture = new Texture("GNOME/Player/1.png");


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

        player = new Player(playerTexture, virtualWidth / 2f, VIRTUAL_HEIGHT / 2f);
        spawner = new Spawner(snailTexture, virtualWidth, VIRTUAL_HEIGHT);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f); // RGBA (1,0,0,1) = rojo sólido
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Input
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

        // Movimiento del player
        Vector2 direction = new Vector2();
        if (isTouching) {
            direction.set(touchCurrent).sub(touchOrigin);
            if (direction.len() > 10f) {
                direction.nor();
                player.update(delta, direction);
            }
        }

        //disapro
        shootCooldown -= delta;
        if (shootCooldown <= 0f && !spawner.getSnails().isEmpty()) {
            // Buscar el más cercano
            Snail closest = null;
            float minDist = Float.MAX_VALUE;

            for (Snail snail : spawner.getSnails()) {
                float dist = player.getPosition().dst2(snail.getPosition()); // dst2 es más rápido
                if (dist < minDist) {
                    minDist = dist;
                    closest = snail;
                }
            }

            // Si está cerca, disparar
            if (closest != null && Math.sqrt(minDist) < 200f) {
                bullets.add(player.shootAt(closest.getPosition(), bulletTexture));
                shootCooldown = shootInterval;
            }
        }


        spawner.update(delta,player.getPosition());

        for (Bullet bullet : bullets) {
            bullet.update(delta);
        }


        // Cámara sigue al jugador
        camera.position.set(player.getPosition(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Dibujar mapa y jugador
        batch.begin();
        batch.draw(mapTexture, 0, 0, virtualWidth, VIRTUAL_HEIGHT);

        spawner.render(batch);
        player.render(batch);
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }

        batch.end();

        // Dibujar joystick en HUD
        batch.setProjectionMatrix(hudCamera.combined);
        if (isTouching) {
            batch.begin();
            float bgSize = 100f;
            float knobSize = 50f;

            batch.draw(touchBg, touchOrigin.x - bgSize / 2f, touchOrigin.y - bgSize / 2f, bgSize, bgSize);

            Vector2 knobPos = new Vector2(touchCurrent);
            if (knobPos.dst(touchOrigin) > bgSize / 2f) {
                knobPos.sub(touchOrigin).nor().scl(bgSize / 2f).add(touchOrigin);
            }

            batch.draw(touchKnob, knobPos.x - knobSize / 2f, knobPos.y - knobSize / 2f, knobSize, knobSize);
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudViewport.update(width, height);

    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
