package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public enum Planet {
    INSTANCE;

    public PerspectiveCamera camera;
    public ModelBatch modelBatch;
    public BulletWorld world;
    public Array<Disposable> disposables = new Array<Disposable>();
    public Level level;
}