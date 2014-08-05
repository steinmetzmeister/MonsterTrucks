package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;

public class Track extends BulletObject {

    public void init() {
        name = "track";
        attrs = new String[]{ "pos" };
    }

    public Track(String line) {
        init();
        loadFromLine(line);
        construct();
    }

    public Track(Vector3 pos) {
        init();
        setPos(pos);
        construct();
    }

    public void construct() {
        entity();
        updateTexture();
    }

    public static void load(String modelFile, String textureFile) {
        Planet.INSTANCE.loader.add("track");
        Planet.INSTANCE.loader.loadModel(modelFile);
        Planet.INSTANCE.loader.loadTexture(textureFile);
        Planet.INSTANCE.loader.getModel().meshes.get(0).scale(4f, 4f, 4f);
        addDefaultConstructor("track");
    }
}