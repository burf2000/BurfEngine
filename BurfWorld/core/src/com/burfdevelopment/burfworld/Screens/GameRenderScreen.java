package com.burfdevelopment.burfworld.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.burfdevelopment.burfworld.Constants;
import com.burfdevelopment.burfworld.Entity.ChunkObject;
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
    //private Array<GameObject> boxInstance;
    //private Array<Chunk> chunks;
    private Array<MeshBuilder> chunks2;

    private Vector3 oldPosition = new Vector3();
    private Array<Vector3> chunksToBuild = new Array<Vector3>();

    private ShaderProgram shaderProgram;
    private Texture texture;

    private ModelBuilder modelBuilder;
    private Model cube;

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

        modelBuilder = new ModelBuilder();
        cube = modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        Skybox.createSkyBox();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.5f;
        camera.far = 1000;
        fps = new ControlsController(camera , this, stage);
        //Gdx.input.setInputProcessor(fps)

        //TODO move
        modelBatch = new ModelBatch();
//        boxInstance = new Array();
//        chunks = new Array();
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

        compileShaderTexture();
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

        accum += Gdx.graphics.getDeltaTime();
        if(accum >= TICK) // fire off chunk builder
        {
            //Gdx.app.log("MyTag 2", "count" + chunks.size);
            accum = 0.0f;
            //executor.submit(task);
            createChunk((int) camera.position.x / 16, (int) camera.position.z / 16, camera.direction);

        }

        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //TODO do we need all of these?
        //Do all your basic OpenGL ES setup to start the screen render.
        Gdx.gl20.glClearColor(0.0f, 0.3f, 0.5f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);

        Skybox.update(camera.position);
        modelBatch.render(Skybox.modelInstance);

