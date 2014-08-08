package com.allsrc.monstertrucks;

import com.badlogic.gdx.ApplicationListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;

import com.badlogic.gdx.physics.bullet.linearmath.*;

public class MonsterTrucks implements ApplicationListener {

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
	public Label activeObjectLabel;

	// rays

    MonsterListener monsterListener;

	@Override
	public void create () {
		init();

        Planet.INSTANCE.main = this;

        monsterListener = new MonsterListener();
        monsterListener.init();

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

		for (int i = 0; i < numPlayers; i++)
		{
			Color c = Color.RED;
			if (i == 1)
				c = Color.BLACK;

			Planet.INSTANCE.cars.add((Car)new MonsterTruck(new Vector3(i * 5f, 3f, 0f), c));

			if (i < Controllers.getControllers().size)
				Controllers.getControllers().get(i).addListener(Planet.INSTANCE.cars.get(i));
		}

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		inputMultiplexer.addProcessor(monsterListener);
		inputMultiplexer.addProcessor(stage);

		activeObjectLabel = new Label("Object", skin);

		Table table = new Table();
		table.left().bottom();
    	table.setFillParent(true);
	    stage.addActor(table);
	    table.add(activeObjectLabel).width(100);

		Planet.INSTANCE.level.loadFromFile();
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
		Planet.INSTANCE.cars.get(playerNum).entity.motionState.getWorldTransform(worldTransform);

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

		// UI
		stage.dispose();
	}

    @Override
    public void resize (int width, int height) {
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }
}
