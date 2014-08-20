package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import java.util.HashMap;

public class TrackBuilder {
    public Array<Track> parts = new Array<Track>();

    private int northWest = 0;
    private int northEast = -90;
    private int southEast = 180;
    private int southWest = 90;

    private Vector3 next = new Vector3(0, 0, 0);
    private Vector3 dir = new Vector3(0, 0, 1);
    private int size = 8;

    private ModelInstance miTemp;

    public void randomizeColors() {
        HashMap<String,Color> colors = new HashMap<String,Color>();

        colors.put("ground", MonsterColor.randomColor());
        colors.put("border1", MonsterColor.randomColor());
        colors.put("border2", MonsterColor.randomColor());
        colors.put("road", MonsterColor.randomColor());

        for (Track part : parts) {
            miTemp = part.entity.modelInstance;

        String id;
        for (int i = 0; i < miTemp.materials.size; i++) {
            id = miTemp.materials.get(i).id;

            int j = id.indexOf(".");
            if (j != -1)
                id = id.substring(0, j);

                miTemp.materials.get(i).set(ColorAttribute.createDiffuse(colors.get(id)));
            }

            Planet.EX.level.terrain.entity.modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(MonsterColor.randomColor()));
        }

        colors.clear();
        colors = null;

        miTemp = null;
    }

    public Vector2 straight() {
        Track temp = new Track();
        temp.setPos(next.cpy());

        if (dir.x != 0) {
            next.x += size * dir.x;
            temp.setRot(0);
        }
        else {
            next.z += size * dir.z;
            temp.setRot(90);
        }

        temp.construct("straight");

        temp.updateRot();
        temp.updatePos();

        parts.add(temp);

        return new Vector2(temp.getPos().x, temp.getPos().z);
    }

    // 0 left, 1 right
    public Vector2 turn(int d) {
        Track temp = new Track();
        temp.setPos(next.cpy());

        switch (d) {
            case 0:
                if (dir.x != 0) {
                    if (dir.x == 1) {
                        temp.setRot(northWest);
                        dir.z = -1;
                    }
                    else {
                        temp.setRot(southEast);
                        dir.z = 1;
                    }
                    next.z += dir.z * size;
                    dir.x = 0;
                }
                else {
                    if (dir.z == 1) {
                        temp.setRot(northEast);
                        dir.x = 1;
                    }
                    else {
                        temp.setRot(southWest);
                        dir.x = -1;
                    }
                    next.x += dir.x * size;
                    dir.z = 0;
                }
                break;
            case 1:
                if (dir.x != 0) {
                    if (dir.x == 1) {
                        temp.setRot(southWest);
                        dir.z = 1;
                    }
                    else {
                        temp.setRot(northEast);
                        dir.z = -1;
                    }
                    next.z += dir.z * size;
                    dir.x = 0;
                }
                else {
                    if (dir.z == 1) {
                        temp.setRot(northWest);
                        dir.x = -1;
                    }
                    else {
                        temp.setRot(southEast);
                        dir.x = 1;
                    }
                    next.x += dir.x * size;
                    dir.z = 0;
                }
                break;
        }

        temp.construct("turn");

        temp.updateRot();
        temp.updatePos();

        parts.add(temp);

        return new Vector2(temp.getPos().x, temp.getPos().z);
    }

    public void clean() {
        next = null;
        dir = null;
    }
}
