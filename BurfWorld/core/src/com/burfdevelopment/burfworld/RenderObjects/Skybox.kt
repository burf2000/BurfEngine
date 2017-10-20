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

        private var model: Model? = null

        //TODO Entity list for things like Clouds, and future weather system
        //public static Array<ModelInstance> skyEntities;
        var modelInstance: ModelInstance? = null
        var manager: AssetManager? = null

        private var enabled: Boolean = false

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
            manager?.load("skybox/skybox.g3db", Model::class.java, modelParam)

            val skyTextureParam = TextureLoader.TextureParameter()
            skyTextureParam.genMipMaps = false
            skyTextureParam.magFilter = Texture.TextureFilter.Linear
            skyTextureParam.minFilter = Texture.TextureFilter.Linear

            //Load Skybox
            // todo make constant
            manager?.load("skybox/xpos.png", Texture::class.java, skyTextureParam)
            manager?.load("skybox/xneg.png", Texture::class.java, skyTextureParam)
            manager?.load("skybox/ypos.png", Texture::class.java, skyTextureParam)
            manager?.load("skybox/yneg.png", Texture::class.java, skyTextureParam)
            manager?.load("skybox/zpos.png", Texture::class.java, skyTextureParam)
            manager?.load("skybox/zneg.png", Texture::class.java, skyTextureParam)

            manager?.finishLoading()

            model = manager?.get("skybox/skybox.g3db", Model::class.java)

        }

        fun createSkyBox() { //Texture xpos, Texture xneg, Texture ypos, Texture yneg, Texture zpos, Texture zneg
            modelInstance = ModelInstance(model!!, "Skycube")

            val xpos = manager?.get("skybox/xpos.png", Texture::class.java)
            val xneg = manager?.get("skybox/xneg.png", Texture::class.java)
            val ypos = manager?.get("skybox/ypos.png", Texture::class.java)
            val yneg = manager?.get("skybox/yneg.png", Texture::class.java)
            val zpos = manager?.get("skybox/zpos.png", Texture::class.java)
            val zneg = manager?.get("skybox/zneg.png", Texture::class.java)

            // Set material textures
            modelInstance!!.materials.get(0).set(TextureAttribute.createDiffuse(xpos))
            modelInstance!!.materials.get(1).set(TextureAttribute.createDiffuse(xneg))
            modelInstance!!.materials.get(2).set(TextureAttribute.createDiffuse(ypos))
            modelInstance!!.materials.get(3).set(TextureAttribute.createDiffuse(yneg))
            modelInstance!!.materials.get(5).set(TextureAttribute.createDiffuse(zpos))
            modelInstance!!.materials.get(4).set(TextureAttribute.createDiffuse(zneg))

            //Disable depth test
            modelInstance!!.materials.get(0).set(DepthTestAttribute(0))
            modelInstance!!.materials.get(1).set(DepthTestAttribute(0))
            modelInstance!!.materials.get(2).set(DepthTestAttribute(0))
            modelInstance!!.materials.get(3).set(DepthTestAttribute(0))
            modelInstance!!.materials.get(4).set(DepthTestAttribute(0))
            modelInstance!!.materials.get(5).set(DepthTestAttribute(0))

            enabled = true
        }

        fun createSkyBox(skybox: Texture) {
            modelInstance = ModelInstance(model!!, "Skybox")

            // Set material texutres and Disable depth test
            modelInstance!!.materials.get(0).set(TextureAttribute.createDiffuse(skybox))
            modelInstance!!.materials.get(0).set(DepthTestAttribute(0))

            enabled = true
        }

        private val tmp = Vector3()
        private val q = Quaternion()
        var yawRotation = 0f
        var yawRotateSpeed = 0.01f

        fun update(position: Vector3) {
            tmp.set(position.x, position.y, position.z)
            modelInstance!!.transform.getRotation(q)
            yawRotation += yawRotateSpeed
            q.setFromAxis(Vector3.Y, yawRotation)
            modelInstance!!.transform.set(q)
            modelInstance!!.transform.setTranslation(tmp)
        }

        fun disable() {
            //TODO Make this a little bit nicer?
            modelInstance = null
            enabled = false
        }
    }
}