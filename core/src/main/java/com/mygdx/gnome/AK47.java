package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private List<Float> baseAngles = new ArrayList<>();
    private float distance = 50f;        // distancia de cada arma al jugador

    private Texture bulletTexture;
    private Texture weaponTexture;
    private List<Bullet> bullets = new ArrayList<>();

    public AK47(Player player) {
        this.player = player;
        this.bulletTexture = player.getGameScreen().game.assetManager.get("GNOME/bullet2.png", Texture.class);
        this.weaponTexture = player.getGameScreen().game.assetManager.get("GNOME/ak47.png", Texture.class);
        updateBaseAngles();
    }

    /** Sube nivel hasta 6 */
    public void upgrade() {
        if (level < 6) {
            level++;
            updateBaseAngles();
        }
    }

    private void updateBaseAngles() {
        baseAngles.clear();
        // repartir las armas uniformemente alrededor de 360°
        for (int i = 0; i < level; i++) {
            baseAngles.add(i * (360f / level));
        }
    }

    @Override
    public void update(float delta) {
        fireCooldown -= delta;
        // actualizar balas
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update(delta);
            if (b.getPosition().dst(player.getPosition()) > 500) it.remove();
        }
        // disparar desde cada arma
        if (fireCooldown <= 0) {
            Snail target = findClosestEnemy();
            if (target != null) {
                Vector2 center = player.getPosition();
                for (float ang : baseAngles) {
                    Vector2 start = new Vector2(
                        center.x + (float)Math.cos(Math.toRadians(ang)) * distance,
                        center.y + (float)Math.sin(Math.toRadians(ang)) * distance
                    );
                    bullets.add(new Bullet(bulletTexture, start, target.getPosition()));
                }
                fireCooldown = fireRate;
            }
        }
    }

    private Snail findClosestEnemy() {
        Snail best = null;
        float md = Float.MAX_VALUE;
        for (Snail s : player.getGameScreen().getSpawner().getSnails()) {
            float d = player.getPosition().dst2(s.getPosition());
            if (d < md) { md = d; best = s; }
        }
        return best;
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 center = player.getPosition();
        // dibujar cada arma alrededor del jugador
        for (float ang : baseAngles) {
            float rad = (float)Math.toRadians(ang);
            float x = center.x + (float)Math.cos(rad) * distance;
            float y = center.y + (float)Math.sin(rad) * distance;
            // rotar la textura para apuntar hacia afuera
            batch.draw(
                weaponTexture,
                x - weaponTexture.getWidth()/2f,
                y - weaponTexture.getHeight()/2f,
                weaponTexture.getWidth()/2f,
                weaponTexture.getHeight()/2f,
                weaponTexture.getWidth(),
                weaponTexture.getHeight(),
                1,1,
                ang,
                0,0,
                weaponTexture.getWidth(),
                weaponTexture.getHeight(),
                false,false
            );
        }
        // render de las balas
        for (Bullet b : bullets) {
            b.render(batch);
        }
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public int getDamage() {
        return damage;
    }
}
