package com.burfdevelopment.burfworld.Activity;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
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

	}

	@Override
	public void render () {
		super.render();
	}
}
