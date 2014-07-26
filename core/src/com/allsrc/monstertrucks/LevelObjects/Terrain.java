package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Terrain extends BulletObject {

    public static String name = "terrain";
    public static String modelFile;

    public Terrain(String _modelFile, Color _color) {
        pos = new Vector3(0f, 0f, 0f);
        rot = 0;
        color = _color;
        modelFile = _modelFile;
        scale = 2f;

        init(name, modelFile);
    }

    public void init() {
        super.init(name, modelFile);
    }
}