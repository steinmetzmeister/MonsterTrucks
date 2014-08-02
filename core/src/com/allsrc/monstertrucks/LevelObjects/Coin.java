package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;

public class Coin extends Collectible {

    public void init() {
        name = "coin";
        attrs = new String[]{ "pos" };
        modelFile = "data/coin.obj";
        soundFile = "data/coins.wav";
        textureFile = "data/coin.png";
    }

    public Coin(String line) {
        super();

        init();
        loadFromLine(line);
        construct();

        randomRot();
        updatePos();
    }

    public Coin(Vector3 pos) {
        super();

        init();
        setPos(pos);
        construct();

        randomRot();
        updatePos();
    }

    public void randomRot() {
        setRot((int)(Math.random() * 360));
        updateRot();
    }

    public void update() {
        entity.transform.rotate(Vector3.Y, 1);
        super.update();
    }
}