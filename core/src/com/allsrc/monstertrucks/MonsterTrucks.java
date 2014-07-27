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
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.LinearMath;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;

import com.badlogic.gdx.physics.bullet.linearmath.*;

import com.badlogic.gdx.Application.ApplicationType;

public class MonsterTrucks extends MonsterTrucksBase {
	ObjLoader objLoader = new ObjLoader();

	boolean initialized;

	int numPlayers = 1;
	
	public void init() {
		if (initialized) return;

		Bullet.init();
		initialized = true;
	}

	public PerspectiveCamera camera;

	public Environment environment;
	public DirectionalLight light;

	public ModelBuilder modelBuilder = new ModelBuilder();

	private Skin skin;
	private Stage stage;

	// rays
	ClosestRayResultCallback rayTestCB;
	Vector3 rayFrom = new Vector3();
	Vector3 rayTo = new Vector3();
	Vector3 tempV = new Vector3();

	Terrain terrain;

	@Override
	public void create () {
		init();

		stage = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		final float width = Gdx.graphics.getWidth();
		final float height = Gdx.graphics.getHeight();
		
		if (numPlayers == 2)
			Planet.INSTANCE.camera = new PerspectiveCamera(67f, 3f * width / (height / 2), 3f);
		else
			Planet.INSTANCE.camera = new PerspectiveCamera(67f, 3f * width / height , 3f);

		Planet.INSTANCE.modelBatch = new ModelBatch();
		Planet.INSTANCE.world = new BulletWorld();

		//
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
		
		light = new DirectionalLight();
		light.set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f);

		environment.add(light);
		//
		
		// Level
		Planet.INSTANCE.level = new Level();

		terrain = new Terrain("data/terrain.obj", Color.GREEN);

		// int i = 0; 
		// for (Controller controller : Controllers.getControllers())
		for (int i = 0; i < numPlayers; i++)
		{
			Color c = Color.RED;
			if (i == 1)
				c = Color.BLACK;

			Planet.INSTANCE.cars.add((Car)new MonsterTruck(new Vector3(i * 5f, 3f, 0f), c));

			if (i < Controllers.getControllers().size)
				Controllers.getControllers().get(i).addListener(Planet.INSTANCE.cars.get(i));

			// i++;
		}

		final Model blockModel = objLoader.loadModel(Gdx.files.internal("data/block.obj"));
		Planet.INSTANCE.world.addConstructor("block", new BulletConstructor(blockModel, 0f, new btBvhTriangleMeshShape(blockModel.meshParts)));

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		inputMultiplexer.addProcessor(this);
		inputMultiplexer.addProcessor(stage);

		// rays
		rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

		Checkpoint checkpoint = new Checkpoint(10);
		checkpoint.setPos(0f, 0f, 10f);
		checkpoint.setColor(new Color(1f, 0f, 1f, 0.4f));
		
		Gate gate = new Gate();
		gate.setPos(new Vector3(0f, -2f, 20f));
		gate.setColor(new Color(0f, 0.75f, 0.33f, 1f));

		Ball ballA = new Ball(3, MonsterColor.CYAN);
		ballA.setPos(-5f, 5f, 5f);

		Ball ballB = new Ball(3, MonsterColor.MAGENTA);
		ballB.setPos(0f, 5f, 5f);

