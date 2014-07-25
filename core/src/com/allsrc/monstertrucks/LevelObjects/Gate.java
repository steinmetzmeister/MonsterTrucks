package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Gate extends BulletObject {
    public Color color;

    public static String name = "gate";
    public static String modelFile = "data/gate.obj";

    public Gate(Vector3 _pos, int _rot, Color _color) {
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
    }

    public void dispose() {
        super.dispose();
    }
}