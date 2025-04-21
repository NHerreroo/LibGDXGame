package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Texture robotTexture;
    private List<Missile> missiles = new ArrayList<>();

    public Robot(Player player) {
        this.player = player;
        this.missileTexture = player.getGameScreen().game.assetManager.get("GNOME/bullet3.png", Texture.class);
        this.robotTexture = player.getGameScreen().game.assetManager.get("GNOME/robot.png", Texture.class);
    }

    @Override
    public void update(float delta) {
        fireCooldown -= delta;

        // Actualizar misiles
        Iterator<Missile> it = missiles.iterator();
        while (it.hasNext()) {
            Missile missile = it.next();
            missile.update(delta);

            if (missile.hasReachedTarget()) {
                it.remove();
            }
        }

        // Disparar misiles
        if (fireCooldown <= 0 && player.getGameScreen() != null) {
            Snail closest = findClosestEnemy();
            if (closest != null) {
                missiles.add(new Missile(missileTexture, player.getPosition(), closest.getPosition(), damage));
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
        batch.draw(robotTexture,
            player.getPosition().x + 30,
            player.getPosition().y + 15,
            robotTexture.getWidth()/2f, robotTexture.getHeight()/2f,
            robotTexture.getWidth(), robotTexture.getHeight(),
            1, 1, 0, 0, 0,
            robotTexture.getWidth(), robotTexture.getHeight(), false, false);
    }

    public List<Missile> getMissiles() {
        return new ArrayList<>(missiles); // Devolvemos copia para evitar modificaciones externas
    }

    public static class Missile {
        private Texture texture;
        private Vector2 position;
        private Vector2 target;
        private Vector2 velocity;
        private float speed = 150f;
        private boolean reachedTarget = false;
        private int damage;

        public Missile(Texture texture, Vector2 start, Vector2 target, int damage) {
            this.texture = texture;
            this.position = new Vector2(start);
            this.target = new Vector2(target);
            this.velocity = new Vector2(target).sub(start).nor().scl(speed);
            this.damage = damage;
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

        public void applyDamage() {
            if (position.dst(target) < 30f) {
                for (Snail snail : getNearbySnails()) {
                    snail.recibirDaño(damage);
                }
            }
        }

        private List<Snail> getNearbySnails() {
            // Implementación para obtener caracoles cercanos al objetivo
            List<Snail> nearby = new ArrayList<>();
            // Aquí deberías acceder a los enemigos del juego, similar a como lo hace Robot
            return nearby;
        }

        public void render(SpriteBatch batch) {
            if (!reachedTarget) {
                float rotation = velocity.angleDeg();
                batch.draw(texture,
                    position.x - texture.getWidth()/2f,
                    position.y - texture.getHeight()/2f,
                    texture.getWidth()/2f,
                    texture.getHeight()/2f,
                    texture.getWidth(),
                    texture.getHeight(),
                    1, 1, rotation,
                    0, 0,
                    texture.getWidth(),
                    texture.getHeight(),
                    false, false);
            }
        }

        public boolean hasReachedTarget() {
            return reachedTarget;
        }

        public Vector2 getPosition() {
            return new Vector2(position);
        }
    }
}