		Ball ballC = new Ball(3, MonsterColor.YELLOW);
		ballC.setPos(5f, 5f, 5f);
	}

	@Override
	public void render () {
		update();

		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (numPlayers == 2)
			renderTwoPlayerScreen();
		else
			renderScreen();

		// UI
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	public void renderScreen() {
		updateCameraPosition(0);
		Planet.INSTANCE.modelBatch.begin(Planet.INSTANCE.camera);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Planet.INSTANCE.camera.update();
		Planet.INSTANCE.world.render(Planet.INSTANCE.modelBatch, environment);
		Planet.INSTANCE.modelBatch.end();
	}

	public void renderTwoPlayerScreen() {
		updateCameraPosition(0);
		Planet.INSTANCE.modelBatch.begin(Planet.INSTANCE.camera);
		Gdx.gl.glViewport(0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
		Planet.INSTANCE.camera.update();
		Planet.INSTANCE.world.render(Planet.INSTANCE.modelBatch, environment);
		Planet.INSTANCE.modelBatch.end();
		
		updateCameraPosition(1);
		Planet.INSTANCE.modelBatch.begin(Planet.INSTANCE.camera);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
		Planet.INSTANCE.camera.update();
		Planet.INSTANCE.world.render(Planet.INSTANCE.modelBatch, environment);
		Planet.INSTANCE.modelBatch.end();
	}

	// context of truck
	Matrix4 worldTransform = new Matrix4();
	Vector3 carPosition = new Vector3();
	Vector3 cameraPosition = new Vector3();

	public void updateCameraPosition(int playerNum) {
		Planet.INSTANCE.cars.get(playerNum).chassis.motionState.getWorldTransform(worldTransform);

		worldTransform.getTranslation(carPosition);
		cameraPosition.set(carPosition);

		cameraPosition.set(cameraPosition.x - 10f, cameraPosition.y + 20f, cameraPosition.z - 15f);

		Planet.INSTANCE.camera.position.set(cameraPosition);
		Planet.INSTANCE.camera.lookAt(carPosition);
        Planet.INSTANCE.camera.up.set(Vector3.Y);
	}

	public void update () {
		Planet.INSTANCE.world.update();

		for (Car car : Planet.INSTANCE.cars)
		{
			car.update();
       	}

       	for (ColorChanger changer : Planet.INSTANCE.level.changers) {
       		changer.update();
       	}

       	for (Collectible collectible : Planet.INSTANCE.level.collectibles) {
       		collectible.update();
       	}

       	for (Trigger trigger : Planet.INSTANCE.level.triggers) {
       		trigger.update();
       	}
	}

	@Override
	public void dispose () {
		Planet.INSTANCE.world.dispose();
		Planet.INSTANCE.world = null;

		for (Disposable disposable : Planet.INSTANCE.disposables)
			disposable.dispose();
		Planet.INSTANCE.disposables.clear();

		Planet.INSTANCE.modelBatch.dispose();
		Planet.INSTANCE.modelBatch = null;

		light = null;

		super.dispose();

		// UI
		stage.dispose();
	}

	@Override
    public boolean keyDown (int keycode) {
        switch (keycode) {
        	case Keys.SPACE:
            	//
            	break;
        }
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        switch (keycode) {
        	case Keys.R:
            	//
            	break;
            case Keys.C:
            	Planet.INSTANCE.level.clearLevel();
            	break;
            case Keys.L:
            	Planet.INSTANCE.level.saveToFile();
            	break;
            case Keys.O:
            	Planet.INSTANCE.level.loadFromFile();
            	break;
        }
        return false;
    }

	public void touchDownDesktop (int screenX, int screenY, int pointer, int button) {
		Ray ray = Planet.INSTANCE.camera.getPickRay(screenX, screenY);
		rayFrom.set(ray.origin);
		rayTo.set(ray.direction).scl(50f).add(rayFrom);

		rayTestCB.setCollisionObject(null);
		rayTestCB.setClosestHitFraction(1f);
		rayTestCB.getRayFromWorld().setValue(rayFrom.x, rayFrom.y, rayFrom.z);
		rayTestCB.getRayToWorld().setValue(rayTo.x, rayTo.y, rayTo.z);

		Planet.INSTANCE.world.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);

		if (rayTestCB.hasHit()) {
			btVector3 p = rayTestCB.getHitPointWorld();

			if (button == 0) {
				ColorChanger changer = new ColorChanger(new Vector3(p.getX(), p.getY() - 0.5f, p.getZ()));
			} else {
				Ball ball = new Ball(3, new Color(1f, 1f, 0f, 1f));
				ball.setPos(p.getX(), p.getY() + 1f, p.getZ());
			}
		}
	}

	int startedX = 0;
	int startedY = 0;

	public void touchDownMobile (int screenX, int screenY, int pointer, int button) {
		if (screenY < 200) {
			if (screenX < Gdx.graphics.getWidth() / 2) {
    			Planet.INSTANCE.cars.get(0).reset();
    		} else {
    			Planet.INSTANCE.level.clearLevel();
    			Planet.INSTANCE.level.loadFromFile();
    		}

    		return;
    	}

    	startedX = screenX;

    	int width = Gdx.graphics.getWidth();
    	if (screenX > width / 2) {
    		Planet.INSTANCE.cars.get(0).downPressed = true;
    	}
    	else {
    		Planet.INSTANCE.cars.get(0).upPressed = true;
    	}
	}

	@Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
    	if ((Gdx.app.getType() == ApplicationType.Android ||
    		Gdx.app.getType() == ApplicationType.iOS) &&
    		Controllers.getControllers().size == 0) {

    		touchDownMobile(screenX, screenY, pointer, button);
    	}
    	else
    		touchDownDesktop(screenX, screenY, pointer, button);

    	return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
    	if (Gdx.app.getType() == ApplicationType.Android ||
    		Gdx.app.getType() == ApplicationType.iOS) {

    		startedX = 0;
        
    		Planet.INSTANCE.cars.get(0).currentAngle = 0f;

        	Planet.INSTANCE.cars.get(0).upPressed = false;
        	Planet.INSTANCE.cars.get(0).downPressed = false;
        }

        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
    	if (Gdx.app.getType() == ApplicationType.Android ||
    		Gdx.app.getType() == ApplicationType.iOS) {

    		int x = (screenX - startedX) * -1;
    		if (x > 200) x = 200;
    		if (x < -200) x = -200;

    		if (startedX != 0) {
    			Planet.INSTANCE.cars.get(0).currentAngle = MonsterUtils.map(x, -200f, 200f,
    				-Planet.INSTANCE.cars.get(0).maxAngle,
    				Planet.INSTANCE.cars.get(0).maxAngle);
    		}
    	}

        return false;
    }    
}
