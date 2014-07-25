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
    Array<ColorChanger> changers = new Array<ColorChanger>();
    Array<Collectible> collectibles = new Array<Collectible>();
    Array<Trigger> triggers = new Array<Trigger>();
    Array<Ball> balls = new Array<Ball>();

    public Level() {
    }

    public void clearLevel() {
        for (int j = changers.size - 1; j >= 0; j--)
            changers.get(j).dispose();

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

            for (ColorChanger changer : changers) {
                out.println(changer.getSaveLine());
            }

            for (Trigger trigger : triggers) {
                out.println(trigger.getSaveLine());
            }

            for (Ball ball : balls) {
                out.println(ball.getSaveLine());
            }

            for (Collectible collectible : collectibles) {
                out.println(collectible.getSaveLine());
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

            String line;
            String word;
            while (in.ready()) {
                line = in.readLine();
                word = line.substring(0, line.indexOf(','));
                
                if (word.equals("checkpoint"))
                    ColorChanger.loadFromLine(line);

                else if (word.equals("trigger"))
                    Trigger.loadFromLine(line);

                else if (word.equals("ball"))
                    Ball.loadFromLine(line);

                else if (word.equals("coin"))
                    Coin.loadFromLine(line);
            }

            in.close();
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }
}