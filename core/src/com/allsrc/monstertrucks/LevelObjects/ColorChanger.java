package com.allsrc.monstertrucks;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

public class ColorChanger extends BulletObject {

    public class ColorChangerCallback extends ContactResultCallback {
        public ColorChanger changer;

        @Override
        public float addSingleResult (btManifoldPoint cp,
            btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
            btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {

                changer.entity.modelInstance.materials.get(0).set(
                    ColorAttribute.createDiffuse(testing),
                    ColorAttribute.createSpecular(Color.WHITE));

                return 0f;
        }
    }

    Color testing;
    ColorChangerCallback colorChangerCallback;

    public void init() {
        name = "changer";
        attrs = new String[]{ "pos" };
        modelFile = "data/block.obj";

        colorChangerCallback = new ColorChangerCallback();
        colorChangerCallback.changer = this;

        Planet.INSTANCE.level.changers.add(this);
    }

    public ColorChanger(String line) {
        init();
        loadFromLine(line);
        construct();
        updatePos();
    }

    public ColorChanger(Vector3 pos) {
        init();
        setPos(pos);
        construct();
        updatePos();
    }

    public void update() {
        for (Car car : Planet.INSTANCE.cars) {
            testing = car.carColor;
            testCollision(car.chassis.body);
        }

        for (BulletObject object : Planet.INSTANCE.level.bulletObjects) {
            testing = object.color;
            testCollision(object.entity.body);
        }
    }

    public void testCollision(btCollisionObject body) {
        Planet.INSTANCE.world.collisionWorld.contactPairTest(body, entity.body, colorChangerCallback);
    }

    public void dispose() {
        Planet.INSTANCE.world.remove(entity);
        Planet.INSTANCE.world.collisionWorld.removeCollisionObject(entity.body);

        Planet.INSTANCE.level.changers.removeValue(this, false);
        
        entity.dispose();
    }
}