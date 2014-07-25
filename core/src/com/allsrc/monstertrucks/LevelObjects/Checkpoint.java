package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

public class Checkpoint extends Trigger {
    public static String name = "checkpoint";

    public static String checkpointModelFile = "data/checkpoint.obj";
    public static Model checkpointModel;
    public static btBvhTriangleMeshShape checkpointMeshShape;

    public BulletEntity checkpointEntity;

    public Checkpoint(Vector3 _pos, int _size, Color color) {
        super(_pos, _size, color);
    }

    public void init() {
        if (checkpointModel == null) {
            checkpointModel = Planet.INSTANCE.objLoader.loadModel(Gdx.files.internal(checkpointModelFile));
            checkpointMeshShape = new btBvhTriangleMeshShape(checkpointModel.meshParts);

            checkpointModel.materials.get(0).set(ColorAttribute.createDiffuse(triggerColor), ColorAttribute.createSpecular(Color.WHITE));

            Planet.INSTANCE.world.addConstructor(name, new BulletConstructor(checkpointModel, 0f, checkpointMeshShape));
        }

        checkpointEntity = Planet.INSTANCE.world.add(name, pos.x, pos.y, pos.z);

        super.init();
    }

    public void dispose() {
        Planet.INSTANCE.world.remove(checkpointEntity);
        Planet.INSTANCE.world.collisionWorld.removeCollisionObject(checkpointEntity.body);
        
        checkpointEntity.dispose();

        super.dispose();
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