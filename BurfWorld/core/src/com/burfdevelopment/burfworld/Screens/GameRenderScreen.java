package com.burfdevelopment.burfworld.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.burfdevelopment.burfworld.Entity.GameObject;
import com.burfdevelopment.burfworld.Skybox;
import com.burfdevelopment.burfworld.Utils.ControlsController;

/**
 * Created by burfies1 on 25/07/15.
 */
public class GameRenderScreen  implements Screen {

    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private PerspectiveCamera camera;
    private ControlsController fps;

    public static int width() { return Gdx.graphics.getWidth(); }
    public static int height() { return Gdx.graphics.getHeight(); }

    private ModelBatch modelBatch;
    private Model box;

    private Array<GameObject> boxInstance;

    @Override
    public void show() {

        //TODO create one asset manager
        stage = new Stage();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        Skybox.createSkyBox();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.5f;
        camera.far = 1000;
        fps = new ControlsController(camera , this);
        Gdx.input.setInputProcessor(fps);

        Cube();

//        //todo do something useful here
//        Skin skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
//        androidGameControls = new AndroidGameControls();
//        androidGameControls.buildGameControls(stage, skin);
    }

    public void update(){

        fps.update();
        camera.position.set(camera.position.x, 0.0f, camera.position.z);

        //TODO WE NEED TO DO SOME TIME THING
        final float delta2 = Math.min(1f/30f, Gdx.graphics.getDeltaTime());

        //updateControls();
//
//        //TODO WE NEED TO DO SOME TIME THING
//        if(!AndroidGameControls.isOnDesktop()){
//            androidGameControls.updateControls(player);
//        }
    }

    @Override
    public void render(float delta) {

        update();

        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //TODO do we need all of these?
        //Do all your basic OpenGL ES setup to start the screen render.
        Gdx.gl20.glClearColor(0.0f, 0.3f, 0.5f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl20.glCullFace(GL20.GL_NONE);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);

        // Like spriteBatch, just with models!  pass in the box Instance and the environment
        modelBatch.begin(camera);

        Skybox.update(camera.position);
        modelBatch.render(Skybox.modelInstance);

        for (int i = 0; i < boxInstance.size; i ++ ) {
            modelBatch.render(boxInstance.get(i));
        }

        modelBatch.end();

        //TODO Whats this for
        stage.getViewport().update(width(), height(), true);
        stage.act(delta);
        stage.draw();

        drawFPS();
    }

    @Override
    public void resize(int width, int height) {
        //camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
    public void dispose() {

    }

    public void drawFPS() {
        stage.getBatch().begin();
        font.draw(stage.getBatch(), "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 10);
        font.draw(stage.getBatch(), "Po: " + camera.position.y, 10, Gdx.graphics.getHeight() - 30);
        stage.getBatch().end();
    }

    public int getObject (int screenX, int screenY) {

        int result = -1;
        float distance = -1;

        Ray ray = camera.getPickRay(screenX, screenY);
        Vector3 pos = new Vector3(camera.position);

        for (int i = 0; i < boxInstance.size; i++) {

            GameObject instance = boxInstance.get(i);
            instance.transform.getTranslation(pos);

            float dist2 = ray.origin.dst2(pos);
            if (distance >= 0f && dist2 > distance) continue;

            if (Intersector.intersectRayBoundsFast(ray, pos, instance.dimensions)) {
                result = i;
                distance = dist2;
            }
        }

        if (result > -1)
        {
            boxInstance.removeIndex(result);
        }

        return 1;
    };


    public void Cube() {

        boxInstance = new Array();
//		cInstance = new Array();
//		sInstance = new Array();

        // A ModelBatch is like a SpriteBatch, just for models.  Use it to batch up geometry for OpenGL
        modelBatch = new ModelBatch();

        // A ModelBuilder can be used to build meshes by hand
        ModelBuilder modelBuilder = new ModelBuilder();

        // It also has the handy ability to make certain premade shapes, like a Cube
        // We pass in a ColorAttribute, making our cubes diffuse ( aka, color ) red.
        // And let openGL know we are interested in the Position and Normal channels
        box = modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        // A model holds all of the information about an, um, model, such as vertex data and texture info
        // However, you need an instance to actually render it.  The instance contains all the
        // positioning information ( and more ).  Remember Model==heavy ModelInstance==Light

        int c = 5;
        for (int x = 0; x < c; x ++ ) {

            for (int y = 0; y < c; y ++ ) {

                for (int z = 0; z < c; z ++ ) {

                    GameObject m = new GameObject(box,x,y,z);
                    boxInstance.add(m);
                }

            }

        }

    }

}


