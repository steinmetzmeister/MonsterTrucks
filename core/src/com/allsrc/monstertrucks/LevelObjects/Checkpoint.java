package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Checkpoint extends Trigger {

    public static String name = "checkpoint";
    public Gate gate;

    public Checkpoint(int _size) {
        super(_size);

        gate = new Gate();
        BulletObject.removeFromBulletObjects(gate);
    }

    public void dispose() {
        gate.dispose();
        super.dispose();
    }

    public void setColor(Color newColor) {
        gate.setColor(newColor);
        super.setColor(newColor);
    }

    public void setPos(Vector3 newPos) {
        gate.setPos(newPos);
        super.setPos(newPos);
    }

    public String getSaveLine() {
        return name + ","
            + pos.x + "," + pos.y + "," + pos.z + ","
            + size + "," + color.r + "," + color.g + "," + color.b + "," + color.a;
    }

    public static void loadFromLine(String line) {
        String[] ls = line.split(",");
        Checkpoint checkpoint = new Checkpoint(Integer.parseInt(ls[4]));
        
        checkpoint.setPos(new Vector3(
            Float.parseFloat(ls[1]),
            Float.parseFloat(ls[2]),
            Float.parseFloat(ls[3])));

        checkpoint.setColor(new Color(
            Float.parseFloat(ls[5]),
            Float.parseFloat(ls[6]),
            Float.parseFloat(ls[7]),
            Float.parseFloat(ls[8])));
    }
}