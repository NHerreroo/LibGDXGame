package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class AK47 implements EquipableItem {
    private Player player;
    private float fireCooldown = 0;

    public AK47(Player player) {
        this.player = player;
    }

    @Override
    public void update(float delta) {
        fireCooldown -= delta;
        if (fireCooldown <= 0) {
            fireCooldown = 0.2f;
            // Simula disparo automático (lógica a futuro)
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.BROWN);
        sr.rect(player.getPosition().x + 20, player.getPosition().y - 10, 30, 10);
        sr.end();
        sr.dispose();
    }
}
