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
    public static btSphereShape meshShape;

    public int size;

    public Ball(int size, Color color) {
        this.size = size;
        this.color = color;

        if (model == null)
        {
            model = getModel();
            meshShape = new btSphereShape(this.size / 2f);

            final BulletConstructor ballConstructor = new BulletConstructor(model, 5f, meshShape);
            ballConstructor.bodyInfo.setRestitution(1f);

            Planet.INSTANCE.world.addConstructor(name, ballConstructor);
        }

        init(name);

        setColor(color);

        addToBulletObjects(this);
    }

    public Model getModel() {
        return Planet.INSTANCE.modelBuilder.createSphere(size, size, size, 16, 16,
            new Material(new ColorAttribute(ColorAttribute.Diffuse, new Color())),
            Usage.Position | Usage.Normal);
    }

    public String getSaveLine() {
        return name + ","+ pos.x + "," + pos.y + "," + pos.z + ","
            + size + ","
            + color.r + "," + color.g + "," + color.b + "," + color.a;
    }

    public static void loadFromLine(String line) {
        String[] ls = line.split(",");
        Ball ball = new Ball(Integer.parseInt(ls[4]),
            new Color(
                Float.parseFloat(ls[5]),
                Float.parseFloat(ls[6]),
                Float.parseFloat(ls[7]),
                Float.parseFloat(ls[8])));

        ball.setPos(new Vector3(
            Float.parseFloat(ls[1]),
            Float.parseFloat(ls[2]),
            Float.parseFloat(ls[3])));
    }
}