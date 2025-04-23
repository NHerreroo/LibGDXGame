package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Snail {
    private Texture normalTexture;
    private Texture damageTexture;
    private Texture currentTexture;

    private Vector2 position;
    private float speed = 20f; // píxeles por segundo

    private int vida = 30;

    // Para controlar el tiempo que mostramos la textura de daño
    private boolean isDamaged = false;
    private float damageTimer = 0f;
    private static final float DAMAGE_DURATION = 0.1f; // segundos

    public Snail(Texture texture, float x, float y) {
        this.normalTexture = texture;
        this.damageTexture = new Texture(Gdx.files.internal("GNOME/Snail/damage.png"));
        this.currentTexture = normalTexture;
        this.position = new Vector2(x, y);
    }

    public void update(float delta, Vector2 playerPosition) {
        if (isDamaged) {
            damageTimer -= delta;
            if (damageTimer <= 0f) {
                isDamaged = false;
                currentTexture = normalTexture;
            }
        }

        // Movimiento hacia el jugador
        Vector2 direction = new Vector2(playerPosition).sub(position).nor();
        if (position.dst(playerPosition) > 1f) {
            position.x += direction.x * speed * delta;
            position.y += direction.y * speed * delta;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(
            currentTexture,
            position.x - currentTexture.getWidth()  / 2f,
            position.y - currentTexture.getHeight() / 2f
        );
    }

    public Vector2 getPosition() {
        return position;
    }

    public void recibirDaño(int cantidad) {
        vida -= cantidad;
        isDamaged = true;
        damageTimer = DAMAGE_DURATION;
        currentTexture = damageTexture;
    }

    public boolean estaMuerto() {
        return vida <= 0;
    }

    public void dispose() {
        damageTexture.dispose();
    }
}
