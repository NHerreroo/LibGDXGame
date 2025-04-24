package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Halo implements EquipableItem {
    private final Player player;
    private final Texture texture;
    private final float baseRadius = 50f;
    private final int damage = 2;
    private final float damageInterval = 1f;
    private final float pulseSpeed = 3f;
    private final float pulseMax = 10f;

    private float pulseTimer = 0f;
    private float damageTimer = 0f;

    public Halo(Player player) {
        this.player = player;
        this.texture = player.getGameScreen()
            .game.assetManager
            .get("GNOME/halo.png", Texture.class);
    }

    @Override
    public void update(float delta) {
        // actualiza pulso (va y vuelve entre 0 y pulseMax)
        pulseTimer = (pulseTimer + delta * pulseSpeed) % (pulseMax * 2);

        // cada damageInterval aplica daño
        damageTimer += delta;
        if (damageTimer >= damageInterval) {
            damageTimer -= damageInterval;
            applyDamage();
        }
    }

    private void applyDamage() {
        Vector2 pos = player.getPosition();
        // calcula el offset de pulso (va de 0→pulseMax→0)
        float p = pulseTimer <= pulseMax
            ? pulseTimer
            : pulseMax * 2 - pulseTimer;
        float radius = baseRadius + p;

        for (Snail snail : player.getGameScreen().getSpawner().getSnails()) {
            if (pos.dst(snail.getPosition()) <= radius) {
                snail.recibirDaño(damage);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 pos = player.getPosition();
        // escalar según baseRadius + pulso
        float p = pulseTimer <= pulseMax
            ? pulseTimer
            : pulseMax * 2 - pulseTimer;
        float scale = (baseRadius + p) * 2f / texture.getWidth();

        batch.draw(
            texture,
            pos.x - (texture.getWidth() * scale) / 2f,
            pos.y - (texture.getHeight() * scale) / 2f,
            texture.getWidth() * scale,
            texture.getHeight() * scale
        );
    }
    public float getRadius() {
        return baseRadius;
    }

    public int getDamage() {
        return damage;
    }

}
