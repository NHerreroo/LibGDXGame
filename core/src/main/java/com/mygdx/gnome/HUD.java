package com.mygdx.gnome;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HUD {
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;
    private BitmapFont font;

    private float virtualWidth;
    private float virtualHeight;
    private float timeLeft;

    private Player player;

    public HUD(float width, float height, Player player) {
        this.virtualWidth = width;
        this.virtualHeight = height;
        this.player = player;
        this.timeLeft = 20f;

        hudCamera = new OrthographicCamera();
        hudViewport = new FillViewport(width, height, hudCamera);
        hudCamera.position.set(width / 2f, height / 2f, 0);
        hudCamera.update();

        font = new BitmapFont();
        font.getData().setScale(2f);
    }

    public float getTimeLeft() {
        return timeLeft;
    }


    public void update(float delta) {
        timeLeft -= delta;
        if (timeLeft < 0) timeLeft = 0;
    }

    public void resize(int width, int height) {
        hudViewport.update(width, height);
    }

    public void render(SpriteBatch batch) {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        String tiempoFormateado = String.format("%01d:%02d", (int)(timeLeft / 60), (int)(timeLeft % 60));
        font.draw(batch, "Tiempo: " + tiempoFormateado, virtualWidth / 2f - 50, virtualHeight - 20);

        float baseY = virtualHeight - 60;
        font.draw(batch, "Vidas: " + player.getVidas(), 200, baseY);
        font.draw(batch, "Velocidad: " + player.getVelocidad(), 200, baseY -40);
        font.draw(batch, "Ataque: " + player.getAtaque(), 200, baseY -80);
        font.draw(batch, "Cadencia: " + player.getCadencia(), 200, baseY -120);

        batch.end();
    }
}
