package com.burfdevelopment.burfworld.Entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;


/**
 * Created by burfies1 on 06/08/15.
 */
public class ChunkObject  implements Pool.Poolable {

    public Vector3 center;
    public ModelInstance model;
    public boolean visable = false;

    public Vector3 getCenter() {
        return center;
    }

    public ChunkObject (Model model, Vector3 position) {
        this.model = new ModelInstance(model, position.x, position.y, position.z);
        this.center = new Vector3(position);
        this.visable = true;
    }

    public  ChunkObject ( Vector3 position) {
        this.center = new Vector3(position);
        this.visable = true;
    }

    public  ChunkObject () {
        this.visable = true;
        this.center = new Vector3(0,0,0);
    }

    public void addModel(Model model, Color color)
    {
        if(this.model != null)
        {
            this.model.transform.setToTranslation(center.x, center.y, center.z);
        }
        else
        {
            this.model = new ModelInstance(model, center.x, center.y, center.z);
            this.model.materials.get(0).set(ColorAttribute.createDiffuse(color));
        }
    }

    @Override
    public void reset() {
        this.center.set(0,0,0);
        visable = true;

//        if (model != null)
//        {
//            model.model.dispose();
//            model = null;
//        }
    }
}