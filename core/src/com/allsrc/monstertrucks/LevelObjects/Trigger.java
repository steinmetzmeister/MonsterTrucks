package com.allsrc.monstertrucks;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

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

    public TriggerCallback triggerCallback;
    public boolean triggered = false;
    public static float size = 10f;

    public void init() {
        name = "trigger";
        attrs = new String[]{ "color", "pos", "size" };

        triggerCallback = new TriggerCallback(this);
    }

    public Trigger(String line) {
        init();
        loadFromLine(line);
        construct();
    }

    public Trigger(Color color, Vector3 pos, Vector3 size) {
        init();
        setColor(color);
        setPos(pos);
        setSize(size);
        construct();
    }

    public void construct() {
        entity();
        updateColor();
        updatePos();
        noResponse();

        Planet.INSTANCE.level.triggers.add(this);
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

    public static void load() {
        Planet.INSTANCE.loader.add("trigger");
        Planet.INSTANCE.loader.objects.get("trigger").model = createSphere();

        addDefaultConstructor("trigger");
    }

    public static Model createSphere() {
        return Planet.INSTANCE.modelBuilder.createSphere(size, size, size, 16, 16,
            new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.RED),
            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)),
            Usage.Position | Usage.Normal);
    }
}