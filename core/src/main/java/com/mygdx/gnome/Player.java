package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private Texture texture;
    private Vector2 position;
    private float speed = 50f;
    private int vidas = 3;
    private int ataque = 10;
    private float cadencia = 1.8f;
    private int dinero = 300;
    private GameScreen gameScreen;
    private List<EquipableItem> habilidadesPermanentes = new ArrayList<>();

    public Player(Texture texture, float x, float y, GameScreen gameScreen) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.gameScreen = gameScreen;
    }

    public void update(float delta, Vector2 direction) {
        if (direction.len() > 0.1f) {
            position.x += direction.x * speed * delta;
            position.y += direction.y * speed * delta;
        }
        actualizarHabilidades(delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - texture.getWidth()/2f, position.y - texture.getHeight()/2f);
    }

    public void actualizarHabilidades(float delta) {
        for (EquipableItem habilidad : habilidadesPermanentes) {
            habilidad.update(delta);
        }
    }

    public void renderizarHabilidades(SpriteBatch batch) {
        for (EquipableItem habilidad : habilidadesPermanentes) {
            habilidad.render(batch);
        }
    }

    public void agregarHabilidadPermanente(EquipableItem habilidad) {
        habilidadesPermanentes.add(habilidad);
    }

    public List<Bullet> getBullets() {
        List<Bullet> allBullets = new ArrayList<>();
        for (EquipableItem item : habilidadesPermanentes) {
            if (item instanceof AK47) {
                allBullets.addAll(((AK47)item).getBullets());
            }
            // Puedes añadir más items que disparen aquí
        }
        return allBullets;
    }

    // Resto de métodos getter y setter...
    public Vector2 getPosition() { return position; }
    public int getVidas() { return vidas; }
    public float getVelocidad() { return speed; }
    public int getAtaque() { return ataque; }
    public float getCadencia() { return cadencia; }
    public int getDinero() { return dinero; }
    public void restarDinero(int cantidad) { dinero -= cantidad; }
    public void sumarDinero(int cantidad) { dinero += cantidad; }
    public GameScreen getGameScreen() { return gameScreen; }
    public Bullet shootAt(Vector2 target, Texture bulletTexture) {
        return new Bullet(bulletTexture, position, target);
    }
}
