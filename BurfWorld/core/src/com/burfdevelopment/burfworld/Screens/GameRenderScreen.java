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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
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

    private Stage stage = new Stage();
    private SpriteBatch batch;
    private BitmapFont font;
    private PerspectiveCamera camera;
    private ControlsController fps;

    public static int width() { return Gdx.graphics.getWidth(); }
    public static int height() { return Gdx.graphics.getHeight(); }

    private ModelBatch modelBatch;
    private Model box;
    private Model floor;

    private Array<GameObject> boxInstance;

    @Override
    public void show() {

        //TODO create one asset manager
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        Skybox.createSkyBox();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.5f;
        camera.far = 1000;
        fps = new ControlsController(camera , this, stage);
        //Gdx.input.setInputProcessor(fps);

        Cube();

    }

    public void update(){
        fps.updateControls();
        fps.update();
        camera.position.set(camera.position.x, 0.0f, camera.position.z);

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
        Gdx.gl20.glCullFace(GL20.GL_BACK);
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

//    public void hitSomething(int screenX, int screenY) {
//        Ray pickRay = camera.getPickRay(screenX, screenY);
//
//        // A bounding box for each of your minecraft blocks
//        BoundingBox boundingBox = new BoundingBox();
//        Vector3 intersection = new Vector3();
//        if (Intersector.intersectRayBounds(pickRay, boundingBox, intersection)) {
//            // The ray has hit the box, intersection is the point it hit
//            Gdx.app.log("MyTag", "my informative message");
//        } else {
//            // Not hit
//            Gdx.app.log("MyTag", "my informative message2");
//        }
//    }

//    public void hitSomething(int screenX, int screenY) {
//        // If you are only using a camera
//        Ray pickRay = camera.getPickRay(screenX, screenY);
////        // If your camera is managed by a viewport
//        //Ray pickRay = stage.getViewport().getPickRay(screenX, screenY);
//
//        // we want to check a collision only on a certain plane, in this case the X/Z plane
//        Plane plane = new Plane(new Vector3(0, -1, 0), -1);
//        Vector3 intersection = new Vector3();
//
//        Intersector.intersectRayPlane(pickRay, plane, intersection);
//
//        int x = (int)intersection.x;
//        int z = (int)intersection.z;
//
//        Gdx.app.log("MyTag", "x " + x  + " z "+ z);
//
//    }

    public int getObject (int screenX, int screenY) {

        int result = -1;
        float distance = -1;

        Ray ray = camera.getPickRay(screenX, screenY);
        Vector3 pos = new Vector3(camera.position);
        Vector3 v = new Vector3();
        
        for (int i = 0; i < boxInstance.size; i++) {

            GameObject instance = boxInstance.get(i);
            instance.transform.getTranslation(pos);
            instance.updateBox();

            float dist2 = ray.origin.dst2(pos);
            if (distance >= 0f && dist2 > distance) continue;



            if (Intersector.intersectRayBounds(ray, instance.bounds, v))
            {
                result = i;
                distance = dist2;


            }

//            if (Intersector.intersectRayBoundsFast(ray, pos, instance.dimensions)) {
//                result = i;
//                distance = dist2;
//
//
//            }
        }

        if (result > -1)
        {
            Gdx.app.log("MyTag 2", "x " + v.x + " y " + v.y +  " z " + v.z);
            boxInstance.get(result).materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));
            //boxInstance.removeIndex(result);
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
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
        );

        // A model holds all of the information about an, um, model, such as vertex data and texture info
        // However, you need an instance to actually render it.  The instance contains all the
        // positioning information ( and more ).  Remember Model==heavy ModelInstance==Light

        int c = 5;
        for (int x = 0; x < c; x ++ ) {

            for (int y = 0; y < c; y ++ ) {

                for (int z = 0; z < c; z ++ ) {

                    GameObject m = new GameObject(box,new Vector3(x,y,z));
                    boxInstance.add(m);
                }

            }

        }

        Material matWhite = new Material(ColorAttribute.createDiffuse(Color.WHITE));

        float size = 10;
        modelBuilder.begin();
        MeshPartBuilder tileBuilder;
        tileBuilder = modelBuilder.part("top", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, matWhite);
        tileBuilder.rect(-size, 0.1f, size,   size, 0.1f, size,    size, 0.1f, -size,  -size, 0.1f, -size,  0f, 1f, 0f);
        floor = modelBuilder.end();

        GameObject m = new GameObject(floor, new Vector3(0,-1,0));
        boxInstance.add(m);


    }

}


