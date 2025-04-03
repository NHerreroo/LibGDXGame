package com.mygdx.gnome;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class GameScreen implements Screen {
    final Main game;

    public GameScreen(final Main gam) {
        this.game = gam;
    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        game.batch.draw(game.assetManager.get("GNOME/Map/Map.png", Texture.class), 0, 0);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}

