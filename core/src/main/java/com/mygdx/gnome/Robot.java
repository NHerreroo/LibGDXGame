package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Robot implements EquipableItem {
    private Player player;
    private float fireCooldown = 0;
    private float fireRate = 2f;
    private int damage = 30;
    private Texture missileTexture;
    private List<Missile> missiles = new ArrayList<>();

    public Robot(Player player) {
        this.player = player;
        this.missileTexture = new Texture("GNOME/bullet.png"); // Necesitarás esta textura
    }

    @Override
    public void update(float delta) {
        fireCooldown -= delta;

        // Actualizar misiles
        Iterator<Missile> it = missiles.iterator();
        while (it.hasNext()) {
            Missile missile = it.next();
            missile.update(delta);

            // Eliminar misiles que han alcanzado su objetivo
            if (missile.hasReachedTarget()) {
                it.remove();
            }
        }

        // Disparar misiles
        if (fireCooldown <= 0 && player.getGameScreen() != null) {
            Snail closest = findClosestEnemy();
            if (closest != null) {
                missiles.add(new Missile(missileTexture, player.getPosition(), closest.getPosition()));
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
        // Dibujar el robot
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Cuerpo del robot
        sr.setColor(Color.GRAY);
        sr.circle(player.getPosition().x + 50, player.getPosition().y + 30, 15);

        // Ojos del robot
        sr.setColor(Color.RED);
        sr.circle(player.getPosition().x + 45, player.getPosition().y + 35, 3);
        sr.circle(player.getPosition().x + 55, player.getPosition().y + 35, 3);

        sr.end();

        // Dibujar misiles
        batch.begin();
        for (Missile missile : missiles) {
            missile.render(batch);
        }
        batch.end();

        sr.dispose();
    }

    private class Missile {
        private Texture texture;
        private Vector2 position;
        private Vector2 target;
        private Vector2 velocity;
        private float speed = 150f;
        private boolean reachedTarget = false;

        public Missile(Texture texture, Vector2 start, Vector2 target) {
            this.texture = texture;
            this.position = new Vector2(start);
            this.target = new Vector2(target);
            this.velocity = new Vector2(target).sub(start).nor().scl(speed);
        }

        public void update(float delta) {
            if (!reachedTarget) {
                position.mulAdd(velocity, delta);
                if (position.dst(target) < 10f) {
                    reachedTarget = true;
                    applyDamage();
                }
            }
        }

        private void applyDamage() {
            for (Snail snail : player.getGameScreen().getSpawner().getSnails()) {
                if (target.dst(snail.getPosition()) < 30f) {
                    snail.recibirDaño(damage);
                }
            }
        }

        public void render(SpriteBatch batch) {
            if (!reachedTarget) {
                float rotation = velocity.angleDeg();
                batch.draw(texture, position.x, position.y,
                    texture.getWidth()/2f, texture.getHeight()/2f,
                    texture.getWidth(), texture.getHeight(),
                    1, 1, rotation, 0, 0,
                    texture.getWidth(), texture.getHeight(), false, false);
            }
        }

        public boolean hasReachedTarget() {
            return reachedTarget;
        }
    }
}
