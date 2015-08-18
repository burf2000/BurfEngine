package com.burfdevelopment.burfworld.Utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.burfdevelopment.burfworld.Screens.GameRenderScreen;

/**
 * Created by burfies1 on 27/07/15.
 */
public class ControlsController extends FirstPersonCameraController {

    private GameRenderScreen gameRenderScreen;
    private TextButton forwardButton;
    private TextButton backwardButton;
    private TextButton leftButton;
    private TextButton rightButton;

    public static int width() { return Gdx.graphics.getWidth(); }
    public static int height() { return Gdx.graphics.getHeight(); }

    public static boolean hasMoved;

    public ControlsController(Camera camera) {
        super(camera);
    }

    public ControlsController(Camera camera, GameRenderScreen gameRenderScreen, Stage stage) {
        super(camera);
        this.gameRenderScreen = gameRenderScreen;
        // create control
        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS)
        {
            forwardButton = new TextButton("Forward", Gui.getSkin());
			forwardButton.setBounds(width() - 140,  180, 80, 70);
            stage.addActor(forwardButton);

            leftButton = new TextButton("Left", Gui.getSkin());
            leftButton.setBounds(width() - 190,  100, 80, 70);
            stage.addActor(leftButton);

            rightButton = new TextButton("Right", Gui.getSkin());
            rightButton.setBounds(width() - 90,  100, 80, 70);
            stage.addActor(rightButton);

            backwardButton = new TextButton("Backward", Gui.getSkin());
            backwardButton.setBounds(width() - 140,  20, 80, 70);
            stage.addActor(backwardButton);
        }

        //Todo fix this as it stops touching working
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);

    }

    @Override
    public boolean keyDown (int keycode) {

        if (keycode == Input.Keys.SPACE)
        {
            gameRenderScreen.isJump = true;
        }

        return super.keyDown(keycode);
    }

    public void updateControls(){

        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS) {

            if (forwardButton.isPressed()) {
                keyDown(Input.Keys.W);
            }
            else
            {
                keyUp(Input.Keys.W);
            }

            if (backwardButton.isPressed()) {
                keyDown(Input.Keys.S);
            }
            else
            {
                keyUp(Input.Keys.S);
            }

            if (leftButton.isPressed()) {
                keyDown(Input.Keys.A);
            }
            else
            {
                keyUp(Input.Keys.A);
            }

            if (rightButton.isPressed()) {
                keyDown(Input.Keys.D);
            }
            else
            {
                keyUp(Input.Keys.D);
            }
        }

    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        hasMoved = true;
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        hasMoved = false;
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (hasMoved == false)
        {
            gameRenderScreen.getObject(screenX, screenY);
        }

        return super.touchUp(screenX, screenY, pointer, button);
    }
}
