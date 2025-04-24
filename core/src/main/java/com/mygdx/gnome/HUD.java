package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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

    // Icon textures
    private Texture vidaIcon;
    private Texture velocidadIcon;
    private Texture ataqueIcon;
    private Texture cadenciaIcon;
    private Texture dineroIcon;

    public HUD(float width, float height, Player player, BitmapFont gamefont) {
        this.virtualWidth = width;
        this.virtualHeight = height;
        this.player = player;
        this.timeLeft = 60f;

        hudCamera = new OrthographicCamera();
        hudViewport = new FillViewport(width, height, hudCamera);
        hudCamera.position.set(width / 2f, height / 2f, 0);
        hudCamera.update();

        font = gamefont;

        // Load icons (ajusta rutas según tu proyecto)
        vidaIcon      = new Texture(Gdx.files.internal("GNOME/HEART.png"));
        velocidadIcon = new Texture(Gdx.files.internal("GNOME/VELOCIDAD.png"));
        ataqueIcon    = new Texture(Gdx.files.internal("GNOME/DAMAGE.png"));
        cadenciaIcon  = new Texture(Gdx.files.internal("GNOME/CADENCIA.png"));
        dineroIcon    = new Texture(Gdx.files.internal("GNOME/Coin.png"));
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

        String tiempoFormateado = String.format("%01d:%02d",
            (int)(timeLeft / 60), (int)(timeLeft % 60));
        font.draw(batch, "Tiempo: " + tiempoFormateado,
            virtualWidth / 2f - 50, virtualHeight - 20);

        float baseY = virtualHeight - 60;
        float iconSize = 32f;
        float textOffset = iconSize + 5f;
        float x = 200;

        batch.draw(vidaIcon, x, baseY - iconSize, iconSize, iconSize);
        font.draw(batch, String.valueOf(player.getVidas()),
            x + textOffset, baseY - iconSize/2f + font.getCapHeight()/2f);

        float yVel = baseY - 40;
        batch.draw(velocidadIcon, x, yVel - iconSize, iconSize, iconSize);
        font.draw(batch, String.valueOf(player.getVelocidad()),
            x + textOffset, yVel - iconSize/2f + font.getCapHeight()/2f);

        float yAtt = baseY - 80;
        batch.draw(ataqueIcon, x, yAtt - iconSize, iconSize, iconSize);
        font.draw(batch, String.valueOf(player.getAtaque()),
            x + textOffset, yAtt - iconSize/2f + font.getCapHeight()/2f);

        float yCad = baseY - 120;
        batch.draw(cadenciaIcon, x, yCad - iconSize, iconSize, iconSize);
        font.draw(batch, String.valueOf(player.getCadencia()),
            x + textOffset, yCad - iconSize/2f + font.getCapHeight()/2f);

        float yMon = baseY - 160;
        batch.draw(dineroIcon, x, yMon - iconSize, iconSize, iconSize);
        font.draw(batch, String.valueOf(player.getDinero()),
            x + textOffset, yMon - iconSize/2f + font.getCapHeight()/2f);

        batch.end();
    }

    public void resetTimeLeft() {
        timeLeft = 60f;
    }

    public void dispose() {
        vidaIcon.dispose();
        velocidadIcon.dispose();
        ataqueIcon.dispose();
        cadenciaIcon.dispose();
        dineroIcon.dispose();
    }
}
