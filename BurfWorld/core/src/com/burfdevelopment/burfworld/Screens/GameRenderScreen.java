package com.burfdevelopment.burfworld.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.burfdevelopment.burfworld.Constants;
import com.burfdevelopment.burfworld.Entity.Chunk;
import com.burfdevelopment.burfworld.Entity.ChunkObject;
import com.burfdevelopment.burfworld.Entity.GameObject;
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

    public static int width() { return Gdx.graphics.getWidth(); }
    public static int height() { return Gdx.graphics.getHeight(); }

    private ModelBatch modelBatch;
    private Model box;
    private Model floor;

    private Array<GameObject> boxInstance;
    private Array<Chunk> chunks;
    private Vector3 oldPosition = new Vector3();

    private static final float TICK =  30 / 60f; //1 / 60
    private float accum = 0.0f;


    private static AsyncExecutor executor = new AsyncExecutor(1);
    private static AsyncTask task;

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

        createChunk(0, 0, camera.direction);
        task= new AsyncTask() {
            @Override
            public Object call() throws Exception {
                createChunk((int) camera.position.x / 16, (int) camera.position.z / 16, camera.direction);

                return null;
            }
        };


    }

    public void update(){

        oldPosition.set(camera.position);
        fps.updateControls();
        camera.position.set(camera.position.x, 1.5f, camera.position.z);
        fps.update();
        // causing issue
        //

    }

    @Override
    public void render(float delta) {

        update();

        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //TODO do we need all of these?
        //Do all your basic OpenGL ES setup to start the screen render.
        Gdx.gl20.glClearColor(0.0f, 0.3f, 0.5f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Like spriteBatch, just with models!  pass in the box Instance and the environment
        //Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        modelBatch.begin(camera);

        Skybox.update(camera.position);
        modelBatch.render(Skybox.modelInstance);

//        float cubeSize = 1.2f;

        accum += Gdx.graphics.getDeltaTime();

        if(accum >= TICK)
        {
            //Gdx.app.log("MyTag 2", "X " + (int)camera.position.x / 16 + " z " + (int)camera.position.z / 16);
            //Gdx.app.log("MyTag 2", "x" + camera.direction.toString());
            Gdx.app.log("MyTag 2", "count" + chunks.size);
            accum = 0.0f;

            executor.submit(task);

        }

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



        for (int i = 0; i < chunks.size; i ++ ) {
                chunks.get(i).render(modelBatch, camera);
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
        box.dispose();
        floor.dispose();
        Skybox.disable();

    }

    public void drawFPS() {

        stage.getBatch().begin();
        font.draw(stage.getBatch(), "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight() - 10);
        font.draw(stage.getBatch(), "Mem: " + Gdx.app.getJavaHeap() / 1000000f + " " + Gdx.app.getNativeHeap() / 1000000f,  10, Gdx.graphics.getHeight() - 30);
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
                    if (distance >= 0f && dist2 > distance) continue;

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

            chunks.get(result).cubeInstance.get(result2).model.materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));

            //boxInstance.get(result).materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));
            //chunks.removeIndex(result);
        }

        return 1;
    };


    public void cubes()
    {
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

        int c = 40;
        for (int x = 0; x < c; x ++ ) {

            for (int y = 0; y < c; y ++ ) {

                for (int z = 0; z < c; z ++ ) {

                    GameObject m = new GameObject(box,new Vector3(x + 2,y,z), true);
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

        GameObject m = new GameObject(floor, new Vector3(0,-1,0), false);
        boxInstance.add(m);
    }


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
            chunks.add(new Chunk(new Vector3((x * Constants.chunkSize ),0,(z * Constants.chunkSize))));
        }
    }

    public void createChunk(float x, float z, Vector3 direction) {

        for (int i = 0; i < chunks.size; i ++ ) {
            chunks.get(i).needed = false;
        }

        int size = 5;

        for (int xx = 0 ; xx < size ; xx++)
        {
            for (int zz = 0 ; zz < size ; zz++)
            {
                markAddChunk(x + xx - ((size - 1) /2),z + zz - ((size - 1) /2));
            }
        }



//        if (direction.z < 0)
//        {
//            //Gdx.app.log("MyTag 2", "Fire A");
//            if (direction.x < -0.5)
//            {
//                //Gdx.app.log("MyTag 2", "Fire 1");
//
////                markAddChunk(x - 2, z);
////                markAddChunk(x - 2, z - 1);
////                markAddChunk(x - 2, z + 1);
//
//                markAddChunk(x - 1, z);
//                markAddChunk(x - 1, z - 1);
//                markAddChunk(x - 1, z + 1);
//
//                markAddChunk(x, z - 1);
//                markAddChunk(x , z + 1);
//            }
//            else if  (direction.x > 0.5)
//            {
//
//                //Gdx.app.log("MyTag 2", "Fire 2");
//
////                markAddChunk(x + 2, z);
////                markAddChunk(x + 2, z - 1);
////                markAddChunk(x + 2, z + 1);
//
//                markAddChunk(x + 1, z);
//                markAddChunk(x + 1, z - 1);
//                markAddChunk(x + 1, z + 1);
//
//                markAddChunk(x, z - 1);
//                markAddChunk(x , z + 1);
//
//            }
//            else
//            {
//
//                //Gdx.app.log("MyTag 2", "Fire 3");
////                markAddChunk(x, z - 2);
////                markAddChunk(x - 1, z - 2);
////                markAddChunk(x + 1, z - 2);
//
//                markAddChunk(x, z - 1);
//                markAddChunk(x - 1, z - 1);
//                markAddChunk(x + 1, z - 1);
//
//                markAddChunk(x - 1, z );
//                markAddChunk(x + 1, z );
//            }
//
//        }
//        else
//        {
//            //Gdx.app.log("MyTag 2", "Fire B");
//
//            if (direction.x < -0.5)
//            {
//
//                //Gdx.app.log("MyTag 2", "Fire 2");
////                markAddChunk(x - 2, z);
////                markAddChunk(x - 2, z - 1);
////                markAddChunk(x - 2, z + 1);
//
//                markAddChunk(x - 1, z);
//                markAddChunk(x - 1, z - 1);
//                markAddChunk(x - 1, z + 1);
//
//                markAddChunk(x, z - 1);
//                markAddChunk(x , z + 1);
//            }
//            else if (direction.x > 0.5)
//            {
//
//                //Gdx.app.log("MyTag 2", "Fire 3");
////                markAddChunk(x + 2,z);
////                markAddChunk(x + 2, z - 1);
////                markAddChunk(x + 2, z + 1);
//
//                markAddChunk(x + 1,z);
//                markAddChunk(x + 1, z - 1);
//                markAddChunk(x + 1, z + 1);
//
//                markAddChunk(x, z - 1);
//                markAddChunk(x , z + 1);
//            }
//            else
//            {
//
//                //Gdx.app.log("MyTag 2", "Fire 4");
////                markAddChunk(x,z + 2);
////                markAddChunk(x - 1, z + 2);
////                markAddChunk(x + 1, z + 2);
//
//                markAddChunk(x,z + 1);
//                markAddChunk(x - 1, z + 1);
//                markAddChunk(x + 1, z + 1);
//
//                markAddChunk(x - 1, z );
//                markAddChunk(x + 1, z );
//            }
//        }

        for (int i = 0; i < chunks.size; i ++ ) {

            if (chunks.get(i).needed == false)
            {
                chunks.get(i).dispose();
                chunks.removeIndex(i);
            }

        }

        //chunks.add(new Chunk(new Vector3(0,0,-16)));
        //chunks.add(new Chunk(new Vector3(16,0,0)));
        //chunks.add(new Chunk(new Vector3(-16,0,0)));




    }

}


