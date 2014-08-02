package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;

public class Terrain extends BulletObject {

    public void init() {
        name = "terrain";
        attrs = new String[]{ "color", "modelFile" };
        scale = 2f;
    }

    public Terrain(String line) {
        init();
        loadFromLine(line);
        construct();
        updateColor();
    }

    public Terrain(Color color, String modelFile) {
        init();
        setColor(color);
        setModelFile(modelFile);
        construct();
        updateColor();
    }
}