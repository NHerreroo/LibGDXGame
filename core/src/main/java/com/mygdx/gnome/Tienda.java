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

public class Tienda {
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private float virtualWidth;
    private float virtualHeight;

    public boolean activa = false;

    private Item[] items = new Item[2];
    private BitmapFont itemFont;
    private float botonWidth = 300f;
    private float botonHeight = 100f;

    private Player player;

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
                generarItems();
            }

            for (int i = 0; i < items.length; i++) {
                float x = virtualWidth / 2f - botonWidth / 2f;
                float y = virtualHeight - 200f - i * (botonHeight + 40f);

                if (touch.x >= x && touch.x <= x + botonWidth &&
                    touch.y >= y && touch.y <= y + botonHeight) {
                    comprarItem(items[i].nombre, items[i].precio);
                }
            }

            float siguienteX = virtualWidth / 2f - botonWidth / 2f;
            float siguienteY = 100f;

            if (touch.x >= siguienteX && touch.x <= siguienteX + botonWidth &&
                touch.y >= siguienteY && touch.y <= siguienteY + botonHeight) {
                activa = false;
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

        for (int i = 0; i < items.length; i++) {
            float x = virtualWidth / 2f - botonWidth / 2f;
            float y = virtualHeight - 200f - i * (botonHeight + 40f);
            shapeRenderer.setColor(0.2f, 0.2f, 0.8f, 1f);
            shapeRenderer.rect(x, y, botonWidth, botonHeight);
        }

        float rerollX = virtualWidth / 2f - botonWidth / 2f;
        float rerollY = virtualHeight - 500f;
        shapeRenderer.setColor(0.8f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(rerollX, rerollY, botonWidth, botonHeight);

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

        for (int i = 0; i < items.length; i++) {
            float x = virtualWidth / 2f - botonWidth / 2f;
            float y = virtualHeight - 200f - i * (botonHeight + 40f);
            itemFont.draw(batch, items[i].nombre + " - $" + items[i].precio, x + 20, y + 60);
        }

        itemFont.draw(batch, "REROLL", rerollX + 90, rerollY + 60);
        itemFont.draw(batch, "SIGUIENTE", siguienteX + 60, siguienteY + 60);

        batch.end();
    }


    private void generarItems() {
        String[] nombres = {"LANZA", "HALO", "ROBOT", "AK47"};
        for (int i = 0; i < items.length; i++) {
            String nombre = nombres[(int) (Math.random() * nombres.length)];
            int precio = 0; // puedes variar si querés por ítem
            items[i] = new Item(nombre, precio);
        }
    }


    private void comprarItem(String nombre, int precio) {
        if (player.getDinero() < precio) {
            System.out.println("No tienes suficiente dinero.");
            return;
        }

        player.restarDinero(precio);

        switch (nombre) {
            case "LANZA":
                player.addItem(new Lanza(player));
                break;
            case "HALO":
                player.addItem(new Halo(player));
                break;
            case "ROBOT":
                player.addItem(new Robot(player));
                break;
            case "AK47":
                player.addItem(new AK47(player));
                break;
            default:
                System.out.println("Ítem no reconocido: " + nombre);
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
