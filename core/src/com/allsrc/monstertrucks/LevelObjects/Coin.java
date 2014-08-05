package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class Coin extends Collectible {

    public void init() {
        name = "coin";
        attrs = new String[]{ "pos" };
    }

    public Coin(String line) {
        super();
        init();
        loadFromLine(line);
        construct();
    }

    public Coin(Vector3 pos) {
        super();
        init();
        setPos(pos);
        construct();
    }

    public void construct() {
        entity();
        noResponse();
        randomRot();
        updateTexture();
    }

    public void update() {
        entity.transform.rotate(Vector3.Y, 1);
        super.update();
    }

    public static void load() {
        Planet.INSTANCE.loader.add("coin");
        Planet.INSTANCE.loader.loadModel("data/coin.obj");
        Planet.INSTANCE.loader.loadSound("data/coins.wav");
        Planet.INSTANCE.loader.loadTexture("data/coin.png");
        addDefaultConstructor("coin");
    }
}