package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

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

public class Level {
    Array<ColorChanger> changers = new Array<ColorChanger>();
    Array<Collectible> collectibles = new Array<Collectible>();
    Array<Trigger> triggers = new Array<Trigger>();
    Array<Ball> balls = new Array<Ball>();
    Array<BulletObject> bulletObjects = new Array<BulletObject>();
    Terrain terrain;

    public Level() {
    }

    public void clearLevel() {
        for (int j = changers.size - 1; j >= 0; j--)
            changers.get(j).dispose();

        for (int j = collectibles.size - 1; j >= 0; j--)
            collectibles.get(j).dispose();

        for (int j = bulletObjects.size - 1; j >= 0; j--)
            bulletObjects.get(j).dispose();
    }

    public void saveToFile() {
        try {
            File file = new File("data/output.txt");  
            FileWriter writer = new FileWriter(file);  
            PrintWriter out = new PrintWriter(writer);

            for (ColorChanger changer : changers) {
                out.println(changer.getSaveLine());
            }

            for (Collectible collectible : collectibles) {
                out.println(collectible.getSaveLine());
            }

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

            String word;
            for (String line : lines) {
                word = line.substring(0, line.indexOf(' '));

                if (word.equals("ball"))
                    new Ball(line);

                else if (word.equals("changer"))
                    new ColorChanger(line);

                else if (word.equals("checkpoint"))
                    new Checkpoint(line);

                else if (word.equals("coin"))
                    new Coin(line);

                else if (word.equals("gate"))
                    new Gate(line);

                else if (word.equals("terrain"))
                    terrain = new Terrain(line);

                else if (word.equals("trigger"))
                    new Trigger(line);
            }

    }

    public void load() {

    }
}