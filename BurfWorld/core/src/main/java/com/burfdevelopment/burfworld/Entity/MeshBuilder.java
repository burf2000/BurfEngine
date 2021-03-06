package com.burfdevelopment.burfworld.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.burfdevelopment.burfworld.Constants;
import com.burfdevelopment.burfworld.Database.DatabaseHelper;

/**
 * Created by burfies1 on 31/10/2017.
 */

public class MeshBuilder {

    public static DatabaseHelper database = new DatabaseHelper();
    public static TextureRegion[][]  regions; // #2
    public Vector3 position;
    public boolean deleting = false;
    public boolean needed = true;
    public boolean finished = false;
    public Array<Mesh> meshes = new Array<Mesh>();
    public Array<Matrix4> transformations = new Array<Matrix4>();
    public int[][][] chunk = new int[Constants.chunkSize][Constants.chunkSize][Constants.chunkSize];
    private Mesh mesh;
    private boolean dirty = false;

    public MeshBuilder(Vector3 position) {
        this.position = position;
        needed = true;

        database.addChunk(position.x, position.y, position.z, chunkToString());
    }

    public MeshBuilder(Vector3 position, Array<Model> cubes) {
        this.position = position;

        String data = database.getChunk(position.x, position.y, position.z);

        if (data == null) {
            Gdx.app.log("NOT FOUND", "NOT FOUND");
            createMeshes(cubes);
        } else {
            Gdx.app.log("FOUND", "FOUND");

            String[] s = data.split(",");
            populateMeshes(cubes, s);
        }

        needed = true;
    }


    public MeshBuilder(Vector3 position, Array<Model> cubes, String data) {
        this.position = position;
        String[] s = data.split(",");
        populateMeshes(cubes, s);
        needed = true;
    }

    public static Mesh mergeMeshes(Array<Mesh> meshes, Array<Matrix4> transformations) {

        if (meshes.size == 0) return null;

        int vertexArrayTotalSize = 0;
        int indexArrayTotalSize = 0;

        VertexAttributes va = meshes.get(0).getVertexAttributes();
        int vaA[] = new int[va.size()];
        for (int i = 0; i < va.size(); i++) {
            vaA[i] = va.get(i).usage;
        }

        for (int i = 0; i < meshes.size; i++) {
            Mesh mesh = meshes.get(i);
            if (mesh.getVertexAttributes().size() != va.size()) {
                meshes.set(i, copyMesh(mesh, true, false, vaA));
            }

            vertexArrayTotalSize += mesh.getNumVertices() * mesh.getVertexSize() / 4;
            indexArrayTotalSize += mesh.getNumIndices();
        }

        final float vertices[] = new float[vertexArrayTotalSize];
        final short indices[] = new short[indexArrayTotalSize];

        int indexOffset = 0;
        int vertexOffset = 0;
        int vertexSizeOffset = 0;
        int vertexSize = 0;

        for (int i = 0; i < meshes.size; i++) {
            Mesh mesh = meshes.get(i);

            int numIndices = mesh.getNumIndices();
            int numVertices = mesh.getNumVertices();
            vertexSize = mesh.getVertexSize() / 4;
            int baseSize = numVertices * vertexSize;
            VertexAttribute posAttr = mesh.getVertexAttribute(VertexAttributes.Usage.Position);
            int offset = posAttr.offset / 4;
            int numComponents = posAttr.numComponents;

            { //uzupelnianie tablicy indeksow
                mesh.getIndices(indices, indexOffset);
                for (int c = indexOffset; c < (indexOffset + numIndices); c++) {
                    indices[c] += vertexOffset;
                }
                indexOffset += numIndices;
            }

            mesh.getVertices(0, baseSize, vertices, vertexSizeOffset);
            Mesh.transform(transformations.get(i), vertices, vertexSize, offset, numComponents, vertexOffset, numVertices);
            vertexOffset += numVertices;
            vertexSizeOffset += baseSize;
        }

        Mesh result = new Mesh(true, vertexOffset, indices.length, meshes.get(0).getVertexAttributes());
        result.setVertices(vertices);
        result.setIndices(indices);
        return result;
    }

