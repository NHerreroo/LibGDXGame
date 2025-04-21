package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    public Halo(Player player) {
        this.player = player;
    }

    @Override
    public void update(float delta) {
        // Efecto de pulso
        if (expanding) {
            currentPulse += pulseSpeed;
            if (currentPulse >= pulseSize) {
                expanding = false;
            }
        } else {
            currentPulse -= pulseSpeed;
            if (currentPulse <= 0) {
                expanding = true;
            }
        }

        // Daño a enemigos
        timer += delta;
        if (timer >= damageInterval) {
            timer = 0;
            applyDamage();
        }
    }

    private void applyDamage() {
        if (player.getGameScreen() == null) return;

        List<Snail> snails = player.getGameScreen().getSpawner().getSnails();
        for (Snail snail : snails) {
            if (player.getPosition().dst(snail.getPosition()) <= radius) {
                snail.recibirDaño(damage);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ShapeRenderer sr = new ShapeRenderer();
        try {
            sr.setProjectionMatrix(batch.getProjectionMatrix());
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            // Aura interior con efecto de pulso
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(0, 0.8f, 0.8f, 0.35f);
            sr.circle(player.getPosition().x, player.getPosition().y, radius + currentPulse);
            sr.end();

            // Borde del halo con efecto de pulso
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(0, 1, 1, 0.8f);
            sr.circle(player.getPosition().x, player.getPosition().y, radius + currentPulse);
            sr.end();
        } finally {
            sr.dispose();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }
}
