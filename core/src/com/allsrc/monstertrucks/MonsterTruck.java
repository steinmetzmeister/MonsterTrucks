package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;

public class MonsterTruck extends Car {

    protected float maxForce = 100f;
    protected float acceleration = 200f; // second

    public MonsterTruck() {
    }

    @Override
    protected void loadModels() {
        // chassis
        chassisModel = objLoader.loadModel(Gdx.files.internal("data/truck.obj"));
        Planet.INSTANCE.disposables.add(chassisModel);
        chassisModel.materials.get(0).clear();
        chassisModel.materials.get(0).set(ColorAttribute.createDiffuse(Color.RED), ColorAttribute.createSpecular(Color.WHITE));

        // wheel
        wheelModel = objLoader.loadModel(Gdx.files.internal("data/wheel.obj"));
        Planet.INSTANCE.disposables.add(wheelModel);
        wheelModel.materials.get(0).clear();
        wheelModel.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK), ColorAttribute.createSpecular(Color.WHITE));
        wheelModel.meshes.get(0).scale(3f, 2f, 2f);
    }
}