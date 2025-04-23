package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Halo implements EquipableItem {
    private Player player;
    private float timer = 0;
    private float damageInterval = 0.5f;
    private float radius = 60f;
    private float pulseSpeed = 3f;
    private float pulseSize = 5f;
    private float currentPulse = 0;
    private boolean expanding = true;
    private int damage = 15;

    // Nueva textura para el sprite del halo
    private Texture haloTexture;

    public Halo(Player player) {
        this.player = player;
        // Carga tu textura; reemplaza "GNOME/halo.png" por la ruta que quieras
        this.haloTexture = player.getGameScreen()
            .game.assetManager
            .get("GNOME/halo.png", Texture.class);
    }

    @Override
    public void update(float delta) {
        // Efecto de pulso
        if (expanding) {
            currentPulse += pulseSpeed * delta;
            if (currentPulse >= pulseSize) expanding = false;
        } else {
            currentPulse -= pulseSpeed * delta;
            if (currentPulse <= 0) expanding = true;
        }

        // Daño a enemigos cada damageInterval
        timer += delta;
        if (timer >= damageInterval) {
            timer = 0;
            applyDamage();
        }
    }

    private void applyDamage() {
        if (player.getGameScreen() == null) return;

        List<Snail> snails = player.getGameScreen().getSpawner().getSnails();
        Vector2 posPlayer = player.getPosition();
        float effectiveRadius = radius;

        for (Snail snail : snails) {
            if (posPlayer.dst(snail.getPosition()) <= effectiveRadius) {
                snail.recibirDaño(damage);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 pos = player.getPosition();
        // Escala para que pulse igual que antes
        float scale = (radius + currentPulse) * 2f / haloTexture.getWidth();

        // -- NO volver a llamar a batch.begin() ni batch.end() aquí --
        batch.draw(
            haloTexture,
            pos.x - (haloTexture.getWidth()  * scale) / 2f,
            pos.y - (haloTexture.getHeight() * scale) / 2f,
            haloTexture.getWidth()  * scale,
            haloTexture.getHeight() * scale
        );
    }

}
