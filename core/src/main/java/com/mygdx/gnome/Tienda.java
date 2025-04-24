package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.HashMap;
import java.util.Map;

public class Tienda {
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;
    private BitmapFont itemFont;
    private ShapeRenderer shapeRenderer;
    private Texture shopTexture;
    private Texture rerollTexture;
    private Texture siguienteTexture;
    private Texture compradoTexture;
    private float virtualWidth;
    private float virtualHeight;
    public boolean activa = false;
    private static final int SLOT_COUNT = 2;
    private Item[] items = new Item[SLOT_COUNT];
    private boolean[] purchased = new boolean[SLOT_COUNT];
    private float botonWidth = 300f;
    private float botonHeight = 100f;
    private int rerollCost = 5;
    private int rerollIncrement = 3;
    private Player player;
    private Map<String, Integer> purchaseCount = new HashMap<>();

    public Tienda(float width, float height, Player player, BitmapFont itemfontshop, BitmapFont titlefont) {
        this.virtualWidth = width;
        this.virtualHeight = height;
        this.player = player;
        camera = new OrthographicCamera();
        viewport = new FillViewport(width, height, camera);
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();
        font = titlefont;
        itemFont = itemfontshop;
        shapeRenderer = new ShapeRenderer();
        shopTexture = player.getGameScreen().game.assetManager.get("GNOME/shop.png", Texture.class);
        rerollTexture = player.getGameScreen().game.assetManager.get("GNOME/reroll.png", Texture.class);
        siguienteTexture = player.getGameScreen().game.assetManager.get("GNOME/siguiente.png", Texture.class);
        compradoTexture = player.getGameScreen().game.assetManager.get("GNOME/comprado.png", Texture.class);
        generarItems();
    }

    public void show() {
        activa = true;
        rerollCost = 5;
        for (int i = 0; i < SLOT_COUNT; i++) {
            purchased[i] = false;
        }
    }

    private void generarItems() {
        String[] nombres = {"LANZA","HALO","ROBOT","AK47","VELOCIDAD","DAÑO","CADENCIA","VIDA"};
        for (int i = 0; i < SLOT_COUNT; i++) {
            String nombre = nombres[(int)(Math.random() * nombres.length)];
            int precio = calcularPrecio(nombre);
            items[i] = new Item(nombre, precio);
            purchased[i] = false;
        }
    }

    private int calcularPrecio(String nombre) {
        int base;
        switch (nombre) {
            case "LANZA":     base = 10; break;
            case "HALO":      base = 10; break;
            case "ROBOT":     base = 20; break;
            case "AK47":      base = 20; break;
            case "VELOCIDAD": base = 10; break;
            case "DAÑO":      base = 10; break;
            case "CADENCIA":  base = 10; break;
            case "VIDA":      base = 10; break;
            default:          base = 20; break;
        }
        int times = purchaseCount.getOrDefault(nombre, 0);
        return Math.round(base * (1f + 0.5f * times));
    }

