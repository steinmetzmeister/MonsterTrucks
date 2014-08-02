package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Gate extends BulletObject {

    public void init() {
        name = "gate";
        attrs = new String[]{ "color", "pos" };
        modelFile = "data/gate.obj";
    }

    public Gate(String line) {
        init();
        loadFromLine(line);
        construct();
        updateColor();
        updatePos();
    }

    public Gate(Color color, Vector3 pos) {
        init();
        setColor(color);
        setPos(pos);
        construct();
        updateColor();
        updatePos();
    }
}