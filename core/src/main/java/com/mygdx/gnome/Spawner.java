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
    private float spawnInterval = 3f; // cada 3 segundos
    private int maxSnails = 10;

    private float mapWidth;
    private float mapHeight;

    public Spawner(Texture snailTexture, float mapWidth, float mapHeight) {
        this.snailTexture = snailTexture;
        this.snails = new ArrayList<>();
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public void update(float delta, Vector2 playerPosition) {
        spawnTimer += delta;

        //if (spawnTimer >= spawnInterval && snails.size() < maxSnails)   LIMITE DE CARACOLES
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            spawnSnail();
        }

        for (Snail snail : snails) {
            snail.update(delta, playerPosition);
        }
    }

    private void spawnSnail() {
        float x = (float) Math.random() * mapWidth;
        float y = (float) Math.random() * mapHeight;

        snails.add(new Snail(snailTexture, x, y));
    }

    public void render(SpriteBatch batch) {
        for (Snail snail : snails) {
            snail.render(batch);
        }
    }

    public List<Snail> getSnails() {
        return snails;
    }
}
