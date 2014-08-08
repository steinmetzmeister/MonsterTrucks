package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;

import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

import com.badlogic.gdx.Input.Keys;

public class MonsterListener extends MonsterListenerBase {
    ClosestRayResultCallback rayTestCB;
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();
    Vector3 tempV = new Vector3();

    String[] levelObjects = new String[]{ "ball", "changer", "checkpoint", "coin", "gate" };
    int activeLevelObject = 0;

    public void init() {
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
    }

    @Override
    public boolean keyDown (int keycode) {
        switch (keycode) {
            case Keys.SPACE:
                //
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {

        if (keycode >= 8 && keycode <= 12) {
            activeLevelObject = keycode - 8;
            Planet.EX.main.activeObjectLabel.setText(
                levelObjects[activeLevelObject]);
            return false;
        }

        switch (keycode) {
            case Keys.R:
                //
                break;
            case Keys.C:
                Planet.EX.level.clearLevel();
                break;
            case Keys.L:
                Planet.EX.level.saveToFile();
                break;
            case Keys.O:
                Planet.EX.level.loadFromFile();
                break;
        }
        return false;
    }

    public void touchDownDesktop (int screenX, int screenY, int pointer, int button) {
        if (Planet.EX.main.numPlayers == 2) {
            float fScreenY;
            if (screenY < Gdx.graphics.getHeight() / 2) {
                Planet.EX.main.updateCameraPosition(0);
                Planet.EX.camera.update();
                fScreenY = MonsterUtils.map(screenY, 0, Gdx.graphics.getHeight() / 2, 0, Gdx.graphics.getHeight());
            } else {
                Planet.EX.main.updateCameraPosition(1);
                Planet.EX.camera.update();
                fScreenY = MonsterUtils.map(screenY, Gdx.graphics.getHeight() / 2, Gdx.graphics.getHeight(), 0, Gdx.graphics.getHeight());
            }

            screenY = Math.round(fScreenY);
        }

        Ray ray = Planet.EX.camera.getPickRay(screenX, screenY);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(50f).add(rayFrom);

        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.getRayFromWorld().setValue(rayFrom.x, rayFrom.y, rayFrom.z);
        rayTestCB.getRayToWorld().setValue(rayTo.x, rayTo.y, rayTo.z);

        Planet.EX.world.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);

        if (rayTestCB.hasHit()) {
            btVector3 p = rayTestCB.getHitPointWorld();

            if (button == 0) {
                if (rayTestCB.getCollisionObject() == Planet.EX.level.terrain.entity.body) {
                    createLevelObject(new Vector3(p.getX(), p.getY(), p.getZ()));
                }
            } else {
                for (BulletObject bulletObj : Planet.EX.level.bulletObjects)
                {
                    if (rayTestCB.getCollisionObject() == bulletObj.entity.body && bulletObj.name != "terrain")
                        bulletObj.dispose();
                }
            }
        }
    }

    public void createLevelObject(Vector3 pos) {
        String lo = levelObjects[activeLevelObject];

        if (lo == "ball") {
            pos.y += 1f;
            Ball ball = new Ball(MonsterColor.randomColor(), new Vector3(3, 3, 3));
            ball.setPos(pos);
            ball.updatePos();
        }

        else if (lo == "changer") {
            ColorChanger c = new ColorChanger(pos);
            c.setPos(pos);
            c.randomRot();
        }

        else if (lo == "checkpoint") {
            Checkpoint c = new Checkpoint(MonsterColor.randomColor(), pos, new Vector3(10, 10, 10));
            c.setPos(pos);
            c.randomRot();
        }

        else if (lo == "coin") {
            pos.y += 1f;
            new Coin(pos);
        }

        else if (lo == "gate") {
            Gate g = new Gate(MonsterColor.randomColor(), pos);
            g.setPos(pos);
            g.randomRot();
        }
    }

    int startedX = 0;
    int startedY = 0;

    public void touchDownMobile (int screenX, int screenY, int pointer, int button) {
        if (screenY < 200) {
            if (screenX < Gdx.graphics.getWidth() / 2) {
                Planet.EX.cars.get(0).reset();
            } else {
                Planet.EX.level.clearLevel();
                Planet.EX.level.loadFromFile();
            }

            return;
        }

        startedX = screenX;
        startedY = screenY;

        int width = Gdx.graphics.getWidth();
        if (screenX > width / 2) {
            Planet.EX.cars.get(0).downPressed = true;
        }
        else {
            Planet.EX.cars.get(0).upPressed = true;
        }
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if ((Gdx.app.getType() == ApplicationType.Android ||
            Gdx.app.getType() == ApplicationType.iOS) &&
            Controllers.getControllers().size == 0) {

            touchDownMobile(screenX, screenY, pointer, button);
        }
        else
            touchDownDesktop(screenX, screenY, pointer, button);

        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (Gdx.app.getType() == ApplicationType.Android ||
            Gdx.app.getType() == ApplicationType.iOS) {

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
        if (Gdx.app.getType() == ApplicationType.Android ||
            Gdx.app.getType() == ApplicationType.iOS) {

            int x = (screenX - startedX) * -1;

            if (startedX != 0) {
                Planet.EX.cars.get(0).horzAxis = MonsterUtils.map(x, -300, 300, 1, -1);

                if (x > 200) x = 200;
                if (x < -200) x = -200;

                Planet.EX.cars.get(0).currentAngle = MonsterUtils.map(x, -200f, 200f,
                    -Planet.EX.cars.get(0).maxAngle,
                    Planet.EX.cars.get(0).maxAngle);
            }

            if (startedY != 0) {
                int y = (screenY - startedY) * -1;
                Planet.EX.cars.get(0).vertAxis = MonsterUtils.map(y, -300, 300, 1, -1);
            }
        }

        return false;
    }   
}