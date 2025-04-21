package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Lanza implements EquipableItem {
    private Player player;
    private float angle;
    private float rotationSpeed = 180f;
    private float distance = 50f;
    private int damage = 20;
    private float damageInterval = 0.5f;
    private float damageTimer = 0;
    private float length = 30f;

    public Lanza(Player player) {
        this.player = player;
    }

    @Override
    public void update(float delta) {
        angle += rotationSpeed * delta;
        damageTimer += delta;

        if (damageTimer >= damageInterval) {
            damageTimer = 0;
            applyDamage();
        }
    }

    private void applyDamage() {
        if (player.getGameScreen() == null) return;

        Vector2 lancePosition = new Vector2(
            player.getPosition().x + (float)Math.cos(Math.toRadians(angle)) * distance,
            player.getPosition().y + (float)Math.sin(Math.toRadians(angle)) * distance
        );

        for (Snail snail : player.getGameScreen().getSpawner().getSnails()) {
            if (lancePosition.dst(snail.getPosition()) < 20f) {
                snail.recibirDaño(damage);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Color dorado para la lanza
        sr.setColor(Color.GOLD);

        float x = player.getPosition().x + (float)Math.cos(Math.toRadians(angle)) * distance;
        float y = player.getPosition().y + (float)Math.sin(Math.toRadians(angle)) * distance;

        // Dibujar la lanza (un rectángulo rotado)
        sr.rect(x - 5, y - 5, 5, 5, 10, length, 1, 1, angle);

        sr.end();
        sr.dispose();
    }
}
