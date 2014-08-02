package com.allsrc.monstertrucks;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

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
                if (trigger.triggered)
                    return 0f;

                trigger.wasTriggered();
                
                return 0f;
        }
    }

    public Model model;
    public btSphereShape meshShape;

    public TriggerCallback triggerCallback;
    public boolean triggered = false;

    public void init() {
        name = "trigger";
        attrs = new String[]{ "color", "pos", "size" };

        triggerCallback = new TriggerCallback(this);
    }

    public Trigger(String line) {
        init();
        loadFromLine(line);
        construct();
        updateColor();
        updatePos();
        noResponse();

        Planet.INSTANCE.level.triggers.add(this);
    }

    public Trigger(Color color, Vector3 pos, Vector3 size) {
        init();
        setPos(pos);
        setSize(size);
        construct();
        updateColor();
        updatePos();
        noResponse();

        Planet.INSTANCE.level.triggers.add(this);
    }

    public void noResponse() {
        entity.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
    }

    public void construct() {
        if (model == null)
        {
            model = getModel();
            meshShape = new btSphereShape(size.x / 2f);

            final BulletConstructor triggerConstructor = new BulletConstructor(model, 0f, meshShape);

            Planet.INSTANCE.world.addConstructor(name, triggerConstructor);
        }

        entity();
        addToBulletObjects(this);
    }

    public Model getModel() {
        return Planet.INSTANCE.modelBuilder.createSphere(size.x, size.y, size.z, 16, 16,
            new Material(new ColorAttribute(ColorAttribute.Diffuse, color),
            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)),
            Usage.Position | Usage.Normal);
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
        triggered = true;System.out.println(name);
        // dispose();
    }
}