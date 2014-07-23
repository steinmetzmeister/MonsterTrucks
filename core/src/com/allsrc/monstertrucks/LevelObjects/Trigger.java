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
                
                return 0f;
        }
    }

    public TriggerCallback triggerCallback;

    public boolean triggered = false;
    
    public BulletEntity entity;

    public static Model triggerModel;
    public Color triggerColor;

    public Trigger(Vector3 pos, Vector3 size, Color color) {
        init(pos, size, color);
    }

    public void init(Vector3 pos, Vector3 size, Color color) {
        triggerCallback = new TriggerCallback(this);

        triggerColor = color;
        
        if (triggerModel == null)
        {
           triggerModel = Planet.INSTANCE.modelBuilder.createSphere(size.x, size.y, size.z, 16, 16,
                new Material(new ColorAttribute(ColorAttribute.Diffuse, color),
                new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)),
                Usage.Position | Usage.Normal);

           Planet.INSTANCE.world.addConstructor("trigger", new BulletConstructor(triggerModel, 0f,
            new btBvhTriangleMeshShape(triggerModel.meshParts)));
        }
        
        entity = Planet.INSTANCE.world.add("trigger", pos.x, pos.y, pos.z);
        entity.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
        
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