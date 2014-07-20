package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;

public class MonsterTruck extends Car {
    public class CheckpointCallback extends ContactResultCallback {
        @Override
        public float addSingleResult (btManifoldPoint cp,
            btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
            btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
                System.out.println("COLLIDE");

                return 0f;
        }
    }

    CheckpointCallback cpb;

    public void init() {
        cpb = new CheckpointCallback();

        maxForce = 100f;
        acceleration = 200f; // second

        chassisModelFile = "data/complexCar.obj";
        wheelScale = new Vector3(3f, 2.5f, 2.5f);

        super.init();
    }

    public void update() {
        super.update();

        for (Checkpoint checkpoint : Planet.INSTANCE.level.checkpoints) {
            Planet.INSTANCE.world.collisionWorld.contactPairTest(chassis.body, checkpoint.entity.body, cpb);
        }
    }
}