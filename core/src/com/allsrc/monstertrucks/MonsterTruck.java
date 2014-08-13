package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class MonsterTruck extends Car {
    public MonsterTruck(Vector3 pos, Color color) {
        super(pos, color);

        maxForce = 50f;
        acceleration = 100f; // second

        chassisModelFile = "data/car2.obj";
        wheelModelFile = "data/wheel.obj";
        wheelScale = new Vector3(1f, 0.75f, 0.75f);

        init();
    }
}