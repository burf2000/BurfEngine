package com.burfdevelopment.burfworld.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.burfdevelopment.burfworld.Activity.GameActivity;
import com.burfdevelopment.burfworld.Utils.Gui;

/**
 * Created by burfies1 on 25/07/15.
 */
public class MainMenuScreen  implements Screen {

    private Stage stage = new Stage();
    private Table table = new Table();

    private TextButton buttonPlay = new TextButton("Play", Gui.skin);
    private TextButton buttonExit = new TextButton("Exit", Gui.skin);
    private TextArea username = new TextArea("", Gui.skin);
    private Label title = new Label("Burf World",Gui.skin);

    public static int width() { return Gdx.graphics.getWidth(); }
    public static int height() { return Gdx.graphics.getHeight(); }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        buttonPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Same way we moved here from the Splash Screen
                //We set it to new Splash because we got no other screens
                //otherwise you put the screen there where you want to go
                ((GameActivity)Gdx.app.getApplicationListener()).setScreen(new GameRenderScreen());
            }
        });
        buttonExit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                // or System.exit(0);
            }
        });

        //The elements are displayed in the order you add them.
        //The first appear on top, the last at the bottom.
        table.add(title).padBottom(40).row();
        table.add(username).size(width() / 6,height() / 10).padBottom(20).row();
        table.add(buttonPlay).size(width() / 6,height() / 10).padBottom(10).row();
        table.add(buttonExit).size(width() / 6,height() / 10).padBottom(10).row();

        table.setFillParent(true);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
