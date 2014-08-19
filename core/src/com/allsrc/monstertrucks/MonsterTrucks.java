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

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Pixmap;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class MonsterTrucks implements ApplicationListener {

    private Vector3 tempV = new Vector3();

	private boolean initialized;

	public Skin skin;
	public Stage stage;

    MonsterListener monsterListener;

    protected ModelBatch modelBatch;

    protected FrameBuffer buffer;
    protected FrameBuffer dest;
    protected FrameBuffer src;

	public void init() {
		if (initialized)
			return;

		Bullet.init();
		initialized = true;
	}

	private ShaderProgram celShader;
	private Mesh fullScreenQuad;

	@Override
	public void create () {
		init();

        stage = new Stage();
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		celShader = new ShaderProgram(
    		Gdx.files.internal("data/shaders/cel.vertex.glsl"),
    		Gdx.files.internal("data/shaders/cel.fragment.glsl"));
		fullScreenQuad = createFullScreenQuad();

		buffer = new FrameBuffer(Pixmap.Format.RGBA8888,
			Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		src = buffer;

        Planet.EX.main = this;

        modelBatch = new ModelBatch();
        Planet.EX.settings = new Settings();
        Planet.EX.world = new BulletWorld();

        Planet.EX.settings.width = Gdx.graphics.getWidth();
        Planet.EX.settings.height = Gdx.graphics.getHeight();

        monsterListener = new MonsterListener();
		Planet.EX.camera = new MonsterCamera(Planet.EX.settings.playerCount);

		Planet.EX.editor = new Editor();

		Planet.EX.level = new Level();
		Planet.EX.level.init();
		Planet.EX.level.loadFromFile();

		for (int i = 0; i < Planet.EX.settings.playerCount; i++)
		{
			Color c = MonsterColor.randomColor();
			Planet.EX.cars.add((Car)new RallyCar(new Vector3(i * 5f, 3f, 0f), c));

			if (i < Controllers.getControllers().size)
				Controllers.getControllers().get(i).addListener(Planet.EX.cars.get(i));
		}

		Planet.EX.cars.add((Car)new AICar(new Vector3(-5f, 3f, 0f), Color.RED));
		Planet.EX.cars.add((Car)new AICar(new Vector3(-5f, 3f, -5f), Color.RED));

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		inputMultiplexer.addProcessor(monsterListener);
		inputMultiplexer.addProcessor(stage);

        // inputMultiplexer.addProcessor(Planet.EX.editor.editorListener);
	}

	@Override
	public void render () {
		update();

		dest = buffer;

		dest.begin(); {
			
    		Gdx.gl.glCullFace(GL20.GL_BACK);
    		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    		Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
    		Gdx.gl.glDepthMask(true);

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
		}
		dest.end();

		src = dest;
		dest = buffer;

		src.getColorBufferTexture().bind(); {
    		celShader.begin();
        	fullScreenQuad.render(celShader, GL20.GL_TRIANGLE_FAN, 0, 4);
    		celShader.end();
    	}

		// UI
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	public Mesh createFullScreenQuad(){
    	float[] verts = new float[16];
    	int i = 0;

    	verts[i++] = -1.f; // x1
    	verts[i++] = -1.f; // y1
    	verts[i++] =  0.f; // u1
    	verts[i++] =  0.f; // v1

    	verts[i++] =  1.f; // x2
    	verts[i++] = -1.f; // y2
    	verts[i++] =  1.f; // u2
    	verts[i++] =  0.f; // v2

    	verts[i++] =  1.f; // x3
    	verts[i++] =  1.f; // y2
    	verts[i++] =  1.f; // u3
    	verts[i++] =  1.f; // v3

    	verts[i++] = -1.f; // x4
    	verts[i++] =  1.f; // y4
    	verts[i++] =  0.f; // u4
    	verts[i++] =  1.f; // v4

    	Mesh mesh = new Mesh(true, 4, 0,
        	new VertexAttribute(Usage.Position, 2, "a_position"),
        	new VertexAttribute(Usage.TextureCoordinates,
        	2, "a_texCoord0"));
    	mesh.setVertices(verts);

    	return mesh;
	}

	public void renderScreen() {
        Planet.EX.camera.focus(Planet.EX.cars.get(0));
		
		modelBatch.begin(Planet.EX.camera.get());
		renderObjects();
		modelBatch.end();
	}

	public void renderSplitScreen() {
		Planet.EX.camera.focus(Planet.EX.cars.get(0));
		modelBatch.begin(Planet.EX.camera.get());
		Gdx.gl.glViewport(0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
		renderObjects();
		modelBatch.end();
		
		//

		Planet.EX.camera.focus(Planet.EX.cars.get(1));
		modelBatch.begin(Planet.EX.camera.get());
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
		renderObjects();
		modelBatch.end();
	}

	public void renderObjects() {
        Planet.EX.level.terrain.render();

		for (BulletObject obj : Planet.EX.level.bulletObjects) {
			if (Planet.EX.camera.isVisible(obj))
				obj.render();
		}

		for (Car car : Planet.EX.cars) {
			car.render();
        }
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

		modelBatch.dispose();
		modelBatch = null;

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
