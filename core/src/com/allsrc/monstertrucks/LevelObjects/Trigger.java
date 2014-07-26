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

public class Trigger extends BulletObject {

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

    public static String name = "trigger";
    public static Model model;

    public TriggerCallback triggerCallback;

    public boolean triggered = false;
    
    public int size;

    public Trigger(Vector3 _pos, int _size, Color _color) {
        pos = _pos;
        size = _size;
        color = _color;

        init();
    }

    public void init() {
        triggerCallback = new TriggerCallback(this);
        
        if (model == null)
        {
            model = Planet.INSTANCE.modelBuilder.createSphere(size, size, size, 16, 16,
                new Material(new ColorAttribute(ColorAttribute.Diffuse, color),
                new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)),
                Usage.Position | Usage.Normal);

            Planet.INSTANCE.world.addConstructor(name, new BulletConstructor(model, 0f, 
                new btBvhTriangleMeshShape(model.meshParts)));
        }
        
        entity = Planet.INSTANCE.world.add(name, pos.x, pos.y, pos.z);
        entity.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
        
        addToBulletObjects();
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

    public String getSaveLine() {
        return name + ","
            + pos.x + "," + pos.y + "," + pos.z + ","
            + size + "," + color.r + "," + color.g + "," + color.b + "," + color.a;
    }

    public static void loadFromLine(String line) {
        String[] ls = line.split(",");
        new Trigger(new Vector3(
            Float.parseFloat(ls[1]),
            Float.parseFloat(ls[2]),
            Float.parseFloat(ls[3])), Integer.parseInt(ls[4]),
            new Color(
                Float.parseFloat(ls[5]),
                Float.parseFloat(ls[6]),
                Float.parseFloat(ls[7]),
                Float.parseFloat(ls[8])));
    }
}