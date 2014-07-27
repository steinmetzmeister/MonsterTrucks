package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

public class Gate extends BulletObject {

    public static String name = "gate";
    public static String modelFile = "data/gate.obj";
    public static Model model;
    public static btBvhTriangleMeshShape meshShape;

    public Gate() {
        if (model == null) {
            model = getModel(modelFile);
            meshShape = getMeshShape(model);

            Planet.INSTANCE.world.addConstructor(name, new BulletConstructor(model, 0f, meshShape));
        }

        init(name);
    }

    public void dispose() {
        super.dispose();
    }

    public static void loadFromLine(String line) {
        String[] ls = line.split(",");
        Gate gate = new Gate();

        gate.setPos(new Vector3(
            Float.parseFloat(ls[1]),
            Float.parseFloat(ls[2]),
            Float.parseFloat(ls[3])));

        gate.setRot(Integer.parseInt(ls[4]));

        gate.setColor(new Color(
            Float.parseFloat(ls[5]),
            Float.parseFloat(ls[6]),
            Float.parseFloat(ls[7]),
            Float.parseFloat(ls[8])));
    }
}