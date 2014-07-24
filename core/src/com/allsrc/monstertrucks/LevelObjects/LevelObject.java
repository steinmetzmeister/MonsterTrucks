package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;

public class LevelObject {
    public static String name;
    public Vector3 pos;

    public LevelObject() {}
    public LevelObject(String _name, Vector3 _pos) {
        name = _name;
        pos = _pos;
    }

    public void update() {
        // update
    }

    public String getSaveText() {
        return name + " "
            + pos.x + "," + pos.y + "," + pos.z;
    }

    public static void loadFromText(String text) {
        System.out.println(name + " loader not implemented.");
    }
}