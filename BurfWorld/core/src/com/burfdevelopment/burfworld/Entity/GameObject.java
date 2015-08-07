package com.burfdevelopment.burfworld.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by burfies1 on 27/07/15.
 */
public class GameObject extends ModelInstance {

    private final Vector3 center;
    public final Vector3 dimensions = new Vector3();
    public static BoundingBox bounds = new BoundingBox();
    public final boolean checkCollison;

    public Vector3 getCenter() {
        return center;
    }

    public GameObject (Model model, Vector3 position, boolean checkCollison) {
        super(model, position.x, position.y, position.z);
        this.center = new Vector3(position);
        this.checkCollison = checkCollison;
        updateBox();
    }

    public void updateBox()
    {
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        bounds.set(bounds.min.add(center.x, center.y, center.z), bounds.max.add(center.x, center.y, center.z));

        Gdx.app.log("MyTag 2", "x " + bounds.min + " z " + bounds.max);
    }

}

