package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Spawner {
    private Texture snailTexture;
    private List<Snail> snails;
    private float spawnTimer = 0f;
    private float spawnInterval = 3f;
    // private int maxSnails = 10;  // ya no lo usamos

    private float mapWidth;
    private float mapHeight;

    private int round = 1;

    public Spawner(Texture snailTexture, float mapWidth, float mapHeight) {
        this.snailTexture = snailTexture;
        this.snails = new ArrayList<>();
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<Snail> getSnails() {
        return snails;
    }

    public void eliminarTodos() {
        snails.clear();
    }

    public void update(float delta, Vector2 playerPosition, boolean tiendaActiva) {
        if (tiendaActiva) return;

        spawnTimer += delta;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            // --- NUEVO: generar 2^(round-1) caracoles esta vez ---
            int count = (int)Math.pow(2, round - 1);
            for (int i = 0; i < count; i++) {
                spawnSnail();
            }
        }

        for (Snail snail : snails) {
            snail.update(delta, playerPosition);
        }
    }

    private void spawnSnail() {
        float x = (float)Math.random() * mapWidth;
        float y = (float)Math.random() * mapHeight;
        snails.add(new Snail(snailTexture, x, y));
    }

    public void render(SpriteBatch batch) {
        for (Snail snail : snails) {
            snail.render(batch);
        }
    }
}
