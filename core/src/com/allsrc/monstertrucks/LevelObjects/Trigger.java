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

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;

public class Trigger {

    public class TriggerCallback extends ContactResultCallback {
        public Trigger trigger;

        public TriggerCallback(Trigger _trigger) {
            trigger = _trigger;
        }
        
        @Override
        public float addSingleResult (btManifoldPoint cp,
            btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
            btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
                if (trigger.triggered)
                    return 0f;

                trigger.wasTriggered();

                System.out.println("Triggered.");
                
                return 0f;
        }
    }

    public TriggerCallback triggerCallback;

    public boolean triggered = false;

    public BulletEntity entity;

    public Trigger(Vector3 pos, Vector3 size) {
        init(pos, size);
    }

    public void init(Vector3 pos, Vector3 size) {
        triggerCallback = new TriggerCallback(this);

        ModelBuilder builder = new ModelBuilder();
        Model sphere = builder.createSphere(size.x, size.y, size.z, 16, 16,
            new Material(new ColorAttribute(ColorAttribute.Diffuse, new Color(1f, 0f, 0f, 0.4f)),
            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)),
            Usage.Position | Usage.Normal);

        Planet.INSTANCE.world.addConstructor("trigger", new BulletConstructor(sphere, 0f, new btBvhTriangleMeshShape(sphere.meshParts)));
        entity = Planet.INSTANCE.world.add("trigger", pos.x, pos.y, pos.z);
        entity.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);

        /*
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

        */
        
        addToTriggers();
    }

    public void addToTriggers() {
        Planet.INSTANCE.level.triggers.add(this);
    }

    public void removeFromTriggers() {
        Planet.INSTANCE.level.triggers.removeValue(this, true);
    }

    public void update() {
        if (!triggered)
        {
            for (Car car : Planet.INSTANCE.cars) {
                if (entity.body != null)
                    Planet.INSTANCE.world.collisionWorld.contactPairTest(car.chassis.body, entity.body, triggerCallback);
            }
        }
    }

    public void wasTriggered() {
        triggered = true;
        // dispose();
    }

    public void dispose () {
        Planet.INSTANCE.world.remove(entity);
        Planet.INSTANCE.world.collisionWorld.removeCollisionObject(entity.body);

        removeFromTriggers();
        
        entity.dispose();
    }
}