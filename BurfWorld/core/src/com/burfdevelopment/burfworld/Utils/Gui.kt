package com.burfdevelopment.burfworld.Utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/**
 * Created by burfies1 on 20/10/2017.
 */
object Gui { // object good for singleton pattern

    @JvmField val skin = Skin(Gdx.files.internal("skins/uiskin.json"))

    fun dispose() {
        skin.dispose()
    }


}
