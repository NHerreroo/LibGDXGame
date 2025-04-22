package com.mygdx.gnome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashMap;

public class Tienda {
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private float virtualWidth;
    private float virtualHeight;

    public boolean activa = false;

    private Item[] items = new Item[2];
    private boolean[] itemsComprados = new boolean[2];
    private BitmapFont itemFont;
    private float botonWidth = 300f;
    private float botonHeight = 100f;
    private int rerollCost = 5; // Coste inicial de 5
    private int rerollIncrement = 3; // Incremento de 3 por reroll

    private Player player;

    private HashMap purchaseCounts = new HashMap();

    public Tienda(float width, float height, Player player) {
        this.virtualWidth = width;
        this.virtualHeight = height;
        this.player = player;

        camera = new OrthographicCamera();
        viewport = new FillViewport(width, height, camera);
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();

        itemFont = new BitmapFont();
        itemFont.getData().setScale(2f);
        font = new BitmapFont();
        font.getData().setScale(4f);

        shapeRenderer = new ShapeRenderer();

        generarItems();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void show() {
        activa = true;
        rerollCost = 5; // Resetear coste al mostrar tienda
    }

    public void render(SpriteBatch batch) {
        if (!activa) return;

        // Detección de input
        if (Gdx.input.justTouched()) {
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touch);

            float rerollX = virtualWidth / 2f - botonWidth / 2f;
            float rerollY = virtualHeight - 500f;

            if (touch.x >= rerollX && touch.x <= rerollX + botonWidth &&
                touch.y >= rerollY && touch.y <= rerollY + botonHeight) {
                if (player.getDinero() >= rerollCost) {
                    player.restarDinero(rerollCost);
                    rerollCost += rerollIncrement; // Aumentar coste para próximo reroll
                    generarItems();
                }
            }

            for (int i = 0; i < items.length; i++) {
                float x = virtualWidth / 2f - botonWidth / 2f;
                float y = virtualHeight - 200f - i * (botonHeight + 40f);

                if (touch.x >= x && touch.x <= x + botonWidth &&
                    touch.y >= y && touch.y <= y + botonHeight && !itemsComprados[i]) {
                    comprarItem(i);
                }
            }

            float siguienteX = virtualWidth / 2f - botonWidth / 2f;
            float siguienteY = 100f;

            if (touch.x >= siguienteX && touch.x <= siguienteX + botonWidth &&
                touch.y >= siguienteY && touch.y <= siguienteY + botonHeight) {
                activa = false;
                if (player.getGameScreen() != null) {
                    player.getGameScreen().getHUD().resetTimeLeft();
                }
            }
        }

        // Fondo semitransparente
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(0, 0, virtualWidth, virtualHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Dibujar botones de ítems + reroll + siguiente
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar ítems
        for (int i = 0; i < items.length; i++) {
            float x = virtualWidth / 2f - botonWidth / 2f;
            float y = virtualHeight - 200f - i * (botonHeight + 40f);

            if (itemsComprados[i]) {
                shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
            } else if (player.getDinero() >= items[i].precio) {
                shapeRenderer.setColor(0.2f, 0.2f, 0.8f, 1f);
            } else {
                shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1f);
            }

            shapeRenderer.rect(x, y, botonWidth, botonHeight);
        }

        // Dibujar botón REROLL
        float rerollX = virtualWidth / 2f - botonWidth / 2f;
        float rerollY = virtualHeight - 500f;
        if (player.getDinero() >= rerollCost) {
            shapeRenderer.setColor(0.8f, 0.3f, 0.3f, 1f);
        } else {
            shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        }
        shapeRenderer.rect(rerollX, rerollY, botonWidth, botonHeight);

        // Dibujar botón SIGUIENTE
        float siguienteX = virtualWidth / 2f - botonWidth / 2f;
        float siguienteY = 100f;
        shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1f);
        shapeRenderer.rect(siguienteX, siguienteY, botonWidth, botonHeight);

        shapeRenderer.end();

        // Texto
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.setColor(Color.WHITE);
        font.draw(batch, "Tienda", virtualWidth / 2f - 100, virtualHeight - 80);

        // Texto de ítems
        for (int i = 0; i < items.length; i++) {
            float x = virtualWidth / 2f - botonWidth / 2f;
            float y = virtualHeight - 200f - i * (botonHeight + 40f);

            if (itemsComprados[i]) {
                itemFont.setColor(Color.DARK_GRAY);
                itemFont.draw(batch, "COMPRADO", x + 90, y + 60);
            } else {
                itemFont.setColor(Color.WHITE);
                itemFont.draw(batch, items[i].nombre + " - $" + items[i].precio, x + 20, y + 60);
            }
        }

        // Texto de botones
        itemFont.setColor(Color.WHITE);
        itemFont.draw(batch, "REROLL ($" + rerollCost + ")", rerollX + 60, rerollY + 60);
        itemFont.draw(batch, "SIGUIENTE", siguienteX + 60, siguienteY + 60);

        batch.end();
    }

    private void generarItems() {
        String[] nombres = {"LANZA", "HALO", "ROBOT", "AK47"};
        for (int i = 0; i < items.length; i++) {
            String nombre = nombres[(int) (Math.random() * nombres.length)];
            int precio = 0;
            //int precio = 100 + (int)(Math.random() * 200);

            items[i] = new Item(nombre, precio);
            itemsComprados[i] = false;
        }
    }

    private void comprarItem(int index) {

        String key = items[index].nombre;
        int count = (int) purchaseCounts.getOrDefault(key, 0);

        if (key.equals("LANZA") && count >= 4) return;
        if (key.equals("AK47")  && count >= 6) return;

        player.restarDinero(items[index].precio);
        purchaseCounts.put(key, count + 1);

        if (player.getDinero() < items[index].precio) {
            System.out.println("No tienes suficiente dinero.");
            return;
        }

        player.restarDinero(items[index].precio);


        switch (items[index].nombre) {
            case "LANZA":
                player.agregarHabilidadPermanente(new Lanza(player));
                break;
            case "HALO":
                player.agregarHabilidadPermanente(new Halo(player));
                System.out.println("Halo comprado y añadido!"); // Debug
                break;
            case "ROBOT": //aparece en random
                float dist = 60f;              // distancia al jugador
                float angle = (float)(Math.random() * Math.PI * 2);
                Vector2 randOffset = new Vector2(
                    (float)Math.cos(angle) * dist,
                    (float)Math.sin(angle) * dist
                );
                player.agregarHabilidadPermanente(new Robot(player, randOffset));
                break;
            case "AK47":
                player.agregarHabilidadPermanente(new AK47(player));
                break;
        }
    }

    private static class Item {
        String nombre;
        int precio;

        public Item(String nombre, int precio) {
            this.nombre = nombre;
            this.precio = precio;
        }
    }
}
