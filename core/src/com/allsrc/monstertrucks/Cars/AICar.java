package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.utils.Array;

public class AICar extends Car {

    public AICar(Vector3 pos, Color color) {
        super(pos, color);

        maxForce = 50f;
        acceleration = 100f; // second

        chassisModelFile = "data/cars/rally.obj";
        wheelModelFile = "data/wheel.obj";
        wheelScale = new Vector3(1f, 0.75f, 0.75f);

        init();
    }

    int seek = 0;

    public void update() {
        delta = Gdx.graphics.getDeltaTime();

        seek = seek();

        vehicle.setSteeringValue(seek * 15 * MathUtils.degreesToRadians, 0);
        vehicle.setSteeringValue(seek * 15 * MathUtils.degreesToRadians, 1);
        force = MathUtils.clamp(force + acceleration * delta, 0f, maxForce);
        vehicle.applyEngineForce(force, 0);
        vehicle.applyEngineForce(force, 1);
        // vehicle.applyEngineForce(force, 2);
        // vehicle.applyEngineForce(force, 3);

        isOnGround = true;

        for (int i = 0; i < wheels.length; i++) {
            vehicle.updateWheelTransform(i, true);
            wheelInfo[i].getOpenGLMatrix(wheels[i].transform.val);
        }
    }

    Vector3 f = new Vector3(0,0,0);
    Vector3 p = new Vector3(0,0,0);

    Vector2 t2 = new Vector2();
    Vector2 f2 = new Vector2();
    Vector2 p2 = new Vector2();

    float dot = 0;
    double angle = 0;

    int currNode = 0;
    
    public int seek() {
        t2.set(Planet.EX.level.path.get(currNode));

        f = vehicle.getForwardVector();
        entity.transform.getTranslation(p);

        f2.x = f.x;
        f2.y = f.z;

        p2.x = p.x;
        p2.y = p.z;

        t2 = t2.sub(p2);

        dot = f2.dot(t2);
        angle = Math.acos(dot / (f2.len() * t2.len()));

        if (p2.dst(Planet.EX.level.path.get(currNode)) < 5) {
            currNode++;
            if (currNode >= Planet.EX.level.path.size)
                currNode = 0;
        }

        if (angle * MathUtils.radiansToDegrees <= 5)
            return 0;

        return (f2.x * -t2.y + f2.y * t2.x > 0) ? 1 : -1;
    }
}