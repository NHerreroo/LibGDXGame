package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class AK47 implements EquipableItem {
    private Player player;
    private float fireCooldown = 0;
    private float fireRate = 0.1f; // Disparos muy rápidos
    private int damage = 8;
    private Texture bulletTexture;
    private List<Bullet> bullets = new ArrayList<>();

    public AK47(Player player) {
        this.player = player;
        this.bulletTexture = new Texture("GNOME/bullet.png"); // Necesitarás esta textura
    }

    @Override
    public void update(float delta) {
        fireCooldown -= delta;

        // Actualizar balas
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.update(delta);

            // Eliminar balas fuera de pantalla
            if (bullet.getPosition().dst(player.getPosition()) > 500) {
                it.remove();
            }
        }

        // Disparar automáticamente al enemigo más cercano
        if (fireCooldown <= 0 && player.getGameScreen() != null) {
            Snail closest = findClosestEnemy();
            if (closest != null) {
                Vector2 target = closest.getPosition();
                bullets.add(new Bullet(bulletTexture, player.getPosition(), target));
                fireCooldown = fireRate;
            }
        }
    }

    private Snail findClosestEnemy() {
        if (player.getGameScreen() == null) return null;

        Snail closest = null;
        float minDist = Float.MAX_VALUE;

        for (Snail snail : player.getGameScreen().getSpawner().getSnails()) {
            float dist = player.getPosition().dst2(snail.getPosition());
            if (dist < minDist) {
                minDist = dist;
                closest = snail;
            }
        }

        return closest;
    }

    @Override
    public void render(SpriteBatch batch) {
        // Dibujar el arma en el jugador
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.BROWN);
        sr.rect(player.getPosition().x + 20, player.getPosition().y - 10, 30, 10);
        sr.end();

        // Dibujar las balas
        batch.begin();
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
        batch.end();

        sr.dispose();
    }

    public Collection<? extends Bullet> getBullets() {
        return bullets;
    }
}
