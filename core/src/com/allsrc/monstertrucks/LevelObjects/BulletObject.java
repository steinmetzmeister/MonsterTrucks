package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

public class BulletObject extends LevelObject {
    public BulletEntity entity;
    public Color color;

    public static String name;
    public static Model model;
    public static btBvhTriangleMeshShape meshShape;

    public void init(String _name, String modelFile) {
        if (model == null) {
            name = _name;
            model = Planet.INSTANCE.objLoader.loadModel(Gdx.files.internal(modelFile));
            meshShape = new btBvhTriangleMeshShape(model.meshParts);
            Planet.INSTANCE.world.addConstructor(name, new BulletConstructor(model, 0f, meshShape));
        }

        entity = Planet.INSTANCE.world.add(name, pos.x, pos.y, pos.z);
    }

    public void dispose() {
        Planet.INSTANCE.world.remove(entity);
        Planet.INSTANCE.world.collisionWorld.removeCollisionObject(entity.body);
        entity.dispose();
    }

    public String getSaveLine() {
        return name + "," + pos.x + "," + pos.y + "," + pos.z + ","
            + rot + ","
            + color.r + "," + color.g + "," + color.b + "," + color.a;
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