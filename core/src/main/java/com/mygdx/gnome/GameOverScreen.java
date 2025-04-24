// core/src/main/java/com/mygdx/gnome/GameOverScreen.java
package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {
    final Main game;
    OrthographicCamera camera;
    BitmapFont titleFont, textFont;

    public GameOverScreen(final Main gam) {
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        titleFont = game.getBigFont();
        textFont  = game.getSmallFont();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();
        titleFont.draw(game.getBatch(), "GAME OVER", 260, 300);
        textFont.draw(game.getBatch(), "Toca para volver al menú", 200, 200);
        game.getBatch().end();

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
    }
}
