package com.burfdevelopment.burfworld.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.burfdevelopment.burfworld.Activity.GameActivity;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(800, 640);
        }

        GameActivity gameActivity = new GameActivity();

        @Override
        public ApplicationListener getApplicationListener () {
                return gameActivity;
        }
}
