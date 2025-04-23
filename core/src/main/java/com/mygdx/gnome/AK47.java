package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AK47 implements EquipableItem {
    private Player player;
    private float fireCooldown = 0;
    private float fireRate = 0.3f;
    private int damage = 8;

    private int level = 1;
    private List<Float> baseAngles = new ArrayList<>();   // offsets en grados
    private float distance = 50f;                         // distancia al jugador

    private Texture bulletTexture;
    private Texture weaponTexture;
    private List<Bullet> bullets = new ArrayList<>();

    public AK47(Player player) {
        this.player = player;
        this.bulletTexture = player.getGameScreen().game.assetManager
            .get("GNOME/bullet2.png", Texture.class);
        this.weaponTexture = player.getGameScreen().game.assetManager
            .get("GNOME/ak47.png", Texture.class);
        updateBaseAngles();
    }

    /** Sube nivel hasta 6 y recalcula offsets */
    public void upgrade() {
        if (level < 6) {
            level++;
            updateBaseAngles();
        }
    }

    private void updateBaseAngles() {
        baseAngles.clear();
        // repartir uniformemente alrededor de 360°
        for (int i = 0; i < level; i++) {
            baseAngles.add(i * (360f / level));
        }
    }

    @Override
    public void update(float delta) {
        fireCooldown -= delta;

        // actualizar balas vivas
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update(delta);
            if (b.getPosition().dst(player.getPosition()) > 500) {
                it.remove();
            }
        }

        // disparo en ráfaga si hay enemigos
        if (fireCooldown <= 0) {
            Snail any = findClosestEnemy();
            if (any != null) {
                Vector2 center = player.getPosition();
                // ángulo de mirada del jugador (última dirección de movimiento)
                Vector2 lastDir = player.getLastDirection().nor();
                float lookAngle = MathUtils.atan2(lastDir.y, lastDir.x) * MathUtils.radiansToDegrees;

                for (float offset : baseAngles) {
                    float actualAngle = lookAngle + offset;
                    float rad = actualAngle * MathUtils.degreesToRadians;

                    // dirección de disparo
                    Vector2 dir = new Vector2(MathUtils.cos(rad), MathUtils.sin(rad)).nor();
                    // punto de inicio
                    Vector2 start = new Vector2(center).mulAdd(dir, distance);
                    // fake target un paso adelante en dir
                    Vector2 fakeTarget = new Vector2(start).mulAdd(dir, 1f);

                    bullets.add(new Bullet(bulletTexture, start, fakeTarget));
                }
                fireCooldown = fireRate;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 center = player.getPosition();

        // ángulo de mirada
        Vector2 lastDir = player.getLastDirection().nor();
        float lookAngle = MathUtils.atan2(lastDir.y, lastDir.x) * MathUtils.radiansToDegrees;

        // dibujar cada arma girada según lookAngle + offset
        for (float offset : baseAngles) {
            float actualAngle = lookAngle + offset;
            float rad = actualAngle * MathUtils.degreesToRadians;

            float x = center.x + MathUtils.cos(rad) * distance;
            float y = center.y + MathUtils.sin(rad) * distance;

            batch.draw(
                weaponTexture,
                x - weaponTexture.getWidth()/2f,
                y - weaponTexture.getHeight()/2f,
                weaponTexture.getWidth()/2f,
                weaponTexture.getHeight()/2f,
                weaponTexture.getWidth(),
                weaponTexture.getHeight(),
                1f, 1f,
                actualAngle,
                0, 0,
                weaponTexture.getWidth(),
                weaponTexture.getHeight(),
                false, false
            );
        }

        // render de las balas
        for (Bullet b : bullets) {
            b.render(batch);
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

    public List<Bullet> getBullets() {
        return bullets;
    }

    public int getDamage() {
        return damage;
    }
}
