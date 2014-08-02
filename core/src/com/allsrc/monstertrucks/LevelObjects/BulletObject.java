package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

public class BulletObject extends LevelObject {
    public BulletEntity entity;

    // color,pos,rot,size,scale

    public String[] attrs;

    public String name;
    public Color color = new Color();
    public String modelFile;
    public Vector3 pos = new Vector3(0, 0, 0);
    public Vector3 rot = new Vector3(0, 0, 0);
    public Vector3 size = new Vector3(1f, 1f, 1f);
    public float scale = 1f;

    public Model model;
    public btBvhTriangleMeshShape meshShape;

    public void construct() {
        if (model == null) {
            model = getModel(modelFile);
            meshShape = getMeshShape(model);

            addConstructor(name, model, meshShape);
        }

        init(name);
        addToBulletObjects(this);
    }

    public void init(String name) {
        entity = Planet.INSTANCE.world.add(name, 0f, 0f, 0f);
    }

    public void entity() {
        entity = Planet.INSTANCE.world.add(name, 0f, 0f, 0f);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Model getModel(String file) {
        Model model = Planet.INSTANCE.objLoader.loadModel(Gdx.files.internal(file));
        model.meshes.get(0).scale(scale, scale, scale);

        return model;
    }

    public btBvhTriangleMeshShape getMeshShape(Model model) {
        return new btBvhTriangleMeshShape(model.meshParts);
    }

    public void addConstructor(String name, Model model, btBvhTriangleMeshShape meshShape) {
        Planet.INSTANCE.world.addConstructor(name, new BulletConstructor(model, 0f, meshShape));
    }

    public void addConstructor(String name, Model model, btSphereShape meshShape) {
        Planet.INSTANCE.world.addConstructor(name, new BulletConstructor(model, 0f, meshShape));
    }

    public void setModelFile(String mf) {
        modelFile = mf;
    }

    //

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(String[] p) {
        setPos(Float.parseFloat(p[0]), Float.parseFloat(p[1]), Float.parseFloat(p[2]));
    }

    public void setPos(float x, float y, float z) {
        pos = new Vector3(x, y, z);
    }

    public void setPos(Vector3 p) {
        pos = p;
        
    }

    public void updatePos() {
        entity.transform.setTranslation(pos);
        entity.body.setWorldTransform(entity.transform);
    }

    //

    public void setRot(String[] r) {
        setRot(Integer.parseInt(r[1]));
    }

    public void setRot(int angle) {
        rot.y = angle;
        entity.transform.rotate(Vector3.Y, rot.y);
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

    //

    public void setSize(String[] s) {
        size = new Vector3(Float.parseFloat(s[0]), Float.parseFloat(s[1]), Float.parseFloat(s[2]));
    }

    public void setSize(Vector3 s) {
        size = s;
    }

    public void updateColor() {
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
        String line = "";
        int i = 0;
        for (String attr : attrs) {
            if (i > 0) line += ",";

            if (attr == "color") line += getSaveColor();
            else if (attr == "modelFile") line += getSaveModelFile();
            else if (attr == "pos") line += getSavePos();
            else if (attr == "rot") line += getSaveRot();
            else if (attr == "size") line += getSaveSize();

            i++;
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
            else if (attr == "modelFile") setModelFile(attrSplit[0]);
            else if (attr == "pos") setPos(attrSplit);
            else if (attr == "rot") setRot(attrSplit);
            else if (attr == "size") setSize(attrSplit);

            i++;
        }
    }

    public static void addToBulletObjects(BulletObject object) {
        Planet.INSTANCE.level.bulletObjects.add(object);
    }

    public static void removeFromBulletObjects(BulletObject object) {
        Planet.INSTANCE.level.bulletObjects.removeValue(object, true);
    }
}