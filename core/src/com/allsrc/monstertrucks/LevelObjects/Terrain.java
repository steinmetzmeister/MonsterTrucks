package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

public class Terrain extends BulletObject {

    public static String name = "terrain";
    public static Model model;
    public static btBvhTriangleMeshShape meshShape;
    public Color color;

    public Terrain(String modelFile, Color color) {
        scale = 2f;

        if (model == null) {
            model = getModel(modelFile);
            meshShape = getMeshShape(model);

            addConstructor(name, model, meshShape);
        }

        init(name);

        this.color = color;
        setColor(color);

        addToBulletObjects(this);
    }

    public String getName() {
        return name;
    }

    public String getSaveLine() {
        return name + "," + color.r + "," + color.g + "," + color.b + "," + color.a;
    }

    public static void loadFromLine(String line) {
        String[] ls = line.split(",");
        Planet.INSTANCE.level.terrain = new Terrain("data/terrain.obj",
            new Color(
                Float.parseFloat(ls[1]),
                Float.parseFloat(ls[2]),
                Float.parseFloat(ls[3]),
                Float.parseFloat(ls[4])));
    }
}