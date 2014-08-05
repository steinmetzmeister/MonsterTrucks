package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Gate extends BulletObject {

    public void init() {
        name = "gate";
        attrs = new String[]{ "color", "pos" };
    }

    public Gate(String line) {
        init();
        loadFromLine(line);
        construct();
    }

    public Gate(Color color, Vector3 pos) {
        init();
        setColor(color);
        setPos(pos);
        construct();
    }

    public void construct() {
        entity();
        updateColor();
        updatePos();
        randomRot();
    }

    public static void load() {
        Planet.INSTANCE.loader.add("gate");
        Planet.INSTANCE.loader.loadModel("data/gate.obj");
        addDefaultConstructor("gate");
    }
}