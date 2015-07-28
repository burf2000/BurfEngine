package com.burfdevelopment.burfworld.Entity;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by burfies1 on 27/07/15.
 */
public class GameObject extends ModelInstance {
    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    private final static BoundingBox bounds = new BoundingBox();

    public GameObject (Model model, float x, float y, float z) {
        super(model, x,y,z);
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
    }
}

