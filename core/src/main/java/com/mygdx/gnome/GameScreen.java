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

public class GameScreen implements Screen {
    final Main game;
    OrthographicCamera camera;
    Viewport viewport;
    SpriteBatch batch;

    Texture mapTexture;
    Texture touchBg;
    Texture touchKnob;

    Player player;

    public static final float VIRTUAL_HEIGHT = 720f;
    public float virtualWidth;

    Vector2 touchOrigin = new Vector2();
    Vector2 touchCurrent = new Vector2();
    boolean isTouching = false;

    OrthographicCamera hudCamera;
    Viewport hudViewport;


    public GameScreen(final Main gam) {
        this.game = gam;
        this.batch = game.batch;

        mapTexture = game.assetManager.get("GNOME/Map/Map.png", Texture.class);
        Texture playerTexture = game.assetManager.get("GNOME/Player/1.png", Texture.class);
        touchBg = game.assetManager.get("GNOME/Player/1.png", Texture.class);
        touchKnob = game.assetManager.get("GNOME/Player/1.png", Texture.class);

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

        // Movimiento del jugador
        Vector2 direction = new Vector2();
        if (isTouching) {
            direction.set(touchCurrent).sub(touchOrigin);
            if (direction.len() > 10f) {
                direction.nor();
                player.update(delta, direction);
            }
        }

        // Cámara sigue al jugador
        camera.position.set(player.getPosition(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Dibujar mapa y jugador
        batch.begin();
        batch.draw(mapTexture, 0, 0, virtualWidth, VIRTUAL_HEIGHT);
        player.render(batch);
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
