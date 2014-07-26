package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class Ball extends BulletObject {
    public static String name = "ball";
    public static Model model;
    public int size;

    public Ball(Vector3 _pos, int _size, Color _color) {
        pos = _pos;
        size = _size;
        color = _color;

        init();
    }

    public void init() {
        if (model == null)
        {
            model = Planet.INSTANCE.modelBuilder.createSphere(size, size, size, 16, 16,
                new Material(new ColorAttribute(ColorAttribute.Diffuse, new Color())),
                Usage.Position | Usage.Normal);

            final BulletConstructor ballConstructor = new BulletConstructor(model, 5f, new btSphereShape(size / 2f));

            ballConstructor.bodyInfo.setRestitution(1f);
            Planet.INSTANCE.world.addConstructor(name, ballConstructor);
        }

        entity = Planet.INSTANCE.world.add(name, pos.x, pos.y, pos.z);
        entity.modelInstance.materials.get(0).set(new Material(new ColorAttribute(ColorAttribute.Diffuse, color)));

        addToBulletObjects();
    }

    public String getSaveLine() {
        return name + ","+ pos.x + "," + pos.y + "," + pos.z + ","
            + size + ","
            + color.r + "," + color.g + "," + color.b + "," + color.a;
    }

    public static void loadFromLine(String line) {
        String[] ls = line.split(",");
        new Ball(
            new Vector3(
                Float.parseFloat(ls[1]),
                Float.parseFloat(ls[2]),
                Float.parseFloat(ls[3])),
            Integer.parseInt(ls[4]),
            new Color(
                Float.parseFloat(ls[5]),
                Float.parseFloat(ls[6]),
                Float.parseFloat(ls[7]),
                Float.parseFloat(ls[8])));
    }
}