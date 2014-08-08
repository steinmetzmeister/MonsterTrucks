package com.allsrc.monstertrucks;

import com.badlogic.gdx.utils.Array;

public class Checkpoints {

    protected Array<Checkpoint> list = new Array<Checkpoint>();
    protected int next = 0;

    public Checkpoints() {
    }

    public void reset() {
        next = 0;
        for (Checkpoint checkpoint : list) {
            checkpoint.setActive();
        }
    }

    public void add(Checkpoint checkpoint) {
        list.add(checkpoint);
        checkpoint.setManager(this);
    }

    public void remove(int i) {
        list.removeIndex(i);
    }

    public boolean test(Checkpoint checkpoint) {
        if (list.indexOf(checkpoint, true) == next) {
            next++;

            if (next >= list.size)
                reset();
            else
                checkpoint.setInactive();

            return true;
        }

        return false;
    }
}