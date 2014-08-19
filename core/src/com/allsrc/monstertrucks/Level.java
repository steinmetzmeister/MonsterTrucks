package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Level {
    Array<BulletObject> bulletObjects = new Array<BulletObject>();
    Array<Collectible> collectibles = new Array<Collectible>();
    Array<Trigger> triggers = new Array<Trigger>();
    Checkpoints checkpoints = new Checkpoints();
    Array<Vector2> path = new Array<Vector2>();
    
    Terrain terrain;
    Track track;
    public Environment environment;
    public DirectionalLight light;

    public Level() {
        load();
    }

    public void load() {
        Ball.load();
        Checkpoint.load();
        ColorChanger.load();
        Coin.load();
        Gate.load();
        SmallRamp.load();
        LargeRamp.load();
    }

    public TrackBuilder tb = new TrackBuilder();    

    public void init() {
        Terrain.load("data/terrain2.obj");
        terrain = new Terrain(MonsterColor.randomColor());

        Track.load();

        tb.straight();
        tb.straight();
        Planet.EX.level.path.add(tb.turn(0));
        tb.straight();
        tb.straight();
        Planet.EX.level.path.add(tb.turn(0));
        tb.straight();
        tb.straight();
        Planet.EX.level.path.add(tb.turn(1));
        Planet.EX.level.path.add(tb.turn(0));
        tb.straight();
        tb.straight();
        Planet.EX.level.path.add(tb.turn(0));
        tb.straight();
        Planet.EX.level.path.add(tb.turn(1));
        Planet.EX.level.path.add(tb.turn(0));
        tb.straight();
        Planet.EX.level.path.add(tb.turn(0));
        tb.straight();
        tb.straight();
        tb.straight();
        tb.straight();
    }

    public void clearLevel() {
        collectibles = new Array<Collectible>();
        triggers = new Array<Trigger>();
        checkpoints = new Checkpoints();

        for (int j = bulletObjects.size - 1; j >= 0; j--)
            bulletObjects.get(j).dispose();

        bulletObjects = new Array<BulletObject>();

        // terrain.dispose();
        // terrain = null;
    }

    public void saveToFile() {
        try {
            File file = new File("data/output.txt");  
            FileWriter writer = new FileWriter(file);  
            PrintWriter out = new PrintWriter(writer);

            for (BulletObject bulletObject : bulletObjects) {
                out.println(bulletObject.getSaveLine());
            }

            out.close();
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }

    public void loadFromFile() {
        FileHandle file = Gdx.files.internal("data/output.txt");
        String[] lines = file.readString().split("\n");

        // Terrain.load("data/terrain.obj");
        // terrain = new Terrain(new Color(0, 0.65f, 0, 1));

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        
        light = new DirectionalLight();
        light.set(1f, 1f, 1f, 0.65f, -0.9f, 0.65f);

        environment.add(light);

        BulletObject obj = null;
        for (String line : lines) {
            obj = Planet.EX.editor.createObject(line);

            if (obj != null) {
                if (obj.name == "checkpoint")
                    checkpoints.add((Checkpoint)obj);
            }
        }
    }
}