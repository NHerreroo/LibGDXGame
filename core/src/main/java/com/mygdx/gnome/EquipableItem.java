package com.mygdx.gnome;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface EquipableItem {
    void update(float delta);
    void render(SpriteBatch batch);
}
