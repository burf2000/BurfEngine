package com.burfdevelopment.burfworld.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.async.AsyncExecutor
import com.badlogic.gdx.utils.async.AsyncTask
import com.burfdevelopment.burfworld.Constants
import com.burfdevelopment.burfworld.Entity.MeshBuilder
import com.burfdevelopment.burfworld.RenderObjects.Skybox
import com.burfdevelopment.burfworld.Utils.InputController

/**
 * Created by burfies1 on 20/10/2017.
 */

class GameRenderScreen : Screen {

    private val stage = Stage()
    private var font: BitmapFont = BitmapFont()
    private var camera: PerspectiveCamera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    private lateinit var fps: InputController
    private var lights: Environment = Environment()
    private var accum = 0.0f

    private val spriteBatch = SpriteBatch()

    private val modelBatch = ModelBatch()
    // chunk size is the chunkArea * chunkArea
    private lateinit var chunks2: Array<MeshBuilder>
    private var oldPosition = Vector3()

    private lateinit var shaderProgram: ShaderProgram

    private var texture: Texture = Texture("textures/texturemap.png")
    private lateinit var modelBuilder: ModelBuilder

    private var cubes: Array<Model> = Array<Model>()

    var isJump: Boolean = false
    var jumping = 0.0f
    private var currentHeight = 15.0f
    private var disableRender = false

    fun setupFont() {
        font.color = Color.WHITE
        font.data.setScale(2.0f)
    }

