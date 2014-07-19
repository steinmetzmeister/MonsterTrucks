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

	boolean initialized;
	
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

	public Array<MonsterTruck> trucks = new Array<MonsterTruck>();

	@Override
	public void create () {
		init();

		stage = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		final float width = Gdx.graphics.getWidth();
		final float height = Gdx.graphics.getHeight();
		
		Planet.INSTANCE.camera = new PerspectiveCamera(67f, 3f * width / height, 3f);

		Planet.INSTANCE.modelBatch = new ModelBatch();
		Planet.INSTANCE.world = new BulletWorld();

		//
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
		
		light = new DirectionalLight();
		light.set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f);

		environment.add(light);
		//

		// TERRAIN
		final Model model = objLoader.loadModel(Gdx.files.internal("data/terrain.obj"));

		model.meshes.get(0).scale(2f, 2f, 2f);
        model.materials.get(0).clear();
        model.materials.get(0).set(ColorAttribute.createDiffuse(Color.GREEN), ColorAttribute.createSpecular(Color.WHITE));

		Planet.INSTANCE.world.addConstructor("terrain", new BulletConstructor(model, 0f, new btBvhTriangleMeshShape(model.meshParts)));
		Planet.INSTANCE.world.add("terrain", 0f, 0f, 0f);

		int i = 0;
		for (Controller controller : Controllers.getControllers())
		{
			trucks.add(new MonsterTruck());
			controller.addListener(trucks.get(i));

			i++;
		}

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

		Planet.INSTANCE.modelBatch.begin(Planet.INSTANCE.camera);
		Planet.INSTANCE.world.render(Planet.INSTANCE.modelBatch, environment);
		Planet.INSTANCE.modelBatch.end();

		// UI
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	// context of truck
	Matrix4 worldTransform = new Matrix4();
	Vector3 truckPosition = new Vector3();
	Vector3 cameraPosition = new Vector3();

	public void update () {
		Planet.INSTANCE.world.update();

		for (MonsterTruck truck : trucks)
		{
			truck.update();

			truck.chassis.motionState.getWorldTransform(worldTransform);

			worldTransform.getTranslation(truckPosition);
			cameraPosition.set(truckPosition);

			cameraPosition.set(cameraPosition.x - 5f, cameraPosition.y + 12f, cameraPosition.z - 10f);

			Planet.INSTANCE.camera.position.set(cameraPosition);
			Planet.INSTANCE.camera.lookAt(truckPosition);
        	Planet.INSTANCE.camera.up.set(Vector3.Y);
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
        	}
        return false;
    }
}
