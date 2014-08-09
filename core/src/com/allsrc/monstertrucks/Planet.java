package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public enum Planet {
    EX;

    public MonsterTrucks main;

    public Loader loader = new Loader();
    public Level level;
    public Settings settings = new Settings();
    
    public PerspectiveCamera camera;

    public ModelBuilder modelBuilder = new ModelBuilder();
    public ModelBatch modelBatch;

    public BulletWorld world;
    public Array<Disposable> disposables = new Array<Disposable>();

    public Array<Player> players = new Array<Player>();
    public Array<Car> cars = new Array<Car>();

    
}