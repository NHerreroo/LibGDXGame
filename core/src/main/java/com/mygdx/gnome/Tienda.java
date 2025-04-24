package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

public class Tienda {
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;
    private BitmapFont itemFont;
    private ShapeRenderer shapeRenderer;

    private float virtualWidth;
    private float virtualHeight;

    public boolean activa = false;

    private static final int SLOT_COUNT = 2;
    private Item[] items = new Item[SLOT_COUNT];
    private boolean[] purchased = new boolean[SLOT_COUNT];

    private float botonWidth  = 300f;
    private float botonHeight = 100f;

    private int rerollCost      = 5;
    private int rerollIncrement = 3;

    private Player player;

    // Cuenta cuántas veces se ha comprado cada tipo de ítem
    private Map<String,Integer> purchaseCount = new HashMap<>();

    public Tienda(float width, float height, Player player, BitmapFont gameFont) {
        this.virtualWidth  = width;
        this.virtualHeight = height;
        this.player        = player;

        camera = new OrthographicCamera();
        viewport = new FillViewport(width, height, camera);
        camera.position.set(width/2f, height/2f, 0);
        camera.update();

        this.font = gameFont;
        this.itemFont = gameFont;
        font.getData().setScale(4f);
        itemFont.getData().setScale(2f);

        shapeRenderer = new ShapeRenderer();

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
        String[] nombres = {
            "LANZA","HALO","ROBOT","AK47",
            "VELOCIDAD","DAÑO","CADENCIA","VIDA"
        };
        for (int i = 0; i < SLOT_COUNT; i++) {
            String nombre = nombres[(int)(Math.random() * nombres.length)];
            int precio  = calcularPrecio(nombre);
            items[i]    = new Item(nombre, precio);
            purchased[i]= false;
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

        // === INPUT ===
        if (Gdx.input.justTouched()) {
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touch);

            // Reroll
            float rx = virtualWidth/2f - botonWidth/2f;
            float ry = virtualHeight - 500f;
            if (touch.x >= rx && touch.x <= rx + botonWidth
                && touch.y >= ry && touch.y <= ry + botonHeight
                && player.getDinero() >= rerollCost) {
                player.restarDinero(rerollCost);
                rerollCost += rerollIncrement;
                generarItems();
            }

            // Comprar slots
            for (int i = 0; i < SLOT_COUNT; i++) {
                if (purchased[i]) continue;
                float x = virtualWidth/2f - botonWidth/2f;
                float y = virtualHeight - 200f - i * (botonHeight + 40f);
                if (touch.x >= x && touch.x <= x + botonWidth
                    && touch.y >= y && touch.y <= y + botonHeight
                    && player.getDinero() >= items[i].precio) {

                    // Descontar dinero
                    player.restarDinero(items[i].precio);

                    // Registrar compra para subir precio la próxima vez
                    purchaseCount.put(
                        items[i].nombre,
                        purchaseCount.getOrDefault(items[i].nombre, 0) + 1
                    );

                    // Aplicar la compra según el tipo
                    switch (items[i].nombre) {
                        case "LANZA":
                            Lanza lanza = player.getHabilidadesPermanentes().stream()
                                .filter(h -> h instanceof Lanza)
                                .map(h -> (Lanza) h).findFirst().orElse(null);
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
                                (float)Math.sin(ang) * d
                            );
                            player.agregarHabilidadPermanente(new Robot(player, off));
                            break;
                        case "AK47":
                            AK47 ak = player.getHabilidadesPermanentes().stream()
                                .filter(h -> h instanceof AK47)
                                .map(h -> (AK47) h).findFirst().orElse(null);
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

                    // Marcamos el slot como comprado
                    purchased[i] = true;
                }
            }

            // Cerrar tienda
            float sx = virtualWidth/2f - botonWidth/2f;
            float sy = 100f;
            if (touch.x >= sx && touch.x <= sx + botonWidth
                && touch.y >= sy && touch.y <= sy + botonHeight) {
                activa = false;
                player.getGameScreen().getHUD().resetTimeLeft();
                player.getGameScreen().nextRound();
            }
        }

        // === FONDO OSCURO ===
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0,0,0,0.5f);
        shapeRenderer.rect(0,0, virtualWidth, virtualHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // === BOTONES ===
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < SLOT_COUNT; i++) {
            float x = virtualWidth/2f - botonWidth/2f;
            float y = virtualHeight - 200f - i * (botonHeight + 40f);
            if (purchased[i]) {
                shapeRenderer.setColor(0.3f,0.3f,0.3f,1f);
            } else if (player.getDinero() >= items[i].precio) {
                shapeRenderer.setColor(0.2f,0.2f,0.8f,1f);
            } else {
                shapeRenderer.setColor(0.8f,0.2f,0.2f,1f);
            }
            shapeRenderer.rect(x,y,botonWidth,botonHeight);
        }
        // Reroll
        float rx2 = virtualWidth/2f - botonWidth/2f;
        float ry2 = virtualHeight - 500f;
        shapeRenderer.setColor(
            player.getDinero() >= rerollCost ? 0.8f : 0.3f,
            0.3f,0.3f,1f
        );
        shapeRenderer.rect(rx2, ry2, botonWidth, botonHeight);
        // Siguiente
        float sx2 = virtualWidth/2f - botonWidth/2f;
        float sy2 = 100f;
        shapeRenderer.setColor(0.2f,0.8f,0.2f,1f);
        shapeRenderer.rect(sx2, sy2, botonWidth, botonHeight);
        shapeRenderer.end();

        // === TEXTOS ===
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Tienda", virtualWidth/2f - 80, virtualHeight - 80);

        for (int i = 0; i < SLOT_COUNT; i++) {
            float x = virtualWidth/2f - botonWidth/2f + 20;
            float y = virtualHeight - 200f - i * (botonHeight + 40f) + 60;
            if (purchased[i]) {
                itemFont.setColor(Color.GRAY);
                itemFont.draw(batch, "COMPRADO", x, y);
            } else {
                itemFont.setColor(Color.WHITE);
                itemFont.draw(batch,
                    items[i].nombre + " - $" + items[i].precio,
                    x, y);
            }
        }
        itemFont.setColor(Color.WHITE);
        itemFont.draw(batch, "REROLL ($" + rerollCost + ")", rx2 + 60, ry2 + 60);
        itemFont.draw(batch, "SIGUIENTE", sx2 + 60, sy2 + 60);
        batch.end();
    }

    private static class Item {
        String nombre;
        int precio;
        Item(String n, int p) { nombre = n; precio = p; }
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
