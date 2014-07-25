package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

public class Checkpoint extends Trigger {

    public static String name = "checkpoint";
    public Gate gate;

    public Checkpoint(Vector3 _pos, int _size, Color color) {
        super(_pos, _size, color);

        gate = new Gate(_pos, 0, color);
    }

    public void init() {
        super.init();
    }

    public void dispose() {
        gate.dispose();
        super.dispose();
    }

    public String getSaveLine() {
        return name + ","
            + pos.x + "," + pos.y + "," + pos.z + ","
            + size + "," + triggerColor.r + "," + triggerColor.g + "," + triggerColor.b + "," + triggerColor.a;
            
    }

    public static void loadFromLine(String line) {
        String[] ls = line.split(",");
        new Checkpoint(new Vector3(
            Float.parseFloat(ls[1]),
            Float.parseFloat(ls[2]),
            Float.parseFloat(ls[3])), Integer.parseInt(ls[4]),
            new Color(
                Float.parseFloat(ls[5]),
                Float.parseFloat(ls[6]),
                Float.parseFloat(ls[7]),
                Float.parseFloat(ls[8])));
    }
}