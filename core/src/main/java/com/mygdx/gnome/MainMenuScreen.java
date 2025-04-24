package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class MainMenuScreen implements Screen {
    final Main game;
    OrthographicCamera camera;
    Texture mapTexture;
    BitmapFont titleFont;
    BitmapFont smallFont;

    public MainMenuScreen(final Main gam) {
        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Cargar el fondo de mapa
        mapTexture = game.assetManager.get("GNOME/Map/Map.png", Texture.class);  // :contentReference[oaicite:0]{index=0}&#8203;:contentReference[oaicite:1]{index=1}

        // Obtener fuentes
        titleFont = game.getTitleFont();
        smallFont = game.getSmallFont();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        // Dibujar fondo
        game.batch.draw(mapTexture,
            0, 0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        // Dibujar título
        titleFont.setColor(1, 1, 1, 1);
        titleFont.draw(game.batch,
            "yard Slug",
            Gdx.graphics.getWidth() * 0.5f - 80,
            Gdx.graphics.getHeight() * 0.75f
        );

        // Dibujar indicación
        smallFont.setColor(1, 1, 1, 1);
        smallFont.draw(game.batch,
            "Toca para empezar",
            Gdx.graphics.getWidth() * 0.5f - 90,
            Gdx.graphics.getHeight() * 0.25f
        );
        game.batch.end();

        if (Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void show() { }

    @Override
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        mapTexture.dispose();
    }
}
