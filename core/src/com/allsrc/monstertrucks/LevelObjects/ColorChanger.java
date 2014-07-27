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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import com.badlogic.gdx.math.Vector3;

public class ColorChanger extends LevelObject {
    BulletEntity entity;

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

    public static String name = "changer";
    public static Model blockModel;

    Color testing;
    ColorChangerCallback colorChangerCallback;
    boolean reached = false;

    public ColorChanger(Vector3 _pos) {
        create(_pos);
    }

    public void create(Vector3 _pos) {
        colorChangerCallback = new ColorChangerCallback();
        colorChangerCallback.changer = this;

        if (blockModel == null) {
            Model blockModel = Planet.INSTANCE.objLoader.loadModel(Gdx.files.internal("data/block.obj"));
            Planet.INSTANCE.disposables.add(blockModel);

            final btBvhTriangleMeshShape meshShape = new btBvhTriangleMeshShape(blockModel.meshParts);
            Planet.INSTANCE.world.addConstructor("changer", new BulletConstructor(blockModel, 0f, meshShape));
        }

        entity = Planet.INSTANCE.world.add("changer", _pos.x, _pos.y, _pos.z);

        pos = _pos;

        Planet.INSTANCE.level.changers.add(this);
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

    public String getSaveLine() {
        return name + "," + pos.x + "," + pos.y + "," + pos.z;
    }

    public static void loadFromLine(String text) {
        String[] p = text.split(",");
        new ColorChanger(new Vector3(
            Float.parseFloat(p[1]),
            Float.parseFloat(p[2]),
            Float.parseFloat(p[3])));
    }
}