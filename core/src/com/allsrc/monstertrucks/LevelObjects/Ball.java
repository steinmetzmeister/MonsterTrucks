package com.allsrc.monstertrucks;

import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class Ball extends BulletObject {

    public Model model;
    public btSphereShape meshShape;

    public void init() {
        name = "ball";
        attrs = new String[]{ "color", "pos", "size" };
    }

    public Ball(String line) {
        init();
        loadFromLine(line);
        construct();
        updateColor();
        updatePos();
    }

    public Ball(Color color, Vector3 size) {
        init();
        setColor(color);
        setSize(size);
        construct();
        updateColor();
        updatePos();
    }

    public void construct() {
        if (model == null)
        {
            model = getModel();
            meshShape = new btSphereShape(size.x / 2f);

            final BulletConstructor ballConstructor = new BulletConstructor(model, 5f, meshShape);
            ballConstructor.bodyInfo.setRestitution(1f);

            Planet.INSTANCE.world.addConstructor(name, ballConstructor);
        }

        entity();
        addToBulletObjects(this);
    }

    public Model getModel() {
        return Planet.INSTANCE.modelBuilder.createSphere(size.x, size.y, size.z, 16, 16,
            new Material(new ColorAttribute(ColorAttribute.Diffuse, new Color())),
            Usage.Position | Usage.Normal);
    }
}