package com.allsrc.monstertrucks;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

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

public class Ball extends LevelObject {
    BulletEntity entity;

    public static Model ballModel;
    public static String name = "ball";
    public int size;
    public Color color;

    public Ball(Vector3 _pos, int _size, Color _color) {
        init(_pos, _size, _color);
    }

    public void init(Vector3 _pos, int _size, Color _color) {
        pos = _pos;
        size = _size;
        color = _color;

        if (ballModel == null)
        {
            ballModel = Planet.INSTANCE.modelBuilder.createSphere(size, size, size, 16, 16,
                new Material(new ColorAttribute(ColorAttribute.Diffuse, new Color())),
                Usage.Position | Usage.Normal);

            final BulletConstructor ballConstructor = new BulletConstructor(ballModel, 5f, new btSphereShape(size / 2f));

            ballConstructor.bodyInfo.setRestitution(1f);
            Planet.INSTANCE.world.addConstructor("ball", ballConstructor);
        }

        entity = Planet.INSTANCE.world.add("ball", pos.x, pos.y, pos.z);
        entity.modelInstance.materials.get(0).set(new Material(new ColorAttribute(ColorAttribute.Diffuse, color)));
        
        addToBalls();
    }

    public void addToBalls() {
        Planet.INSTANCE.level.balls.add(this);
    }

    public void removeFromBalls() {
        Planet.INSTANCE.level.balls.removeValue(this, true);
    }

    public void dispose () {
        Planet.INSTANCE.world.remove(entity);
        Planet.INSTANCE.world.collisionWorld.removeCollisionObject(entity.body);

        removeFromBalls();
        
        entity.dispose();
    }

    public String getSaveLine() {
        return name + ","+ pos.x + "," + pos.y + "," + pos.z + ","
            + size + ","
            + color.r + "," + color.g + "," + color.b + "," + color.a;
    }

    public static void loadFromLine(String line) {
        String[] ls = line.split(",");
        new Ball(
            new Vector3(
                Float.parseFloat(ls[1]),
                Float.parseFloat(ls[2]),
                Float.parseFloat(ls[3])),
            Integer.parseInt(ls[4]),
            new Color(
                Float.parseFloat(ls[5]),
                Float.parseFloat(ls[6]),
                Float.parseFloat(ls[7]),
                Float.parseFloat(ls[8]))
        );
    }
}