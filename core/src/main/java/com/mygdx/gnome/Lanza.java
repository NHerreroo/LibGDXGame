// core/src/main/java/com/mygdx/gnome/Lanza.java
package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Lanza implements EquipableItem {
    private Player player;
    private float angle = 0;
    private float rotationSpeed = 180f;
    private float distance = 50f;
    private int damage = 20;
    private float damageInterval = 0.5f;
    private float damageTimer = 0;

    private int level = 1;
    private List<Float> baseAngles = new ArrayList<>();

    private Texture spearTexture;

    public Lanza(Player player) {
        this.player = player;
        this.spearTexture = player.getGameScreen().game.assetManager.get("GNOME/spear.png", Texture.class);
        updateBaseAngles();
    }

    /** Sube de nivel hasta 4 */
    public void upgrade() {
        if (level < 4) {
            level++;
            updateBaseAngles();
        }
    }

    private void updateBaseAngles() {
        baseAngles.clear();
        switch(level) {
            case 1: baseAngles.add(0f); break;
            case 2:
                baseAngles.add(0f);
                baseAngles.add(180f);
                break;
            case 3:
                baseAngles.add(90f);
                baseAngles.add(210f);
                baseAngles.add(330f);
                break;
            case 4:
                baseAngles.add(45f);
                baseAngles.add(135f);
                baseAngles.add(225f);
                baseAngles.add(315f);
                break;
        }
    }

    @Override
    public void update(float delta) {
        angle += rotationSpeed * delta;
        damageTimer += delta;
        if (damageTimer >= damageInterval) {
            damageTimer = 0;
            applyDamage();
        }
    }

    private void applyDamage() {
        Vector2 center = player.getPosition();
        for (float b : baseAngles) {
            float ang = b + angle;
            Vector2 pos = new Vector2(
                center.x + (float)Math.cos(Math.toRadians(ang)) * distance,
                center.y + (float)Math.sin(Math.toRadians(ang)) * distance
            );
            for (Snail s: player.getGameScreen().getSpawner().getSnails()) {
                if (pos.dst(s.getPosition()) < 20f) {
                    s.recibirDaño(damage);
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 center = player.getPosition();
        for (float b : baseAngles) {
            float ang = b + angle;
            float rad = (float)Math.toRadians(ang);
            float x = center.x + (float)Math.cos(rad) * distance;
            float y = center.y + (float)Math.sin(rad) * distance;
            float originX = spearTexture.getWidth();
            float originY = spearTexture.getHeight()/2f;
            batch.draw(spearTexture,
                x - originX, y - originY,
                originX, originY,
                spearTexture.getWidth(), spearTexture.getHeight(),
                1,1, ang,
                0,0,
                spearTexture.getWidth(), spearTexture.getHeight(),
                false,false);
        }
    }
}
