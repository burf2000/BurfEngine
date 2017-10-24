package com.burfdevelopment.burfworld.OLD

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

/**
 * Created by burfies1 on 20/10/2017.
 */
class ChunkObject : Pool.Poolable {

    var center: Vector3
    var model: ModelInstance? = null
    var visable = false

    constructor(model: Model, position: Vector3) {
        this.model = ModelInstance(model, position.x, position.y, position.z)
        this.center = Vector3(position)
        this.visable = true
    }

    constructor(position: Vector3) {
        this.center = Vector3(position)
        this.visable = true
    }

    constructor() {
        this.visable = true
        this.center = Vector3(0f, 0f, 0f)
    }

    fun addModel(model: Model, color: Color) {
        if (this.model != null) {
            this.model!!.transform.setToTranslation(center.x, center.y, center.z)
        } else {
            this.model = ModelInstance(model, center.x, center.y, center.z)
            this.model!!.materials.get(0).set(ColorAttribute.createDiffuse(color))
        }
    }

    override fun reset() {
        this.center.set(0f, 0f, 0f)
        visable = true

        //        if (model != null)
        //        {
        //            model.model.dispose();
        //            model = null;
        //        }
    }
}