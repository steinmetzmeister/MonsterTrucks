package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import java.util.HashMap;

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
        addToBulletObjects(this);
    }

    public static void load() {
        Planet.EX.loader.add("straight");
        Planet.EX.loader.loadModel("data/road.obj");

        HashMap<String,Color> colors = new HashMap<String,Color>();

        colors.put("ground", MonsterColor.randomColor());
        colors.put("border1", MonsterColor.randomColor());
        colors.put("border2", MonsterColor.randomColor());
        colors.put("road", MonsterColor.randomColor());

        for (int i = 0; i < Planet.EX.loader.getModel().meshes.size; i++)
        {
            String id = Planet.EX.loader.getModel().materials.get(i).id;

            int j = id.indexOf(".");
            if (j != -1)
                id = id.substring(0, j);

            Planet.EX.loader.getModel().materials.get(i).set(ColorAttribute.createDiffuse(colors.get(id)));
        }

        addDefaultConstructor("straight");

        Planet.EX.loader.add("turn");
        Planet.EX.loader.loadModel("data/roadbend.obj");

        for (int i = 0; i < Planet.EX.loader.getModel().meshes.size; i++)
        {
            String id = Planet.EX.loader.getModel().materials.get(i).id;

            int j = id.indexOf(".");
            if (j != -1)
                id = id.substring(0, j);

            Planet.EX.loader.getModel().materials.get(i).set(ColorAttribute.createDiffuse(colors.get(id)));
        }

        addDefaultConstructor("turn");
    }
}