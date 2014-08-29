package com.allsrc.monstertrucks;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;

public class Race { // extends level
    public class Racer {
        public String color;
        public int carNumber;

        public int lap = 1;
        public int checkpoint = 0;
        public float distance;

        public float finishTime;

        public void finish() {
            finishTime = TimeUtils.millis() - startTime;
        }
    }

    public Array<Checkpoint> checkpoints = new Array<Checkpoint>();
    public Racer[] racers;
    public int[] placed;

    public float startTime;
    public float finishTime;

    public Race(int numRacers) {
        racers = new Racer[numRacers];
        placed = new int[numRacers];
        for (int i = 0; i < numRacers; i++)
        {
            addRacer(i);
            placed[i] = i;
        }
    }

    public void start() {
        startTime = TimeUtils.millis();
    }

    public void finish() {
        finishTime = TimeUtils.millis() - startTime;
    }

    public void addCheckpoint(Vector2 pos2) {
        Vector3 pos3 = new Vector3(pos2.x, 0, pos2.y);

        Checkpoint checkpoint = new Checkpoint(pos3);
        checkpoints.add(checkpoint);
        checkpoint.id = checkpoints.size - 1;
    }

    public void addRacer(int racerNum) {
        Racer racer = new Racer();
        racers[racerNum] = racer;
    }

    public void reachedCheckpoint(int checkpoint, int racer) {
        if (racers[racer].checkpoint != checkpoint)
            return;

        racers[racer].checkpoint++;
        if (racers[racer].checkpoint >= checkpoints.size) {
            racers[racer].checkpoint = 0;
            racers[racer].lap++;
        }
    }

    int tempInt;

    public void update() {
        for (int i = 1; i < racers.length; i++) {
            if (racers[placed[i]].lap > racers[placed[i - 1]].lap) {
                moveUp(i);
            } else if (racers[placed[i]].lap == racers[placed[i - 1]].lap) {

                if (racers[placed[i]].checkpoint > racers[placed[i - 1]].checkpoint) {
                    moveUp(i);
                } else if (racers[placed[i]].checkpoint == racers[placed[i - 1]].checkpoint) {

                    if (distanceToCheckpoint(placed[i]) < distanceToCheckpoint(placed[i - 1])) {
                        moveUp(i);
                    }
                }
            }
        }
    }

    public void moveUp(int racer) {
        tempInt = placed[racer - 1];
        placed[racer - 1] = placed[racer];
        placed[racer] = tempInt;
    }

    private Matrix4 worldTransform = new Matrix4();
    private Vector3 racerPosition = new Vector3();

    private Vector2 tempV2 = new Vector2();
    private Vector3 tempV3 = new Vector3();

    // distance between racer and their last checkpoint
    public float distanceToCheckpoint(int racerNum) {
        Planet.EX.cars.get(racerNum).entity.motionState.getWorldTransform(worldTransform);
        worldTransform.getTranslation(racerPosition);

        tempV2.x = racerPosition.x;
        tempV2.y = racerPosition.z;

        tempV3 = checkpoints.get(racers[racerNum].checkpoint).getPos();

        return tempV2.dst(tempV3.x, tempV3.z);
    }
}