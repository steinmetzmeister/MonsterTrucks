package com.allsrc.monstertrucks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDefaultVehicleRaycaster;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle;
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle.btVehicleTuning;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btVehicleRaycaster;
import com.badlogic.gdx.physics.bullet.dynamics.btWheelInfo;

public class MonsterTruck {
    public ObjLoader objLoader = new ObjLoader();

    public btVehicleRaycaster raycaster;
    public btRaycastVehicle vehicle;
    public btVehicleTuning tuning;

    BulletEntity chassis;
    BulletEntity wheels[] = new BulletEntity[4];

    boolean downPressed;
    boolean upPressed;
    boolean leftPressed;
    boolean rightPressed;

    float maxForce = 175f;
    float currentForce = 0f;
    float acceleration = 250f; // force/second
    float maxAngle = 35f;
    float currentAngle = 0f;
    float steerSpeed = 65f; // angle/second

    float frictionSlip = 125f;
    float maxSuspensionForce = 8000f;
    float maxSuspensionTravelCm = 30f;
    float suspensionCompression = 2.4f;
    float suspensionDamping = 2.3f;
    float suspensionStiffness = 40f;

    Vector3 tmpV = new Vector3();

    public MonsterTruck() {
        init();
    }

    public void init() {
        final Model chassisModel = objLoader.loadModel(Gdx.files.internal("data/truck.obj"));
        final Model wheelModel = objLoader.loadModel(Gdx.files.internal("data/wheel.obj"));

        // disposables.add(chassisModel);
        // disposables.add(wheelModel);

        chassisModel.meshes.get(0).scale(0.5f, 0.5f, 0.5f);
        chassisModel.materials.get(0).clear();
        chassisModel.materials.get(0).set(ColorAttribute.createDiffuse(Color.RED), ColorAttribute.createSpecular(Color.WHITE));

        wheelModel.materials.get(0).clear();
        wheelModel.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK), ColorAttribute.createSpecular(Color.WHITE));
        wheelModel.meshes.get(0).scale(3f, 1.5f, 1.5f);

        // Matrix4 m4 = new Matrix4();
        // m4.setTranslation(0, 0.5f, 0);
        // chassisModel.meshes.get(0).transform(m4);

        BoundingBox bounds = new BoundingBox();

        Vector3 chassisHalfExtents = new Vector3(chassisModel.calculateBoundingBox(bounds).getDimensions()).scl(0.5f);
        Vector3 wheelHalfExtents = new Vector3(wheelModel.calculateBoundingBox(bounds).getDimensions()).scl(0.5f);

        Planet.INSTANCE.world.addConstructor("chassis", new BulletConstructor(chassisModel, 100f, new btBoxShape(chassisHalfExtents)));
        Planet.INSTANCE.world.addConstructor("wheel", new BulletConstructor(wheelModel, 12.5f, null));

        chassis = Planet.INSTANCE.world.add("chassis", 0, 3f, 0);
        wheels[0] = Planet.INSTANCE.world.add("wheel", 0, 0f, 0);
        wheels[1] = Planet.INSTANCE.world.add("wheel", 0, 0f, 0);
        wheels[2] = Planet.INSTANCE.world.add("wheel", 0, 0f, 0);
        wheels[3] = Planet.INSTANCE.world.add("wheel", 0, 0f, 0);

        // Create the vehicle
        raycaster = new btDefaultVehicleRaycaster((btDynamicsWorld)Planet.INSTANCE.world.collisionWorld);

        tuning = new btVehicleTuning();
        tuning.setFrictionSlip(frictionSlip);
        tuning.setMaxSuspensionForce(maxSuspensionForce);
        tuning.setMaxSuspensionTravelCm(maxSuspensionTravelCm);
        tuning.setSuspensionCompression(suspensionCompression);
        tuning.setSuspensionDamping(suspensionDamping);
        tuning.setSuspensionStiffness(suspensionStiffness);

        vehicle = new btRaycastVehicle(tuning, (btRigidBody)chassis.body, raycaster);

        chassis.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        ((btDynamicsWorld)Planet.INSTANCE.world.collisionWorld).addVehicle(vehicle);

        vehicle.setCoordinateSystem(0, 1, 2);

        btWheelInfo wheelInfo;
        Vector3 point = new Vector3();
        Vector3 direction = new Vector3(0, -1, 0);
        Vector3 axis = new Vector3(-1, 0, 0);

        vehicle.addWheel(point.set(chassisHalfExtents).scl(1.75f, 0f, 0.9f), direction, axis, 0.5f, wheelHalfExtents.z, tuning, true);
        vehicle.addWheel(point.set(chassisHalfExtents).scl(-1.75f, 0f, 0.9f), direction, axis, 0.5f, wheelHalfExtents.z, tuning, true);
        vehicle.addWheel(point.set(chassisHalfExtents).scl(1.75f, 0f, -0.9f), direction, axis, 0.5f, wheelHalfExtents.z, tuning, false);
        vehicle.addWheel(point.set(chassisHalfExtents).scl(-1.75f, 0f, -0.9f), direction, axis, 0.5f, wheelHalfExtents.z, tuning, false);

        for (int i = 0; i < wheels.length; i++) {
            vehicle.getWheelInfo(i).setRollInfluence(0f);

            // ?
            vehicle.getWheelInfo(i).setWheelsDampingCompression(4f);
            vehicle.getWheelInfo(i).setWheelsDampingRelaxation(6f);
        }
    }

    public void update() {
        final float delta = Gdx.graphics.getDeltaTime();

        float angle = currentAngle;
        float force = currentForce;

        if (rightPressed) {
            if (angle > 0f) angle = 0f;
            angle = MathUtils.clamp(angle - steerSpeed * delta, -maxAngle, 0f);
        } else if (leftPressed) {
            if (angle < 0f) angle = 0f;
            angle = MathUtils.clamp(angle + steerSpeed * delta, 0f, maxAngle);
        } else
            angle = 0f;

        if (angle != currentAngle) {
            currentAngle = angle;
            vehicle.setSteeringValue(angle * MathUtils.degreesToRadians, 0);
            vehicle.setSteeringValue(angle * MathUtils.degreesToRadians, 1);
        }

        if (upPressed) {
            if (force < 0f) force = 0f;
            force = MathUtils.clamp(force + acceleration * delta, 0f, maxForce);
        } else if (downPressed) {
            if (force > 0f) force = 0f;
            force = MathUtils.clamp(force - acceleration * delta, -maxForce, 0f);
        } else
            force = 0f;

        if (force != currentForce) {
            currentForce = force;
            vehicle.applyEngineForce(force, 0);
            vehicle.applyEngineForce(force, 1);
            // vehicle.applyEngineForce(force, 2);
            // vehicle.applyEngineForce(force, 3);
        }

        for (int i = 0; i < wheels.length; i++) {
            vehicle.updateWheelTransform(i, true);
            vehicle.getWheelInfo(i).getWorldTransform().getOpenGLMatrix(wheels[i].transform.val);
        }
    }
}