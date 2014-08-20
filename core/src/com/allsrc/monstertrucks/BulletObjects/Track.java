package com.allsrc.monstertrucks;

public class Track extends BulletObject {

    public void init() {
        name = "track";
        attrs = new String[]{ "pos" };
    }

    public Track(String line) {
        init();
        loadFromLine(line);
    }

    public Track() {
        init();
    }

    public void construct(String type) {
        name = type;
        entity(type);
    }

    public void entity(String type) {
        entity = Planet.EX.world.add(type, 0f, 0f, 0f);
        // addToBulletObjects(this);
    }

    public static void load() {
        Planet.EX.loader.add("straight");
        Planet.EX.loader.loadModel("data/road.obj");
        Planet.EX.loader.loadMeshShape();
        addDefaultConstructor("straight");

        Planet.EX.loader.add("turn");
        Planet.EX.loader.loadModel("data/roadbend.obj");
        Planet.EX.loader.loadMeshShape();
        addDefaultConstructor("turn");
    }
}