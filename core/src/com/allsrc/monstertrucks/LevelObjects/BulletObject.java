package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

public class BulletObject {
    public BulletEntity entity;

    public String[] attrs;

    public String name;
    public Color color = new Color();
    public String modelFile;
    public Vector3 pos = new Vector3(0, 0, 0);
    public Vector3 rot = new Vector3(0, 0, 0);
    public Vector3 size = new Vector3(1f, 1f, 1f);
    public float scale = 1f;

    public static Model model;
    public btBvhTriangleMeshShape meshShape;

    public String textureFile;
    public static Texture texture;
    public static TextureAttribute textureAttribute;

    public static void addDefaultConstructor(String name) {
        Planet.EX.loader.set(name);

        addConstructor(name,
            Planet.EX.loader.getModel(),
            new btBvhTriangleMeshShape(Planet.EX.loader.getModel().meshParts));
    }

    public static void addConstructor(String name, Model model, btBvhTriangleMeshShape meshShape) {
        Planet.EX.world.addConstructor(name, new BulletConstructor(model, 0f, meshShape));
    }

    public static void addConstructor(String name, Model model, btSphereShape meshShape) {
        Planet.EX.world.addConstructor(name, new BulletConstructor(model, 0f, meshShape));
    }

    public void entity() {
        entity = Planet.EX.world.add(name, 0f, 0f, 0f);
        addToBulletObjects(this);
    }

    //

    public void randomRot() {
        setRot((int)(Math.random() * 360));
        updateRot();
        updatePos();
    }

    public void scale(float to) {
        //
    }

    //

    public Color getColor() {
        return color;
    }

    public void setColor(String[] c) {
        this.color = new Color(Float.parseFloat(c[0]), Float.parseFloat(c[1]), Float.parseFloat(c[2]), Float.parseFloat(c[3]));
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(String[] p) {
        setPos(Float.parseFloat(p[0]), Float.parseFloat(p[1]), Float.parseFloat(p[2]));
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
        
    }

    public void setPos(float x, float y, float z) {
        pos = new Vector3(x, y, z);
    }

    public float getRot() {
        return rot.y;
    }

    public void setRot(String[] r) {
        rot.y = Float.parseFloat(r[1]);
    }

    public void setRot(int angle) {
        rot.y += angle;
    }

    public void setRot(float angle) {
        rot.y += (int)angle;
    }


    public void setSize(String[] s) {
        size = new Vector3(Float.parseFloat(s[0]), Float.parseFloat(s[1]), Float.parseFloat(s[2]));
    }

    public void setSize(Vector3 size) {
        this.size = size;
    }

    //

    public void updateColor() {
        entity.modelInstance.materials.get(0).set(
            ColorAttribute.createDiffuse(color),
            ColorAttribute.createSpecular(Color.WHITE));
    }

    public void updatePos() {
        entity.transform.setTranslation(pos);
        entity.body.setWorldTransform(entity.transform); 
    }

    public void updateRot() {
        entity.transform.setToRotation(Vector3.Y, rot.y);
    }

    public void updateTexture() {
        TextureAttribute textureAttribute = new TextureAttribute(
            TextureAttribute.Diffuse,
            Planet.EX.loader.objects.get(name).texture);

        entity.modelInstance.materials.get(0).set(textureAttribute);
    }

    public void dispose() {
        Planet.EX.world.remove(entity);
        Planet.EX.world.collisionWorld.removeCollisionObject(entity.body);
        entity.dispose();

        removeFromBulletObjects(this);
    }

    public String getSaveLine() {
        String line = name;
        for (String attr : attrs) {
            line += " ";

            if (attr == "color") line += getSaveColor();
            else if (attr == "pos") line += getSavePos();
            else if (attr == "rot") line += getSaveRot();
            else if (attr == "size") line += getSaveSize();
        }

        return line;
    }

    public String getSaveColor() {
        return color.r + "," + color.g + "," + color.b + "," + color.a;
    }

    public String getSaveModelFile() {
        return modelFile;
    }

    public String getSavePos() {
        return pos.x + "," + pos.y + "," + pos.z;
    }

    public String getSaveRot() {
        return rot.x + "," + rot.y + "," + rot.z;
    }

    public String getSaveSize() {
        return size.x + "," + size.y + "," + size.z;
    }

    public void loadFromLine(String line) {
        String[] lineSplit = line.split(" ");

        int i = 1; // after name
        for (String attr : attrs) {
            String[] attrSplit = lineSplit[i].split(",");

            if (attr == "color") setColor(attrSplit);
            else if (attr == "pos") setPos(attrSplit);
            else if (attr == "rot") setRot(attrSplit);
            else if (attr == "size") setSize(attrSplit);

            i++;
        }
    }

    public static void addToBulletObjects(BulletObject object) {
        Planet.EX.level.bulletObjects.add(object);
    }

    public static void removeFromBulletObjects(BulletObject object) {
        Planet.EX.level.bulletObjects.removeValue(object, true);
    }

    public void noResponse() {
        entity.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
    }
}