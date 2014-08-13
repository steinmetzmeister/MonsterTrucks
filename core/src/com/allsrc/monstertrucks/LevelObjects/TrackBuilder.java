package com.allsrc.monstertrucks;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class TrackBuilder {
    Array<Track> parts = new Array<Track>();

    private int northWest = 0;
    private int northEast = -90;
    private int southEast = 180;
    private int southWest = 90;

    public Vector3 next = new Vector3(0, 0, 0);
    public Vector3 dir = new Vector3(0, 0, 1);
    private int size = 8;

    public void straight() {
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

        System.out.println(next);
    }

    // 0 left, 1 right
    public void turn(int d) {
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
                        dir.z = -1;
                    }
                    else {
                        temp.setRot(northEast);
                        dir.z = 1;
                    }
                    next.z += dir.z * size;
                    dir.x = 0;
                }
                else {
                    if (dir.z == 1) {
                        temp.setRot(northWest);
                        dir.x = 1;
                    }
                    else {
                        temp.setRot(southEast);
                        dir.x = -1;
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

        System.out.println(next);
    }
}
