package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

import com.badlogic.gdx.Input.Keys;

public class MonsterListener extends MonsterListenerBase {
    
    int startedX = 0;
    int startedY = 0;

    @Override
    public boolean keyDown (int keycode) {
        switch (keycode) {
            case Keys.SPACE:
                //
                break;
            case Keys.W:
                Planet.EX.cars.get(0).upPressed = true;
                break;
            case Keys.D:
                Planet.EX.cars.get(0).rightPressed = true;
                break;
            case Keys.S:
                Planet.EX.cars.get(0).downPressed = true;
                break;
            case Keys.A:
                Planet.EX.cars.get(0).leftPressed = true;
                break;
            case Keys.P:
                Planet.EX.cars.get(0).pause();
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {

        switch (keycode) {
            case Keys.R:
                //
                break;
            case Keys.W:
                Planet.EX.cars.get(0).upPressed = false;
                break;
            case Keys.D:
                Planet.EX.cars.get(0).rightPressed = false;
                break;
            case Keys.S:
                Planet.EX.cars.get(0).downPressed = false;
                break;
            case Keys.A:
                Planet.EX.cars.get(0).leftPressed = false;
                break;

            case Keys.Q:
                Planet.EX.level.tb.randomizeColors();
                for (Car car : Planet.EX.cars)
                    car.randomizeColors();
                break;
        }

        return false;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (MonsterUtils.isMobile()) {
            startedX = screenX;
            startedY = screenY;

            if (screenY < 200) {
                if (screenX < Planet.EX.settings.width / 2) {
                    Planet.EX.cars.get(0).reset();
                } else {
                    Planet.EX.editor.deselect();
                    Planet.EX.level.clearLevel();
                    Planet.EX.level.loadFromFile();
                }

                return false;
            }

            if (screenX > Planet.EX.settings.width / 2)
                Planet.EX.cars.get(0).downPressed = true;
            else
                Planet.EX.cars.get(0).upPressed = true;
        }

        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (MonsterUtils.isMobile()) {

            startedX = 0;
            startedY = 0;
        
            Planet.EX.cars.get(0).currentAngle = 0f;
            Planet.EX.cars.get(0).upPressed = false;
            Planet.EX.cars.get(0).downPressed = false;
        }

        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (MonsterUtils.isMobile()) {
            if (startedX != 0) {
                int x = (screenX - startedX) * -1;

                // off terrain rotation
                Planet.EX.cars.get(0).horzAxis = MonsterUtils.map(x, -300, 300, 1, -1);

                if (x > 200) x = 200;
                if (x < -200) x = -200;

                // steering
                Planet.EX.cars.get(0).currentAngle = MonsterUtils.map(x, -200f, 200f,
                    -Planet.EX.cars.get(0).maxAngle,
                    Planet.EX.cars.get(0).maxAngle);
            }

            // covers off terrain rotation
            if (startedY != 0) {
                int y = (screenY - startedY) * -1;
                Planet.EX.cars.get(0).vertAxis = MonsterUtils.map(y, -300, 300, 1, -1);
            }
        }

        return false;
    }
}