package com.allsrc.monstertrucks;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.assets.AssetManager;

public enum Planet {
    EX;

    public BulletWorld world;

    public Editor editor;

    public MonsterTrucks main;

    
    public Level level;
    public Settings settings = new Settings();
    
    public ModelBuilder modelBuilder = new ModelBuilder();

    
    public Array<Disposable> disposables = new Array<Disposable>();

    public Array<Car> cars = new Array<Car>();

    public Loader loader = new Loader();
    // public AssetManager assets = new AssetManager();
}