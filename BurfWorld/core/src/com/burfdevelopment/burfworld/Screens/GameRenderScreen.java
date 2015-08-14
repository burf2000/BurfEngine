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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.burfdevelopment.burfworld.Constants;
import com.burfdevelopment.burfworld.Entity.MeshBuilder;
import com.burfdevelopment.burfworld.Skybox;
import com.burfdevelopment.burfworld.Utils.ControlsController;

/**
 * Created by burfies1 on 25/07/15.
 */
public class GameRenderScreen  implements Screen {

    private Stage stage = new Stage();
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

    private ModelBatch modelBatch = new ModelBatch();;
    private Array<MeshBuilder> chunks2;
    private Vector3 oldPosition = new Vector3();
    //private Array<Vector3> chunksToBuild = new Array<Vector3>();

    private ShaderProgram shaderProgram;
    private Texture texture;

    private ModelBuilder modelBuilder;
    private Model cube;

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

        setupChunks();

        //Light..
        lights = new Environment();
        lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -0.2f));

        compileShaderTexture();
    }

    public void setupChunks()
    {
        chunks2 = new Array();
        createChunk(0, 0); //, camera.direction
        task= new AsyncTask() {
            @Override
            public Object call() throws Exception {
                createChunk((int) camera.position.x / Constants.chunkSize, (int) camera.position.z / Constants.chunkSize); //, camera.direction

                return null;
            }
        };
    }


    public void update(){

        oldPosition.set(camera.position);
        fps.updateControls();

        camera.position.set(camera.position.x, 1.0f, camera.position.z);
        fps.update();

        checkCollison();
        //camera.position.set(oldPosition);
        // causing issue
        //
    }

    public void checkCollison() {


        float cubeSize = 1.2f;

        for (int i = 0; i < chunks2.size; i++) {

            if (camera.position.x > chunks2.get(i).position.x - (Constants.chunkSize / 2) &&
                    camera.position.x < chunks2.get(i).position.x + (Constants.chunkSize / 2) &&
                    camera.position.y > chunks2.get(i).position.y - Constants.chunkSize &&
                    camera.position.y < chunks2.get(i).position.y &&
                    camera.position.z > chunks2.get(i).position.z - (Constants.chunkSize / 2) &&
                    camera.position.z < chunks2.get(i).position.z + (Constants.chunkSize / 2)) {

                for (int a = 0; a < chunks2.get(i).transformations.size; a++) {

                    //Gdx.app.log("MyTag 2", "x " + chunks2.get(i).transformations.get(a).val[12] + " y " + chunks2.get(i).transformations.get(a).val[13] + " z " + chunks2.get(i).transformations.get(a).val[14]);
                    //Gdx.app.log("MyTag 3", "x " + camera.position);


                    if (camera.position.x >  chunks2.get(i).transformations.get(a).val[12] - cubeSize &&
                            camera.position.y >  chunks2.get(i).transformations.get(a).val[13] - cubeSize  &&
                            camera.position.z >  chunks2.get(i).transformations.get(a).val[14] - cubeSize  &&
                            camera.position.x <  chunks2.get(i).transformations.get(a).val[12] + cubeSize &&
                            camera.position.y <  chunks2.get(i).transformations.get(a).val[13] + cubeSize  &&
                            camera.position.z <  chunks2.get(i).transformations.get(a).val[14] + cubeSize
                            )
                    {

                        camera.position.set(oldPosition);
                        //Gdx.app.log("MyTag 3", "x " + chunks2.get(i).transformations.get(a).val[12] + " y " + chunks2.get(i).transformations.get(a).val[13] + " z " + chunks2.get(i).transformations.get(a).val[14]);
                    }

                    //Gdx.app.log("FIRE", "X " + ((int) camera.position.x / Constants.chunkSize) + " y " + ((int) camera.position.y / Constants.chunkSize) + " z " + ((int) camera.position.z / Constants.chunkSize));
                    //wGdx.app.log("FIRE", "X " + chunks2.get(i).position.x + " y " + chunks2.get(i).position.y + " z " + chunks2.get(i).position.z);
                }
            }

        }
    }



    @Override
    public void render(float delta) {

        Constants.renderCount = 0;
        update(); // controls

        accum += Gdx.graphics.getDeltaTime();

        if(accum >= TICK) // fire off chunk builder
        {
            accum = 0.0f;
            //TODO fix
            executor.submit(task);
            //createChunk((int) camera.position.x / 16, (int) camera.position.z / 16, camera.direction);
        }

        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //TODO do we need all of these?
        //Do all your basic OpenGL ES setup to start the screen render.
        Gdx.gl20.glClearColor(0.0f, 0.3f, 0.5f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        Skybox.update(camera.position);
        modelBatch.render(Skybox.modelInstance);
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
            chunks2.get(i).render(shaderProgram);
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
        font.draw(stage.getBatch(), "Mem: " + Gdx.app.getJavaHeap() / 1000000f + " " + Gdx.app.getNativeHeap() / 1000000f , 10, Gdx.graphics.getHeight() - 30);
        stage.getBatch().end();
    }

    public void removeObject(int chunkIndex, int meshIndex)
    {
        chunks2.get(chunkIndex).setDirtyPosition(meshIndex);
    }

    public void addObject(float x, float y, float z)
    {
        Gdx.app.log("PART 2", "x " + x + "y " + y + " z " + z );
        Gdx.app.log("PART 2", "x " + (int) x / Constants.chunkSize + " y " + MathUtils.round(y) + " z " + (int) z / Constants.chunkSize );

        boolean foundChunk = false;
        for (int i = 0; i < chunks2.size; i++) {

            if (x > chunks2.get(i).position.x - (Constants.chunkSize / 2) &&
                    x < chunks2.get(i).position.x + (Constants.chunkSize / 2) &&
                    y > chunks2.get(i).position.y - Constants.chunkSize &&
                    y < chunks2.get(i).position.y  &&
                    z > chunks2.get(i).position.z - (Constants.chunkSize / 2) &&
                    z < chunks2.get(i).position.z + (Constants.chunkSize / 2) )
            {
                Gdx.app.log("PART 2", "Found chunk");
                foundChunk = true;

                chunks2.get(i).addMesh(new Vector3(MathUtils.round(x), MathUtils.round(y), MathUtils.round(z)), cube);
            }
        }

        if (foundChunk == false)
        {

            MeshBuilder m = new MeshBuilder(new Vector3(((int) x / Constants.chunkSize), ((int)y / Constants.chunkSize) + Constants.chunkSize, ((int) z / Constants.chunkSize)));
            m.addMesh(new Vector3(MathUtils.round(x),MathUtils.round(y),MathUtils.round(z)), cube);
            // create empty chunk
            chunks2.add(m);

            // add item

        }
    }

    public int getObject (int screenX, int screenY) {

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

            if (Intersector.intersectRayBoundsFast(ray, chunkInstance.position, new Vector3(Constants.chunkSize,Constants.chunkSize,Constants.chunkSize)))
            {
                Gdx.app.log("RAN", "SHOULD BE once");

                for (int a = 0; a < chunkInstance.transformations.size; a++) {

                    //todo probably can improve as pissing memorys
                    transfor.set(chunkInstance.transformations.get(a).getValues());
                    Mesh m = chunkInstance.meshes.get(a).copy(false);
                    transfor.getTranslation(pos);
                    m.transform(transfor);
                    m.calculateBoundingBox(bounds);

                    //Gdx.app.log("MyTag 2", "Mesh" + transfor.toString());
                    //bounds.set(bounds.min.add(transfor.M03, transfor.M13, transfor.M23), bounds.max.add(transfor.M03, transfor.M13, transfor.M23));
                    //bounds.mul(transfor);

                    float dist2 = ray.origin.dst2(pos);
                    if (distance >= 0f && dist2 > distance || dist2 > Constants.rayDistance) continue;

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
        }

        if (chunkIndex > -1)
        {
            Gdx.app.log("MyTag 2", "x " + v.x + " y " + v.y + " z " + v.z);
            Gdx.app.log("MyTag 2", "x " + MathUtils.round(v.x) + " y " + MathUtils.round(v.y) + " z " + MathUtils.round(v.z));

            //removeObject(chunkIndex, meshIndex);

            addObject(v.x,v.y,v.z);
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
            chunks2.add(new MeshBuilder(new Vector3((x * Constants.chunkSize), 0, (z * Constants.chunkSize)), cube));
        }
    }

    public void compileShaderTexture()
    {
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

    public void createChunk(float x, float z) { //, Vector3 direction

        for (int i = 0; i < chunks2.size; i ++ ) {
            chunks2.get(i).needed = false;
            chunks2.get(i).checkDirty();
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

        for (int i = 0; i < chunks2.size; i ++ ) {

            if (chunks2.get(i).needed == false)
            {
                MeshBuilder m = chunks2.get(i);
                m.dispose();
            }
        }
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

