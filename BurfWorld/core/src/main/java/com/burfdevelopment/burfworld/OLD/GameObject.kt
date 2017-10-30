package com.burfdevelopment.burfworld.OLD

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox

/**
 * Created by burfies1 on 20/10/2017.
 */
class GameObject(model: Model, position: Vector3, val checkCollison: Boolean) : ModelInstance(model, position.x, position.y, position.z) {

    val center: Vector3
    val dimensions = Vector3()

    init {
        this.center = Vector3(position)
        updateBox()
    }

    fun updateBox() {
        calculateBoundingBox(bounds)
        bounds.getCenter(center)
        bounds.getDimensions(dimensions)
        bounds.set(bounds.min.add(center.x, center.y, center.z), bounds.max.add(center.x, center.y, center.z))

        Gdx.app.log("MyTag 2", "x " + bounds.min + " z " + bounds.max)
    }

    companion object {
        var bounds = BoundingBox()
    }

}
