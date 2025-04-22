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
    private Texture bulletTexture;
    private Texture weaponTexture;
    private List<Bullet> bullets = new ArrayList<>();

    public AK47(Player player) {
        this.player = player;
        this.bulletTexture = player.getGameScreen().game.assetManager.get("GNOME/bullet2.png", Texture.class);
        this.weaponTexture = player.getGameScreen().game.assetManager.get("GNOME/ak47.png", Texture.class);
    }

    @Override
    public void update(float delta) {
        fireCooldown -= delta;

        // Actualizar balas
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.update(delta);

            if (bullet.getPosition().dst(player.getPosition()) > 500) {
                it.remove();
            }
        }

        // Disparar automáticamente
        if (fireCooldown <= 0 && player.getGameScreen() != null) {
            Snail closest = findClosestEnemy();
            if (closest != null) {
                bullets.add(new Bullet(bulletTexture, getBarrelEndPosition(), closest.getPosition()));
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

    private Vector2 getBarrelEndPosition() {
        Vector2 offset = new Vector2(player.getLastDirection()).nor().scl(weaponTexture.getWidth());
        return new Vector2(player.getPosition()).add(offset);
    }


    @Override
    public void render(SpriteBatch batch) {
        float rotation = player.getLastDirection().angleDeg();

        boolean mirandoIzquierda = player.getLastDirection().x < 0;

        float originX = 0f;
        float originY = weaponTexture.getHeight() / 2f;

        float drawX = player.getPosition().x;
        float drawY = player.getPosition().y - weaponTexture.getHeight() / 2f;

        batch.draw(weaponTexture,
            drawX, drawY,
            originX, originY,
            weaponTexture.getWidth(), weaponTexture.getHeight(),
            1, 1, rotation,
            0, 0,
            weaponTexture.getWidth(), weaponTexture.getHeight(),
            false, mirandoIzquierda);

    }




    public List<Bullet> getBullets() {
        return bullets;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public float getFireCooldown() {
        return fireCooldown;
    }

    public void setFireCooldown(float fireCooldown) {
        this.fireCooldown = fireCooldown;
    }

    public float getFireRate() {
        return fireRate;
    }

    public void setFireRate(float fireRate) {
        this.fireRate = fireRate;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Texture getBulletTexture() {
        return bulletTexture;
    }

    public void setBulletTexture(Texture bulletTexture) {
        this.bulletTexture = bulletTexture;
    }

    public Texture getWeaponTexture() {
        return weaponTexture;
    }

    public void setWeaponTexture(Texture weaponTexture) {
        this.weaponTexture = weaponTexture;
    }

    public void setBullets(List<Bullet> bullets) {
        this.bullets = bullets;
    }
}
