package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class MonsterTruck extends Car {
    public MonsterTruck(Vector3 pos, Color color) {
        super(pos, color);

        maxForce = 200f;
        acceleration = 300f; // second

        chassisModelFile = "data/truck.obj";
        wheelScale = new Vector3(3f, 2.5f, 2.5f);

        init();
    }
}