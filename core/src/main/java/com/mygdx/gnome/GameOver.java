package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameOver implements Screen {
    final Main game;
    OrthographicCamera camera;
    Viewport viewport;
    Texture mapTexture;
    Texture titleTexture;
    Texture touchTexture;

    float virtualHeight = 720f;
    float virtualWidth;

    public GameOver(final Main gam) {
        game = gam;

        mapTexture = game.assetManager.get("GNOME/Map/Map.png", Texture.class);
        titleTexture = game.assetManager.get("GNOME/gameover.png", Texture.class);
        touchTexture = game.assetManager.get("GNOME/TOUCH.png", Texture.class);

        float aspectRatio = (float) mapTexture.getWidth() / mapTexture.getHeight();
        virtualWidth = virtualHeight * aspectRatio;

        camera = new OrthographicCamera();
        viewport = new FillViewport(virtualWidth, virtualHeight, camera);
        camera.position.set(virtualWidth / 2f, virtualHeight / 2f, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        SpriteBatch batch = game.batch;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(mapTexture, 0, 0, virtualWidth, virtualHeight);

        float titleScale = 0.3f;
        float titleW = titleTexture.getWidth() * titleScale;
        float titleH = titleTexture.getHeight() * titleScale;
        batch.draw(titleTexture,
            virtualWidth / 2f - titleW / 2f,
            virtualHeight - titleH - 50,
            titleW, titleH
        );

        float touchScale = 0.2f;
        float touchW = touchTexture.getWidth() * touchScale;
        float touchH = touchTexture.getHeight() * touchScale;
        batch.draw(touchTexture,
            virtualWidth / 2f - touchW / 2f,
            50,
            touchW, touchH
        );

        batch.end();

        if (Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
