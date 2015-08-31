package com.burfdevelopment.burfworld.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
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
import com.burfdevelopment.burfworld.Database.DatabaseHelper;
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

    private SpriteBatch spriteBatch = new SpriteBatch();

    private ModelBatch modelBatch = new ModelBatch();;
    private Array<MeshBuilder> chunks2;
    private Vector3 oldPosition = new Vector3();

    private ShaderProgram shaderProgram;
    private Texture texture;
    private TextureRegion[][]  regions; // #2

    private ModelBuilder modelBuilder;

    private Array<Model> cubes;

    public Boolean isJump = false;
    public float jumping = 0.0f;
    private float currentHeight = 15.0f;


    private Model person;

    @Override
    public void show() {

        //TODO create one asset manager
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        compileShaderTexture();
        Skybox.createSkyBox();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.near = 0.1f; // 0.5 //todo find out what this is again
        camera.far = 1000;
        fps = new ControlsController(camera , this, stage);

        setupChunks();

        //Light..
        lights = new Environment();
        lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -0.2f));


        modelBuilder.begin();
        MeshPartBuilder mpb = modelBuilder.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, new Material(ColorAttribute.createDiffuse(Color.BLUE)));
        mpb.setUVRange(regions[1][1]);
        mpb.box(0, 9, 0, 0.5f, 0.5f, 0.5f);
        mpb.box(0,8,0,0.5f,0.5f,0.5f);

        person = modelBuilder.end();



        //chunks2.add(new MeshBuilder(new Vector3((0 * Constants.chunkSize), Constants.chunkSize, (-1 * Constants.chunkSize)), cubes));
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

        //camera.position.set(camera.position.x, (Constants.chunkSize / 2) * Constants.cubeSize + Constants.headHeight, camera.position.z);

        if (isJump == true) {

            jumping += Gdx.graphics.getDeltaTime() * Constants.jumpRate;
            //camera.position.set(camera.position.x, (Constants.chunkSize / 2) * Constants.cubeSize + jumping + Constants.headHeight, camera.position.z);

            if (jumping > Constants.maxJump)
            {
                isJump = false;
            }
        }
        else
        {
            //todo smooth jumping
            jumping -= Gdx.graphics.getDeltaTime() * Constants.jumpRate;

            if (jumping < 0)
                jumping = 0;

            //camera.position.set(camera.position.x, (Constants.chunkSize / 2) * Constants.cubeSize + jumping + Constants.headHeight , camera.position.z);
        }

        checkCollison();
        fps.update();


        //camera.position.set(oldPosition);
        // causing issue
        //
    }

    public void checkCollison() {

        float height = 0;
        boolean collison = false;

        for (int i = 0; i < chunks2.size; i++) {

            if (camera.position.x > chunks2.get(i).position.x - (Constants.chunkSize / 2) &&
                    camera.position.x < chunks2.get(i).position.x + (Constants.chunkSize / 2) &&
                    //camera.position.y >= chunks2.get(i).position.y - (Constants.chunkSize / 2) &&
                    //camera.position.y <= chunks2.get(i).position.y + (Constants.chunkSize / 2) &&
                    camera.position.z > chunks2.get(i).position.z - (Constants.chunkSize / 2) &&
                    camera.position.z < chunks2.get(i).position.z + (Constants.chunkSize / 2)) {

                for (int a = 0; a < chunks2.get(i).transformations.size; a++) {

                    //Gdx.app.log("MyTag 2", "x " + chunks2.get(i).transformations.get(a).val[12] + " y " + chunks2.get(i).transformations.get(a).val[13] + " z " + chunks2.get(i).transformations.get(a).val[14]);
                    //Gdx.app.log("MyTag 1", "y " + camera.position.y + " y " + chunks2.get(i).position.y);
                    if (camera.position.x >  chunks2.get(i).transformations.get(a).val[12] - Constants.cubeCollisonSize &&
                            camera.position.z >  chunks2.get(i).transformations.get(a).val[14] - Constants.cubeCollisonSize  &&
                            camera.position.x <  chunks2.get(i).transformations.get(a).val[12] + Constants.cubeCollisonSize &&
                            camera.position.z <  chunks2.get(i).transformations.get(a).val[14] + Constants.cubeCollisonSize)
                    {

                        //Gdx.app.log("MyTag 2", "h " + chunks2.get(i).transformations.get(a).val[13] + " h " + camera.position.y);
                        if (camera.position.y >  chunks2.get(i).transformations.get(a).val[13] - Constants.cubeCollisonSize  &&
                                camera.position.y <  chunks2.get(i).transformations.get(a).val[13] + Constants.cubeCollisonSize)
                        {
                            //Gdx.app.log("MyTag 3","COLLISONs");
                            collison = true;
                            break;
                        }
                        else if (chunks2.get(i).transformations.get(a).val[13] + 1.0 <= camera.position.y)
                        {
                            //Gdx.app.log("MyTag 4","POO");
                            if (chunks2.get(i).transformations.get(a).val[13] + Constants.headHeight > height)
                            {
                                height = chunks2.get(i).transformations.get(a).val[13] + Constants.headHeight;
                            }
                        }
                        else
                        {
                            //Gdx.app.log("MyTag 5","POO3");
                            if (chunks2.get(i).transformations.get(a).val[13] > height)
                            {
                                height = chunks2.get(i).transformations.get(a).val[13];
                            }
                        }
                    }

                    //Gdx.app.log("FIRE", "X " + ((int) camera.position.x / Constants.chunkSize) + " y " + ((int) camera.position.y / Constants.chunkSize) + " z " + ((int) camera.position.z / Constants.chunkSize));
                    //wGdx.app.log("FIRE", "X " + chunks2.get(i).position.x + " y " + chunks2.get(i).position.y + " z " + chunks2.get(i).position.z);
                }
            }
        }

        if (collison == true)
        {
           // Gdx.app.log("MyTag 2","COLLISONs");
            camera.position.set(oldPosition);
        }
        else {

            if (height > currentHeight + 2.0f)
            {
                camera.position.set(oldPosition);
            }
            else if ( height < currentHeight)
            {
                //Gdx.app.log("MyTag 3"," c " + currentHeight);
                currentHeight -= Gdx.graphics.getDeltaTime() * 2;
                camera.position.set(camera.position.x, currentHeight + jumping, camera.position.z);
            }
            else
            {
                //Gdx.app.log("MyTag 4"," c2 " + currentHeight);
                currentHeight = height;
                camera.position.set(camera.position.x, currentHeight + jumping, camera.position.z);
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
        //shaderProgram.setUniformi(uniformLocation, context.textureBinder.bind(texture));
        shaderProgram.setUniformMatrix("u_projTrans", camera.combined);
        shaderProgram.setAttributef("a_color", 1, 1, 1, 1);
        shaderProgram.setUniformi("u_texture", 0);

        for (int i = 0; i < chunks2.size; i ++ ) {
            chunks2.get(i).render(shaderProgram);
        }

        //person.getNode("box").translation(new Vector3(0f,2f,0f));
        person.meshes.get(0).render(shaderProgram, GL20.GL_TRIANGLES);

        shaderProgram.end();

        // todo fix
        //spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        font.draw(spriteBatch, "Testing 1 2 3", 0, 10);
        spriteBatch.end();

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
        font.draw(stage.getBatch(), "X: " + camera.position.x + " Y " + camera.position.y + " Z " + camera.position.z , 10, Gdx.graphics.getHeight() - 50);
        stage.getBatch().end();
    }

    public void removeObject(int chunkIndex, int meshIndex)
    {
        chunks2.get(chunkIndex).setDirtyPosition(meshIndex, cubes);
    }

    public void addObject(float x, float y, float z, Vector3 vv)
    {
        //todo tidy this up
        Gdx.app.log("PART 1", "x " + x + "y " + y + " z " + z + " V" + vv.toString() );
        //Gdx.app.log("PART 2", "x " + (int) x / Constants.chunkSize + " y " + MathUtils.round(y) + " z " + (int) z / Constants.chunkSize );
        //Gdx.app.log("PART 2", "SIZE " + chunks2.size);

        // work out what side we touched
        float xDif, yDif, zDif;
        float xOffset = 0, yOffset = 0, zOffset = 0;
        float dif = 0.5f;

        if (x > vv.x)
        {
           xDif = x - vv.x;
        }
        else
        {
            xDif = vv.x - x;
        }

        if (y > vv.y)
        {
            yDif = y - vv.y;
        }
        else
        {
            yDif = vv.y - y;
        }

        if (z > vv.z)
        {
            zDif =z - vv.z;
        }
        else
        {
            zDif = vv.z - z;
        }

        //Gdx.app.log("PART 1", "x " + xDif + "y " + yDif + " z " + zDif);

        if (xDif > yDif && xDif > zDif)
        {
            if (x > vv.x)
            {
                xOffset = dif;
            }
            else
            {
                xOffset = -dif;
            }
        }
        else if (yDif > xDif && yDif > zDif)
        {
            if (y > vv.y)
            {
                yOffset = dif;
            }
            else
            {
                yOffset = -dif;
            }
        }
        else
        {
            if (z > vv.z)
            {
                zOffset = dif;
            }
            else
            {
                zOffset = -dif;
            }
        }

        x = MathUtils.round(x + xOffset);
        y = MathUtils.round(y + yOffset);
        z = MathUtils.round(z + zOffset);

        boolean foundChunk = false;
        for (int i = 0; i < chunks2.size; i++) {

            Gdx.app.log("PART 2", "searching chunk " + chunks2.get(i).position.toString() );

            if (x >= chunks2.get(i).position.x - (Constants.chunkSize / 2) &&
                    x < chunks2.get(i).position.x + (Constants.chunkSize / 2) &&
                    y >= chunks2.get(i).position.y - (Constants.chunkSize / 2) &&
                    y < chunks2.get(i).position.y + (Constants.chunkSize / 2) &&
                    z >= chunks2.get(i).position.z - (Constants.chunkSize / 2) &&
                    z < chunks2.get(i).position.z + (Constants.chunkSize / 2) )
            {
                Gdx.app.log("PART 2", "Found chunk " + chunks2.get(i).position.toString() + " " + new Vector3(x,y,z)  );
                foundChunk = true;

                // take away the plus 1
//                x = x - xOffset;
//                y = y - yOffset;
//                z = z - zOffset;

                chunks2.get(i).addMesh(new Vector3(x, y, z), cubes);
                break;
            }
        }

        // create the new chunk
        if (foundChunk == false)
        {
            int yy, xx, zz;

            if (x > 0)
            {
                xx = (int) ((x + ( Constants.chunkSize /2)) / Constants.chunkSize) * Constants.chunkSize;
            }
            else
            {
                xx = (int) ((x - ( Constants.chunkSize /2)) / Constants.chunkSize) * Constants.chunkSize;
            }

            if (y > 0)
            {
                yy = (int) ((y + ( Constants.chunkSize /2)) / Constants.chunkSize) * Constants.chunkSize;
            }
            else
            {
                yy = (int) ((y - ( Constants.chunkSize /2)) / Constants.chunkSize) * Constants.chunkSize;
            }

            if (z > 0)
            {
                zz =  (int) ((z + ( Constants.chunkSize /2)) / Constants.chunkSize) * Constants.chunkSize;
            }
            else
            {
                zz =  (int) ((z - ( Constants.chunkSize /2)) / Constants.chunkSize) * Constants.chunkSize;
            }

            Vector3 v = new Vector3(xx, yy, zz);
            Gdx.app.log("PART 2", "DID NOT FIND chunk " + v.toString() + " " + new Vector3(x,y,z));
            MeshBuilder m = new MeshBuilder(v);

//            x = x - xOffset;
//            y = y - yOffset;
//            z = z - zOffset;

            m.addMesh(new Vector3(MathUtils.round(x), MathUtils.round(y), MathUtils.round(z)), cubes);
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
        Vector3 center = new Vector3();

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
                    //Gdx.app.log("MyTag 2","DIS " + dist2 );

                    if (distance >= 0f && dist2 > distance || dist2 > Constants.rayDistance) continue;

                    if (Intersector.intersectRayBounds(ray, bounds, v))
                    {
                        Gdx.app.log("MyTag 2", "WE HIT " + v.toString() + " " + transfor.getTranslation(pos));
                        chunkIndex = i;
                        meshIndex = a;
                        distance = dist2;
                        center = new Vector3(transfor.getTranslation(pos));
                    }

                    m.dispose();
                }
            }
        }

        if (chunkIndex > -1)
        {
            Gdx.app.log("MyTag 2","DIS LAST " + distance );
            Gdx.app.log("MyTag 2", "x " + v.x + " y " + v.y + " z " + v.z + " " + center);
            //Gdx.app.log("MyTag 2", "x " + MathUtils.round(v.x) + " y " + MathUtils.round(v.y) + " z " + MathUtils.round(v.z));

            if (fps.isAdding == true)
            {
                addObject(v.x, v.y, v.z, center);
            }
            else
            {
                removeObject(chunkIndex, meshIndex);
            }

            String s = chunks2.get(0).chunkToString();
            Gdx.app.log("CHUNK",s);
        }

        return 1;
    }

    public void markAddChunk(float x, float z)
    {
        boolean found = false;
        for (int i = 0; i < chunks2.size; i ++ ) {

            if(chunks2.get(i).position.x == (x * Constants.chunkSize) && chunks2.get(i).position.z == (z * Constants.chunkSize))
            {
                //Gdx.app.log("ERROR", "Adding2 " + chunks2.get(i).position.y);
                chunks2.get(i).needed = true;
                found = true;
            }
        }

        if (found == false)
        {
            Gdx.app.log("ERROR", "Adding " + new Vector3((x * Constants.chunkSize), 0, (z * Constants.chunkSize)));
            chunks2.add(new MeshBuilder(new Vector3((x * Constants.chunkSize), 0, (z * Constants.chunkSize)), cubes));

            Array<Vector3> v = MeshBuilder.database.getHeightChunk((x * Constants.chunkSize), (z * Constants.chunkSize));

            for (int a = 0; a < v.size; a ++ ) {

                if (v.get(a).y != 0)
                {
                    chunks2.add(new MeshBuilder(v.get(a), cubes));
                }
            }
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

        texture = new Texture("texturemap.png");
        regions = TextureRegion.split(texture, 64, 64);

        //texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        Model cube;

        modelBuilder = new ModelBuilder();
//        cube = modelBuilder.createBox(Constants.cubeSize, Constants.cubeSize, Constants.cubeSize,
//                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);


        //todo nasty need to think about
        cubes = new Array();
        for (int x = 0; x < 8; x ++ ) {
            for (int y = 0; y < 8; y ++ ) {

                modelBuilder.begin();
                MeshPartBuilder mpb = modelBuilder.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, new Material(ColorAttribute.createDiffuse(Color.BLUE)));
                mpb.setUVRange(regions[x][y]);
                mpb.box(1.0f, 1.0f, 1.0f);
                cube = modelBuilder.end();
                cube.meshes.get(0).scale(Constants.cubeSize,Constants.cubeSize,Constants.cubeSize);
                cubes.add(cube);
            }
        }
    }

    public void createChunk(float x, float z) { //, Vector3 direction

        for (int i = 0; i < chunks2.size; i ++ ) {
            chunks2.get(i).needed = false;
            chunks2.get(i).checkDirty();
        }

        int size = 5;
        for (int xx = 0 ; xx < size ; xx++)
        {
            for (int zz = 0 ; zz < size ; zz++)
            {
                markAddChunk(x + xx - ((size - 1) / 2),z + zz - ((size - 1) /2));
            }
        }

        for (int i = 0; i < chunks2.size; i ++ ) {

            if (chunks2.get(i).needed == false)
            {
                Gdx.app.log("ERROR", "DELETING " + chunks2.get(i).position + " " + chunks2.size);

                MeshBuilder m = chunks2.removeIndex(i);
                m.dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height , true);
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
        cubes.clear();
        stage.dispose();
        font.dispose();
        modelBatch.dispose();
        shaderProgram.dispose();
        texture.dispose();
        Skybox.disable();
        chunks2.clear();

    }

}

