package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Checkpoint extends Trigger {

    public Gate gate;

    public void init() {
        super.init();

        name = "checkpoint";
        attrs = new String[]{ "color", "pos", "size" };
    }

    public Checkpoint(String line) {
        super(line);
        addGate();
        adjustColor();
    }

    public Checkpoint(Color color, Vector3 pos, Vector3 size) {
        super(color, pos, size);
        addGate();
        adjustColor();
    }

    public void setRot(int angle) {
        Vector3 pos = getPos();

        rot.y = angle;
        updateRot();
        setPos(pos);
        updatePos();
        
        gate.rot.y = angle;
        gate.updateRot();
        gate.setPos(pos);
        gate.updatePos();
    }

    public void adjustColor() {
        color.a = 0.5f;
        updateColor();
    }

    public void addGate() {
        gate = new Gate(getColor(), getPos());
        gate.updateColor();
        BulletObject.removeFromBulletObjects(gate);
    }

    public void dispose() {
        gate.dispose();
        super.dispose();
    }
}