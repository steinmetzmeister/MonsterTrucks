package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

public class Terrain extends BulletObject {

    public static String name = "terrain";
    public static Model model;
    public static btBvhTriangleMeshShape meshShape;

    public Terrain(String modelFile) {
        scale = 2f;

        if (model == null) {
            model = getModel(modelFile);
            meshShape = getMeshShape(model);

            Planet.INSTANCE.world.addConstructor(name, new BulletConstructor(model, 0f, meshShape));
        }

        init(name);
    }
}