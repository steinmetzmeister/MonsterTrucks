package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class Level {
    Array<Checkpoint> checkpoints = new Array<Checkpoint>();
    Array<Collectible> collectibles = new Array<Collectible>();
    Array<Trigger> triggers = new Array<Trigger>();
    Array<Ball> balls = new Array<Ball>();

    public Level() {
    }

    public void clearLevel() {
        for (int j = checkpoints.size - 1; j >= 0; j--)
            checkpoints.get(j).dispose();

        for (int j = collectibles.size - 1; j >= 0; j--)
            collectibles.get(j).dispose();

        for (int j = triggers.size - 1; j >= 0; j--)
            triggers.get(j).dispose();

        for (int j = balls.size - 1; j >= 0; j--)
            balls.get(j).dispose();
    }

    public void saveToFile() {
        try {
            File file = new File("output.txt");  
            FileWriter writer = new FileWriter(file);  
            PrintWriter out = new PrintWriter(writer);

            for (Checkpoint checkpoint : checkpoints) {
                out.println(checkpoint.name + ','
                    + checkpoint.pos.x + ','
                    + checkpoint.pos.y + ','
                    + checkpoint.pos.z);
            }

            out.close();
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }

    public void loadFromFile() {
        try {
            File file = new File("output.txt");  
            BufferedReader in = new BufferedReader(new FileReader(file));

            while (in.ready()) {
                Checkpoint.loadFromLine(in.readLine());
            }

            in.close();
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }
}