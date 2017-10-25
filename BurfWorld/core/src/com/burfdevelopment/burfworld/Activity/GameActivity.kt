package com.burfdevelopment.burfworld.Activity

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Game
import com.burfdevelopment.burfworld.RenderObjects.Skybox
import com.burfdevelopment.burfworld.Screens.MainMenuScreen

/**
 * Created by burfies1 on 20/10/2017.
 */
class GameActivity : Game(), ApplicationListener {

    internal lateinit var mainMenu: MainMenuScreen

    override fun create() {

        Skybox.init()
        mainMenu = MainMenuScreen()
        setScreen(mainMenu)

        //val p = Parse()
        //p.add_net_score()


        //		String javaLibPath = System.getProperty("java.library.path");
        //		Map<String, String> envVars = System.getenv();
        //		System.out.println(envVars.get("Path"));
        //		System.out.println(javaLibPath);
        //		for (String var : envVars.keySet()) {
        //			System.err.println("examining " + var);
        //			if (envVars.get(var).equals(javaLibPath)) {
        //				System.out.println(var);
        //			}
        //		}

        //DatabaseHelper d = new DatabaseHelper();
        //SQLHelper h = new SQLHelper("simon");

    }

    override fun render() {
        super.render()
    }
}
