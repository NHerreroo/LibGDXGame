package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private Vector2 position;
    private Vector2 velocity;
    private Texture texture;
    private float speed = 200f;

    public Bullet(Texture texture, Vector2 start, Vector2 target) {
        this.texture = texture;
        this.position = new Vector2(start);
        this.velocity = new Vector2(target).sub(start).nor().scl(speed);
    }

    public void update(float delta) {
        position.mulAdd(velocity, delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - texture.getWidth() / 2f, position.y - texture.getHeight() / 2f);
    }

    public Vector2 getPosition() {
        return position;
    }
}
