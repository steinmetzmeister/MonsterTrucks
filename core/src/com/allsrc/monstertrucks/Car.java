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

import com.badlogic.gdx.controllers.Controller;;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.mappings.Ouya;

public class Car implements ControllerListener {
    Vector3 tmpV = new Vector3();

    protected ObjLoader objLoader = new ObjLoader();

    protected btVehicleRaycaster raycaster;
    protected btRaycastVehicle vehicle;
    protected btVehicleTuning tuning;

    protected BulletEntity chassis;
    protected BulletEntity wheels[] = new BulletEntity[4];

    protected float currentForce = 0f;
    protected float currentAngle = 0f;

    protected float maxForce = 150f;
    protected float acceleration = 250f; // second
    protected float maxAngle = 25f;
    // protected float steerSpeed = 65f; // second

    protected float frictionSlip = 125f;
    protected float maxSuspensionForce = 8000f;
    protected float maxSuspensionTravelCm = 30f;
    protected float suspensionCompression = 2.4f;
    protected float suspensionDamping = 2.3f;
    protected float suspensionStiffness = 40f;

    protected boolean upPressed;
    protected boolean downPressed;
    protected boolean leftPressed;
    protected boolean rightPressed;

    protected Model chassisModel;
    protected Model wheelModel;

    protected String chassisModelFile = "data/car.obj";
    protected String wheelModelFile = "data/wheel.obj";
    protected Vector3 wheelScale = new Vector3(1f, 1f, 1f);

    public Car() {
        init();
    }

    protected void loadModels() {
        // chassis
        chassisModel = objLoader.loadModel(Gdx.files.internal(chassisModelFile));
        Planet.INSTANCE.disposables.add(chassisModel);
        chassisModel.materials.get(0).clear();
        chassisModel.materials.get(0).set(ColorAttribute.createDiffuse(Color.RED), ColorAttribute.createSpecular(Color.WHITE));

        // wheel
        wheelModel = objLoader.loadModel(Gdx.files.internal(wheelModelFile));
        Planet.INSTANCE.disposables.add(wheelModel);
        wheelModel.materials.get(0).clear();
        wheelModel.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK), ColorAttribute.createSpecular(Color.WHITE));
        wheelModel.meshes.get(0).scale(wheelScale.x, wheelScale.y, wheelScale.z);
    }

    protected void init() {
        loadModels();
        
        BoundingBox bounds = new BoundingBox();

        Vector3 chassisHalfExtents = new Vector3(chassisModel.calculateBoundingBox(bounds).getDimensions());
        Vector3 wheelHalfExtents = new Vector3(wheelModel.calculateBoundingBox(bounds).getDimensions()).scl(0.5f);

        Planet.INSTANCE.world.addConstructor("chassis", new BulletConstructor(chassisModel, 100f, new btBoxShape(chassisHalfExtents.cpy().scl(1f, 0.5f, 0.5f))));
        Planet.INSTANCE.world.addConstructor("wheel", new BulletConstructor(wheelModel, 7.5f, null));

        chassisHalfExtents.scl(0.5f);

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

        /*
        // turn
        if (rightPressed) {
            if (angle > 0f) angle = 0f;
            angle = MathUtils.clamp(angle - steerSpeed * delta, -maxAngle, 0f);
        } else if (leftPressed) {
            if (angle < 0f) angle = 0f;
            angle = MathUtils.clamp(angle + steerSpeed * delta, 0f, maxAngle);
        } else
            angle = 0f;
        */

        // if (angle != currentAngle) {
        //     currentAngle = angle;
            vehicle.setSteeringValue(angle * MathUtils.degreesToRadians, 0);
            vehicle.setSteeringValue(angle * MathUtils.degreesToRadians, 1);
        // }

        // de/accelerate
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
            vehicle.applyEngineForce(force, 2);
            vehicle.applyEngineForce(force, 3);
        }

        for (int i = 0; i < wheels.length; i++) {
            vehicle.updateWheelTransform(i, true);
            vehicle.getWheelInfo(i).getWorldTransform().getOpenGLMatrix(wheels[i].transform.val);
        }
    }

    public void reset() {
        chassis.body.setWorldTransform(chassis.transform.setToTranslation(0, 3f, 0));
        chassis.body.setInterpolationWorldTransform(chassis.transform);
        ((btRigidBody)(chassis.body)).setLinearVelocity(Vector3.Zero);
        ((btRigidBody)(chassis.body)).setAngularVelocity(Vector3.Zero);
        chassis.body.activate();
    }

    // CONTROLLERS
    @Override
    public void connected(Controller controller) {
        // TODO Auto-generated method stub
    }

    @Override
    public void disconnected(Controller controller) {
        // TODO Auto-generated method stub
    }

    float axisValue;

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (axisCode == 0) {
            if (value < 0.1f && value > -0.1f)
                currentAngle = 0;
            else
                currentAngle = MonsterUtils.map(value, 1f, -1f, -maxAngle, maxAngle);
            // rightPressed = (value > 0.25) ? true : false;
            // leftPressed = (value < -0.25) ? true : false;
        }
        return false;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (buttonCode == 1 || buttonCode == Ouya.BUTTON_O)
            upPressed = true;

        if (buttonCode == 0 || buttonCode == Ouya.BUTTON_U)
            downPressed = true;

        if (buttonCode == 2) {}
            // vehicle.getRigidBody().applyCentralImpulse(new Vector3(0, 1000f, 0));

        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (buttonCode == 3 || buttonCode == Ouya.BUTTON_Y)
            reset();

        if (buttonCode == 1 || buttonCode == Ouya.BUTTON_O)
            upPressed = false;

        if (buttonCode == 0 || buttonCode == Ouya.BUTTON_U)
            downPressed = false;

        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        // TODO Auto-generated method stub
        return false;
    }
}