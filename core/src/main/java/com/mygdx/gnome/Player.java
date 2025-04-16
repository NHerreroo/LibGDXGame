package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Texture texture;
    private Vector2 position;
    private float speed = 50f; // píxeles por segundo
    private int vidas = 3;
    private int ataque = 10;
    private float cadencia = 1.8f;

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

    public Bullet shootAt(Vector2 target, Texture bulletTexture) {
        return new Bullet(bulletTexture, position, target);
    }


    public int getVidas() {
        return vidas;
    }

    public float getVelocidad() {
        return speed;
    }

    public int getAtaque() {
        return ataque;
    }

    public float getCadencia() {
        return cadencia;
    }


}
