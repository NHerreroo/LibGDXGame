package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Moneda {
    private Vector2 position;
    private Texture texture;
    private float pickupRadius = 20f;   // radio de recogida
    private int value = (int) (Math.random() * 3);

    public Moneda(Texture texture, float x, float y) {
        this.texture  = texture;
        this.position = new Vector2(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getPickupRadius() {
        return pickupRadius;
    }

    public int getValue() {
        return value;
    }

    public void render(SpriteBatch batch) {
        batch.draw(
            texture,
            position.x - texture.getWidth()  / 2f,
            position.y - texture.getHeight() / 2f
        );
    }
}
