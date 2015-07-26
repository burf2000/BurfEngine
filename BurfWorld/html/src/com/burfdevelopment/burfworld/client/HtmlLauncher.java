package com.burfdevelopment.burfworld.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.burfdevelopment.burfworld.GameActivity;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(480, 320);
        }

        GameActivity gameActivity = new GameActivity();

        @Override
        public ApplicationListener getApplicationListener () {
                return gameActivity;
        }
}