//        for (int i = 0; i < chunks.size; i ++ ) {
//                chunks.get(i).render(modelBatch, camera);
//        }

        modelBatch.end();

        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl20.glCullFace(GL20.GL_BACK);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);


        shaderProgram.begin();
        texture.bind();
        shaderProgram.setUniformMatrix("u_projTrans", camera.combined);
        shaderProgram.setAttributef("a_color", 1, 1, 1, 1);
        shaderProgram.setUniformi("u_texture", 0);

        for (int i = 0; i < chunks2.size; i ++ ) {
            chunks2.get(i).render(camera, shaderProgram, cube);
        }

        shaderProgram.end();

        //TODO Whats this for
        stage.getViewport().update(width(), height(), true);
        stage.act(delta);
        stage.draw();

        drawFPS();
    }

    public void drawFPS() {

        stage.getBatch().begin();
        font.draw(stage.getBatch(), "FPS: " + Gdx.graphics.getFramesPerSecond() + " Cube Count " + Constants.cubeCount + " rend Count " + Constants.renderCount, 10, Gdx.graphics.getHeight() - 10);
        font.draw(stage.getBatch(), "Mem: " + Gdx.app.getJavaHeap() / 1000000f + " " + Gdx.app.getNativeHeap() / 1000000f + " " + GameRenderScreen.chunkPool.getFree() + " " + GameRenderScreen.chunkPoolNoModel.getFree(), 10, Gdx.graphics.getHeight() - 30);
        stage.getBatch().end();
    }

    public int getObject (int screenX, int screenY) {

        //createChunk((int) camera.position.x / 16, (int) camera.position.z / 16, camera.direction);

        int chunkIndex = -1;
        int meshIndex = -1;
        float distance = -1;
        BoundingBox bounds = new BoundingBox();

        Ray ray = camera.getPickRay(screenX, screenY);
        Vector3 pos = new Vector3(camera.position);
        Vector3 v = new Vector3();
        Matrix4 transfor = new Matrix4();

        for (int i = 0; i < chunks2.size; i++) {

            MeshBuilder chunkInstance = chunks2.get(i);

            for (int a = 0; a < chunkInstance.transformations.size; a++) {

                transfor.set(chunkInstance.transformations.get(a).getValues());
                Mesh m = chunkInstance.meshes.get(a).copy(false);
                transfor.getTranslation(pos);
                m.transform(transfor);
                m.calculateBoundingBox(bounds);

                //Gdx.app.log("MyTag 2", "Mesh" + transfor.toString());

                //bounds.set(bounds.min.add(transfor.M03, transfor.M13, transfor.M23), bounds.max.add(transfor.M03, transfor.M13, transfor.M23));
                //bounds.mul(transfor);

                float dist2 = ray.origin.dst2(pos);
                //TODO move out
                if (distance >= 0f && dist2 > distance || dist2 > 80) continue;

                if (Intersector.intersectRayBounds(ray, bounds, v))
                {
                    //Gdx.app.log("MyTag 2","BLLA");
                    chunkIndex = i;
                    meshIndex = a;
                    distance = dist2;
                }

                m.dispose();
            }
        }

        if (chunkIndex > -1)
        {
            Gdx.app.log("MyTag 2", "x " + v.x + " y " + v.y + " z " + v.z);
            Gdx.app.log("MyTag 2", "x " + (int)v.x + " y " + (int)v.y + " z " + (int)v.z);
            chunks2.get(chunkIndex).setDirtyPosition(new Vector3((int)v.x , (int)v.y,(int)v.z));

            //chunks2.get(result).meshes.get(result2) .materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));

            //boxInstance.get(result).materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));
            //chunks.removeIndex(result);
            //chunks.get(result).cubeInstance.get(result2).model = null;
        }

        return 1;
    }



    public void markAddChunk(float x, float z)
    {
        boolean found = false;
        for (int i = 0; i < chunks2.size; i ++ ) {

            if(chunks2.get(i).position.x == (x * Constants.chunkSize) && chunks2.get(i).position.z == (z * Constants.chunkSize))
            {
                chunks2.get(i).needed = true;
                found = true;
            }
        }

        if (found == false)
        {
            chunksToBuild.add(new Vector3((x * Constants.chunkSize), 0, (z * Constants.chunkSize)));
            //chunks.add(new Chunk(new Vector3((x * Constants.chunkSize ),0,(z * Constants.chunkSize))));
        }
    }

    public void compileShaderTexture()
    {
//        String vertexShader = "attribute vec4 a_position;\n" +
//                "attribute vec4 a_color;\n" +
//                "attribute vec2 a_texCoord0;\n" +
//                "\n" +
//                "uniform mat4 u_projTrans;\n" +
//                "\n" +
//                "varying vec4 v_color;\n" +
//                "varying vec2 v_texCoords;\n" +
//                "\n" +
//                "void main() {\n" +
//                "    v_color = a_color;\n" +
//                "    v_texCoords = a_texCoord0;\n" +
//                "    gl_Position = u_projTrans * a_position;\n" +
//                "}"; //Gdx.files.internal("vert.glsl").readString();
//        String  fragmentShader = "#ifdef GL_ES\n" +
//                "#define LOWP lowp\n" +
//                "    precision mediump float;\n" +
//                "#else\n" +
//                "    #define LOWP\n" +
//                "#endif\n" +
//                "\n" +
//                "varying LOWP vec4 v_color;\n" +
//                "varying vec2 v_texCoords;\n" +
//                "\n" +
//                "uniform sampler2D u_texture;\n" +
//                "\n" +
//                "void main()\n" +
//                "{\n" +
//                "    gl_FragColor =  texture2D(u_texture, v_texCoords) * v_color;\n" +
//                "}"; // Gdx.files.internal("frag.glsl").readString();

        String vertexShader = Gdx.files.internal("vert.glsl").readString();
        String  fragmentShader = Gdx.files.internal("frag.glsl").readString();
        shaderProgram = new ShaderProgram(vertexShader,fragmentShader);

        if (!shaderProgram.isCompiled()) {

            Gdx.app.log("ERROR", "Couldn't compile shader: " + shaderProgram.getLog());
            throw new GdxRuntimeException("Couldn't compile shader: " + shaderProgram.getLog());
        }

        texture = new Texture("badlogic.jpg");
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
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

        int size = 3;

        for (int xx = 0 ; xx < size ; xx++)
        {
            for (int zz = 0 ; zz < size ; zz++)
            {
                markAddChunk(x + xx - ((size - 1) /2),z + zz - ((size - 1) /2));
            }
        }

        for (int i = 0; i < chunks2.size; i ++ ) {

            if (chunks2.get(i).needed == false)
            {
                MeshBuilder m = chunks2.get(i);
                m.dispose();
            }
        }

        //Gdx.app.log("CHUNK", "b " + chunksToBuild.size);

//        for (int i = 0; i < chunks2.size; i ++ ) {
//
//            if (chunks2.get(i).needed == false)
//            {
//                //Constants.cubeCount -= chunks.get(i).cubeInstance.size ;
//                Chunk c = chunks.get(i);
//                c.dispose();
//                chunks.removeIndex(i);
//                Gdx.app.log("CHUNK", "disposed");
//            }
//        }

        for (int i = 0; i < chunksToBuild.size; i ++ ) {
            //Gdx.app.log("CHUNK", "made");
            chunks2.add(new MeshBuilder(chunksToBuild.get(i), cube));
            //chunks.add(new Chunk(b.get(i)));
        }

        chunksToBuild.clear();
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

        //boxInstance.clear();
        modelBuilder = null;
        cube.dispose();
        stage.dispose();
        font.dispose();
        modelBatch.dispose();
        shaderProgram.dispose();
        texture.dispose();
        Skybox.disable();
        chunks2.clear();

    }

}

