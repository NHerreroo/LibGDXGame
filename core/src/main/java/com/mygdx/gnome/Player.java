package com.mygdx.gnome;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private Vector2 position;
    private float speed = 50f;
    private int vidas = 3;
    private int ataque = 10;
    private float cadencia = 1.8f;
    private int dinero = 300;
    private GameScreen gameScreen;
    private List<EquipableItem> habilidadesPermanentes = new ArrayList<>();

    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> walkAnim;
    private float stateTime = 0f;
    private boolean moving = false;

    // Dirección de mirada (x positivo = derecha, x negativo = izquierda)
    private Vector2 lastDirection = new Vector2(1, 0);

    public Player(Texture texture, float x, float y, GameScreen gameScreen) {
        this.position = new Vector2(x, y);
        this.gameScreen = gameScreen;

        // Idle frames: 1, 2, 3
        Array<TextureRegion> idleFrames = new Array<>();
        idleFrames.add(new TextureRegion(
            gameScreen.game.assetManager.get("GNOME/Player/1.png", Texture.class)));
        idleFrames.add(new TextureRegion(
            gameScreen.game.assetManager.get("GNOME/Player/2.png", Texture.class)));
        idleFrames.add(new TextureRegion(
            gameScreen.game.assetManager.get("GNOME/Player/3.png", Texture.class)));
        idleAnim = new Animation<>(0.3f, idleFrames, Animation.PlayMode.LOOP);

        // Walk frames: w1, w2, w3
        Array<TextureRegion> walkFrames = new Array<>();
        walkFrames.add(new TextureRegion(
            gameScreen.game.assetManager.get("GNOME/Player/w1.png", Texture.class)));
        walkFrames.add(new TextureRegion(
            gameScreen.game.assetManager.get("GNOME/Player/w2.png", Texture.class)));
        walkFrames.add(new TextureRegion(
            gameScreen.game.assetManager.get("GNOME/Player/w3.png", Texture.class)));
        walkAnim = new Animation<>(0.2f, walkFrames, Animation.PlayMode.LOOP);
    }

    public void update(float delta, Vector2 direction) {
        moving = direction.len() > 0.1f;

        if (moving) {
            direction.nor();
            position.mulAdd(direction, speed * delta);
            lastDirection.set(direction);
        }

        stateTime += delta;
        actualizarHabilidades(delta);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getLastDirection() {
        return lastDirection;
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = moving
            ? walkAnim.getKeyFrame(stateTime)
            : idleAnim.getKeyFrame(stateTime);

        boolean shouldFlip = lastDirection.x > 0;

        if (frame.isFlipX() != shouldFlip) {
            frame.flip(true, false);
        }

        float w = frame.getRegionWidth();
        float h = frame.getRegionHeight();
        batch.draw(frame, position.x - w/2f, position.y - h/2f);
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
        List<Bullet> all = new ArrayList<>();
        for (EquipableItem item : habilidadesPermanentes) {
            if (item instanceof AK47) {
                all.addAll(((AK47)item).getBullets());
            }
        }
        return all;
    }

    public Bullet shootAt(Vector2 target, Texture bulletTexture) {
        return new Bullet(bulletTexture, position, target);
    }

    // Getters y setters de estadísticas
    public int getVidas()      { return vidas; }
    public float getVelocidad(){ return speed; }
    public int getAtaque()     { return ataque; }
    public float getCadencia() { return cadencia; }
    public int getDinero()     { return dinero; }

    public void restarDinero(int cantidad) { dinero -= cantidad; }
    public void sumarDinero(int cantidad)  { dinero += cantidad; }

    // Métodos de mejora de stats
    public void incrementarVelocidad(float v) { speed += v; }
    public void incrementarAtaque(int i)      { ataque += i; }
    public void mejorarCadencia(float v)      { cadencia = Math.max(0.1f, cadencia - v); }
    public void incrementarVidas(int i)       { vidas += i; }

    public GameScreen getGameScreen() { return gameScreen; }

    public List<EquipableItem> getHabilidadesPermanentes() {
        return habilidadesPermanentes;
    }

    public void restarVida(int cantidad) {
        vidas -= cantidad;
        if (vidas < 0) vidas = 0;
    }

}
