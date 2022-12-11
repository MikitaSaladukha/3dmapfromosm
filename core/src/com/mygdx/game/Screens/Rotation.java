package com.mygdx.game.Screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.game.Geo;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Utils.SkyBox;
import com.mygdx.game.Utils.TestShader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.math.Plane3D;
import com.vividsolutions.jts.math.Vector3D;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osm4j.core.access.OsmInputException;

import java.io.IOException;

import static com.badlogic.gdx.graphics.VertexAttributes.*;

public class Rotation implements InputProcessor, Screen {

    private PerspectiveCamera camera;
    private CameraInputController camController;
    private ModelBatch modelBatch;
    private ModelBuilder modelBuilder;
    private MeshBuilder meshBuilder;
    private MercatorImage mercatorImage;
    private BitmapFont font;
    private SpriteBatch batch;

    private Model box;
    private Mesh mesh;
    private Model sphere;
    private ModelInstance modelInstance;
    private Environment environment;
    private final Vector3 zeros = new Vector3(0f,0f,0f);
    private final Vector3 tempDir = new Vector3();
    private final Vector3 yRotateVector = new Vector3(0f,1f,0f);
    private final Vector3 xRotateVector = new Vector3(1f,0f,0f);
    private final Vector3 zRotateVector = new Vector3(0f,0f,1f);
    private TestShader shader;
    final MyGdxGame game;
    public Menu menu;

    private boolean pressedRight;
    private boolean pressedLeft;
    private boolean pressedUp;
    private boolean pressedDown;
    private boolean pressedW;
    private boolean pressedS;
    private boolean pressedA;
    private boolean pressedD;
    private Model gridModel, streetModel, buildModel, railsModel, waterModel, leisureModel;
    private ModelInstance gridInstance;
    private ModelInstance streetInstance, buildInstance, railInstance, waterInstance, leisureInstance, plInst;

    private float gridMin = -50f;
    private float gridMax = 50f;
    private float gridMaxZ = 50f;
    private float gridMaxX = 50f;
    private final float scale = 1f;

    Geo geo;
    SkyBox skyBox;

    Plane3D pl;
    ShaderProgram program;
    DefaultShader sh;
    String vertexShader;
    String fragmentShader;
    Renderable renderable;
    RenderContext renderContext;


