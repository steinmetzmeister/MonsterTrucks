package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.Model;

public class Checkpoint extends LevelObject {
    ObjLoader objLoader = new ObjLoader();
    BulletEntity entity;

    public void init() {
        final Model blockModel = objLoader.loadModel(Gdx.files.internal("data/block.obj"));
        Planet.INSTANCE.world.addConstructor("block", new BulletConstructor(blockModel, 0f, new btBvhTriangleMeshShape(blockModel.meshParts)));
        entity = Planet.INSTANCE.world.add("block", 5f, 0f, 5f);
        entity.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
    }
}