package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

public class Gate extends LevelObject {
    public BulletEntity entity;

    public static String name = "gate";
    public static Model model;
    public static String modelFile = "data/gate.obj";
    public static btBvhTriangleMeshShape meshShape;

    public Gate(Vector3 _pos, int _rot, Color color) {
        pos = _pos;
        rot = _rot;

        if (model == null) {
            model = Planet.INSTANCE.objLoader.loadModel(Gdx.files.internal(modelFile));
            meshShape = new btBvhTriangleMeshShape(model.meshParts);
            Planet.INSTANCE.world.addConstructor(name, new BulletConstructor(model, 0f, meshShape));
        }

        entity = Planet.INSTANCE.world.add(name, pos.x, pos.y, pos.z);
        entity.modelInstance.materials.get(0).set(
            ColorAttribute.createDiffuse(color),
            ColorAttribute.createSpecular(Color.WHITE));

        entity.transform.rotate(Vector3.Y, rot);
    }

    public void dispose() {
        Planet.INSTANCE.world.remove(entity);
        Planet.INSTANCE.world.collisionWorld.removeCollisionObject(entity.body);
        entity.dispose();
    }
}