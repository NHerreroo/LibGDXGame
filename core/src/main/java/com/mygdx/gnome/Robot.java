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

    private Vector2 offset = new Vector2(-40, 60); // posición relativa del robot respecto al jugador

    public Robot(Player player, Vector2 offset) {
        this.player = player;
        this.offset = offset;
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

        // Disparar desde la posición del robot
        if (fireCooldown <= 0 && player.getGameScreen() != null) {
            Snail closest = findClosestEnemy();
            if (closest != null) {
                Vector2 startPos = getRobotPosition(); // posición del robot, no del jugador
                missiles.add(new Missile(missileTexture, startPos, closest.getPosition(), damage, player.getGameScreen()));
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

    private Vector2 getRobotPosition() {
        return new Vector2(player.getPosition()).add(offset);
    }

    @Override
    public void render(SpriteBatch batch) {
        // Dibujar el robot flotando al lado del jugador
        Vector2 robotPos = getRobotPosition();
        batch.draw(robotTexture,
            robotPos.x - robotTexture.getWidth() / 2f,
            robotPos.y - robotTexture.getHeight() / 2f);

        // Dibujar los misiles
        for (Missile missile : missiles) {
            missile.render(batch);
        }
    }

    public List<Missile> getMissiles() {
        return new ArrayList<>(missiles);
    }

    public class Missile {
        private Texture texture;
        private Vector2 position;
        private Vector2 target;
        private Vector2 velocity;
        private float speed = 150f;
        private boolean reachedTarget = false;
        private int damage;
        private float rotation = 0f;
        private float waveOffset = (float)(Math.random() * Math.PI * 2);
        private float time = 0f;

        private GameScreen gameScreen;

        public Missile(Texture texture, Vector2 start, Vector2 target, int damage, GameScreen gameScreen) {
            this.texture = texture;
            this.position = new Vector2(start);
            this.target = new Vector2(target);
            this.velocity = new Vector2(target).sub(start).nor().scl(speed);
            this.damage = damage;
            this.gameScreen = gameScreen;
        }

        public void update(float delta) {
            if (reachedTarget) return;

            time += delta;

            // Movimiento base + efecto de onda
            Vector2 baseVelocity = new Vector2(velocity);
            float wave = (float)Math.sin(time * 10 + waveOffset) * 50f;
            Vector2 perpendicular = new Vector2(-velocity.y, velocity.x).nor().scl(wave);
            Vector2 finalVelocity = baseVelocity.add(perpendicular).nor().scl(speed);

            position.mulAdd(finalVelocity, delta);
            rotation += 720 * delta;

            if (position.dst(target) < 15f) {
                reachedTarget = true;
                applyDamage();
            }
        }

        public void applyDamage() {
            for (Snail snail : gameScreen.getSpawner().getSnails()) {
                if (snail.getPosition().dst(position) < 30f) {
                    snail.recibirDaño(damage);
                }
            }
        }

        public void render(SpriteBatch batch) {
            if (reachedTarget) return;

            batch.draw(texture,
                position.x - texture.getWidth()/2f,
                position.y - texture.getHeight()/2f,
                texture.getWidth()/2f,
                texture.getHeight()/2f,
                texture.getWidth(),
                texture.getHeight(),
                1f, 1f,
                rotation,
                0, 0,
                texture.getWidth(),
                texture.getHeight(),
                false, false);
        }

        public boolean hasReachedTarget() {
            return reachedTarget;
        }

        public Vector2 getPosition() {
            return new Vector2(position);
        }
    }
}
