package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;

public class Coin extends Collectible {

    public Coin(Vector3 pos) {
        name = "coin";
        modelFile = "data/coin.obj";
        textureFile = "data/coin.png";
        soundFile = "data/coins.wav";

        init(pos);
    }

    public void update() {
        entity.transform.rotate(Vector3.Y, 1f);

        super.update();
    }
}