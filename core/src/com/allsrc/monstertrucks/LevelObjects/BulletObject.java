package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;

public class BulletObject extends LevelObject {
    public BulletEntity entity;
    public Color color;
    public float scale = 1f;

    public void init(String name) {
        entity = Planet.INSTANCE.world.add(name, 0f, 0f, 0f);
        addToBulletObjects(this);
    }

    public Model getModel(String file) {
        Model model = Planet.INSTANCE.objLoader.loadModel(Gdx.files.internal(file));
        model.meshes.get(0).scale(scale, scale, scale);

        return model;
    }

    public btBvhTriangleMeshShape getMeshShape(Model model) {
        return new btBvhTriangleMeshShape(model.meshParts);
    }

    public void setPos(float x, float y, float z) {
        setPos(new Vector3(x, y, z));
    }

    public void setPos(Vector3 newPos) {
        pos = newPos;
        entity.transform.setTranslation(newPos);
        entity.body.setWorldTransform(entity.transform);
    }

    public void setRot(int angle) {
        rot = angle;
        entity.transform.rotate(Vector3.Y, rot);
    }

    public void setColor(Color color) {
        color = color;

        entity.modelInstance.materials.get(0).set(
            ColorAttribute.createDiffuse(color),
            ColorAttribute.createSpecular(Color.WHITE));
    }

    public void dispose() {
        Planet.INSTANCE.world.remove(entity);
        Planet.INSTANCE.world.collisionWorld.removeCollisionObject(entity.body);
        entity.dispose();

        removeFromBulletObjects(this);
    }

    public String getSaveLine() {
        return name + "," + pos.x + "," + pos.y + "," + pos.z + ","
            + rot + ","
            + color.r + "," + color.g + "," + color.b + "," + color.a;
    }

    public static void addToBulletObjects(BulletObject object) {
        Planet.INSTANCE.level.bulletObjects.add(object);
    }

    public static void removeFromBulletObjects(BulletObject object) {
        Planet.INSTANCE.level.bulletObjects.removeValue(object, true);
    }
}