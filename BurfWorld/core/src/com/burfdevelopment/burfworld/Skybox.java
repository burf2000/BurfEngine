
package com.burfdevelopment.burfworld;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Skybox {
    private static Model model;

    //TODO Entity list for things like Clouds, and future weather system
    //public static Array<ModelInstance> skyEntities;
    public static ModelInstance modelInstance;
    public static AssetManager manager;

    private static boolean enabled;

    public static void init() {
        enabled = false;

        TextureLoader.TextureParameter modTexParam = new TextureLoader.TextureParameter();
        modTexParam.genMipMaps = true;
        modTexParam.magFilter = Texture.TextureFilter.Nearest;
        modTexParam.minFilter = Texture.TextureFilter.MipMapNearestNearest;
        modTexParam.wrapU = Texture.TextureWrap.Repeat;
        modTexParam.wrapV = Texture.TextureWrap.Repeat;

        // Load managed model
        manager = new AssetManager();

        ModelLoader.ModelParameters modelParam = new ModelLoader.ModelParameters();
        modelParam.textureParameter = modTexParam;
        manager.load("skybox/skybox.g3db", Model.class, modelParam);

        TextureLoader.TextureParameter skyTextureParam = new TextureLoader.TextureParameter();
        skyTextureParam.genMipMaps = false;
        skyTextureParam.magFilter = Texture.TextureFilter.Linear;
        skyTextureParam.minFilter = Texture.TextureFilter.Linear;

        //Load Skybox
        // todo make constant
        manager.load("skybox/xpos.png", Texture.class, skyTextureParam);
        manager.load("skybox/xneg.png", Texture.class, skyTextureParam);
        manager.load("skybox/ypos.png", Texture.class, skyTextureParam);
        manager.load("skybox/yneg.png", Texture.class, skyTextureParam);
        manager.load("skybox/zpos.png", Texture.class, skyTextureParam);
        manager.load("skybox/zneg.png", Texture.class, skyTextureParam);

        manager.finishLoading();

        model = manager.get("skybox/skybox.g3db", Model.class);

    }

    public static void createSkyBox() { //Texture xpos, Texture xneg, Texture ypos, Texture yneg, Texture zpos, Texture zneg
        modelInstance = new ModelInstance(model, "Skycube");

        Texture xpos = manager.get("skybox/xpos.png", Texture.class);
        Texture xneg = manager.get("skybox/xneg.png", Texture.class);
        Texture ypos = manager.get("skybox/ypos.png", Texture.class);
        Texture yneg = manager.get("skybox/yneg.png", Texture.class);
        Texture zpos = manager.get("skybox/zpos.png", Texture.class);
        Texture zneg = manager.get("skybox/zneg.png", Texture.class);

        // Set material textures
        modelInstance.materials.get(0).set(TextureAttribute.createDiffuse(xpos));
        modelInstance.materials.get(1).set(TextureAttribute.createDiffuse(xneg));
        modelInstance.materials.get(2).set(TextureAttribute.createDiffuse(ypos));
        modelInstance.materials.get(3).set(TextureAttribute.createDiffuse(yneg));
        modelInstance.materials.get(5).set(TextureAttribute.createDiffuse(zpos));
        modelInstance.materials.get(4).set(TextureAttribute.createDiffuse(zneg));

        //Disable depth test
        modelInstance.materials.get(0).set(new DepthTestAttribute(0));
        modelInstance.materials.get(1).set(new DepthTestAttribute(0));
        modelInstance.materials.get(2).set(new DepthTestAttribute(0));
        modelInstance.materials.get(3).set(new DepthTestAttribute(0));
        modelInstance.materials.get(4).set(new DepthTestAttribute(0));
        modelInstance.materials.get(5).set(new DepthTestAttribute(0));

        enabled = true;
    }

    public static void createSkyBox(Texture skybox) {
        modelInstance = new ModelInstance(model, "Skybox");

        // Set material texutres and Disable depth test
        modelInstance.materials.get(0).set(TextureAttribute.createDiffuse(skybox));
        modelInstance.materials.get(0).set(new DepthTestAttribute(0));

        enabled = true;
    }

    private static final Vector3 tmp = new Vector3();
    private static final Quaternion q = new Quaternion();
    public static float yawRotation = 0f;
    public static float yawRotateSpeed = 0.01f;

    public static void update(Vector3 position) {
        tmp.set(position.x, position.y, position.z);
        modelInstance.transform.getRotation(q);
        yawRotation += yawRotateSpeed;
        q.setFromAxis(Vector3.Y, yawRotation);
        modelInstance.transform.set(q);
        modelInstance.transform.setTranslation(tmp);
    }

    public static void disable() {
        //TODO Make this a little bit nicer?
        modelInstance = null;
        enabled = false;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