    public void render(SpriteBatch batch) {
        if (!activa) return;
        if (Gdx.input.justTouched()) {
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touch);
            float rx = virtualWidth / 2f - botonWidth / 2f;
            float ry = virtualHeight - 500f;
            if (touch.x >= rx && touch.x <= rx + botonWidth
                && touch.y >= ry && touch.y <= ry + botonHeight
                && player.getDinero() >= rerollCost) {
                player.restarDinero(rerollCost);
                rerollCost += rerollIncrement;
                generarItems();
            }
            for (int i = 0; i < SLOT_COUNT; i++) {
                if (purchased[i]) continue;
                float x = virtualWidth / 2f - botonWidth / 2f;
                float y = virtualHeight - 200f - i * (botonHeight + 40f);
                if (touch.x >= x && touch.x <= x + botonWidth
                    && touch.y >= y && touch.y <= y + botonHeight
                    && player.getDinero() >= items[i].precio) {
                    player.restarDinero(items[i].precio);
                    purchaseCount.put(items[i].nombre,
                        purchaseCount.getOrDefault(items[i].nombre, 0) + 1);
                    switch (items[i].nombre) {
                        case "LANZA":
                            Lanza lanza = player.getGameScreen()
                                .getHabilidadesPermanentes().stream()
                                .filter(h -> h instanceof Lanza)
                                .map(h -> (Lanza) h)
                                .findFirst().orElse(null);
                            if (lanza != null) lanza.upgrade();
                            else player.agregarHabilidadPermanente(new Lanza(player));
                            break;
                        case "HALO":
                            player.agregarHabilidadPermanente(new Halo(player));
                            break;
                        case "ROBOT":
                            float d = 60f;
                            float ang = (float)(Math.random() * Math.PI * 2);
                            Vector2 off = new Vector2(
                                (float)Math.cos(ang) * d,
                                (float)Math.sin(ang) * d);
                            player.agregarHabilidadPermanente(new Robot(player, off));
                            break;
                        case "AK47":
                            AK47 ak = player.getGameScreen()
                                .getHabilidadesPermanentes().stream()
                                .filter(h -> h instanceof AK47)
                                .map(h -> (AK47) h)
                                .findFirst().orElse(null);
                            if (ak != null) ak.upgrade();
                            else player.agregarHabilidadPermanente(new AK47(player));
                            break;
                        case "VELOCIDAD":
                            player.incrementarVelocidad(5f);
                            break;
                        case "DAÑO":
                            player.incrementarAtaque(5);
                            break;
                        case "CADENCIA":
                            player.mejorarCadencia(0.1f);
                            break;
                        case "VIDA":
                            player.incrementarVidas(1);
                            break;
                    }
                    purchased[i] = true;
                }
            }
            float sx = virtualWidth / 2f - botonWidth / 2f;
            float sy = 100f;
            if (touch.x >= sx && touch.x <= sx + botonWidth
                && touch.y >= sy && touch.y <= sy + botonHeight) {
                activa = false;
                player.getGameScreen().getHUD().resetTimeLeft();
                player.getGameScreen().nextRound();
            }
        }
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(0, 0, virtualWidth, virtualHeight);
        shapeRenderer.end();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Tienda", virtualWidth / 2f - 80, virtualHeight - 80);
        for (int i = 0; i < SLOT_COUNT; i++) {
            float x = virtualWidth / 2f - botonWidth / 2f;
            float y = virtualHeight - 200f - i * (botonHeight + 40f);
            if (purchased[i]) batch.draw(compradoTexture, x, y, botonWidth, botonHeight);
            else batch.draw(shopTexture, x, y, botonWidth, botonHeight);
            float tx = x + 20;
            float ty = y + 60;
            itemFont.setColor(purchased[i] ? Color.GRAY : Color.WHITE);
            itemFont.draw(batch,
                purchased[i] ? "COMPRADO"
                    : items[i].nombre + " - $" + items[i].precio,
                tx, ty);
        }
        float rx2 = virtualWidth / 2f - botonWidth / 2f;
        float ry2 = virtualHeight - 500f;
        batch.draw(rerollTexture, rx2, ry2, botonWidth, botonHeight);
        itemFont.setColor(Color.WHITE);
        itemFont.draw(batch,
            "REROLL ($" + rerollCost + ")",
            rx2 + 60, ry2 + 60);
        float sx2 = virtualWidth / 2f - botonWidth / 2f;
        float sy2 = 100f;
        batch.draw(siguienteTexture, sx2, sy2, botonWidth, botonHeight);
        itemFont.draw(batch, "SIGUIENTE", sx2 + 60, sy2 + 60);
        batch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private static class Item {
        String nombre;
        int precio;
        Item(String nombre, int precio) {
            this.nombre = nombre;
            this.precio = precio;
        }
    }
}
