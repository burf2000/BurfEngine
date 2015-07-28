package com.burfdevelopment.burfworld.Utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.burfdevelopment.burfworld.Screens.GameRenderScreen;

/**
 * Created by burfies1 on 27/07/15.
 */
public class ControlsController extends FirstPersonCameraController {

    private GameRenderScreen gameRenderScreen;

    public ControlsController(Camera camera) {
        super(camera);
    }

    public ControlsController(Camera camera, GameRenderScreen gameRenderScreen) {
        super(camera);
        this.gameRenderScreen = gameRenderScreen;

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        gameRenderScreen.getObject(screenX, screenY);

        return super.touchUp(screenX, screenY, pointer, button);
    }
}
