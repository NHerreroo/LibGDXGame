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
    private float fireRate = 0.1f;
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
                bullets.add(new Bullet(bulletTexture, player.getPosition(), closest.getPosition()));
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
        // Dibujar el arma en el jugador (rotada hacia la dirección del movimiento)
        float rotation = player.getLastDirection().angleDeg();
        batch.draw(weaponTexture,
            player.getPosition().x - weaponTexture.getWidth()/2f + 20,
            player.getPosition().y - weaponTexture.getHeight()/2f - 10,
            weaponTexture.getWidth()/2f, weaponTexture.getHeight()/2f,
            weaponTexture.getWidth(), weaponTexture.getHeight(),
            1, 1, rotation, 0, 0,
            weaponTexture.getWidth(), weaponTexture.getHeight(), false, false);
    }


    public List<Bullet> getBullets() {
        return bullets;
    }
}
