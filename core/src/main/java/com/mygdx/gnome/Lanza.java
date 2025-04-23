// core/src/main/java/com/mygdx/gnome/Lanza.java
package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Lanza implements EquipableItem {
    private final Player player;
    private final Texture texture;
    private final float rotationSpeed = 180f;
    private final float distance = 50f;
    private final int damage = 20;

    private float angle = 0f;
    private int level = 1;
    private final List<Float> baseAngles = new ArrayList<>();

    public Lanza(Player player) {
        this.player  = player;
        this.texture = player.getGameScreen()
            .game.assetManager
            .get("GNOME/spear.png", Texture.class);
        updateBaseAngles();
    }

    public void upgrade() {
        if (level < 4) {
            level++;
            updateBaseAngles();
        }
    }

    private void updateBaseAngles() {
        baseAngles.clear();
        switch(level) {
            case 1: baseAngles.add(   0f); break;
            case 2: baseAngles.addAll(List.of(  0f, 180f)); break;
            case 3: baseAngles.addAll(List.of( 90f, 210f, 330f)); break;
            case 4: baseAngles.addAll(List.of( 45f, 135f, 225f, 315f)); break;
        }
    }

    @Override
    public void update(float delta) {
        angle = (angle + rotationSpeed * delta) % 360f;
    }

    @Override
    public void render(SpriteBatch batch) {
        Vector2 center = player.getPosition();
        float halfW = texture.getWidth() * 0.5f;
        float halfH = texture.getHeight() * 0.5f;

        for (float base : baseAngles) {
            float a   = base + angle;
            float rad = (float)Math.toRadians(a);

            // Punto sobre el que pivotar (centro del sprite)
            float px = center.x + (float)Math.cos(rad) * distance;
            float py = center.y + (float)Math.sin(rad) * distance;

            batch.draw(
                texture,
                px - halfW,               // x bottom-left
                py - halfH,               // y bottom-left
                halfW,                    // originX: centro del sprite
                halfH,                    // originY
                texture.getWidth(),       // width
                texture.getHeight(),      // height
                1f, 1f,                   // scaleX, scaleY
                a,                        // rotation
                0, 0,                     // srcX, srcY
                texture.getWidth(),       // srcWidth
                texture.getHeight(),      // srcHeight
                false, false              // flipX, flipY
            );
        }
    }


    public List<Vector2> getHitPoints() {
        List<Vector2> pts = new ArrayList<>();
        Vector2 c = player.getPosition();
        for (float base : baseAngles) {
            float a   = base + angle;
            float rad = (float)Math.toRadians(a);
            pts.add(new Vector2(
                c.x + (float)Math.cos(rad) * distance,
                c.y + (float)Math.sin(rad) * distance
            ));
        }
        return pts;
    }

    public int getDamage() {
        return damage;
    }
}
