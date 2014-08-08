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
        Planet.EX.loader.add("track");
        Planet.EX.loader.loadModel(modelFile);
        Planet.EX.loader.loadTexture(textureFile);
        Planet.EX.loader.getModel().meshes.get(0).scale(4f, 4f, 4f);
        addDefaultConstructor("track");
    }
}