    public static Mesh copyMesh(Mesh meshToCopy, boolean isStatic, boolean removeDuplicates, final int[] usage) {
        // TODO move this to a copy constructor?
        // TODO duplicate the buffers without double copying the data if possible.
        // TODO perhaps move this code to JNI if it turns out being too slow.
        final int vertexSize = meshToCopy.getVertexSize() / 4;
        int numVertices = meshToCopy.getNumVertices();
        float[] vertices = new float[numVertices * vertexSize];
        meshToCopy.getVertices(0, vertices.length, vertices);
        short[] checks = null;
        VertexAttribute[] attrs = null;
        int newVertexSize = 0;
        if (usage != null) {
            int size = 0;
            int as = 0;
            for (int i = 0; i < usage.length; i++)
                if (meshToCopy.getVertexAttribute(usage[i]) != null) {
                    size += meshToCopy.getVertexAttribute(usage[i]).numComponents;
                    as++;
                }
            if (size > 0) {
                attrs = new VertexAttribute[as];
                checks = new short[size];
                int idx = -1;
                int ai = -1;
                for (int i = 0; i < usage.length; i++) {
                    VertexAttribute a = meshToCopy.getVertexAttribute(usage[i]);
                    if (a == null)
                        continue;
                    for (int j = 0; j < a.numComponents; j++)
                        checks[++idx] = (short) (a.offset / 4 + j);
                    attrs[++ai] = new VertexAttribute(a.usage, a.numComponents, a.alias);
                    newVertexSize += a.numComponents;
                }
            }
        }
        if (checks == null) {
            checks = new short[vertexSize];
            for (short i = 0; i < vertexSize; i++)
                checks[i] = i;
            newVertexSize = vertexSize;
        }

        int numIndices = meshToCopy.getNumIndices();
        short[] indices = null;
        if (numIndices > 0) {
            indices = new short[numIndices];
            meshToCopy.getIndices(indices);
            if (removeDuplicates || newVertexSize != vertexSize) {
                float[] tmp = new float[vertices.length];
                int size = 0;
                for (int i = 0; i < numIndices; i++) {
                    final int idx1 = indices[i] * vertexSize;
                    short newIndex = -1;
                    if (removeDuplicates) {
                        for (short j = 0; j < size && newIndex < 0; j++) {
                            final int idx2 = j * newVertexSize;
                            boolean found = true;
                            for (int k = 0; k < checks.length && found; k++) {
                                if (tmp[idx2 + k] != vertices[idx1 + checks[k]])
                                    found = false;
                            }
                            if (found)
                                newIndex = j;
                        }
                    }
                    if (newIndex > 0)
                        indices[i] = newIndex;
                    else {
                        final int idx = size * newVertexSize;
                        for (int j = 0; j < checks.length; j++)
                            tmp[idx + j] = vertices[idx1 + checks[j]];
                        indices[i] = (short) size;
                        size++;
                    }
                }
                vertices = tmp;
                numVertices = size;
            }
        }

        Mesh result;
        if (attrs == null)
            result = new Mesh(isStatic, numVertices, indices == null ? 0 : indices.length, meshToCopy.getVertexAttributes());
        else
            result = new Mesh(isStatic, numVertices, indices == null ? 0 : indices.length, attrs);
        result.setVertices(vertices, 0, numVertices * newVertexSize);
        result.setIndices(indices);
        return result;
    }

    public void setDirtyPosition(int index, Array<Model> cubes) {

        Vector3 v = new Vector3(transformations.get(index).val[12] - position.x, transformations.get(index).val[13] - position.y, transformations.get(index).val[14] - position.z);

        Gdx.app.log("PART 2", " " + v.x + " " + v.y + " " + v.z);
        Gdx.app.log("PART 2", " " + (int) (v.x + (Constants.chunkSize / 2)) + " " + (int) (v.y + (Constants.chunkSize / 2)) + " " + (int) (v.z + (Constants.chunkSize / 2)));

        int indexX, indexY, indexZ;
        indexX = (int) (v.x + (Constants.chunkSize / 2));
        indexY = (int) (v.y + (Constants.chunkSize / 2));
        indexZ = (int) (v.z + (Constants.chunkSize / 2));

        chunk[indexX][indexY][indexZ] = Constants.BrickState.DELETED.getValue();
        meshes.removeIndex(index);
        transformations.removeIndex(index);

        Constants.renderCount -=1;

        // todo maybe reimplement
//        if (chunk[indexX][indexY - 1][indexZ] == Constants.BrickState.HIDDEN.value) {
//
//            Gdx.app.log("PART 3", " " + (v.x + position.x) + " " + ((v.y + position.y) - 1) + " " + (v.z + position.z));
//            addMesh(new Vector3(v.x + position.x, (v.y + position.y) - 1, v.z + position.z), cubes);
//
//            chunk[indexX][indexY - 1][indexZ] = Constants.BrickState.SHOW.value;
//        }

        dirty = true;

        database.updateChunk(position.x, position.y, position.z, chunkToString());
    }

