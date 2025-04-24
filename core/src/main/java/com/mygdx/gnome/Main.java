package com.mygdx.gnome;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends Game {
    SpriteBatch batch;
    BitmapFont smallFont, titleFont;

    AssetManager assetManager;

    @Override
    public void create() {

        batch = new SpriteBatch();
        assetManager = new AssetManager();

        assetManager.load("GNOME/Player/1.png", Texture.class);
        assetManager.load("GNOME/Player/2.png", Texture.class);
        assetManager.load("GNOME/Player/3.png", Texture.class);
        assetManager.load("GNOME/Player/w1.png", Texture.class);
        assetManager.load("GNOME/Player/w2.png", Texture.class);
        assetManager.load("GNOME/Player/w3.png", Texture.class);


        assetManager.load("GNOME/Map/Map.png", Texture.class);
        assetManager.load("GNOME/bullet.png", Texture.class);
        assetManager.load("GNOME/joysk1.png", Texture.class);
        assetManager.load("GNOME/joysk2.png", Texture.class);
        assetManager.load("GNOME/bullet2.png", Texture.class);
        assetManager.load("GNOME/bullet3.png", Texture.class);
        assetManager.load("GNOME/robot.png", Texture.class);
        assetManager.load("GNOME/spear.png", Texture.class);
        assetManager.load("GNOME/ak47.png", Texture.class);
        assetManager.load("GNOME/halo.png", Texture.class);
        assetManager.load("GNOME/Coin.png", Texture.class);


        assetManager.load("GNOME/shop.png", Texture.class);
        assetManager.load("GNOME/reroll.png", Texture.class);
        assetManager.load("GNOME/siguiente.png", Texture.class);

        //HABILIADES
        assetManager.load("GNOME/Coin.png", Texture.class);
        assetManager.load("GNOME/HEART.png", Texture.class);
        assetManager.load("GNOME/VELOCIDAD.png", Texture.class);
        assetManager.load("GNOME/DAMAGE.png", Texture.class);
        assetManager.load("GNOME/CADENCIA.png", Texture.class);


        assetManager.load("GNOME/Snail/1.png", Texture.class);
        assetManager.load("GNOME/Snail/damage.png", Texture.class);

        assetManager.finishLoading();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("GNOME/DynaPuff.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter paramSmall = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramSmall.size = 24;
        smallFont = generator.generateFont(paramSmall);

        FreeTypeFontGenerator.FreeTypeFontParameter paramTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramTitle.size = 32;
        titleFont = generator.generateFont(paramTitle);

        generator.dispose();

        this.setScreen(new MainMenuScreen(this));
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    public BitmapFont getTitleFont() {
        return titleFont;
    }
}
