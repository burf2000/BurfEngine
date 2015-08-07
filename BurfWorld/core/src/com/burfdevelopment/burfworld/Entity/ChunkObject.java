package com.burfdevelopment.burfworld.Entity;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by burfies1 on 06/08/15.
 */
public class ChunkObject {

    private final Vector3 center;
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
        this.visable = false;
    }

    public void addModel(Model model)
    {
        this.model = new ModelInstance(model, center.x, center.y, center.z);
    }

}