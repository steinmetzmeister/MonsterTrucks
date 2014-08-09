package com.allsrc.monstertrucks;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.math.Vector3;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

public class Editor {
    protected String[] levelObjects = new String[]{ 
        "Ball",
        "ColorChanger",
        "Checkpoint",
        "Coin",
        "Gate"
    };

    protected int activeObject = 0;

    protected Label activeObjectLabel;

    public Editor() {
        activeObjectLabel = new Label("Object", Planet.EX.main.skin);

        Table table = new Table();
        table.left().bottom();
        table.setFillParent(true);
        table.add(activeObjectLabel).width(100);
        
        Planet.EX.main.stage.addActor(table);
    }

    public void setActiveObject(int i) {
        activeObject = i;
        activeObjectLabel.setText(levelObjects[activeObject]);
    }

    public String getActiveObject() {
        return levelObjects[activeObject];
    }

    public void leftClick(btCollisionObject obj, Vector3 pos) {
        if (obj == Planet.EX.level.terrain.entity.body) {
            Planet.EX.main.editor.createObject(pos);
        }
    }

    public void rightClick(btCollisionObject obj) {
        if (obj != Planet.EX.level.terrain.entity.body) {
            removeObject(obj);
        }
    }

    public BulletObject createObject(Vector3 pos) {
        String lo = levelObjects[activeObject];
        if (lo == "Ball" || lo == "Coin")
            pos.y += 1;

        BulletObject obj = null;

        try {
            Class<?> clazz = Class.forName("com.allsrc.monstertrucks." + levelObjects[activeObject]);
            Constructor<?> constructor = clazz.getConstructor(Vector3.class);
            
            obj = (BulletObject)constructor.newInstance(pos);
            obj.randomRot();
        }
        catch (ClassNotFoundException ie) {}
        catch (NoSuchMethodException ie) {}
        catch (IllegalAccessException ie) {}
        catch (InstantiationException ie) {}
        catch (InvocationTargetException ie) {}

        return obj;
    }

    public void removeObject(btCollisionObject obj) {
        for (BulletObject bulletObj : Planet.EX.level.bulletObjects)
        {
            if (obj == bulletObj.entity.body)
                bulletObj.dispose();
        }
    }
}