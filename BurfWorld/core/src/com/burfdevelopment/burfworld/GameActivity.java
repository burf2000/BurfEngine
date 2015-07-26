package com.burfdevelopment.burfworld;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.burfdevelopment.burfworld.Screens.MainMenuScreen;

public class GameActivity extends Game implements ApplicationListener {

	MainMenuScreen mainMenu;
	
	@Override
	public void create () {

		Skybox.init();

		mainMenu = new MainMenuScreen();
		setScreen(mainMenu);
	}

	@Override
	public void render () {
		super.render();
	}
}
