package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Snail {
    private Texture texture;
    private Vector2 position;
    private float speed = 20f; // píxeles por segundo

    public Snail(Texture texture, float x, float y) {
        this.texture = texture;
        this.position = new Vector2(x, y);
    }

    public void update(float delta, Vector2 playerPosition) {
        Vector2 direction = new Vector2(playerPosition).sub(position).nor(); // Dirección hacia el jugador

        if (position.dst(playerPosition) > 1f) {
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
