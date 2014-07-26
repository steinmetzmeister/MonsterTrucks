package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Checkpoint extends Trigger {

    public static String name = "checkpoint";
    public Gate gate;

    public Checkpoint(Vector3 _pos, int _size, Color _color) {
        super(_pos, _size, _color);
    }

    public void init() {
        gate = new Gate(pos, 0, color);
        gate.removeFromBulletObjects();

        super.init();
    }

    public void dispose() {
        gate.dispose();
        super.dispose();
    }

    public String getSaveLine() {
        return name + ","
            + pos.x + "," + pos.y + "," + pos.z + ","
            + size + "," + color.r + "," + color.g + "," + color.b + "," + color.a;
    }

    public static void loadFromLine(String line) {
        String[] ls = line.split(",");
        new Checkpoint(
            new Vector3(
                Float.parseFloat(ls[1]),
                Float.parseFloat(ls[2]),
                Float.parseFloat(ls[3])),
            Integer.parseInt(ls[4]),
            new Color(
                Float.parseFloat(ls[5]),
                Float.parseFloat(ls[6]),
                Float.parseFloat(ls[7]),
                Float.parseFloat(ls[8])));
    }
}