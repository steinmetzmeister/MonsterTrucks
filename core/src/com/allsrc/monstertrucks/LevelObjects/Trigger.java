package com.allsrc.monstertrucks;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;

public class Trigger extends BulletObject {

    public class TriggerCallback extends ContactResultCallback {
        public Trigger trigger;

        public TriggerCallback(Trigger trigger) {
            this.trigger = trigger;
        }
        
        @Override
        public float addSingleResult (btManifoldPoint cp,
            btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
            btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
                if (trigger.paused)
                    return 0f;

                trigger.triggered();
                
                return 0f;
        }
    }

    public TriggerCallback triggerCallback;
    public BulletObject testing;

    public boolean paused = false;

    public Trigger() {
        triggerCallback = new TriggerCallback(this);
        Planet.INSTANCE.level.triggers.add(this);
    }

    public void update() {
        for (Car car : Planet.INSTANCE.cars) {
            testing = (BulletObject)car;
            testCollision(car.chassis.body);
        }

        for (BulletObject object : Planet.INSTANCE.level.bulletObjects) {
            testing = object;
            testCollision(object.entity.body);
        }
    }

    public void testCollision(btCollisionObject body) {
        Planet.INSTANCE.world.collisionWorld.contactPairTest(body, entity.body, triggerCallback);
    }

    public void dispose() {
        super.dispose();
        Planet.INSTANCE.level.triggers.removeValue(this, true);
    }

    public void triggered() {
        //
    }

    public void start() {
        paused = false;
    }

    public void stop() {
        paused = true;
    }
}