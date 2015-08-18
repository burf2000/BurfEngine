package com.burfdevelopment.burfworld.Activity;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.burfdevelopment.burfworld.Database.DatabaseTest;
import com.burfdevelopment.burfworld.Networking.Parse;
import com.burfdevelopment.burfworld.Screens.MainMenuScreen;
import com.burfdevelopment.burfworld.Skybox;

public class GameActivity extends Game implements ApplicationListener {

	MainMenuScreen mainMenu;
	
	@Override
	public void create () {

		Skybox.init();

		mainMenu = new MainMenuScreen();
		setScreen(mainMenu);

		Parse p = new Parse();
		p.add_net_score();


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

		DatabaseTest d = new DatabaseTest();
		//SQLHelper h = new SQLHelper("simon");


	}

	@Override
	public void render () {
		super.render();
	}
}
