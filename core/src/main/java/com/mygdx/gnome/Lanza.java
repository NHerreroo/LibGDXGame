package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Lanza implements EquipableItem {
    private Player player;
    private float angle = 0f;

    public Lanza(Player player) {
        this.player = player;
    }

    @Override
    public void update(float delta) {
        angle += 180 * delta; // 180° por segundo
    }

    @Override
    public void render(SpriteBatch batch) {
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.GOLD);

        float radius = 40f;
        float x = player.getPosition().x + (float)Math.cos(Math.toRadians(angle)) * radius;
        float y = player.getPosition().y + (float)Math.sin(Math.toRadians(angle)) * radius;

        sr.rect(x - 5, y - 5, 10, 30);
        sr.end();
        sr.dispose();
    }
}
