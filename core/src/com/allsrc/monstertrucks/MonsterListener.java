package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

import com.badlogic.gdx.Input.Keys;

public class MonsterListener extends MonsterListenerBase {
    ClosestRayResultCallback rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();

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
        }
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {

        if (keycode >= 8 && keycode <= 12) {
            Planet.EX.main.editor.setActiveObject(keycode - 8);

            return false;
        }

        switch (keycode) {
            case Keys.R:
                //
                break;
            case Keys.C:
                Planet.EX.main.editor.deselect();
                Planet.EX.level.clearLevel();
                break;
            case Keys.L:
                Planet.EX.main.editor.deselect();
                Planet.EX.level.saveToFile();
                break;
            case Keys.O:
                Planet.EX.main.editor.deselect();
                Planet.EX.level.loadFromFile();
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
        }

        Planet.EX.main.editor.keyUp(keycode);

        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        Planet.EX.main.editor.scroll(amount);
        return false;
    }

    Vector3 vTemp = new Vector3(0,0,0);

    public void touchDownDesktop (int screenX, int screenY, int pointer, int button) {
        if (Planet.EX.settings.playerCount == 2)
            screenY = splitScreenCorrection(screenY);

        Ray ray = Planet.EX.camera.getPickRay(screenX, screenY);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(50f).add(rayFrom);

        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.setRayFromWorld(rayFrom);
        rayTestCB.setRayToWorld(rayTo);

        Planet.EX.world.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);

        if (rayTestCB.hasHit()) {
            Vector3 p = new Vector3(0,0,0);
            rayTestCB.getHitPointWorld(p);

            if (button == 0)
                Planet.EX.main.editor.leftClick(rayTestCB.getCollisionObject(), new Vector3(p.x, p.y, p.z));
            else
                Planet.EX.main.editor.rightClick(rayTestCB.getCollisionObject());
        }
    }

    private int splitScreenCorrection(float screenY) {
        if (screenY < Planet.EX.settings.height / 2) {
            Planet.EX.main.updateCameraPosition(0);
            Planet.EX.camera.update();
            screenY = MonsterUtils.map(screenY, 0, Planet.EX.settings.height / 2, 0, Planet.EX.settings.height);
        } else {
            Planet.EX.main.updateCameraPosition(1);
            Planet.EX.camera.update();
            screenY = MonsterUtils.map(screenY, Planet.EX.settings.height / 2, Planet.EX.settings.height, 0, Planet.EX.settings.height);
        }

        return Math.round(screenY);
    }

    public void touchDownMobile (int screenX, int screenY, int pointer, int button) {
        startedX = screenX;
        startedY = screenY;

        if (screenY < 200) {
            if (screenX < Planet.EX.settings.width / 2) {
                Planet.EX.cars.get(0).reset();
            } else {
                Planet.EX.main.editor.deselect();
                Planet.EX.level.clearLevel();
                Planet.EX.level.loadFromFile();
            }

            return;
        }

        if (screenX > Planet.EX.settings.width / 2)
            Planet.EX.cars.get(0).downPressed = true;
        else
            Planet.EX.cars.get(0).upPressed = true;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (MonsterUtils.isMobile())
            touchDownMobile(screenX, screenY, pointer, button);
        else
            touchDownDesktop(screenX, screenY, pointer, button);

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