package com.burfdevelopment.burfworld;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AndroidGameControls {
	private Touchpad aPadA;
	private Touchpad aPadB;
//	private TextButton quitBtn;

	public static int width() { return Gdx.graphics.getWidth(); }
	public static int height() { return Gdx.graphics.getHeight(); }

	public void buildGameControls(Stage stage, Skin skin){
		stage.clear();
		stage.setViewport(new ScreenViewport());
		
		if(!isOnDesktop()){
			
			aPadA = CreatePad(skin, 10, 30, 200, 200, 1,0,0,0.4f);
			aPadB = CreatePad(skin, width() - 200 - 30, 30, 200, 200, 0,0,1,0.4f);

//			quitBtn = new TextButton("Quit", skin);
//			quitBtn.setBounds(740, 420, 50, 50);
			
			stage.addActor(aPadA);
			stage.addActor(aPadB);
//			stage.addActor(quitBtn);
			
			InputMultiplexer multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(stage);
			
			Gdx.input.setInputProcessor(multiplexer);
		}
	}
	
	public Touchpad CreatePad( Skin skin, int x, int y, int width, int height, float r, float g, float b, float a){
		Touchpad tpad = new Touchpad(0.5f, skin);
		tpad.setX(x);
		tpad.setY(y);

		tpad.setWidth(width);
		tpad.setHeight(height);

		tpad.setColor(r, g, b, a);
		return tpad;
	}
	
	public static boolean isOnDesktop(){
		return (Gdx.app.getType() != ApplicationType.Android && Gdx.app.getType() != ApplicationType.iOS);
		//return false; //comment out the line above this, and uncomment this line to see the android controls on desktop.
	}
	
	public boolean isPadALeft(){
		if(aPadA == null) return false;
		return (aPadA.getKnobX() < (aPadA.getWidth()*0.33f));
	}
	
	public boolean isPadARight(){
		if(aPadA == null) return false;
		return (aPadA.getKnobX() > (aPadA.getWidth()*0.66f));
	}
	
	
	public boolean isPadAUp(){
		if(aPadA == null) return false;
		return (aPadA.getKnobY() > (aPadA.getHeight()*0.66f));
	}
	
	public boolean isPadADown(){
		if(aPadA == null) return false;
		return (aPadA.getKnobY() < (aPadA.getHeight()*0.33f));
	}
	
	
	public boolean isPadBLeft(){
		if(aPadB == null) return false;
		return (aPadB.getKnobX() < (aPadB.getWidth()*0.33f));
	}
	
	public boolean isPadBRight(){
		if(aPadB == null) return false;
		return (aPadB.getKnobX() > (aPadB.getWidth()*0.66f));
	}

	public boolean isPadBUp(){
		if(aPadB == null) return false;
		return (aPadB.getKnobY() < (aPadB.getWidth()*0.33f));
	}

	public boolean isPadBDown(){
		if(aPadB == null) return false;
		return (aPadB.getKnobY() > (aPadB.getWidth()*0.66f));
	}
	
	public void updateControls(Player player){
		if(this.isPadAUp()) player.moveForward();
		if(this.isPadADown()) player.moveBackward();
		if(this.isPadALeft()) player.moveLeft();
		if(this.isPadARight()) player.moveRight();
		
//		if(quitBtn.isPressed()) Gdx.app.exit();
		
		if(this.isPadBLeft())  player.turnLeft();
		if(this.isPadBRight()) player.turnRight();
		if(this.isPadBUp())  player.lookUp();
		if(this.isPadBDown()) player.lookDown();
	}
}
