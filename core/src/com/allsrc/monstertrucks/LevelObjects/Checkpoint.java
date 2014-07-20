package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.Model;

import com.badlogic.gdx.math.Vector3;

public class Checkpoint extends LevelObject {
    ObjLoader objLoader = new ObjLoader();
    BulletEntity entity;

    public class CheckpointCallback extends ContactResultCallback {
        public Checkpoint checkpoint;

        @Override
        public float addSingleResult (btManifoldPoint cp,
            btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
            btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
                System.out.println("Checkpoint reached.");
                checkpoint.reached = true;
                return 0f;
        }
    }

    CheckpointCallback cpb;
    boolean reached = false;

    public Checkpoint(Vector3 _pos) {
        create(_pos);
    }

    public void create(Vector3 _pos) {
        cpb = new CheckpointCallback();
        cpb.checkpoint = this;

        final Model blockModel = objLoader.loadModel(Gdx.files.internal("data/block.obj"));
        Planet.INSTANCE.world.addConstructor("checkpoint", new BulletConstructor(blockModel, 0f, new btBvhTriangleMeshShape(blockModel.meshParts)));
        entity = Planet.INSTANCE.world.add("checkpoint", _pos.x, _pos.y, _pos.z);
        entity.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);

        pos = _pos;

        Planet.INSTANCE.level.checkpoints.add(this);
    }

    public void update() {
        if (!reached)
        {
            for (Car car : Planet.INSTANCE.cars) {
                Planet.INSTANCE.world.collisionWorld.contactPairTest(car.chassis.body, entity.body, cpb);
            }
        }
    }
}