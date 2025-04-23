package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Robot implements EquipableItem {
    private Player player;

    // Offset base donde aparece el robot alrededor del jugador
    private Vector2 baseOffset;
    // Parámetros del “idle”:
    private float idleTimer = 0f;
    private float idleSpeed = 2f;       // velocidad de oscilación
    private float idleAmplitude = 10f;  // amplitud en píxeles

    private float fireCooldown = 0;
    private float fireRate = 2f;
    private int damage = 30;
    private Texture missileTexture;
    private Texture robotTexture;
    private List<Missile> missiles = new ArrayList<>();

    public Robot(Player player, Vector2 offset) {
        this.player     = player;
        this.baseOffset = new Vector2(offset);
        this.missileTexture = player.getGameScreen().game.assetManager.get("GNOME/bullet3.png", Texture.class);
        this.robotTexture   = player.getGameScreen().game.assetManager.get("GNOME/robot.png",   Texture.class);
    }

    @Override
    public void update(float delta) {
        // Actualizamos el timer del idle
        idleTimer += delta;

        // Calculamos el offset oscilatorio
        float idleX = (float)Math.cos(idleTimer * idleSpeed) * idleAmplitude;
        float idleY = (float)Math.sin(idleTimer * idleSpeed) * idleAmplitude;
        Vector2 currentOffset = new Vector2(baseOffset.x + idleX, baseOffset.y + idleY);

        // Disparo de misiles
        fireCooldown -= delta;
        Iterator<Missile> it = missiles.iterator();
        while (it.hasNext()) {
            Missile m = it.next();
            m.update(delta);
            if (m.hasReachedTarget()) it.remove();
        }

        if (fireCooldown <= 0) {
            Snail target = findClosestEnemy();
            if (target != null) {
                Vector2 start = new Vector2(player.getPosition()).add(currentOffset);
                missiles.add(new Missile(
                    missileTexture,
                    start,
                    target.getPosition(),
                    damage,
                    player.getGameScreen()
                ));
                fireCooldown = fireRate;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Recalculamos el mismo offset idle para render
        float idleX = (float)Math.cos(idleTimer * idleSpeed) * idleAmplitude;
        float idleY = (float)Math.sin(idleTimer * idleSpeed) * idleAmplitude;
        Vector2 currentOffset = new Vector2(baseOffset.x + idleX, baseOffset.y + idleY);

        // Dibujamos el robot oscilando
        Vector2 pos = new Vector2(player.getPosition()).add(currentOffset);
        batch.draw(
            robotTexture,
            pos.x - robotTexture.getWidth()/2f,
            pos.y - robotTexture.getHeight()/2f
        );

        // Dibujamos sus misiles
        for (Missile m : missiles) {
            m.render(batch);
        }
    }

    private Snail findClosestEnemy() {
        Snail best = null;
        float md = Float.MAX_VALUE;
        for (Snail s : player.getGameScreen().getSpawner().getSnails()) {
            float d = player.getPosition().dst2(s.getPosition());
            if (d < md) {
                md = d;
                best = s;
            }
        }
        return best;
    }

    public List<Missile> getMissiles() {
        return new ArrayList<>(missiles);
    }

    // === Clase interna Missile sin cambios ===
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
            this.texture   = texture;
            this.position  = new Vector2(start);
            this.target    = new Vector2(target);
            this.velocity  = new Vector2(target).sub(start).nor().scl(speed);
            this.damage    = damage;
            this.gameScreen= gameScreen;
        }

        public void update(float delta) {
            if (reachedTarget) return;
            time += delta;
            Vector2 baseVel = new Vector2(velocity);
            float wave = (float)Math.sin(time * 10 + waveOffset) * 50f;
            Vector2 perp = new Vector2(-velocity.y, velocity.x).nor().scl(wave);
            Vector2 finalVel = baseVel.add(perp).nor().scl(speed);
            position.mulAdd(finalVel, delta);
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
            batch.draw(
                texture,
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
                false, false
            );
        }

        public boolean hasReachedTarget() {
            return reachedTarget;
        }

        public Vector2 getPosition() {
            return new Vector2(position);
        }
    }
}
