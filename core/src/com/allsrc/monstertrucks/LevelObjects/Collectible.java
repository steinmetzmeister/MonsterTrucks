package com.allsrc.monstertrucks;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class Collectible extends BulletObject {

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
    
    public String soundFile;
    public Sound pickupSound;

    public Collectible() {
        collectibleCallback = new CollectibleCallback(this);
    }

    public void construct() {
        pickupSound = Gdx.audio.newSound(Gdx.files.internal(soundFile));

        super.construct();

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
            for (Car car : Planet.INSTANCE.cars) {
                if (entity.body != null)
                    Planet.INSTANCE.world.collisionWorld.contactPairTest(car.chassis.body, entity.body, collectibleCallback);
            }
        }
    }

    public void pickedUp() {
        System.out.println("PI");
        pickupSound.play();

        touched = true;
        dispose();
    }

    public void dispose () {
        Planet.INSTANCE.world.remove(entity);
        Planet.INSTANCE.world.collisionWorld.removeCollisionObject(entity.body);

        removeFromCollectibles();
        removeFromBulletObjects(this);
        
        entity.dispose();
    }
}