package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Gate extends BulletObject {

    public static String name = "gate";
    public static String modelFile = "data/gate.obj";

    public Gate(Vector3 _pos, int _rot, Color _color) {
        pos = _pos;
        rot = _rot;
        color = _color;

        init(name, modelFile);
    }

    public void dispose() {
        super.dispose();
    }

    public static void loadFromLine(String line) {
        String[] ls = line.split(",");
        new Gate(
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