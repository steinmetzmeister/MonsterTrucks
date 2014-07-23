package com.allsrc.monstertrucks;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class Collectible {

    public class CollectibleCallback extends ContactResultCallback {
        public Collectible collectible;

        public CollectibleCallback(Collectible _collectible) {
            collectible = _collectible;
        }
        
        @Override
        public float addSingleResult (btManifoldPoint cp,
            btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
            btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
                if (collectible.touched)
                    return 0f;

                collectible.pickedUp();
                
                return 0f;
        }
    }

    public CollectibleCallback collectibleCallback;;

    public boolean touched = false;

    public BulletEntity entity;

    public static Model collectibleModel = null;
    public static Texture texture;
    public static TextureAttribute textureAttribute;
    public static Sound pickupSound;
    public static btBvhTriangleMeshShape meshShape;

    public String name;
    public String modelFile;
    public String textureFile;
    public String soundFile;

    public void init(Vector3 pos) {
        collectibleCallback = new CollectibleCallback(this);

        if (collectibleModel == null) {
            collectibleModel = Planet.INSTANCE.objLoader.loadModel(Gdx.files.internal(modelFile));
            texture = new Texture(Gdx.files.internal(textureFile), true);
            textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, texture);
            pickupSound = Gdx.audio.newSound(Gdx.files.internal(soundFile));
            meshShape = new btBvhTriangleMeshShape(collectibleModel.meshParts);
            
            Planet.INSTANCE.world.addConstructor(name, new BulletConstructor(collectibleModel, 0f, meshShape));
        }

        entity = Planet.INSTANCE.world.add(name, pos.x, pos.y, pos.z);
        entity.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);

        entity.modelInstance.materials.get(0).set(
            textureAttribute,
            ColorAttribute.createSpecular(Color.WHITE));

        addToCollectibles();
    }

    public void addToCollectibles() {
        Planet.INSTANCE.level.collectibles.add(this);
    }

    public void removeFromCollectibles() {
        Planet.INSTANCE.level.collectibles.removeValue(this, true);
    }

    public void update() {
        if (!touched)
        {
            entity.transform.rotate(Vector3.Y, 1f);

            for (Car car : Planet.INSTANCE.cars) {
                if (entity.body != null)
                    Planet.INSTANCE.world.collisionWorld.contactPairTest(car.chassis.body, entity.body, collectibleCallback);
            }
        }
    }

    public void pickedUp() {
        pickupSound.play();

        touched = true;
        dispose();
    }

    public void dispose () {
        Planet.INSTANCE.world.remove(entity);
        Planet.INSTANCE.world.collisionWorld.removeCollisionObject(entity.body);

        removeFromCollectibles();
        
        entity.dispose();
    }
}