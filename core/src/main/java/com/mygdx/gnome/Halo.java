package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Halo implements EquipableItem {
    private Player player;
    private float time = 0;

    @Override
    public void update(float delta) {
        time += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.CYAN);
        sr.circle(player.getPosition().x, player.getPosition().y, 60);
        sr.end();
        sr.dispose();
    }

    public Halo(Player player) {
        this.player = player;
    }
}
