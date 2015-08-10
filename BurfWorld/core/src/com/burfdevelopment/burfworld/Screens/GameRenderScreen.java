package com.burfdevelopment.burfworld.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.burfdevelopment.burfworld.Constants;
import com.burfdevelopment.burfworld.Entity.Chunk;
import com.burfdevelopment.burfworld.Entity.ChunkObject;
import com.burfdevelopment.burfworld.Entity.GameObject;
import com.burfdevelopment.burfworld.Entity.MeshBuilder;
import com.burfdevelopment.burfworld.Skybox;
import com.burfdevelopment.burfworld.Utils.ControlsController;

/**
 * Created by burfies1 on 25/07/15.
 */
public class GameRenderScreen  implements Screen {

    private Stage stage = new Stage();
    //private SpriteBatch batch;
    private BitmapFont font;
    private PerspectiveCamera camera;
    private ControlsController fps;
    private Environment lights;

    public static int width() { return Gdx.graphics.getWidth(); }
    public static int height() { return Gdx.graphics.getHeight(); }

    private static final float TICK =  30 / 60f; //1 / 60
    private float accum = 0.0f;

    private static AsyncExecutor executor = new AsyncExecutor(1);
    private static AsyncTask task;

    private ModelBatch modelBatch;
    private Array<GameObject> boxInstance;
    private Array<Chunk> chunks;
    private Array<MeshBuilder> chunks2;

    private Vector3 oldPosition = new Vector3();
    private Array<Vector3> b = new Array<Vector3>();

    public static Pool<ChunkObject> chunkPool = new Pool<ChunkObject>() {

        @Override
        protected ChunkObject newObject() {
            //Gdx.app.log("CHUNK", "New Model");
            return new ChunkObject();
        }

    };

    public static Pool<ChunkObject> chunkPoolNoModel = new Pool<ChunkObject>() {

        @Override
        protected ChunkObject newObject() {
            //Gdx.app.log("CHUNK", "New no modelw");
            return new ChunkObject();
        }

    };

