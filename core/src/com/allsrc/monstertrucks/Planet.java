package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public enum Planet {
    INSTANCE;

    public PerspectiveCamera camera;
    public ModelBatch modelBatch;
    public ModelBatch shadowBatch;
    public BulletWorld world;
}