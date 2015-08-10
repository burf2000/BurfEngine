package com.burfdevelopment.burfworld.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.burfdevelopment.burfworld.Constants;
import com.burfdevelopment.burfworld.Screens.GameRenderScreen;

import java.util.Random;

/**
 * Created by burfies1 on 05/08/15.
 */
public class Chunk {

    public Array<ChunkObject> cubeInstance = new Array(16 * 16 * 16);
    public Vector3 position;
    private static ModelBuilder modelBuilder;
    private static Model cube;
    public boolean needed = true;
    public boolean deleting = false;
    private static Random rand = new Random();

    static {
        modelBuilder = new ModelBuilder();
        cube = modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }

    public Chunk(Vector3 position)
    {
        this.position = position;

        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        Color color = new Color(r, g, b, 1);

        for (int x = 0; x < Constants.chunkSize; x ++ ) {

            for (int y = 0; y < Constants.chunkSize; y ++ ) {

                for (int z = 0; z < Constants.chunkSize; z ++ ) {

                    ChunkObject m;

                    if (x > 0 && x < (Constants.chunkSize - 1) && y > 0 && y < (Constants.chunkSize - 1) && z > 0 && z < (Constants.chunkSize - 1) )
                    {
                        m = GameRenderScreen.chunkPoolNoModel.obtain();
                        m.center.set(position.x + x - (Constants.chunkSize / 2), position.y - y, position.z + z - (Constants.chunkSize / 2));
                        //m = new ChunkObject(new Vector3(position.x + x - (Constants.chunkSize / 2),position.y - y, position.z + z -  (Constants.chunkSize / 2)));
                    }
                    else
                    {
                        //Gdx.app.log("CUBE", "x " + x + " y " + y + " z " + z);
                        m = GameRenderScreen.chunkPool.obtain();
                        Constants.cubeCount +=1;
                        m.center.set(position.x + x - (Constants.chunkSize / 2), position.y - y, position.z + z - (Constants.chunkSize / 2));
                        m.addModel(cube, color);
                        //m = new ChunkObject(cube,new Vector3(position.x + x - (Constants.chunkSize /2),position.y - y, position.z + z -  (Constants.chunkSize /2)));
                        //m.model.materials.get(0).set(new IntAttribute(IntAttribute.CullFace, GL20.GL_BACK));

                    }

                    cubeInstance.add(m);
                }
            }
        }

        Gdx.app.log("CUBE COUNT", "count" + Constants.cubeCount);
    }

    public void render(ModelBatch modelBatch, Camera camera)
    {

        if (deleting == true)
        {
            return;
        }

        for (int i = 0; i < cubeInstance.size; i ++ ) {

            if (deleting == true)
            {
                return;
            }

            if (cubeInstance.get(i) != null && cubeInstance.get(i).visable == true && cubeInstance.get(i).model != null) {

                if (camera.frustum.boundsInFrustum(cubeInstance.get(i).getCenter().x,cubeInstance.get(i).getCenter().y,cubeInstance.get(i).getCenter().z,8,8,8))
                {
                    Constants.renderCount +=1;
                    modelBatch.render(cubeInstance.get(i).model);
                }
                else
                {
                    //cubeInstance.get(i).model = null;
                }

            }
            else
            {
                if (camera.frustum.boundsInFrustum(cubeInstance.get(i).getCenter().x,cubeInstance.get(i).getCenter().y,cubeInstance.get(i).getCenter().z,8,8,8)) {
                    //cubeInstance.get(i).model = new ModelInstance(cube);
                }
            }
        }

    }

    public void dispose()
    {
        deleting = true;
        ChunkObject chunkObject;
        for (int aa = 0; aa < cubeInstance.size; aa ++) {

            chunkObject = cubeInstance.get(aa);
            if (chunkObject.model != null)
            {
                GameRenderScreen.chunkPool.free(chunkObject);
            }
            else
            {
                GameRenderScreen.chunkPoolNoModel.free(chunkObject);
            }

            cubeInstance.removeIndex(aa);

        }
    }
}