    public Rotation(final MyGdxGame myGdxGame) throws IOException, OsmInputException {
        game = myGdxGame;
        //menu = new Menu(game);
        //Gdx.graphics.setWindowedMode(1920, 1080);

        camera = new PerspectiveCamera(100, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 30f);
        camera.lookAt(0f, 4f, 0f);
        camera.near = 0.2f;
        camera.far = 5000f;
        camController = new CameraInputController(camera);

        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f,0.8f,0.8f,1f));
       // environment.add(new DirectionalLight().set(0.8f, 0.3f, 0.8f, 0f, -0.5f, 0f));

        environment.set(new ColorAttribute( ColorAttribute.AmbientLight, .6f, .6f, .6f, 1f));
        //environment.add(new DirectionalLight().set(.8f, .8f, .8f, 50f, 50f, 50f));
        //environment.add(new DirectionalLight().set(.5f, .5f, .5f, -50f, -50f, 50f));
        skyBox = new SkyBox(new Pixmap(Gdx.files.internal("skybox.jpg")));
        font = new BitmapFont();
        batch = new SpriteBatch();

        vertexShader = Gdx.files.internal("C:\\Users\\ilyas\\Downloads\\test\\core\\shader\\vertShader.glsl").readString();
        fragmentShader = Gdx.files.internal("C:\\Users\\ilyas\\Downloads\\test\\core\\shader\\fragShader.glsl").readString();
        renderable = new Renderable();
        //program = new ShaderProgram(vertexShader,fragmentShader);
        //sh = new DefaultShader(new Renderable(), new DefaultShader.Config(vertexShader, fragmentShader));
        //sh.init();
        //if (!program.isCompiled())
          //  throw new GdxRuntimeException(program.getLog());

        //BBox bbox = new BBox(13.45546, 52.51229, 13.46642, 52.50761);
        //mercatorImage = new MercatorImage(bbox, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        /*
        System.out.println(mercatorImage.getWidth() + " w H " + mercatorImage.getHeight());
        System.out.println(mercatorImage.getImageSx() + " sx Sy " + mercatorImage.getImageSy());
        System.out.println(mercatorImage.getWorldSize());
        System.out.println(mercatorImage.getVisibleBoundingBox());
        System.out.println(mercatorImage.getX(13.46642) + " x Y " + mercatorImage.getY(52.50761));
        System.out.println(mercatorImage.getX(13.45546) + " x Y " + mercatorImage.getY(52.51229));

        gridMaxX = (float) ((mercatorImage.getX(13.46642) - mercatorImage.getX(13.45546)));
        gridMaxZ = (float) ((mercatorImage.getY(52.50761) - mercatorImage.getY(52.51229)));
        //System.out.println(gridMaxX + "grMaxX");
        //System.out.println(gridMaxZ + "grmaxZ");
        //(-25.783539,0.887781,-493.0364)
        //(26.590761,8.691451,492.60114)a

       // box = modelBuilder.createBox((float) 1412.4958,0.2f, (float) 991.0,
        //        new Material(ColorAttribute.createEmissive(Color.BLUE)),
         //       Usage.Position| Usage.Normal);
        //modelInstance = new ModelInstance(box, 0,0,0);
       */
        createGrid();
        geo = new Geo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        geo.collectData();
        ModelBuilder b  = new ModelBuilder();
        b.begin();
        buildModel = geo.buildBuilding();
        NodePart buildPart = buildModel.nodes.get(0).parts.get(0);
        buildPart.setRenderable(renderable);
        streetModel = geo.buildStreets();
        railsModel = geo.buildRails();
        waterModel = geo.buildWater();
        leisureModel = geo.buildLeisures();
        b.manage(buildModel);
        b.manage(streetModel);
        b.manage(railsModel);
        b.manage(waterModel);
        b.manage(leisureModel);
        buildInstance = new ModelInstance(buildModel);
        streetInstance = new ModelInstance(streetModel);
        railInstance = new ModelInstance(railsModel);
        waterInstance = new ModelInstance(waterModel);
        leisureInstance = new ModelInstance(leisureModel);
        b.end();

        camera.position.set(Gdx.graphics.getWidth()/2 , 4, Gdx.graphics.getHeight()/2);
        System.out.println(camera.direction);
        renderable.environment = environment;
        renderable.worldTransform.idt();

        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.MAX_GLES_UNITS, 1));
        shader = new TestShader();
        shader.init();
        //regionBuilder = new RegionBuilder();
       // wayBuilder = new WayBuilder();
        //Geo geo = new Geo();
        //geo.collectData();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

       /* float effectiveViewportWidth = camera.viewportWidth;
        float effectiveViewportHeight = camera.viewportHeight;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
        */
        if(pressedRight)
            camera.rotateAround(camera.position, yRotateVector, -1f);
        if(pressedLeft)
            camera.rotateAround(camera.position, yRotateVector, 1f);
        if(pressedUp)
           camera.rotateAround(camera.position, zRotateVector, 1f);
        if (pressedDown)
            camera.rotateAround(camera.position, zRotateVector, -1f);
        if (pressedW){
            camera.position.add(tempDir.set(camera.direction).scl(2f));
            //camera.position.set(tempDir.scl(camera.far));
        }
        if (pressedS){
            camera.position.add(tempDir.set(camera.direction).scl(-0.5f));
        }


        camera.update();
        geo.setCamPos(camera.position);
        //program.begin();
        //program.bind();
        //skyBox.render(camera);
        //sh.begin(camera, );
        renderContext.begin();
        shader.begin(camera, renderContext);

        modelBatch.begin(camera);
        modelBatch.render(gridInstance);
        modelBatch.render(streetInstance);
        modelBatch.render(railInstance);
        //modelBatch.render( modelInstance, environment);
        shader.render(renderable);
        modelBatch.render( buildInstance);
        modelBatch.render(waterInstance);
        modelBatch.render(leisureInstance);
        //program.end();
        try {
            if (geo.refreshMercatorImage()){
                modelBatch.end();
                geo.collectData();
                buildModel.dispose();
                streetModel.dispose();
                railsModel.dispose();
                waterModel.dispose();
                leisureModel.dispose();
                ModelBuilder b  = new ModelBuilder();
                b.begin();
                buildModel = geo.buildBuilding();
                streetModel = geo.buildStreets();
                railsModel = geo.buildRails();
                waterModel = geo.buildWater();
                leisureModel = geo.buildLeisures();
                b.manage(buildModel);
                b.manage(streetModel);
                b.manage(railsModel);
                b.manage(waterModel);
                b.manage(leisureModel);
                buildInstance = new ModelInstance(buildModel);
                streetInstance = new ModelInstance(streetModel);
                railInstance = new ModelInstance(railsModel);
                waterInstance = new ModelInstance(waterModel);
                leisureInstance = new ModelInstance(leisureModel);
                b.end();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (OsmInputException e) {
            throw new RuntimeException(e);
        }
        /*
        batch.begin();
        font.setColor(Color.GREEN);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        try {
            font.draw(batch, geo.getStreetName(), 40, 50);
        }catch (NullPointerException ex){}
        font.draw(batch, camera.position.toString(), 40, 70);
        batch.end();
        modelBatch.end();

         */
        skyBox.render(camera);
        modelBatch.end();
        shader.end();
        renderContext.end();
        InputMultiplexer inputMultiplexer = new InputMultiplexer(this, camController);
        Gdx.input.setInputProcessor(inputMultiplexer);
        //System.out.println(camera.position);
    }

    private void createGrid() {
        ModelBuilder modelBuilder = new ModelBuilder();

        modelBuilder.begin();


        MeshPartBuilder builder = modelBuilder.part("gridpart1", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
        builder.setColor(new Color(Color.LIGHT_GRAY));

        for(float z = gridMin; z < gridMax; z += scale){
            for(float x = gridMin; x < gridMax; x += scale){
                builder.line(x, 0.0F, z, x+scale, 0.0f, z);
            }
        }
        // So we add another part with a different id
       builder = modelBuilder.part("gridpart2", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
        for(float z = gridMin; z < gridMax; z += scale){
            for(float x = gridMin; x < gridMax; x += scale){
                builder.line(x, 0.0f, z, x, 0.0f, z+scale);
            }
        }
        builder = modelBuilder.part("gridpart3", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
        builder.setColor(Color.GREEN);
        builder.line(-100.0f, 0.0f, 0.0f, 100.0f, 0.0f, 0.0f);
        builder.line(0.0f, 0.0f, -100.0f, 0.0f, 0.0f, -100.0f);
        builder.line(0.0f, -100.0f, 0.0f, 0.0f, 100.0f, 0.0f);
        builder.sphere(5.0f, 5.0f, 5.0f, 9, 9);
        gridModel = modelBuilder.end();
        gridInstance = new ModelInstance(gridModel);
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
       /* modelBatch.dispose();
        box.dispose();
        shader.dispose();*/
       //super.dispose();
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
            case Input.Keys.W:
                pressedW = true;
                break;
            case Input.Keys.S:
                pressedS = true;
                break;
            case Input.Keys.A:
                pressedA = true;
                break;
            case Input.Keys.D:
                pressedD = true;
                break;
            case Input.Keys.ESCAPE: {
                System.out.println("Exit done!");
                game.setScreen(menu);
                Gdx.input.setInputProcessor(menu.buttons);
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
            case Input.Keys.W:
                pressedW = false;
            case Input.Keys.S:
                pressedS = false;
            case Input.Keys.A:
                pressedA = false;
            case Input.Keys.D:
                pressedD = false;
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
