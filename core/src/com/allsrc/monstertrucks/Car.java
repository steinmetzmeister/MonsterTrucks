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
import com.badlogic.gdx.math.Quaternion;
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

public class Car extends BulletObject implements ControllerListener {
    Vector3 tmpV = new Vector3();

    protected ObjLoader objLoader = new ObjLoader();

    protected btVehicleRaycaster raycaster;
    protected btRaycastVehicle vehicle;
    protected btVehicleTuning tuning;

    protected BulletEntity chassis;
    protected BulletEntity wheels[] = new BulletEntity[4];

    protected float currentForce = 0f;
    protected float currentAngle = 0f;

    protected float maxForce = 50f;
    protected float acceleration = 150f; // second
    protected float maxAngle = 25f;
    protected float steerSpeed = 65f; // second

    protected float frictionSlip = 125f;
    protected float maxSuspensionForce = 8000f;
    protected float maxSuspensionTravelCm = 75f;
    protected float suspensionCompression = 2.4f;
    protected float suspensionDamping = 2.3f;
    protected float suspensionStiffness = 20f;

    protected Model chassisModel;
    protected Model wheelModel;

    protected String chassisModelFile = "data/car.obj";
    protected String wheelModelFile = "data/wheel.obj";

    protected Vector3 chassisScale = new Vector3(1f, 1f, 1f);
    protected Vector3 wheelScale = new Vector3(1f, 1f, 1f);

    protected Vector3 chassisHalfExtents;
    protected Vector3 wheelHalfExtents;

    protected Vector3 initPos;

    // left axii of any controller
    float horzAxis = 0;
    float vertAxis = 0;

    protected boolean upPressed;
    protected boolean downPressed;
    protected boolean leftPressed;
    protected boolean rightPressed;

    protected boolean paused = false;

    public Car(Vector3 pos, Color color) {
        setColor(color);
        setPos(pos);

        initPos = pos;
    }

    protected void loadAssets() {
        // chassis
        chassisModel = objLoader.loadModel(Gdx.files.internal(chassisModelFile));
        Planet.EX.disposables.add(chassisModel);
        chassisModel.meshes.get(0).scale(chassisScale.x, chassisScale.y, chassisScale.z);
        for (int i = 0; i < chassisModel.meshes.size; i++)
            chassisModel.materials.get(i).set(ColorAttribute.createDiffuse(MonsterColor.randomColor()));


        // wheel
        wheelModel = objLoader.loadModel(Gdx.files.internal(wheelModelFile));
        Planet.EX.disposables.add(wheelModel);
        for (int i = 0; i < wheelModel.meshes.size; i++)
            wheelModel.materials.get(i).set(ColorAttribute.createDiffuse(MonsterColor.randomColor()));
        wheelModel.meshes.get(0).scale(wheelScale.x, wheelScale.y, wheelScale.z);

        BoundingBox bounds = new BoundingBox();

        chassisHalfExtents = new Vector3(chassisModel.calculateBoundingBox(bounds).getDimensions()).scl(0.5f);
        wheelHalfExtents = new Vector3(wheelModel.calculateBoundingBox(bounds).getDimensions()).scl(0.5f);

        Planet.EX.world.addConstructor("chassis", new BulletConstructor(chassisModel, 100f, new btBoxShape(chassisHalfExtents)));
        Planet.EX.world.addConstructor("wheel", new BulletConstructor(wheelModel, 0, null));

        entity = Planet.EX.world.add("chassis", initPos.x, initPos.y, initPos.z);
        wheels[0] = Planet.EX.world.add("wheel", 0, 0f, 0);
        wheels[1] = Planet.EX.world.add("wheel", 0, 0f, 0);
        wheels[2] = Planet.EX.world.add("wheel", 0, 0f, 0);
        wheels[3] = Planet.EX.world.add("wheel", 0, 0f, 0);
    }

    protected void init() {
        loadAssets();
        
        setTuning();
        raycaster = new btDefaultVehicleRaycaster((btDynamicsWorld)Planet.EX.world.collisionWorld);
        vehicle = new btRaycastVehicle(tuning, (btRigidBody)entity.body, raycaster);

        ((btDynamicsWorld)Planet.EX.world.collisionWorld).addVehicle(vehicle);

        vehicle.setCoordinateSystem(0, 1, 2);

        addWheels();

        entity.body.setActivationState(Collision.DISABLE_DEACTIVATION);
    }

    public void pause() {
        paused = !paused;
    }

    public void setTuning() {
        tuning = new btVehicleTuning();
        tuning.setFrictionSlip(frictionSlip);
        tuning.setMaxSuspensionForce(maxSuspensionForce);
        tuning.setMaxSuspensionTravelCm(maxSuspensionTravelCm);
        tuning.setSuspensionCompression(suspensionCompression);
        tuning.setSuspensionDamping(suspensionDamping);
        tuning.setSuspensionStiffness(suspensionStiffness);
    }

