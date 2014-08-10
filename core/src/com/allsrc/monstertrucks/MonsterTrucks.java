package com.allsrc.monstertrucks;

import com.badlogic.gdx.ApplicationListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class MonsterTrucks implements ApplicationListener {

	private boolean initialized;

	public Editor editor;
	public Skin skin;
	public Stage stage;

    MonsterListener monsterListener;

	public void init() {
		if (initialized)
			return;

		Bullet.init();
		initialized = true;
	}

	@Override
	public void create () {
		init();

        Planet.EX.main = this;

        Planet.EX.modelBatch = new ModelBatch();
        Planet.EX.settings = new Settings();
        Planet.EX.world = new BulletWorld();

        Planet.EX.settings.width = Gdx.graphics.getWidth();
        Planet.EX.settings.height = Gdx.graphics.getHeight();

        monsterListener = new MonsterListener();

		stage = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		switch (Planet.EX.settings.playerCount) {
			case 1:
				Planet.EX.camera = new PerspectiveCamera(
					70f,
					3f * Planet.EX.settings.width / Planet.EX.settings.height,
					3f);
				break;
			case 2:
				Planet.EX.camera = new PerspectiveCamera(
					70f,
					3f * Planet.EX.settings.width / (Planet.EX.settings.height / 2),
					3f);
				break;

			case 3:
			case 4:
				break;
		}

		for (int i = 0; i < Planet.EX.settings.playerCount; i++)
		{
			Color c = MonsterColor.randomColor();
			Planet.EX.cars.add((Car)new MonsterTruck(new Vector3(i * 5f, 3f, 0f), c));

			if (i < Controllers.getControllers().size)
				Controllers.getControllers().get(i).addListener(Planet.EX.cars.get(i));
		}

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		inputMultiplexer.addProcessor(monsterListener);
		inputMultiplexer.addProcessor(stage);

		Planet.EX.level = new Level();
		Planet.EX.level.loadFromFile();

		editor = new Editor();
	}

	@Override
	public void render () {
		update();

		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		switch (Planet.EX.settings.playerCount) {
			case 1:
				renderScreen();
				break;
			case 2:
				renderSplitScreen();
				break;

			case 3:
			case 4:
				break;
		}

		// UI
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	public void renderScreen() {
		updateCameraPosition(0);
		Planet.EX.camera.update();

		Planet.EX.modelBatch.begin(Planet.EX.camera);
		// Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// Planet.EX.world.render(Planet.EX.modelBatch, Planet.EX.level.environment);
		for (BulletObject obj : Planet.EX.level.bulletObjects) {
			if (isVisible(Planet.EX.camera, obj.entity.modelInstance)) {
				Planet.EX.modelBatch.render(obj.entity.modelInstance, Planet.EX.level.environment);
			}
		}

		Planet.EX.modelBatch.render(Planet.EX.level.terrain.entity.modelInstance, Planet.EX.level.environment);
		Planet.EX.modelBatch.render(Planet.EX.cars.get(0).entity.modelInstance, Planet.EX.level.environment);
	
		Planet.EX.modelBatch.end();
	}

	private Vector3 position = new Vector3();
	protected boolean isVisible(final Camera cam, final ModelInstance instance) {
    	instance.transform.getTranslation(position);
    	return cam.frustum.pointInFrustum(position);
	}

	public void renderSplitScreen() {
		updateCameraPosition(0);
		Planet.EX.camera.update();

		Planet.EX.modelBatch.begin(Planet.EX.camera);
		Gdx.gl.glViewport(0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
		
		Planet.EX.world.render(Planet.EX.modelBatch, Planet.EX.level.environment);
		Planet.EX.modelBatch.end();
		
		updateCameraPosition(1);
		Planet.EX.camera.update();

		Planet.EX.modelBatch.begin(Planet.EX.camera);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
		
		Planet.EX.world.render(Planet.EX.modelBatch, Planet.EX.level.environment);
		Planet.EX.modelBatch.end();
	}

	// context of truck
	Matrix4 worldTransform = new Matrix4();
	Vector3 carPosition = new Vector3();
	Vector3 cameraPosition = new Vector3();

	public void updateCameraPosition(int playerNum) {
		Planet.EX.cars.get(playerNum).entity.motionState.getWorldTransform(worldTransform);

		worldTransform.getTranslation(carPosition);
		cameraPosition.set(carPosition);

		cameraPosition.set(cameraPosition.x - 10f, cameraPosition.y + 20f, cameraPosition.z - 15f);

		Planet.EX.camera.position.set(cameraPosition);
		Planet.EX.camera.lookAt(carPosition);
        Planet.EX.camera.up.set(Vector3.Y);
	}

	public void update () {
		Planet.EX.world.update();

		for (Car car : Planet.EX.cars)
			car.update();

       	for (Collectible collectible : Planet.EX.level.collectibles)
       		collectible.update();

       	for (Trigger trigger : Planet.EX.level.triggers)
       		trigger.update();
	}

	@Override
	public void dispose () {
		Planet.EX.world.dispose();
		Planet.EX.world = null;

		for (Disposable disposable : Planet.EX.disposables)
			disposable.dispose();

		Planet.EX.disposables.clear();

		Planet.EX.modelBatch.dispose();
		Planet.EX.modelBatch = null;

		Planet.EX.level.environment = null;
		Planet.EX.level.light = null;

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
