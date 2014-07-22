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

public class Coin extends LevelObject {
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

                coin.touched = true;

                return 0f;
        }
    }

    CoinCallback coinCallback;

    public static Texture texture = texture = new Texture(Gdx.files.internal("data/coin.png"), true);

    TextureAttribute textureAttribute;

    public boolean touched = false;

    public Coin(Vector3 _pos) {
        pos = _pos;

        coinCallback = new CoinCallback(this);

        textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, texture);

        final Model blockModel = Planet.INSTANCE.objLoader.loadModel(Gdx.files.internal("data/coin.obj"));
        Planet.INSTANCE.disposables.add(blockModel);
        Planet.INSTANCE.world.addConstructor("coin", new BulletConstructor(blockModel, 0f, new btBvhTriangleMeshShape(blockModel.meshParts)));
        entity = Planet.INSTANCE.world.add("coin", pos.x, pos.y, pos.z);
        entity.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);

        entity.model.materials.get(0).set(
            textureAttribute,
            ColorAttribute.createSpecular(Color.WHITE));

        entity.transform.rotate(Vector3.Y, 90f);

        Planet.INSTANCE.level.coins.add(this);
    }

    public void update() {
        entity.transform.rotate(Vector3.Y, 1f);
    }
}