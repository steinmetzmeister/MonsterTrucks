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

import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.audio.Sound;

import com.badlogic.gdx.utils.Disposable;

public class Coin {
    BulletEntity entity;

    public class CoinCallback extends ContactResultCallback {
        public Coin coin;

        public CoinCallback(Coin _coin) {
            coin = _coin;
        }

        @Override
        public float addSingleResult (btManifoldPoint cp,
            btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
            btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
                if (coin.touched)
                    return 0f;

                System.out.println("Coin collected.");
                pickupSound.play();

                coin.touched = true;
                coin.dispose();

                return 0f;
        }
    }

    CoinCallback coinCallback;

    public static Texture texture = texture = new Texture(Gdx.files.internal("data/coin.png"), true);
    public static Sound pickupSound = Gdx.audio.newSound(Gdx.files.internal("data/coins.wav"));

    TextureAttribute textureAttribute;

    public boolean touched = false;

    public Coin(Vector3 pos) {
        coinCallback = new CoinCallback(this);

        textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, texture);

        final Model coinModel = Planet.INSTANCE.objLoader.loadModel(Gdx.files.internal("data/coin.obj"));
        // Planet.INSTANCE.disposables.add(coinModel);
        Planet.INSTANCE.world.addConstructor("coin", new BulletConstructor(coinModel, 0f, new btBvhTriangleMeshShape(coinModel.meshParts)));
        entity = Planet.INSTANCE.world.add("coin", pos.x, pos.y, pos.z);
        entity.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);

        entity.modelInstance.materials.get(0).set(
            textureAttribute,
            ColorAttribute.createSpecular(Color.WHITE));

        Planet.INSTANCE.level.coins.add(this);
    }

    public void update() {
        if (!touched)
        {
            entity.transform.rotate(Vector3.Y, 1f);

            for (Car car : Planet.INSTANCE.cars) {
                Planet.INSTANCE.world.collisionWorld.contactPairTest(car.chassis.body, entity.body, coinCallback);
            }
        }
    }

    public void dispose () {
        Planet.INSTANCE.world.remove(entity);

        Planet.INSTANCE.level.coins.removeValue(this, true);
        Planet.INSTANCE.world.collisionWorld.removeCollisionObject(entity.body);
        entity.dispose();

        System.out.println("Coin disposed.");
    }
}