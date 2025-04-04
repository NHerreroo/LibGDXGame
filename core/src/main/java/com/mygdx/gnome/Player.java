package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Texture texture;
    private Vector2 position;
    private float speed = 200f; // píxeles por segundo

    public Player(Texture texture, float x, float y) {
        this.texture = texture;
        this.position = new Vector2(x, y);
    }

    public void update(float delta, Vector2 direction) {
        if (direction.len() > 0.1f) {
            position.x += direction.x * speed * delta;
            position.y += direction.y * speed * delta;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - texture.getWidth() / 2f, position.y - texture.getHeight() / 2f);
    }

    public Vector2 getPosition() {
        return position;
    }
}
