package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;

public class ColorChanger extends Trigger {

    public void init() {
        name = "changer";
        attrs = new String[]{ "pos" };
    }

    public ColorChanger(String line) {
        super();
        init();
        loadFromLine(line);
        construct();
    }

    public ColorChanger(Vector3 pos) {
        super();
        init();
        setPos(pos);
        construct();
    }

    public void construct() {
        entity();
        updatePos();
    }

    public static void load() {
        Planet.INSTANCE.loader.add("changer");
        Planet.INSTANCE.loader.loadModel("data/block.obj");
        addDefaultConstructor("changer");
    }

    public void triggered() {
        color = testing.color;
        updateColor();
    }
}