    public void addMesh(Vector3 pos, Array<Model> cubes) {
        int r = MathUtils.random(cubes.size - 1);

        finished = false;

        ModelInstance model = new ModelInstance(cubes.get(r), pos.x, pos.y, pos.z);
        meshes.addAll(model.model.meshes);
        transformations.add(model.transform);

        mesh = MeshBuilder.mergeMeshes(meshes, transformations);
        finished = true;

        int x, y, z;
        x = (int) (pos.x - position.x) + (Constants.chunkSize / 2);//pos.x % 16;
        y = (int) (pos.y - position.y) + (Constants.chunkSize / 2); //pos.y % 16;
        z = (int) (pos.z - position.z) + (Constants.chunkSize / 2); //pos.z % 16;


        chunk[x][y][z] = Constants.BrickState.SHOW.getValue() + r;
        database.updateChunk(position.x, position.y, position.z, chunkToString());

        Constants.renderCount +=1;
    }

    public void populateMeshes(Array<Model> cubes, String[] data) {
        ModelInstance model;
        int index = 0;

        for (int x = 0; x < Constants.chunkSize; x++) {
            for (int y = 0; y < Constants.chunkSize; y++)
                for (int z = 0; z < Constants.chunkSize; z++) {

                    int textureValue = Integer.parseInt(data[index]);

                    chunk[x][y][z] = textureValue;

                    if (textureValue > 0) {
                        model = new ModelInstance(cubes.get(chunk[x][y][z] - Constants.BrickState.SHOW.getValue()), (position.x + x - (Constants.chunkSize / 2)) * Constants.cubeSize, (position.y + y - (Constants.chunkSize / 2)) * Constants.cubeSize, (position.z + z - (Constants.chunkSize / 2)) * Constants.cubeSize);
                        meshes.addAll(model.model.meshes);
                        transformations.add(model.transform);

                        Constants.renderCount +=1;
                    }

                    index += 1;
                }
        }

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {

                mesh = MeshBuilder.mergeMeshes(meshes, transformations);
                finished = true;
            }
        });
    }

    public void createMeshes(Array<Model> cubes) {
        ModelInstance model;

        for (int x = 0; x < Constants.chunkSize; x++) {

            for (int y = 0; y < Constants.chunkSize; y++) {

                for (int z = 0; z < Constants.chunkSize; z++) {

                    // todo removed hiding bricks?
//                    if (x > 0 && x < (Constants.chunkSize - 1) && y > 0 && y < (Constants.chunkSize - 1) && z > 0 && z < (Constants.chunkSize - 1)) {
//                        chunk[x][y][z] = Constants.BrickState.HIDDEN.getValue();
//                    } else {
                        int r = MathUtils.random(cubes.size - 1);

                        // r = Constants.TextureName.GRASS_NORMAL.ordinal();

                        chunk[x][y][z] = Constants.BrickState.SHOW.getValue() + r;

                        //cube.materials.get(0).set(ColorAttribute.createDiffuse(color));
                        //new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
                        //model.materials.get(0).set(ColorAttribute.createDiffuse(color));
                        //model.model.materials.get(0).set(ColorAttribute.createDiffuse(color));

                        //model.transform.setToTranslation(position.x + x - (Constants.chunkSize / 2), position.y - y, position.z + z - (Constants.chunkSize / 2));
                        model = new ModelInstance(cubes.get(r), (position.x + x - (Constants.chunkSize / 2)) * Constants.cubeSize, (position.y + y - (Constants.chunkSize / 2)) * Constants.cubeSize, (position.z + z - (Constants.chunkSize / 2)) * Constants.cubeSize);

                        meshes.addAll(model.model.meshes);
                        transformations.add(model.transform);

                        Constants.renderCount +=1;
//                    }
                }
            }
        }

        database.addChunk(position.x, position.y, position.z, chunkToString());

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                mesh = MeshBuilder.mergeMeshes(meshes, transformations);
                finished = true;
            }
        });
    }

    public String chunkToString() {

        String s = new String();

        for (int x = 0; x < Constants.chunkSize; x++) {
            for (int y = 0; y < Constants.chunkSize; y++) {
                for (int z = 0; z < Constants.chunkSize; z++) {
                    s += chunk[x][y][z] + ",";
                }
            }
        }

        return s.substring(0, s.length() - 1);
    }

    public void checkDirty() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (dirty == true) {
                    mesh.dispose();
                    mesh = MeshBuilder.mergeMeshes(meshes, transformations);
                    dirty = false;
                    finished = true;
                }
            }
        });
    }

    public void render(ShaderProgram shaderProgram) {
        if (finished == true && deleting == false && mesh != null) {
            mesh.render(shaderProgram, GL20.GL_TRIANGLES);
        }
    }

    public void dispose() {
        // todo causes a crash
        deleting = true;
        mesh.dispose();
    }
}