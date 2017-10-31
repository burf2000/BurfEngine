package com.burfdevelopment.burfworld.RenderObjects

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.ModelLoader
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3

/**
 * Created by burfies1 on 20/10/2017.
 */
public class Skybox {

    companion object {

        private lateinit var model: Model
        lateinit var modelInstance: ModelInstance
        private lateinit var manager: AssetManager
        private var enabled: Boolean = false

        const val skyboxXpos = "skybox/xpos.png"
        const val skyboxXneg = "skybox/xneg.png"
        const val skyboxYpos = "skybox/ypos.png"
        const val skyboxYneg = "skybox/yneg.png"
        const val skyboxZpos = "skybox/zpos.png"
        const val skyboxZneg = "skybox/zneg.png"
        const val skyBoxModel = "skybox/skybox.g3db"

        fun init() {
            enabled = false

            val modTexParam = TextureLoader.TextureParameter()
            modTexParam.genMipMaps = true
            modTexParam.magFilter = Texture.TextureFilter.Nearest
            modTexParam.minFilter = Texture.TextureFilter.MipMapNearestNearest
            modTexParam.wrapU = Texture.TextureWrap.Repeat
            modTexParam.wrapV = Texture.TextureWrap.Repeat

            // Load managed model
            manager = AssetManager()

            val modelParam = ModelLoader.ModelParameters()
            modelParam.textureParameter = modTexParam
            manager.load(skyBoxModel, Model::class.java, modelParam)

            val skyTextureParam = TextureLoader.TextureParameter()
            skyTextureParam.genMipMaps = false
            skyTextureParam.magFilter = Texture.TextureFilter.Linear
            skyTextureParam.minFilter = Texture.TextureFilter.Linear

            //Load Skybox
            manager.load(skyboxXpos, Texture::class.java, skyTextureParam)
            manager.load(skyboxXneg, Texture::class.java, skyTextureParam)
            manager.load(skyboxYpos, Texture::class.java, skyTextureParam)
            manager.load(skyboxYneg, Texture::class.java, skyTextureParam)
            manager.load(skyboxZpos, Texture::class.java, skyTextureParam)
            manager.load(skyboxZneg, Texture::class.java, skyTextureParam)

            manager.finishLoading()

            model = manager.get(skyBoxModel, Model::class.java)
        }

        fun createSkyBox() { //Texture xpos, Texture xneg, Texture ypos, Texture yneg, Texture zpos, Texture zneg
            modelInstance = ModelInstance(model, "Skycube")

            val xpos = manager.get(skyboxXpos, Texture::class.java)
            val xneg = manager.get(skyboxXneg, Texture::class.java)
            val ypos = manager.get(skyboxYpos, Texture::class.java)
            val yneg = manager.get(skyboxYneg, Texture::class.java)
            val zpos = manager.get(skyboxZpos, Texture::class.java)
            val zneg = manager.get(skyboxZneg, Texture::class.java)

            // Set material textures
            modelInstance.materials.get(0).set(TextureAttribute.createDiffuse(xpos))
            modelInstance.materials.get(1).set(TextureAttribute.createDiffuse(xneg))
            modelInstance.materials.get(2).set(TextureAttribute.createDiffuse(ypos))
            modelInstance.materials.get(3).set(TextureAttribute.createDiffuse(yneg))
            modelInstance.materials.get(5).set(TextureAttribute.createDiffuse(zpos))
            modelInstance.materials.get(4).set(TextureAttribute.createDiffuse(zneg))

            //Disable depth test
            modelInstance.materials.get(0).set(DepthTestAttribute(0))
            modelInstance.materials.get(1).set(DepthTestAttribute(0))
            modelInstance.materials.get(2).set(DepthTestAttribute(0))
            modelInstance.materials.get(3).set(DepthTestAttribute(0))
            modelInstance.materials.get(4).set(DepthTestAttribute(0))
            modelInstance.materials.get(5).set(DepthTestAttribute(0))

            enabled = true
        }

        fun createSkyBox(skybox: Texture) {
            modelInstance = ModelInstance(model, "Skybox")

            // Set material texutres and Disable depth test
            modelInstance.materials.get(0).set(TextureAttribute.createDiffuse(skybox))
            modelInstance.materials.get(0).set(DepthTestAttribute(0))

            enabled = true
        }

        private val tmp = Vector3()
        private val q = Quaternion()
        var yawRotation = 0f
        var yawRotateSpeed = 0.01f

        fun update(position: Vector3) {
            tmp.set(position.x, position.y, position.z)
            modelInstance.transform.getRotation(q)
            yawRotation += yawRotateSpeed
            q.setFromAxis(Vector3.Y, yawRotation)
            modelInstance.transform.set(q)
            modelInstance.transform.setTranslation(tmp)
        }

        fun disable() {
            enabled = false
        }
    }
}