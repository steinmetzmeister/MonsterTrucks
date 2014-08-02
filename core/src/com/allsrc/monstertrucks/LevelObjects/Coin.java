package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;

public class Coin extends Collectible {

    public Coin(Vector3 _pos) {
        name = "coin";
        modelFile = "data/coin.obj";
        textureFile = "data/coin.png";
        soundFile = "data/coins.wav";

        init(_pos);
    }

    public void init(Vector3 _pos) {
        super.init(_pos);
        rot = (int)(Math.random() * 360);
        entity.transform.rotate(Vector3.Y, rot);
    }

    public void update() {
        entity.transform.rotate(Vector3.Y, 1f);

        super.update();
    }

    public String getSaveLine() {
        return name + "," + pos.x + "," + pos.y + "," + pos.z;
    }

    public void loadFromLine(String line) {
        String[] ls = line.split(",");
        new Coin(new Vector3(
            Float.parseFloat(ls[1]),
            Float.parseFloat(ls[2]),
            Float.parseFloat(ls[3])));
    }
}