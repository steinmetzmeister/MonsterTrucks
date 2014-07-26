package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Terrain extends BulletObject {
    public static String name = "terrain";

    public Terrain(String modelFile, Color _color) {
        pos = new Vector3(0f, 0f, 0f);
        rot = 0;

        super.init(name, modelFile);
    }
}