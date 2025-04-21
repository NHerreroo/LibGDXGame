package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Lanza implements EquipableItem {
    private Player player;
    private float angle;
    private float rotationSpeed = 180f;
    private float distance = 50f;
    private int damage = 20;
    private float damageInterval = 0.5f;
    private float damageTimer = 0;
    private Texture spearTexture;

    public Lanza(Player player) {
        this.player = player;
        this.spearTexture = player.getGameScreen().game.assetManager.get("GNOME/spear.png", Texture.class);
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
        float x = player.getPosition().x + (float)Math.cos(Math.toRadians(angle)) * distance;
        float y = player.getPosition().y + (float)Math.sin(Math.toRadians(angle)) * distance;

        batch.draw(spearTexture,
            x - spearTexture.getWidth()/2f,
            y - spearTexture.getHeight()/2f,
            spearTexture.getWidth()/2f, spearTexture.getHeight()/2f,
            spearTexture.getWidth(), spearTexture.getHeight(),
            1, 1, angle, 0, 0,
            spearTexture.getWidth(), spearTexture.getHeight(), false, false);
    }
}
