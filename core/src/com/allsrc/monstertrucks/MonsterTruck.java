package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class MonsterTruck extends Car {
    public void init() {
        maxForce = 100f;
        acceleration = 200f; // second

        chassisModelFile = "data/complexCar.obj";
        wheelScale = new Vector3(3f, 2.5f, 2.5f);

        super.init();
    }

    public void update() {
        super.update();
    }
}