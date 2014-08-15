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

        chassisModelFile = "data/car1.obj";
        wheelModelFile = "data/wheel.obj";
        wheelScale = new Vector3(1f, 0.75f, 0.75f);

        init();

        path.add(new Vector2(0, 16));
        path.add(new Vector2(24, 16));
        path.add(new Vector2(24, -8));
        path.add(new Vector2(0, -8));
    }

    public void update() {
        delta = Gdx.graphics.getDeltaTime();

        vehicle.setSteeringValue(seek() * 15f * MathUtils.degreesToRadians, 0);
        vehicle.setSteeringValue(seek() * 15f * MathUtils.degreesToRadians, 1);
        force = MathUtils.clamp(force + acceleration * delta, 0f, maxForce);
        vehicle.applyEngineForce(force, 0);
        vehicle.applyEngineForce(force, 1);
        vehicle.applyEngineForce(force, 2);
        vehicle.applyEngineForce(force, 3);

        isOnGround = false;

        for (int i = 0; i < wheels.length; i++) {
            vehicle.updateWheelTransform(i, true);
            vehicle.getWheelInfo(i).getWorldTransform().getOpenGLMatrix(wheels[i].transform.val);

            if (vehicle.getWheelInfo(i).getRaycastInfo().getGroundObject() != 0)
                isOnGround = true;
        }

        // seek();
    }

    Vector3 f = new Vector3(0,0,0);
    Vector3 p = new Vector3(0,0,0);

    Vector2 t2 = new Vector2();
    Vector2 f2 = new Vector2();
    Vector2 p2 = new Vector2();

    int currNode = 0;
    Array<Vector2> path = new Array<Vector2>();
    
    public int seek() {
        t2 = path.get(currNode).cpy();

        f = vehicle.getForwardVector().cpy();
        entity.transform.getTranslation(p);

        f2 = new Vector2(f.x, f.z);
        p2 = new Vector2(p.x, p.z);

        t2 = t2.sub(p2);

        float dot = f2.dot(t2);
        double angle = Math.acos(dot / (f2.len() * t2.len()));

        if (p2.dst(path.get(currNode)) < 5) {
            currNode++;
            if (currNode >= path.size)
                currNode = 0;
        }

        if (angle * MathUtils.radiansToDegrees <= 5)
            return 0;

        return (f2.x * -t2.y + f2.y * t2.x > 0) ? 1 : -1;
    }
}