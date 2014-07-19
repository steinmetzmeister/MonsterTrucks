package com.allsrc.monstertrucks;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;

public class MonsterTruck extends Car {

    public MonsterTruck() {
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (axisCode == 0) {
            rightPressed = (value > 0.25) ? true : false;
            leftPressed = (value < -0.25) ? true : false;
        }
        return false;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (buttonCode == 1)
            upPressed = true;

        if (buttonCode == 0)
            downPressed = true;

        if (buttonCode == 2) {}
            // vehicle.getRigidBody().applyCentralImpulse(new Vector3(0, 1000f, 0));

        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (buttonCode == 3)
            reset();

        if (buttonCode == 1)
            upPressed = false;

        if (buttonCode == 0)
            downPressed = false;

        return false;
    }
}