
package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen implements Screen {
    final Main game;
    OrthographicCamera camera;

    public MainMenuScreen(final Main gam) {
        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();
        BitmapFont big = game.getBigFont();
        String title = "Gnome";
        float tw = big.getRegion().getRegionWidth() * title.length() / 2f;
        big.draw(game.getBatch(), title, 400 - tw, 300);


        BitmapFont small = game.getSmallFont();
        String prompt = "Toca para iniciar";
        float pw = small.getRegion().getRegionWidth() * prompt.length() / 2f;
        small.draw(game.getBatch(), prompt, 400 - pw, 240);
        game.getBatch().end();

        if (Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override public void resize(int width, int height) { }
    @Override public void show() { }
    @Override public void hide() { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void dispose() { }
}
