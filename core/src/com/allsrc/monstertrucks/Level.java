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

    public Level() {
    }

    public void saveToFile() {
        try {
            File file = new File("output.txt");  
            FileWriter writer = new FileWriter(file);  
            PrintWriter out = new PrintWriter(writer);

            for (Checkpoint checkpoint : Planet.INSTANCE.level.checkpoints) {
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
                String[] checkpoint = in.readLine().split(",");
                Planet.INSTANCE.level.checkpoints.add(new Checkpoint(new Vector3(
                    Float.parseFloat(checkpoint[1]),
                    Float.parseFloat(checkpoint[2]),
                    Float.parseFloat(checkpoint[3]))));
            }

            in.close();
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }
}