package com.mygdx.game.Screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Screens.Menu;

public class TriangleRotation implements InputProcessor, Screen {

    private PerspectiveCamera camera;
    private CameraInputController camController;
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private Model triangle;
    private ModelInstance modelInstance;
    private Environment environment;
    private final Vector3 zeros = new Vector3(0f,0f,0f);
    private final Vector3 yRotateVector = new Vector3(0f,1f,0f);
    private final Vector3 xRotateVector = new Vector3(1f,0f,0f);
    private final Vector3 zRotateVector = new Vector3(0f,0f,1f);
    final MyGdxGame game;
    public Menu menu;

    private boolean pressedRight;
    private boolean pressedLeft;
    private boolean pressedUp;
    private boolean pressedDown;

    public TriangleRotation(final MyGdxGame myGdxGame){
        game = myGdxGame;
        //menu = new Menu(game);

        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 3f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 400f;
        camController = new CameraInputController(camera);

        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        triangle = modelBuilder.createCone(2f,2f,2f,32,
                new Material(ColorAttribute.createEmissive(Color.BLUE)),
                VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal);
        modelInstance = new ModelInstance(triangle, 0, 0, 0);
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f,0.8f,0.8f,1f));
        environment.add(new DirectionalLight().set(0.3f, 0.3f, 0.8f, 0f, -0.1f, 2f));

    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

        if(pressedRight)
            camera.rotateAround(zeros, yRotateVector, -1f);
        if(pressedLeft)
            camera.rotateAround(zeros, yRotateVector, 1f);
        if(pressedUp)
            camera.rotateAround(zeros, xRotateVector, 1f);
        if (pressedDown)
            camera.rotateAround(zeros, xRotateVector, -1f);
        camera.update();
        modelBatch.begin(camera);
        modelBatch.render( modelInstance, environment);
        modelBatch.end();

        InputMultiplexer inputMultiplexer = new InputMultiplexer(this, camController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose(){
        modelBatch.dispose();
        triangle.dispose();
        //shader.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode){
            case Input.Keys.LEFT:
                pressedLeft = true;
                break;
            case Input.Keys.RIGHT:
                pressedRight = true;
                break;
            case Input.Keys.UP:
                pressedUp = true;
                break;
            case Input.Keys.DOWN:
                pressedDown = true;
                break;
            case Input.Keys.ESCAPE: {
                System.out.println("Exit done!");
                game.setScreen(menu);
                //Gdx.input.setInputProcessor(menu.buttons);
                dispose();
                break;
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode){
            case Input.Keys.LEFT:
                pressedLeft = false;
                break;
            case Input.Keys.RIGHT:
                pressedRight = false;
                break;
            case Input.Keys.UP:
                pressedUp = false;
                break;
            case Input.Keys.DOWN:
                pressedDown = false;
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}

