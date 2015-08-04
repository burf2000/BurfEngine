package com.burfdevelopment.burfworld.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by burfies1 on 01/08/15.
 */
public class Gui {

    private static Skin skin = new Skin(Gdx.files.internal("skins/uiskin.json"));

    public static Skin getSkin() {
        return skin;
    }

    public static void dispose()
    {
        skin.dispose();
    }


}
