package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class AICar extends Car {

    public AICar(Vector3 pos, String color) {
        super(pos, color);

        maxForce = 50f;
        acceleration = 100f; // second

        name = "parsche";
        chassisName = "parsche";
        wheelName = "parsche_wheel";

        wheelScale = new Vector3(1f, 0.75f, 0.75f);

        init();
    }

    private int seekValue = 0;

    public void update() {
        delta = Gdx.graphics.getDeltaTime();

        seekValue = seek();

        vehicle.setSteeringValue(seekValue * 15 * MathUtils.degreesToRadians, 0);
        vehicle.setSteeringValue(seekValue * 15 * MathUtils.degreesToRadians, 1);
        force = MathUtils.clamp(force + acceleration * delta, 0f, maxForce);
        vehicle.applyEngineForce(force, 0);
        vehicle.applyEngineForce(force, 1);
        // vehicle.applyEngineForce(force, 2);
        // vehicle.applyEngineForce(force, 3);

        isOnGround = false;

        for (int i = 0; i < wheels.length; i++) {
            vehicle.updateWheelTransform(i, true);
            wheelTransform[i].getOpenGLMatrix(wheels[i].transform.val);
            // allocation problem
            // if (wheelInfo[i].getRaycastInfo().getGroundObject() != 0)
            //     isOnGround = true;
        }
    }

    private Vector3 f = new Vector3(0,0,0);
    private Vector3 p = new Vector3(0,0,0);

    private Vector2 t2 = new Vector2();
    private Vector2 f2 = new Vector2();
    private Vector2 p2 = new Vector2();

    private float dot = 0;
    private double angle = 0;

    private int currNode = 0;
    
    private int seek() {
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