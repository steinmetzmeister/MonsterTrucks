package com.allsrc.monstertrucks;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

import com.badlogic.gdx.Input.Keys;

public class Editor {
    public EditorListener editorListener;

    Vector3 tempV = new Vector3(0,0,0);

    protected String[] levelObjects = new String[]{ 
        "Ball",
        "ColorChanger",
        "Checkpoint",
        "Coin",
        "Gate",
        "SmallRamp",
        "LargeRamp"
    };

    public boolean active = false;
    protected int activeObject = 0;

    protected Label activeObjectLabel;
    protected BulletObject selectedObj;
    protected Color selectedColor;

    protected float rotSpeed = 3.9f;

    public Editor() {
        activeObjectLabel = new Label("Object", Planet.EX.main.skin);

        Table table = new Table();
        table.left().bottom();
        table.setFillParent(true);
        table.add(activeObjectLabel).width(100);
        
        Planet.EX.main.stage.addActor(table);

        editorListener = new EditorListener();
    }

    public void setActive(boolean to) {
        active = to;
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
            createObject(pos);
        } else {
            select(obj);
        }
    }

    public void select(btCollisionObject obj) {
        for (BulletObject bulletObj : Planet.EX.level.bulletObjects)
        {
            if (obj == bulletObj.entity.body) {
                if (bulletObj == selectedObj) {
                    deselect();
                    break;
                }

                deselect();

                selectedColor = bulletObj.getColor();
                bulletObj.setColor(Color.YELLOW);
                bulletObj.updateColor();
                selectedObj = bulletObj;

                break;
            }
        }
    }

    public void deselect() {
        if (selectedObj != null) {
            selectedObj.setColor(selectedColor);
            selectedObj.updateColor();

            selectedObj = null;
        }
    }

    public void rightClick(btCollisionObject obj) {
        if (obj != Planet.EX.level.terrain.entity.body) {
            removeObject(obj);
        }
    }

    public void scroll(int amount) {
        if (selectedObj == null)
            return;

        selectedObj.addRot(amount * rotSpeed);
        selectedObj.updateRot();
    }

    public void keyUp(int keycode) {
        if (selectedObj == null)
            return;

        tempV = selectedObj.getPos();


        selectedObj.setPos(tempV);
        selectedObj.updatePos();
    }

    public void moveY(float amt) {
        if (selectedObj == null)
            return;

        selectedObj.pos.y += amt;
        selectedObj.updatePos();
    }

    public void mouseMoved(Vector3 atPos) {
        if (selectedObj == null)
            return;

        selectedObj.setPos(atPos.cpy());
        selectedObj.updatePos();
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

    public BulletObject createObject(String line) {
        String word;
        
        if (line.indexOf(' ') == -1)
            return null;

        word = line.substring(0, line.indexOf(' '));

        BulletObject obj = null;

        try {
            Class<?> clazz = Class.forName("com.allsrc.monstertrucks." + word);
            Constructor<?> constructor = clazz.getConstructor(String.class);
            
            obj = (BulletObject)constructor.newInstance(line);
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