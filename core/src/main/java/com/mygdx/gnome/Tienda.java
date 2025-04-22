// core/src/main/java/com/mygdx/gnome/Tienda.java
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
    private ShapeRenderer shapeRenderer;

    private float virtualWidth;
    private float virtualHeight;

    public boolean activa = false;

    private Item[] items = new Item[2];
    private BitmapFont itemFont;
    private float botonWidth = 300f;
    private float botonHeight = 100f;
    private int rerollCost = 5;        // Coste inicial de 5
    private int rerollIncrement = 3;   // Incremento de 3 por reroll

    private Player player;
    private Map<String, Integer> purchaseCounts = new HashMap<>();

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
        rerollCost = 5;               // Resetear coste al mostrar tienda
    }

    public void render(SpriteBatch batch) {
        if (!activa) return;

        // INPUT
        if (Gdx.input.justTouched()) {
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touch);

            // Reroll
            float rx = virtualWidth/2f - botonWidth/2f;
            float ry = virtualHeight - 500f;
            if (touch.x>=rx && touch.x<=rx+botonWidth
                && touch.y>=ry && touch.y<=ry+botonHeight
                && player.getDinero()>=rerollCost) {

                player.restarDinero(rerollCost);
                rerollCost += rerollIncrement;
                generarItems();
            }

            // Comprar cada ítem
            for (int i = 0; i < items.length; i++) {
                float x = virtualWidth/2f - botonWidth/2f;
                float y = virtualHeight - 200f - i*(botonHeight+40f);
                if (touch.x>=x && touch.x<=x+botonWidth
                    && touch.y>=y && touch.y<=y+botonHeight) {
                    comprarItem(i);
                }
            }

            // Cerrar tienda
            float sx = virtualWidth/2f - botonWidth/2f;
            float sy = 100f;
            if (touch.x>=sx && touch.x<=sx+botonWidth
                && touch.y>=sy && touch.y<=sy+botonHeight) {
                activa = false;
                player.getGameScreen().getHUD().resetTimeLeft();
            }
        }

        // DIBUJO FONDO
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0,0,0,0.5f);
        shapeRenderer.rect(0,0,virtualWidth,virtualHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // DIBUJO BOTONES
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // ítems
        for (int i = 0; i < items.length; i++) {
            float x = virtualWidth/2f - botonWidth/2f;
            float y = virtualHeight - 200f - i*(botonHeight+40f);
            if (player.getDinero() >= items[i].precio) shapeRenderer.setColor(0.2f,0.2f,0.8f,1f);
            else                                   shapeRenderer.setColor(0.8f,0.2f,0.2f,1f);
            shapeRenderer.rect(x,y,botonWidth,botonHeight);
        }
        // reroll
        float rx = virtualWidth/2f - botonWidth/2f;
        float ry = virtualHeight - 500f;
        shapeRenderer.setColor(player.getDinero()>=rerollCost ? 0.8f : 0.3f, 0.3f,0.3f,1f);
        shapeRenderer.rect(rx,ry,botonWidth,botonHeight);
        // siguiente
        float sx = virtualWidth/2f - botonWidth/2f;
        float sy = 100f;
        shapeRenderer.setColor(0.2f,0.8f,0.2f,1f);
        shapeRenderer.rect(sx,sy,botonWidth,botonHeight);
        shapeRenderer.end();

        // DIBUJO TEXTOS
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Tienda", virtualWidth/2f-100, virtualHeight-80);

        for (int i = 0; i < items.length; i++) {
            float x = virtualWidth/2f - botonWidth/2f + 20;
            float y = virtualHeight - 200f - i*(botonHeight+40f) + 60;
            itemFont.setColor(Color.WHITE);
            itemFont.draw(batch, items[i].nombre + " - $" + items[i].precio, x, y);
        }
        itemFont.draw(batch, "REROLL ($"+rerollCost+")", rx+60, ry+60);
        itemFont.draw(batch, "SIGUIENTE", sx+60, sy+60);
        batch.end();
    }

    private void generarItems() {
        String[] nombres = {"LANZA","HALO","ROBOT","AK47"};
        for (int i = 0; i < items.length; i++) {
            String nombre = nombres[(int)(Math.random()*nombres.length)];
            items[i] = new Item(nombre, 0);  // ajusta precio
        }
    }

    private void comprarItem(int index) {
        Item it = items[index];
        if (player.getDinero() < it.precio) return;

        int count = purchaseCounts.getOrDefault(it.nombre, 0);

        if (it.nombre.equals("LANZA") && count >= 4) return;
        if (it.nombre.equals("AK47")  && count >= 6) return;

        player.restarDinero(it.precio);
        purchaseCounts.put(it.nombre, count+1);

        switch (it.nombre) {
            case "LANZA":
                // upgrade o nuevo
                Lanza lanza = player.getHabilidadesPermanentes().stream()
                    .filter(h->h instanceof Lanza).map(h->(Lanza)h).findFirst().orElse(null);
                if (lanza != null) lanza.upgrade();
                else player.agregarHabilidadPermanente(new Lanza(player));
                break;
            case "HALO":
                player.agregarHabilidadPermanente(new Halo(player));
                break;
            case "ROBOT":
                float d = 60f;
                float ang = (float)(Math.random()*Math.PI*2);
                Vector2 off = new Vector2((float)Math.cos(ang)*d, (float)Math.sin(ang)*d);
                player.agregarHabilidadPermanente(new Robot(player, off));
                break;
            case "AK47":
                AK47 ak = player.getHabilidadesPermanentes().stream()
                    .filter(h->h instanceof AK47).map(h->(AK47)h).findFirst().orElse(null);
                if (ak != null) ak.upgrade();
                else player.agregarHabilidadPermanente(new AK47(player));
                break;
        }
    }

    private static class Item {
        String nombre;
        int precio;
        public Item(String n,int p){nombre=n;precio=p;}
    }
}
