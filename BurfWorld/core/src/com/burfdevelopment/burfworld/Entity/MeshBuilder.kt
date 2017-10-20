package com.burfdevelopment.burfworld.Entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.burfdevelopment.burfworld.Constants
import com.burfdevelopment.burfworld.Database.DatabaseHelper
import com.burfdevelopment.burfworld.Screens.GameRenderHelper

/**
 * Created by burfies1 on 20/10/2017.
 */

//todo save chuncks
public class MeshBuilder constructor(position: Vector3) {
    @JvmField
    var position: Vector3 = position
    @JvmField
    var needed = true
    @JvmField
    var deleting = false
    private var mesh: Mesh? = null
    @JvmField
    var finished = false
    @JvmField
    var meshes = com.badlogic.gdx.utils.Array<Mesh>()
    @JvmField
    var transformations = com.badlogic.gdx.utils.Array<Matrix4>()

    private var dirty = false

    companion object {
        @JvmField var  database = DatabaseHelper()
    }

    init {
        database.addChunk(position.x, position.y, position.z, chunkToString())
    }

    constructor(position: Vector3, cubes: com.badlogic.gdx.utils.Array<Model>) : this(position) {

        val data = database.getChunk(position.x, position.y, position.z)

        if (data == null) {
            Gdx.app.log("NOT FOUND", "NOT FOUND")
            createMeshes(cubes)
        } else {
            Gdx.app.log("FOUND", "FOUND")

            val s = data.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            populateMeshes(cubes, s)
        }

        needed = true
    }

    constructor(position: Vector3, cubes: com.badlogic.gdx.utils.Array<Model>, data: String) : this(position) {
        this.position = position
        val s = data.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        populateMeshes(cubes, s)
        needed = true
    }

    fun setDirtyPosition(index: Int, cubes: com.badlogic.gdx.utils.Array<Model>) {

        val v = Vector3(transformations.get(index).`val`[12] - position.x, transformations.get(index).`val`[13] - position.y, transformations.get(index).`val`[14] - position.z)

        Gdx.app.log("PART 2", " " + v.x + " " + v.y + " " + v.z)
        Gdx.app.log("PART 2", " " + (v.x + Constants.chunkSize / 2).toInt() + " " + (v.y + Constants.chunkSize / 2).toInt() + " " + (v.z + Constants.chunkSize / 2).toInt())

        val indexX: Int
        val indexY: Int
        val indexZ: Int
        indexX = (v.x + Constants.chunkSize / 2).toInt()
        indexY = (v.y + Constants.chunkSize / 2).toInt()
        indexZ = (v.z + Constants.chunkSize / 2).toInt()

        GameRenderHelper.chunk[indexX][indexY][indexZ] = Constants.BrickState.DELETED.value
        meshes.removeIndex(index)
        transformations.removeIndex(index)

        if (GameRenderHelper.chunk[indexX][indexY - 1][indexZ] == Constants.BrickState.HIDDEN.value) {

            Gdx.app.log("PART 3", " " + (v.x + position.x) + " " + (v.y + position.y - 1) + " " + (v.z + position.z))
            addMesh(Vector3(v.x + position.x, v.y + position.y - 1, v.z + position.z), cubes)

            GameRenderHelper.chunk[indexX][indexY - 1][indexZ] = Constants.BrickState.SHOW.value
        }

        dirty = true

        database.updateChunk(position.x, position.y, position.z, chunkToString())
    }

    fun addMesh(pos: Vector3, cubes: com.badlogic.gdx.utils.Array<Model>) {
        val r = MathUtils.random(cubes.size - 1)

        finished = false

        val model = ModelInstance(cubes.get(r), pos.x, pos.y, pos.z)
        meshes.addAll(model.model.meshes)
        transformations.add(model.transform)

        mesh = GameRenderHelper.mergeMeshes(meshes, transformations)
        finished = true

        val x: Int
        val y: Int
        val z: Int
        x = (pos.x - position.x).toInt() + Constants.chunkSize / 2//pos.x % 16;
        y = (pos.y - position.y).toInt() + Constants.chunkSize / 2 //pos.y % 16;
        z = (pos.z - position.z).toInt() + Constants.chunkSize / 2 //pos.z % 16;

        if (x > 15 || x < 0 || y > 15 || y < 0 || z > 15 || z < 0) {
            Gdx.app.log("PART 3", " " + position.x.toInt() + " " + position.y.toInt() + " " + position.z.toInt())
            Gdx.app.log("PART 3", " " + pos.x.toInt() + " " + pos.y.toInt() + " " + pos.z.toInt())
            //Gdx.app.log("PART 3", " " + (int)pos.x + (Constants.chunkSize / 2) + " " + (int)pos.y + " " + (int)pos.z + (Constants.chunkSize / 2) );
            Gdx.app.log("PART 3", " $x $y $z")
        }

        GameRenderHelper.chunk[x][y][z] = Constants.BrickState.SHOW.value + r
        database.updateChunk(position.x, position.y, position.z, chunkToString())
    }

