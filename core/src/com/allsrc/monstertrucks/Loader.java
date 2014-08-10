package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.Vector3;
import java.util.HashMap;

public class Loader {
    public class Object {
        Model model;
        Sound sound;
        Texture texture;

        Vector3 center = new Vector3();
        float radius;
    }

    protected Object obj;
    protected ObjLoader objLoader = new ObjLoader();
    public HashMap<String,Object> objects = new HashMap<String,Object>();

    String name;

    public void set(String name) {
        this.name = name;
        this.obj = objects.get(name);
    }

    public void add(String name) {
        if (!objects.containsKey(name))
            objects.put(name, new Object());
        set(name);
    }

    public void remove(String name) {
        obj.model = null;
        obj.sound = null;
        obj.texture = null;

        objects.remove(name);

        if (this.name == name) {
            name = null;
        }
    }

    public Vector3 getCenter() {
        return obj.center;
    }

    public float getRadius() {
        return obj.radius;
    }

    private Vector3 dimensions = new Vector3();
    private BoundingBox bounds = new BoundingBox();

    public void loadModel(String file) {
        obj.model = objLoader.loadModel(Gdx.files.internal(file));
        
        obj.model.calculateBoundingBox(bounds);
        obj.center.set(bounds.getCenter());
        dimensions.set(bounds.getDimensions());
        obj.radius = dimensions.len() / 2f;
    }

    public void loadSound(String file) {
        obj.sound = Gdx.audio.newSound(Gdx.files.internal(file));
    }

    public void loadTexture(String file) {
        obj.texture = new Texture(Gdx.files.internal(file), true);
    }

    public Model getModel() {
        return obj.model;
    }

    public Sound getSound() {
        return obj.sound;
    }

    public Texture getTexture() {
        return obj.texture;
    }
}