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
    BitmapFont smallFont, bigFont;

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



        assetManager.load("GNOME/Snail/1.png", Texture.class);
        assetManager.load("GNOME/Snail/damage.png", Texture.class);

        assetManager.finishLoading();

        this.setScreen(new MainMenuScreen(this));
    }
}