    fun populateMeshes(cubes: com.badlogic.gdx.utils.Array<Model>, data: Array<String>) {
        var model: ModelInstance
        var index = 0

        for (x in 0 until Constants.chunkSize) {

            for (y in 0 until Constants.chunkSize)
                for (z in 0 until Constants.chunkSize) {

                    val textureValue = Integer.parseInt(data[index])

                    GameRenderHelper.chunk[x][y][z] = textureValue

                    if (textureValue > 0) {
                        model = ModelInstance(cubes.get(GameRenderHelper.chunk[x][y][z] - Constants.BrickState.SHOW.value), (position.x + x - Constants.chunkSize / 2) * Constants.cubeSize, (position.y + y - Constants.chunkSize / 2) * Constants.cubeSize, (position.z + z - Constants.chunkSize / 2) * Constants.cubeSize)
                        meshes.addAll(model.model.meshes)
                        transformations.add(model.transform)
                    }

                    index += 1
                }
        }

        Gdx.app.postRunnable {
            mesh = GameRenderHelper.mergeMeshes(meshes, transformations)
            finished = true
        }
    }

    fun createMeshes(cubes: com.badlogic.gdx.utils.Array<Model>) {
        var model: ModelInstance

        for (x in 0 until Constants.chunkSize) {

            for (y in 0 until Constants.chunkSize) {

                for (z in 0 until Constants.chunkSize) {

                    if (x > 0 && x < Constants.chunkSize - 1 && y > 0 && y < Constants.chunkSize - 1 && z > 0 && z < Constants.chunkSize - 1) {
                        GameRenderHelper.chunk[x][y][z] = Constants.BrickState.HIDDEN.value
                    } else {
                        val r = MathUtils.random(cubes.size - 1)

                        // r = Constants.TextureName.GRASS_NORMAL.ordinal();

                        GameRenderHelper.chunk[x][y][z] = Constants.BrickState.SHOW.value + r

                        //cube.materials.get(0).set(ColorAttribute.createDiffuse(color));
                        //new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
                        //model.materials.get(0).set(ColorAttribute.createDiffuse(color));
                        //model.model.materials.get(0).set(ColorAttribute.createDiffuse(color));

                        //model.transform.setToTranslation(position.x + x - (Constants.chunkSize / 2), position.y - y, position.z + z - (Constants.chunkSize / 2));
                        model = ModelInstance(cubes.get(r), (position.x + x - Constants.chunkSize / 2) * Constants.cubeSize, (position.y + y - Constants.chunkSize / 2) * Constants.cubeSize, (position.z + z - Constants.chunkSize / 2) * Constants.cubeSize)

                        meshes.addAll(model.model.meshes)
                        transformations.add(model.transform)
                    }
                }
            }
        }

        database.addChunk(position.x, position.y, position.z, chunkToString())

        Gdx.app.postRunnable {
            mesh = GameRenderHelper.mergeMeshes(meshes, transformations)
            finished = true
        }
    }

    fun chunkToString(): String {

        var s = String()

        for (x in 0 until Constants.chunkSize) {

            for (y in 0 until Constants.chunkSize) {

                for (z in 0 until Constants.chunkSize) {

                    s += GameRenderHelper.chunk[x][y][z].toString() + ","
                }
            }
        }

        return s.substring(0, s.length - 1)
    }

    fun checkDirty() {
        Gdx.app.postRunnable {
            if (dirty == true) {
                mesh!!.dispose()
                mesh = GameRenderHelper.mergeMeshes(meshes, transformations)
                dirty = false
                finished = true
            }
        }
    }

    fun render(shaderProgram: ShaderProgram) {
        //checkDirty();

        if (finished == true && deleting == false) {
            mesh!!.render(shaderProgram, GL20.GL_TRIANGLES)
        }
    }

    fun dispose() {
        // todo causes a crash
        deleting = true
        mesh!!.dispose()
    }
}
