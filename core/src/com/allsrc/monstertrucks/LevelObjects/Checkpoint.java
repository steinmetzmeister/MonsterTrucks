package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public class Checkpoint extends Trigger {

    public Gate gate;
    public static float size = 10f;

    public void init() {
        name = "checkpoint";
        attrs = new String[]{ "color", "pos", "rot", "size" };
    }

    public Checkpoint(String line) {
        super();
        init();
        loadFromLine(line);
        construct();
    }

    public Checkpoint(Color color, Vector3 pos, Vector3 size) {
        super();
        init();
        setColor(color);
        setPos(pos);
        setSize(size);
        construct();
    }

    public void construct() {
        entity();
        addGate();
        adjustColor();
        updateColor();
        updateRot();
        updatePos();
        noResponse();
    }

    public void setRot(int angle) {
        rot.y = angle;
        gate.setRot(angle);
    }

    public void setRot(float angle) {
        rot.y = angle;
        gate.setRot(angle);
    }

    public void adjustColor() {
        color.a = 0.5f;
        updateColor();
    }

    public void addGate() {
        gate = new Gate(getColor(), getPos());
        gate.updateColor();
        gate.updateRot();
        gate.updatePos();
        BulletObject.removeFromBulletObjects(gate);
    }

    public void dispose() {
        gate.dispose();
        super.dispose();
    }

    public void triggered() {
        stop();
    }

    public static void load() {
        Gate.load();

        Planet.INSTANCE.loader.add("checkpoint");
        Planet.INSTANCE.loader.objects.get("checkpoint").model = createSphere();

        addDefaultConstructor("checkpoint");
    }

    public static Model createSphere() {
        return Planet.INSTANCE.modelBuilder.createSphere(size, size, size, 16, 16,
            new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.RED),
            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)),
            Usage.Position | Usage.Normal);
    }
}