    override fun show() {

        setupFont()

        compileShaderTexture()
        Skybox.createSkyBox()

        camera.near = 0.5f
        camera.far = 300.0f
        camera.position.set(0f, Constants.startingHeight, 0f)

        fps = InputController(camera, this, stage)

        disableRender = true
        setupChunks()

        // This allows to setup the scene before we render
        var task: Timer.Task = object : Timer.Task() {
            override fun run() {
                disableRender = false
            }
        }
        // 2 second delay
        Timer.schedule(task, 2.0f)

        //Light..
        lights.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        lights.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -0.2f))
    }

    fun setupChunks() {
        chunks2 = Array()
        createChunk(0f, 0f)

        // task so does not get fired off
        task = AsyncTask<Void> {
            Gdx.app.log("DEBUG", "Firing of createChunk");
            createChunk((camera.position.x.toInt() / Constants.chunkSize).toFloat(), (camera.position.z.toInt() / Constants.chunkSize).toFloat())
            null
        }

    }

    private fun update() {

        oldPosition.set(camera.position)
        fps.updateControls()
        fps.update()
        if (isJump == true) {

            jumping += Gdx.graphics.deltaTime * Constants.jumpRate

            if (jumping > Constants.maxJump) {
                isJump = false
            }
        } else {
            //todo add a linear increase decrease to jumping
            jumping -= Gdx.graphics.deltaTime * Constants.jumpRate

            if (jumping < 0)
                jumping = 0f

        }

        checkCollison()
    }

    private fun checkCollison() {

        var height = 0.0f
        var collison = false

        for (i in 0 until chunks2.size) {

            for (a in 0 until chunks2.get(i).transformations.size) {
                if (camera.position.x > chunks2.get(i).transformations.get(a).`val`[12] - Constants.cubeCollisonSize &&
                        camera.position.z > chunks2.get(i).transformations.get(a).`val`[14] - Constants.cubeCollisonSize &&
                        camera.position.x < chunks2.get(i).transformations.get(a).`val`[12] + Constants.cubeCollisonSize &&
                        camera.position.z < chunks2.get(i).transformations.get(a).`val`[14] + Constants.cubeCollisonSize) {

                    if (camera.position.y > chunks2.get(i).transformations.get(a).`val`[13] - Constants.cubeCollisonSize && camera.position.y < chunks2.get(i).transformations.get(a).`val`[13] + Constants.cubeCollisonSize) {
                        //Gdx.app.log("DEBUG", "Collision 1 "+ camera.position.toString());
                        collison = true
                        break
                    } else if (chunks2.get(i).transformations.get(a).`val`[13] + 1.0 <= camera.position.y) {
                        if (chunks2.get(i).transformations.get(a).`val`[13] + Constants.headHeight > height) {
                            height = chunks2.get(i).transformations.get(a).`val`[13] + Constants.headHeight
                        }
                    } else {
                        if (chunks2.get(i).transformations.get(a).`val`[13] > height) {
                            height = chunks2.get(i).transformations.get(a).`val`[13]
                        }
                    }
                }

            }
        }

        if (collison == true) {
            camera.position.set(oldPosition)
            //Gdx.app.log("DEBUG", "Collision 2 " + oldPosition.toString());
        } else {

            if (height > currentHeight + 2.0f) {
                camera.position.set(oldPosition)
            } else if (height < currentHeight) {
                currentHeight -= Gdx.graphics.deltaTime * 2
                camera.position.set(camera.position.x, currentHeight + jumping, camera.position.z)
            } else {
                currentHeight = height
                camera.position.set(camera.position.x, currentHeight + jumping, camera.position.z)
            }


        }
    }

    override fun render(delta: Float) {

        if (disableRender == true) {
            return
        }

        update() // controls

        accum += Gdx.graphics.deltaTime

        if (accum >= TICK)
        {
            accum = 0.0f
            // fire off chunk builder
            executor.submit(task)
        }

        Gdx.gl20.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)

        //TODO do we need all of these?
        //Do all your basic OpenGL ES setup to start the screen render.
        Gdx.gl20.glClearColor(0.0f, 0.3f, 0.5f, 1f)
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch.begin(camera)
        Skybox.update(camera.position)
        modelBatch.render(Skybox.modelInstance)
        modelBatch.end()

        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D)
        Gdx.gl20.glEnable(GL20.GL_BLEND)
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl20.glCullFace(GL20.GL_BACK)
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST)

        shaderProgram.begin()
        texture.bind()
        shaderProgram.setUniformMatrix("u_projTrans", camera.combined)
        shaderProgram.setAttributef("a_color", 1f, 1f, 1f, 1f)
        shaderProgram.setUniformi("u_texture", 0)


        synchronized(chunks2) {
            var i = chunks2.iterator()
            var x = 0
            while (i.hasNext()) {
                chunks2.get(x).render(shaderProgram)
                x += 1
                i.next()
            }

        }

        shaderProgram.end()

        // display text
        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
        font.draw(spriteBatch, "Testing 1 2 3", 0f, 10f)
        spriteBatch.end()

        stage.viewport.update(width(), height(), true)
        stage.act(delta)
        stage.draw()

        drawFPS()
    }

    fun drawFPS() {

        val fonYOffSet = 35.0f
        val FontXOffSet = 10.0f

        stage.batch.begin()

        font.draw(stage.batch, "FPS: " + Gdx.graphics.framesPerSecond + " Cube Count " + Constants.renderCount + " chunk Count " + chunks2.size, FontXOffSet, Gdx.graphics.height - (fonYOffSet * 1))
        font.draw(stage.batch, "Mem: " + Gdx.app.javaHeap / 1000000f + " " + Gdx.app.nativeHeap / 1000000f, FontXOffSet, Gdx.graphics.height - (fonYOffSet * 2))
        font.draw(stage.batch, "X: " + camera.position.x + " Y " + camera.position.y + " Z " + camera.position.z, FontXOffSet, Gdx.graphics.height - (fonYOffSet * 3))
        // cool if :)
        font.draw(stage.batch, "Mode: " + if (fps.isAdding) "adding" else "removing", FontXOffSet, Gdx.graphics.height - (fonYOffSet * 4))

        stage.batch.end()
    }

    fun removeObject(chunkIndex: Int, meshIndex: Int) {

        Gdx.app.log("BUILDING", "Removing chunk " + chunkIndex + "mesh " + meshIndex)
        chunks2.get(chunkIndex).setDirtyPosition(meshIndex, cubes)
    }

    fun addObject(xxx: Float, yyy: Float, zzz: Float, vv: Vector3) {

        // function parameters are passed as VAL
        var x = xxx
        var y = yyy
        var z = zzz

        // work out what side we touched
        val xDif: Float
        val yDif: Float
        val zDif: Float
        var xOffset = 0f
        var yOffset = 0f
        var zOffset = 0f
        val dif = 0.5f

        if (x > vv.x) {
            xDif = x - vv.x
        } else {
            xDif = vv.x - x
        }

        if (y > vv.y) {
            yDif = y - vv.y
        } else {
            yDif = vv.y - y
        }

        if (z > vv.z) {
            zDif = z - vv.z
        } else {
            zDif = vv.z - z
        }

        if (xDif > yDif && xDif > zDif) {
            if (x > vv.x) {
                xOffset = dif
            } else {
                xOffset = -dif
            }
        } else if (yDif > xDif && yDif > zDif) {
            if (y > vv.y) {
                yOffset = dif
            } else {
                yOffset = -dif
            }
        } else {
            if (z > vv.z) {
                zOffset = dif
            } else {
                zOffset = -dif
            }
        }

        x = MathUtils.round(x + xOffset).toFloat()
        y = MathUtils.round(y + yOffset).toFloat()
        z = MathUtils.round(z + zOffset).toFloat()

        var foundChunk = false
        for (i in 0 until chunks2.size) {

            Gdx.app.log("PART 2", "searching chunk " + chunks2.get(i).position.toString())

            if (x >= chunks2.get(i).position.x - Constants.chunkSize / 2 &&
                    x < chunks2.get(i).position.x + Constants.chunkSize / 2 &&
                    y >= chunks2.get(i).position.y - Constants.chunkSize / 2 &&
                    y < chunks2.get(i).position.y + Constants.chunkSize / 2 &&
                    z >= chunks2.get(i).position.z - Constants.chunkSize / 2 &&
                    z < chunks2.get(i).position.z + Constants.chunkSize / 2) {
                Gdx.app.log("PART 2", "Found chunk " + chunks2.get(i).position.toString() + " " + Vector3(x, y, z))
                foundChunk = true

                chunks2.get(i).addMesh(Vector3(x, y, z), cubes)
                break
            }
        }

        // create the new chunk
        if (foundChunk == false) {
            val yy: Int
            val xx: Int
            val zz: Int

            if (x > 0) {
                xx = ((x + Constants.chunkSize / 2) / Constants.chunkSize).toInt() * Constants.chunkSize
            } else {
                xx = ((x - Constants.chunkSize / 2) / Constants.chunkSize).toInt() * Constants.chunkSize
            }

            if (y > 0) {
                yy = ((y + Constants.chunkSize / 2) / Constants.chunkSize).toInt() * Constants.chunkSize
            } else {
                yy = ((y - Constants.chunkSize / 2) / Constants.chunkSize).toInt() * Constants.chunkSize
            }

            if (z > 0) {
                zz = ((z + Constants.chunkSize / 2) / Constants.chunkSize).toInt() * Constants.chunkSize
            } else {
                zz = ((z - Constants.chunkSize / 2) / Constants.chunkSize).toInt() * Constants.chunkSize
            }

            val v = Vector3(xx.toFloat(), yy.toFloat(), zz.toFloat())
            Gdx.app.log("PART 2", "DID NOT FIND chunk " + v.toString() + " " + Vector3(x, y, z))
            val m = MeshBuilder(v)
            m.addMesh(Vector3(MathUtils.round(x).toFloat(), MathUtils.round(y).toFloat(), MathUtils.round(z).toFloat()), cubes)
            // create empty chunk
            chunks2.add(m)
            // add item
        }
    }

    fun getObject(screenX: Int, screenY: Int): Int {

        Gdx.app.log("BUILDING", "GETTING x " + screenX + "y " + screenY)

        var chunkIndex = -1
        var meshIndex = -1
        var distance = -1f
        val bounds = BoundingBox()

        val ray = camera.getPickRay(screenX.toFloat(), screenY.toFloat())
        val pos = Vector3(camera.position)
        val v = Vector3()
        val transfor = Matrix4()
        var center = Vector3()

        for (i in 0 until chunks2.size) {

            val chunkInstance = chunks2.get(i)

            if (Intersector.intersectRayBoundsFast(ray, chunkInstance.position, Vector3(Constants.chunkSize.toFloat(), Constants.chunkSize.toFloat(), Constants.chunkSize.toFloat()))) {
                Gdx.app.log("RAN", "SHOULD BE once")

                for (a in 0 until chunkInstance.transformations.size) {

                    //todo probably can improve as pissing memorys
                    transfor.set(chunkInstance.transformations.get(a).values)
                    val m = chunkInstance.meshes.get(a).copy(false)
                    transfor.getTranslation(pos)
                    m.transform(transfor)
                    m.calculateBoundingBox(bounds)

                    //Gdx.app.log("MyTag 2", "Mesh" + transfor.toString());
                    //bounds.set(bounds.min.add(transfor.M03, transfor.M13, transfor.M23), bounds.max.add(transfor.M03, transfor.M13, transfor.M23));
                    //bounds.mul(transfor);

                    val dist2 = ray.origin.dst2(pos)
                    Gdx.app.log("MyTag 2", "DIS " + dist2);

                    if (distance >= 0f && dist2 > distance || dist2 > Constants.rayDistance) continue

                    if (Intersector.intersectRayBounds(ray, bounds, v)) {
                        Gdx.app.log("MyTag 2", "WE HIT " + v.toString() + " " + transfor.getTranslation(pos))
                        chunkIndex = i
                        meshIndex = a
                        distance = dist2
                        center = Vector3(transfor.getTranslation(pos))
                    }

                    m.dispose()
                }
            }
        }

        if (chunkIndex > -1) {
            Gdx.app.log("MyTag 2", "DIS LAST " + distance)
            Gdx.app.log("MyTag 2", "x " + v.x + " y " + v.y + " z " + v.z + " " + center)
            Gdx.app.log("MyTag 2", "x " + MathUtils.round(v.x) + " y " + MathUtils.round(v.y) + " z " + MathUtils.round(v.z));

            if (fps.isAdding == true) {
                addObject(v.x, v.y, v.z, center)
            } else {
                removeObject(chunkIndex, meshIndex)
            }

            val s = chunks2.get(0).chunkToString()
            Gdx.app.log("CHUNK", s)
        }

        return 1
    }

    fun markAddChunk(x: Float, z: Float) {
        var found = false
        for (i in 0 until chunks2.size) {

            if (chunks2.get(i).position.x == x * Constants.chunkSize && chunks2.get(i).position.z == z * Constants.chunkSize) {
                //Gdx.app.log("ERROR", "FOUND " + chunks2.get(i).position.x.toString());
                chunks2.get(i).needed = true
                found = true
            }
        }

        if (found == false) {
            Gdx.app.log("ERROR", "Adding " + Vector3(x * Constants.chunkSize, 0f, z * Constants.chunkSize))
            chunks2.add(MeshBuilder(Vector3(x * Constants.chunkSize, 0.0f, z * Constants.chunkSize), cubes))

            val v = MeshBuilder.database.getHeightChunk(x * Constants.chunkSize, z * Constants.chunkSize)
            for (a in 0 until v.size) {

                if (v.get(a).y != 0f) {
                    chunks2.add(MeshBuilder(v.get(a), cubes))
                }
            }
        }
    }

    fun compileShaderTexture() {

        val vertexShader = Gdx.files.internal("shaders/vert.glsl").readString()
        val fragmentShader = Gdx.files.internal("shaders/frag.glsl").readString()
        shaderProgram = ShaderProgram(vertexShader, fragmentShader)

        if (!shaderProgram.isCompiled) {
            Gdx.app.log("ERROR", "Couldn't compile shader: " + shaderProgram.log)
            throw GdxRuntimeException("Couldn't compile shader: " + shaderProgram.log)
        }

        MeshBuilder.regions = TextureRegion.split(texture, 64, 64)

        var cube: Model

        modelBuilder = ModelBuilder()
        //        cube = modelBuilder.createBox(Constants.cubeSize, Constants.cubeSize, Constants.cubeSize,
        //                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
        //                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);


        //todo nasty need to think about as its creating all of the textures cubes
        cubes = Array()
        for (x in 0..7) {
            for (y in 0..7) {

                modelBuilder.begin()
                val mpb = modelBuilder.part("box", GL20.GL_TRIANGLES, (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong(), Material(ColorAttribute.createDiffuse(Color.BLUE)))
                mpb.setUVRange(MeshBuilder.regions[x][y])
                mpb.box(1.0f, 1.0f, 1.0f)
                cube = modelBuilder.end()
                cube.meshes.get(0).scale(Constants.cubeSize, Constants.cubeSize, Constants.cubeSize)
                cubes.add(cube)
            }
        }
    }

    fun createChunk(x: Float, z: Float) { //, Vector3 direction

        disableRender = true

        for (i in 0 until chunks2.size) {
            chunks2.get(i).needed = false
            chunks2.get(i).checkDirty()
        }

        val size = Constants.chunkArea
        for (xx in 0 until size) {
            for (zz in 0 until size) {
                Gdx.app.log("POSITION", "x ${x + xx - (size - 1) / 2} z ${z + zz - (size - 1) / 2}")
                markAddChunk(x + xx - (size - 1) / 2, z + zz - (size - 1) / 2)
            }
        }

        // make a copy of all items we want
        var chunks3 = Array<MeshBuilder>()

        for (i in 0 until chunks2.size) {

            if (chunks2.get(i).needed == true) {
                chunks3.add(chunks2.get(i))
            }
        }

        chunks2 = chunks3

        disableRender = false
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {

        cubes.clear()
        stage.dispose()
        font.dispose()
        modelBatch.dispose()
        shaderProgram.dispose()
        texture.dispose()
        Skybox.disable()
        chunks2.clear()

    }

    companion object {

        fun width(): Int {
            return Gdx.graphics.width
        }

        fun height(): Int {
            return Gdx.graphics.height
        }

        private val TICK = 30 / 60f //1 / 60

        private val executor = AsyncExecutor(1)
        private var task: AsyncTask<*>? = null
    }

}