    @Override
    public void show() {

        //TODO create one asset manager
        //batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        Skybox.createSkyBox();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.5f;
        camera.far = 1000;
        fps = new ControlsController(camera , this, stage);
        //Gdx.input.setInputProcessor(fps)

        //TODO move
        modelBatch = new ModelBatch();
        boxInstance = new Array();
        chunks = new Array();
        chunks2 = new Array();

        createChunk(0, 0, camera.direction);
        task= new AsyncTask() {
            @Override
            public Object call() throws Exception {
                createChunk((int) camera.position.x / 16, (int) camera.position.z / 16, camera.direction);

                return null;
            }
        };

        //Light..
        lights = new Environment();
        lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));

        lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -0.2f));


    }

    public void update(){

        oldPosition.set(camera.position);
        fps.updateControls();
        camera.position.set(camera.position.x, 1.5f, camera.position.z);
        fps.update();
        // causing issue
        //

    }

    public void checkCollison()
    {

        //        float cubeSize = 1.2f;

        //        for (int i = 0; i < boxInstance.size; i ++ ) {
//            modelBatch.render(boxInstance.get(i));
//
//
//            if(accum >= TICK)
//            {
//                Date date = new Date(TimeUtils.millis());
//                Gdx.app.log("FIRE", "Collison detect" + date);
//
//
////                if (boxInstance.get(i).checkCollison == true)
////                {
////                    if (camera.position.x >  boxInstance.get(i).getCenter().x - cubeSize  &&
////                            //camera.position.y >  boxInstance.get(i).getCenter().y - cubeSize  &&
////                            camera.position.z >  boxInstance.get(i).getCenter().z - cubeSize  &&
////                            camera.position.x <  boxInstance.get(i).getCenter().x + cubeSize &&
////                            //camera.position.y >  boxInstance.get(i).getCenter().y + cubeSize  &&
////                            camera.position.z <  boxInstance.get(i).getCenter().z + cubeSize
////                            )
////                    {
////
////
////                        camera.position.set(oldPosition);
////                        Gdx.app.log("MyTag 2", "BANG");
////                    }
////                }
////
//                accum = 0.0f;
//            }


//        }

    }

    @Override
    public void render(float delta) {

        Constants.renderCount = 0;
        update(); // controls

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

        for (int i = 0; i < chunks2.size; i ++ ) {
            chunks2.get(i).render(camera);
        }

//        modelBatch.begin(camera);
//
//        Skybox.update(camera.position);
//        modelBatch.render(Skybox.modelInstance);
//        accum += Gdx.graphics.getDeltaTime();
//
//        if(accum >= TICK) // fire off chunk builder
//        {
//            Gdx.app.log("MyTag 2", "count" + chunks.size);
//            accum = 0.0f;
//            executor.submit(task);
//
//        }
//
//        for (int i = 0; i < chunks.size; i ++ ) {
//                chunks.get(i).render(modelBatch, camera);
//        }
//
//        modelBatch.end();

        //TODO Whats this for
        stage.getViewport().update(width(), height(), true);
        stage.act(delta);
        stage.draw();

        drawFPS();
    }



    @Override
    public void resize(int width, int height) {
        //camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getViewport().update(width, height, false);
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

        boxInstance.clear();
        stage.dispose();
        font.dispose();
        modelBatch.dispose();

        Skybox.disable();

    }

    public void drawFPS() {

        stage.getBatch().begin();
        font.draw(stage.getBatch(), "FPS: " + Gdx.graphics.getFramesPerSecond() +" Cube Count " + Constants.cubeCount +  " rend Count " + Constants.renderCount, 10, Gdx.graphics.getHeight() - 10);
        font.draw(stage.getBatch(), "Mem: " + Gdx.app.getJavaHeap() / 1000000f + " " + Gdx.app.getNativeHeap() / 1000000f + " " + GameRenderScreen.chunkPool.getFree() + " " + GameRenderScreen.chunkPoolNoModel.getFree(),  10, Gdx.graphics.getHeight() - 30);
        stage.getBatch().end();
    }

    public int getObject (int screenX, int screenY) {

        int result = -1;
        int result2 = -1;
        float distance = -1;
        BoundingBox bounds = new BoundingBox();

        Ray ray = camera.getPickRay(screenX, screenY);
        Vector3 pos = new Vector3(camera.position);
        Vector3 v = new Vector3();
        
        for (int i = 0; i < chunks.size; i++) {

            Chunk chunkInstance = chunks.get(i);

            for (int a = 0; a < chunkInstance.cubeInstance.size; a++) {

                ChunkObject cubeInstance = chunkInstance.cubeInstance.get(a);

                if (cubeInstance.model != null)
                {
                    cubeInstance.model.transform.getTranslation(pos);
                    cubeInstance.model.calculateBoundingBox(bounds);
                    bounds.set(bounds.min.add(cubeInstance.getCenter().x, cubeInstance.getCenter().y, cubeInstance.getCenter().z), bounds.max.add(cubeInstance.getCenter().x, cubeInstance.getCenter().y, cubeInstance.getCenter().z));

                    //Gdx.app.log("MyTag 2", "BLLA" + bounds.toString());

                    float dist2 = ray.origin.dst2(pos);
                    //TODO move out
                    if (distance >= 0f && dist2 > distance || dist2 > 80) continue;

                    Gdx.app.log("Distance","Distance " + dist2);

                    if (Intersector.intersectRayBounds(ray, bounds, v))
                    {
                        Gdx.app.log("MyTag 2","BLLA");
                        result = i;
                        result2 = a;
                        distance = dist2;
                    }
                }
            }

        }

        if (result > -1)
        {
            Gdx.app.log("MyTag 2", "x " + v.x + " y " + v.y + " z " + v.z);
            Gdx.app.log("MyTag 2", "x " + (int)v.x + " y " + (int)v.y + " z " + (int)v.z);

            chunks.get(result).cubeInstance.get(result2).model.materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));

            //boxInstance.get(result).materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));
            //chunks.removeIndex(result);
            //chunks.get(result).cubeInstance.get(result2).model = null;
        }

        return 1;
    };

    public void markAddChunk(float x, float z)
    {
        boolean found = false;
        for (int i = 0; i < chunks.size; i ++ ) {

            if(chunks.get(i).position.x == (x * Constants.chunkSize) && chunks.get(i).position.z == (z * Constants.chunkSize))
            {
                chunks.get(i).needed = true;
                found = true;
            }
        }

        if (found == false)
        {
            b.add(new Vector3((x * Constants.chunkSize ),0,(z * Constants.chunkSize)));
            //chunks.add(new Chunk(new Vector3((x * Constants.chunkSize ),0,(z * Constants.chunkSize))));
        }
    }

    public void createChunk(float x, float z, Vector3 direction) {

        for (int i = 0; i < chunks2.size; i ++ ) {
            chunks2.get(i).needed = false;
        }

//        markAddChunk(x, z );
//
//        if (direction.z < 0)
//        {
//            Gdx.app.log("MyTag 2", "Fire A");
//            if (direction.x < -0.5)
//            {
//                Gdx.app.log("MyTag 2", "Fire 1");
//
//                markAddChunk(x - 2, z);
//                markAddChunk(x - 2, z - 1);
//                markAddChunk(x - 2, z + 1);
//                markAddChunk(x - 2, z - 2);
//                markAddChunk(x - 2, z + 2);
//
//                markAddChunk(x - 1, z);
//                markAddChunk(x - 1, z - 1);
//                markAddChunk(x - 1, z + 1);
//                markAddChunk(x - 1, z - 2);
//                markAddChunk(x - 1, z + 2);
//
//                markAddChunk(x, z - 1);
//                markAddChunk(x , z + 1);
//            }
//            else if (direction.x > 0.5)
//            {
//
//                Gdx.app.log("MyTag 2", "Fire 2");
//
//                markAddChunk(x + 2, z);
//                markAddChunk(x + 2, z - 1);
//                markAddChunk(x + 2, z + 1);
//                markAddChunk(x + 2, z - 2);
//                markAddChunk(x + 2, z + 2);
//
//                markAddChunk(x + 1, z);
//                markAddChunk(x + 1, z - 1);
//                markAddChunk(x + 1, z + 1);
//                markAddChunk(x + 1, z - 2);
//                markAddChunk(x + 1, z + 2);
//
//                markAddChunk(x, z - 1);
//                markAddChunk(x , z + 1);
//            }
//            else
//            {
//                Gdx.app.log("MyTag 2", "Fire 3");
//
//                markAddChunk(x, z - 2);
//                markAddChunk(x - 1, z - 2);
//                markAddChunk(x + 1, z - 2);
//                markAddChunk(x - 2, z - 2);
//                markAddChunk(x + 2, z - 2);
//
//                markAddChunk(x, z - 1);
//                markAddChunk(x - 1, z - 1);
//                markAddChunk(x + 1, z - 1);
//                markAddChunk(x - 2, z - 1);
//                markAddChunk(x + 2, z - 1);
//
//                markAddChunk(x - 1, z );
//                markAddChunk(x + 1, z );
//            }
//        }
//        else
//        {
//            Gdx.app.log("MyTag 2", "Fire B");
//
//            if (direction.x < -0.5)
//            {
//                Gdx.app.log("MyTag 2", "Fire 2");
//                markAddChunk(x - 2, z);
//                markAddChunk(x - 2, z - 1);
//                markAddChunk(x - 2, z + 1);
//                markAddChunk(x - 2, z - 2);
//                markAddChunk(x - 2, z + 2);
//
//                markAddChunk(x - 1, z);
//                markAddChunk(x - 1, z - 1);
//                markAddChunk(x - 1, z + 1);
//                markAddChunk(x - 1, z - 2);
//                markAddChunk(x - 1, z + 2);
//
//                markAddChunk(x, z - 1);
//                markAddChunk(x , z + 1);
//            }
//            else if (direction.x > 0.5)
//            {
//                Gdx.app.log("MyTag 2", "Fire 3");
//                markAddChunk(x + 2,z);
//                markAddChunk(x + 2, z - 1);
//                markAddChunk(x + 2, z + 1);
//                markAddChunk(x + 2, z - 2);
//                markAddChunk(x + 2, z + 2);
//
//                markAddChunk(x + 1,z);
//                markAddChunk(x + 1, z - 1);
//                markAddChunk(x + 1, z + 1);
//                markAddChunk(x + 1, z - 2);
//                markAddChunk(x + 1, z + 2);
//
//                markAddChunk(x, z - 1);
//                markAddChunk(x , z + 1);
//            }
//            else
//            {
//                Gdx.app.log("MyTag 2", "Fire 4");
//                markAddChunk(x,z + 2);
//                markAddChunk(x - 1, z + 2);
//                markAddChunk(x + 1, z + 2);
//                markAddChunk(x - 2, z + 2);
//                markAddChunk(x + 2, z + 2);
//
//                markAddChunk(x,z + 1);
//                markAddChunk(x - 1, z + 1);
//                markAddChunk(x + 1, z + 1);
//                markAddChunk(x - 2, z + 1);
//                markAddChunk(x + 2, z + 1);
//
//                markAddChunk(x - 1, z );
//                markAddChunk(x + 1, z );
//            }
//        }

        int size = 5;

        for (int xx = 0 ; xx < size ; xx++)
        {
            for (int zz = 0 ; zz < size ; zz++)
            {
                markAddChunk(x + xx - ((size - 1) /2),z + zz - ((size - 1) /2));
            }
        }

        for (int i = 0; i < chunks.size; i ++ ) {

            if (chunks.get(i).needed == false)
            {
                //Constants.cubeCount -= chunks.get(i).cubeInstance.size ;
                Chunk c = chunks.get(i);
                c.dispose();
                chunks.removeIndex(i);
                Gdx.app.log("CHUNK", "disposed");
            }
        }

        for (int i = 0; i < b.size; i ++ ) {
            Gdx.app.log("CHUNK", "made");
            chunks2.add(new MeshBuilder(b.get(i)));
            //chunks.add(new Chunk(b.get(i)));
        }

        b.clear();
    }

}