    public void addWheels() {
        Vector3 point = new Vector3();
        Vector3 direction = new Vector3(0, -1, 0);
        Vector3 axis = new Vector3(-1, 0, 0);

        vehicle.addWheel(point.set(chassisHalfExtents).scl(0.95f, -1f, 0.7f), direction, axis, wheelHalfExtents.z * 0.2f, wheelHalfExtents.z, tuning, true);
        vehicle.addWheel(point.set(chassisHalfExtents).scl(-0.95f, -1f, 0.7f), direction, axis, wheelHalfExtents.z * 0.2f, wheelHalfExtents.z, tuning, true);
        vehicle.addWheel(point.set(chassisHalfExtents).scl(0.95f, -1f, -0.475f), direction, axis, wheelHalfExtents.z * 0.2f, wheelHalfExtents.z, tuning, false);
        vehicle.addWheel(point.set(chassisHalfExtents).scl(-0.95f, -1f, -0.475f), direction, axis, wheelHalfExtents.z * 0.2f, wheelHalfExtents.z, tuning, false);

        for (int i = 0; i < wheels.length; i++) {
            vehicle.getWheelInfo(i).setRollInfluence(0f);

            // ?
            vehicle.getWheelInfo(i).setWheelsDampingCompression(8f);
            vehicle.getWheelInfo(i).setWheelsDampingRelaxation(12f);
        }
    }

    float angle = 0;
    float force = 0;
    float delta = 0;
    float impulseScale = 1f;
    Matrix4 m;
    boolean isOnGround = false;

    public void update() {
        delta = Gdx.graphics.getDeltaTime();

        angle = currentAngle;
        force = currentForce;

        // turn
        if (rightPressed) {
            if (angle > 0f) angle = 0f;
            angle = MathUtils.clamp(angle - steerSpeed * delta, -maxAngle, 0f);
        } else if (leftPressed) {
            if (angle < 0f) angle = 0f;
            angle = MathUtils.clamp(angle + steerSpeed * delta, 0f, maxAngle);
        } else if (Planet.EX.settings.keyboard)
            angle = 0f;

        vehicle.setSteeringValue(angle * MathUtils.degreesToRadians, 0);
        vehicle.setSteeringValue(angle * MathUtils.degreesToRadians, 1);


            currentAngle = angle;
 

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

        isOnGround = false;

        for (int i = 0; i < wheels.length; i++) {
            vehicle.updateWheelTransform(i, true);
            vehicle.getWheelInfo(i).getWorldTransform().getOpenGLMatrix(wheels[i].transform.val);

            if (vehicle.getWheelInfo(i).getRaycastInfo().getGroundObject() != 0)
                isOnGround = true;
        }

        if (!isOnGround) {
            if (horzAxis != 0) {
                m = new Matrix4();
                m.rotate(((btRigidBody)(entity.body)).getOrientation());
                m.translate(new Vector3(0, 0, horzAxis * (impulseScale / 2)));

                ((btRigidBody)(entity.body)).applyTorqueImpulse(m.getTranslation(tmpV));
            }

            if (vertAxis != 0) {
                m = new Matrix4();
                m.rotate(((btRigidBody)(entity.body)).getOrientation());
                m.translate(new Vector3(-1 * vertAxis * impulseScale * 2, 0, 0));

                ((btRigidBody)(entity.body)).applyTorqueImpulse(m.getTranslation(tmpV));
            }
        }

        if (paused) {
            vehicle.getRigidBody().setLinearVelocity(new Vector3(0,0,0));
            vehicle.getRigidBody().setAngularVelocity(new Vector3(0,0,0));
        }
    }

    public void reset() {
        entity.body.setWorldTransform(entity.transform.setToTranslation(initPos));
        entity.body.setInterpolationWorldTransform(entity.transform);
        ((btRigidBody)(entity.body)).setLinearVelocity(Vector3.Zero);
        ((btRigidBody)(entity.body)).setAngularVelocity(Vector3.Zero);
        entity.body.activate();

        horzAxis = 0;
        vertAxis = 0;
    }

    public void render() {
        Planet.EX.main.modelBatch.render(entity.modelInstance, Planet.EX.level.environment);
        for (BulletEntity wheel : wheels)
            Planet.EX.main.modelBatch.render(wheel.modelInstance, Planet.EX.level.environment);
    }

    // Controllers
    @Override
    public void connected(Controller controller) {
        // TODO Auto-generated method stub
    }

    @Override
    public void disconnected(Controller controller) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (axisCode == 0) {
            if (value < 0.1f && value > -0.1f)
                currentAngle = 0;
            else
                currentAngle = MonsterUtils.map(value, 1f, -1f, -maxAngle, maxAngle);
        }

        if (axisCode == 0)
            horzAxis = (value < 0.1f && value > -0.1f) ? 0 : value;

        else if (axisCode == 1)
            vertAxis = (value < 0.1f && value > -0.1f) ? 0 : value;

        return false;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (buttonCode == 1 || buttonCode == 14 || buttonCode == Ouya.BUTTON_O)
            upPressed = true;

        if (buttonCode == 0 || buttonCode == 15 || buttonCode == Ouya.BUTTON_U)
            downPressed = true;

        if (buttonCode == 2) {}
            // vehicle.getRigidBody().applyCentralImpulse(new Vector3(0, 1000f, 0));

        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (buttonCode == 3 || buttonCode == 12 || 
            buttonCode == Ouya.BUTTON_Y)
            reset();

        if (buttonCode == 1 || buttonCode == 14 || 
            buttonCode == Ouya.BUTTON_O)
            upPressed = false;

        if (buttonCode == 0 || buttonCode == 15 || 
            buttonCode == Ouya.BUTTON_U)
            downPressed = false;

        return false;
    }

    //

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