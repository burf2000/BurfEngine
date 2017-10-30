package com.burfdevelopment.burfworld.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.burfdevelopment.burfworld.Activity.GameActivity
import com.burfdevelopment.burfworld.Utils.Gui

/**
 * Created by burfies1 on 20/10/2017.
 */
class MainMenuScreen : Screen {

    private val stage = Stage()
    private val table = Table()

    private val buttonPlay = TextButton("Play", Gui.skin)
    private val buttonExit = TextButton("Exit", Gui.skin)
    private val username = TextArea("", Gui.skin)
    private val title = Label("Burf World", Gui.skin)

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {}

    override fun show() {
        buttonPlay.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                //Same way we moved here from the Splash Screen
                //We set it to new Splash because we got no other screens
                //otherwise you put the screen there where you want to go
                (Gdx.app.applicationListener as GameActivity).screen = GameRenderScreen()
            }
        })
        buttonExit.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
                // or System.exit(0);
            }
        })

        //The elements are displayed in the order you add them.
        //The first appear on top, the last at the bottom.
        table.add(title).padBottom(40f).row()
        table.add(username).size((width() / 6).toFloat(), (height() / 10).toFloat()).padBottom(20f).row()
        table.add(buttonPlay).size((width() / 6).toFloat(), (height() / 10).toFloat()).padBottom(10f).row()
        table.add(buttonExit).size((width() / 6).toFloat(), (height() / 10).toFloat()).padBottom(10f).row()

        table.setFillParent(true)
        stage.addActor(table)

        Gdx.input.inputProcessor = stage
    }

    override fun hide() {
        dispose()
    }

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {
        stage.dispose()
    }

    companion object {

        fun width(): Int {
            return Gdx.graphics.width
        }

        fun height(): Int {
            return Gdx.graphics.height
        }
    }
}