package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Robot implements EquipableItem {
    private Player player;
    private float fireCooldown = 0;

    public Robot(Player player) {
        this.player = player;
    }

    @Override
    public void update(float delta) {
        fireCooldown -= delta;
        if (fireCooldown <= 0) {
            fireCooldown = 2f;
            // Aquí puedes añadir un misil a una lista si lo implementás
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.GRAY);
        sr.circle(player.getPosition().x + 50, player.getPosition().y + 30, 10);
        sr.end();
        sr.dispose();
    }
}
