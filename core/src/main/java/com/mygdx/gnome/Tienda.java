package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Tienda {
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private float virtualWidth;
    private float virtualHeight;

    public boolean activa = false;

    public Tienda(float width, float height) {
        this.virtualWidth = width;
        this.virtualHeight = height;

        camera = new OrthographicCamera();
        viewport = new FillViewport(width, height, camera);
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();

        font = new BitmapFont();
        font.getData().setScale(4f); // tamaño grande para el título

        shapeRenderer = new ShapeRenderer();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void show() {
        activa = true;
    }

    public void render(SpriteBatch batch) {
        if (!activa) return;


        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f); // negro con 50% alpha
        shapeRenderer.rect(0, 0, virtualWidth, virtualHeight);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Tienda", virtualWidth / 2f - 100, virtualHeight / 2f + 20);
        batch.end();
    }
}
