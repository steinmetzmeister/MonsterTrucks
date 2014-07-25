package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Gate extends BulletObject {

    public static String name = "gate";
    public static String modelFile = "data/gate.obj";

    public boolean isChild = false;

    public Gate(Vector3 _pos, int _rot, Color _color) {
        pos = _pos;
        rot = _rot;
        color = _color;

        super.init(name, modelFile);
        init();
    }

    public Gate(Vector3 _pos, int _rot, Color _color, boolean _isChild) {
        isChild = _isChild;

        pos = _pos;
        rot = _rot;
        color = _color;

        super.init(name, modelFile);
        init();
    }

    public void init() {
        entity.modelInstance.materials.get(0).set(
            ColorAttribute.createDiffuse(color),
            ColorAttribute.createSpecular(Color.WHITE));

        entity.transform.rotate(Vector3.Y, rot);

        if (!isChild)
            Planet.INSTANCE.level.bulletObjects.add(this);
    }

    public void dispose() {
        if (!isChild)
            Planet.INSTANCE.level.bulletObjects.removeValue(this, true);

        super.dispose();
    }
}