package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class Loader {
    public class Object {
        Model model;
        Sound sound;
        Texture texture;
    }

    public HashMap<String,Object> objects = new HashMap<String,Object>();
    String name;

    public void set(String name) {
        this.name = name;
    }

    public void add(String name) {
        objects.put(name, new Object());
        set(name);
    }

    public void remove(String name) {
        objects.get(name).model = null;
        objects.get(name).sound = null;
        objects.get(name).texture = null;

        objects.remove(name);

        if (this.name == name) {
            name = null;
        }
    }

    public void loadModel(String file) {
        objects.get(name).model = Planet.INSTANCE.objLoader.loadModel(Gdx.files.internal(file));
    }

    public void loadSound(String file) {
        objects.get(name).sound = Gdx.audio.newSound(Gdx.files.internal(file));
    }

    public void loadTexture(String file) {
        objects.get(name).texture = new Texture(Gdx.files.internal(file), true);
    }

    public Model getModel() {
        return objects.get(name).model;
    }

    public Sound getSound() {
        return objects.get(name).sound;
    }

    public Texture getTexture() {
        return objects.get(name).texture;
    }
}