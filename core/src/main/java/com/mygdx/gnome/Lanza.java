package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

// Lanza.java (ejemplo de implementación permanente)
public class Lanza implements EquipableItem {
    private Player player;
    private float angulo;
    private float velocidadRotacion = 180f;
    private float distancia = 50f;
    private float daño = 10f;
    private float intervaloDaño = 0.5f;
    private float tiempo;

    public Lanza(Player player) {
        this.player = player;
    }

    @Override
    public void update(float delta) {
        angulo += velocidadRotacion * delta;
        tiempo += delta;

        if (tiempo >= intervaloDaño) {
            tiempo = 0;
            aplicarDaño();
        }
    }

    private void aplicarDaño() {
        if (player.getGameScreen() == null) return;

        Vector2 posicionLanza = new Vector2(
            player.getPosition().x + (float)Math.cos(Math.toRadians(angulo)) * distancia,
            player.getPosition().y + (float)Math.sin(Math.toRadians(angulo)) * distancia
        );

        for (Snail caracol : player.getGameScreen().getSpawner().getSnails()) {
            if (posicionLanza.dst(caracol.getPosition()) < 20f) {
                caracol.recibirDaño((int) daño);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.GOLD);

        float x = player.getPosition().x + (float)Math.cos(Math.toRadians(angulo)) * distancia;
        float y = player.getPosition().y + (float)Math.sin(Math.toRadians(angulo)) * distancia;

        sr.rect(x - 5, y - 5, 10, 30);
        sr.end();
        sr.dispose();
    }
}
