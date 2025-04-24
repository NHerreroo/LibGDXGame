package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {
    final Main game;
    OrthographicCamera camera;
    BitmapFont font;
    SpriteBatch batch;

    public GameOverScreen(final Main gam) {
        this.game = gam;
        this.batch = game.batch;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        font = new BitmapFont();
        font.getData().setScale(3f);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, "GAME OVER", 260, 300);
        font.draw(batch, "Toca para volver al menú", 200, 200);
        batch.end();

        if (Gdx.input.justTouched()) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override public void resize(int w, int h) { }
    @Override public void show() { }
    @Override public void hide() { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override
    public void dispose() {
        font.dispose();
    }
}
