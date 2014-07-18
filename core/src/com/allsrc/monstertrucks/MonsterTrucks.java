package com.allsrc.monstertrucks;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.LinearMath;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.MathUtils;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;

public class MonsterTrucks extends MonsterTrucksBase {
	ObjLoader objLoader = new ObjLoader();

	MonsterTruck truck;

	boolean initialized;
	boolean shadows = false;
	
	public void init() {
		if (initialized) return;

		Bullet.init();
		initialized = true;
	}

	public PerspectiveCamera camera;

	public Environment environment;
	public DirectionalLight light;

	public ModelBuilder modelBuilder = new ModelBuilder();
	public Array<Disposable> disposables = new Array<Disposable>();

	private Skin skin;
	private Stage stage;

	@Override
	public void create () {
		init();

		stage = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		final float width = Gdx.graphics.getWidth();
		final float height = Gdx.graphics.getHeight();
		
		Planet.INSTANCE.camera = new PerspectiveCamera(67f, 3f * width / height, 3f);

		Planet.INSTANCE.modelBatch = new ModelBatch();
		Planet.INSTANCE.shadowBatch = new ModelBatch();
		Planet.INSTANCE.world = new BulletWorld();

		//
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
		
		light = shadows ? new DirectionalShadowLight(1024, 1024, 20f, 20f, 1f, 300f) : new DirectionalLight();
		light.set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f);

		environment.add(light);

		if (shadows) environment.shadowMap = (DirectionalShadowLight)light;
		Planet.INSTANCE.shadowBatch = new ModelBatch(new DepthShaderProvider());
		//

		// TERRAIN
		final Model model = objLoader.loadModel(Gdx.files.internal("data/terrain.obj"));

		model.meshes.get(0).scale(2f, 2f, 2f);
        model.materials.get(0).clear();
        model.materials.get(0).set(ColorAttribute.createDiffuse(Color.GREEN), ColorAttribute.createSpecular(Color.WHITE));

		Planet.INSTANCE.world.addConstructor("terrain", new BulletConstructor(model, 0f, new btBvhTriangleMeshShape(model.meshParts)));
		Planet.INSTANCE.world.add("terrain", 0f, 0f, 0f);

		truck = new MonsterTruck();

		Controllers.addListener(this);

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		inputMultiplexer.addProcessor(this);
		inputMultiplexer.addProcessor(stage);
	}

	@Override
	public void render () {
		update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		Planet.INSTANCE.camera.update();

		if (shadows)
		{
			((DirectionalShadowLight)light).begin(Vector3.Zero, Planet.INSTANCE.camera.direction);
			Planet.INSTANCE.shadowBatch.begin(((DirectionalShadowLight)light).getCamera());
			Planet.INSTANCE.world.render(Planet.INSTANCE.shadowBatch, null);
			Planet.INSTANCE.shadowBatch.end();
			((DirectionalShadowLight)light).end();
		}

		Planet.INSTANCE.modelBatch.begin(Planet.INSTANCE.camera);
		Planet.INSTANCE.world.render(Planet.INSTANCE.modelBatch, environment);
		Planet.INSTANCE.modelBatch.end();

		// UI
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	// context of truck
	Matrix4 transform = new Matrix4();
	Vector3 translation1 = new Vector3();
	Vector3 translation2 = new Vector3();

	public void update () {
		Planet.INSTANCE.world.update();

		truck.update();

		truck.chassis.motionState.getWorldTransform(transform);

		transform.getTranslation(translation1);
		translation2.set(translation1);

		translation1.set(translation1.x - 5f, translation1.y + 12f, translation1.z - 10f);

		Planet.INSTANCE.camera.position.set(translation1);
		Planet.INSTANCE.camera.lookAt(translation2);
        Planet.INSTANCE.camera.up.set(Vector3.Y);
	}

	@Override
	public void dispose () {
		Planet.INSTANCE.world.dispose();
		Planet.INSTANCE.world = null;

		for (Disposable disposable : disposables)
			disposable.dispose();
		disposables.clear();

		Planet.INSTANCE.modelBatch.dispose();
		Planet.INSTANCE.modelBatch = null;

		Planet.INSTANCE.shadowBatch.dispose();
		Planet.INSTANCE.shadowBatch = null;

		if (shadows) ((DirectionalShadowLight)light).dispose();

		light = null;

		super.dispose();

		// UI
		stage.dispose();
	}

	public void resetTruck() {
		truck.chassis.body.setWorldTransform(truck.chassis.transform.setToTranslation(0, 3f, 0));
		truck.chassis.body.setInterpolationWorldTransform(truck.chassis.transform);
		((btRigidBody)(truck.chassis.body)).setLinearVelocity(Vector3.Zero);
		((btRigidBody)(truck.chassis.body)).setAngularVelocity(Vector3.Zero);
		truck.chassis.body.activate();
	}

	@Override
	public boolean keyDown (int keycode) {
		switch (keycode) {
		case Keys.S:
			truck.downPressed = true;
			break;
		case Keys.W:
			truck.upPressed = true;
			break;
		case Keys.A:
			truck.leftPressed = true;
			break;
		case Keys.D:
			truck.rightPressed = true;
			break;
		case Keys.SPACE:
			// truck.vehicle.getRigidBody().applyCentralImpulse(new Vector3(0, 500f, 0));
			break;
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp (int keycode) {
		switch (keycode) {
		case Keys.S:
			truck.downPressed = false;
			break;
		case Keys.W:
			truck.upPressed = false;
			break;
		case Keys.A:
			truck.leftPressed = false;
			break;
		case Keys.D:
			truck.rightPressed = false;
			break;
		case Keys.R:
			resetTruck();
			break;
		}
		return super.keyUp(keycode);
	}

	@Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (axisCode == 0) {
        	truck.rightPressed = (value > 0.25) ? true : false;
        	truck.leftPressed = (value < -0.25) ? true : false;
        }

        // if (axisCode == 1) {
        // 	truck.upPressed = (value < -0.25) ? true : false;
        // 	truck.downPressed = (value > 0.25) ? true : false;
        // }
        return false;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
    	if (buttonCode == 1)
    		truck.upPressed = true;

    	if (buttonCode == 0)
    		truck.downPressed = true;

        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
		if (buttonCode == 3)
    		resetTruck();

    	if (buttonCode == 1)
    		truck.upPressed = false;

    	if (buttonCode == 0)
    		truck.downPressed = false;

        return false;
    }
}
