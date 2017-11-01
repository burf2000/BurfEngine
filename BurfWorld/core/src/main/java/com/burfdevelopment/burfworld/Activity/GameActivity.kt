package com.burfdevelopment.burfworld.Activity

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Game
import com.burfdevelopment.burfworld.RenderObjects.Skybox
import com.burfdevelopment.burfworld.Screens.MainMenuScreen

/**
 * Created by burfies1 on 20/10/2017.
 */
class GameActivity : Game(), ApplicationListener {

    private lateinit var mainMenu: MainMenuScreen

    override fun create() {

        Skybox.init()
        mainMenu = MainMenuScreen()
        setScreen(mainMenu)

        //val p = Parse()
        //p.add_net_score()

    }
}
