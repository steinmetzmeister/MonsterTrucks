package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.PerspectiveCamera;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class MonsterCamera {
    private PerspectiveCamera camera;

    // context of truck
    Matrix4 worldTransform = new Matrix4();
    Vector3 cameraPosition = new Vector3();
    Vector3 objPosition = new Vector3();

    Vector3 tempV1 = new Vector3(0,0,0);
    Vector3 tempV2 = new Vector3(0,0,0);

    public MonsterCamera(int playerCount) {
        float width = 0;

        switch (Planet.EX.settings.playerCount) {
            case 1:
                width = 3f * Planet.EX.settings.width / Planet.EX.settings.height;
                break;
            case 2:
                width = 3f * Planet.EX.settings.width / (Planet.EX.settings.height / 2);
                break;
            case 3:
            case 4:
                break;
        }

        camera = new PerspectiveCamera(63f, width, 3f);
    }

    public PerspectiveCamera get() {
        return this.camera;
    }

    public void focus(BulletObject obj) {
        obj.entity.motionState.getWorldTransform(worldTransform);

        worldTransform.getTranslation(objPosition);

        if (!Planet.EX.editor.active)
            tempV1 = Planet.EX.settings.camOffset.cpy();
        else
            tempV1 = Planet.EX.settings.editorCamOffset.cpy();

        cameraPosition.set(objPosition.x + tempV1.x, tempV1.y, objPosition.z + tempV1.z);

        camera.position.set(cameraPosition);
        camera.lookAt(objPosition);
        camera.up.set(Vector3.Y);
        camera.update();
    }

    protected boolean isVisible(BulletObject obj) {
        obj.entity.modelInstance.transform.getTranslation(tempV2);
        tempV2.add(Planet.EX.loader.getCenter(obj.name));

        return camera.frustum.sphereInFrustum(tempV2, Planet.EX.loader.getRadius());
    }
}