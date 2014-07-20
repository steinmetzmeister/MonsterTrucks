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
    Vector3[][] objs;

    public void saveToFile() {
        try {
            File file = new File("output.txt");  
            FileWriter writer = new FileWriter(file);  
            PrintWriter out = new PrintWriter(writer);

            // out.print("hello, world");  

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
                System.out.println(in.readLine());
            }

            in.close();
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }
}