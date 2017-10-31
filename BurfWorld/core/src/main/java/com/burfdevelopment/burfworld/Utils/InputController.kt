package com.burfdevelopment.burfworld.Utils

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.burfdevelopment.burfworld.Screens.GameRenderScreen

/**
 * Created by burfies1 on 20/10/2017.
 */
class InputController : FirstPersonCameraController {

    private var gameRenderScreen: GameRenderScreen = GameRenderScreen()
    private val forwardButton = TextButton("Forward", Gui.skin)
    private val backwardButton = TextButton("Backward", Gui.skin)
    private val leftButton = TextButton("Left", Gui.skin)
    private val rightButton = TextButton("Right", Gui.skin)
    private val jumpButton = TextButton("Jump", Gui.skin)
    private val modeButton = TextButton("Mode", Gui.skin)
    var myCamera: Camera

    var tmp2 = Vector3();
    private var degreesPerPixel2 = 0.5f

    // stop to much dragging
    private val touchPos = Vector2()
    private val dragPos = Vector2()
    private val radius = 20f

    @JvmField
    var isAdding = true

    constructor(camera: Camera, gameRenderScreen: GameRenderScreen, stage: Stage) : super(camera) {
        this.gameRenderScreen = gameRenderScreen
        myCamera = camera

        // create control
        if (Gdx.app.type == Application.ApplicationType.Android || Gdx.app.type == Application.ApplicationType.iOS) {

            var width: Float = width() / 10f
            var height: Float = height() / 10f

            forwardButton.setBounds(50f + width, 100f + (height * 2), width, height)
            stage.addActor(forwardButton)

            leftButton.setBounds(50f, 50f + height, width, height)
            stage.addActor(leftButton)

            rightButton.setBounds(50f + (width * 2), 50f + height, width, height)
            stage.addActor(rightButton)

            backwardButton.setBounds(50f + width, 10f, width, height)
            stage.addActor(backwardButton)

            jumpButton.setBounds(Gdx.graphics.width - width - 50f, 50f + height, width, height)
            jumpButton.addListener(object : InputListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    if (gameRenderScreen.isJump == false && gameRenderScreen.jumping == 0.0f) {
                        gameRenderScreen.isJump = true
                    }
                    return super.touchDown(event, x, y, pointer, button)
                }
            })
            stage.addActor(jumpButton)

            modeButton.setBounds(Gdx.graphics.width - width - 50f, 100f + (height * 2), width, height)
            modeButton.addListener(object : InputListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    isAdding = !isAdding
                    return super.touchDown(event, x, y, pointer, button)
                }
            })
            stage.addActor(modeButton)
        }

        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(stage)
        multiplexer.addProcessor(this)
        Gdx.input.inputProcessor = multiplexer

    }

    override fun keyDown(keycode: Int): Boolean {

        if (keycode == Input.Keys.SPACE && gameRenderScreen.isJump == false && gameRenderScreen.jumping == 0.0f) {
            gameRenderScreen.isJump = true
        }

        if (keycode == Input.Keys.ENTER || modeButton.isPressed) {
            isAdding = !isAdding
        }

        if (keycode == Input.Keys.Q || keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit()
        }

        return super.keyDown(keycode)
    }

    // Called from Render loop
    fun updateControls() {

        if (Gdx.app.type == Application.ApplicationType.Android || Gdx.app.type == Application.ApplicationType.iOS) {

            if (forwardButton.isPressed) {
                keyDown(Input.Keys.W)
            } else {
                keyUp(Input.Keys.W)
            }

            if (backwardButton.isPressed) {
                keyDown(Input.Keys.S)
            } else {
                keyUp(Input.Keys.S)
            }

            if (leftButton.isPressed) {
                keyDown(Input.Keys.A)
            } else {
                keyUp(Input.Keys.A)
            }

            if (rightButton.isPressed) {
                keyDown(Input.Keys.D)
            } else {
                keyUp(Input.Keys.D)
            }
        }
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {

        dragPos.set(screenX.toFloat(), height() - screenY.toFloat())
        val distance = touchPos.dst(dragPos)

        if (distance <= radius) {
        } else {
            hasMoved = true

            var cutoff = width() * 0.75
            if (screenX > cutoff && pointer == 1 && Gdx.app.type == Application.ApplicationType.Android || Gdx.app.type == Application.ApplicationType.iOS) {

                var deltaX = -Gdx.input.getDeltaX(1) * degreesPerPixel2
                var deltaY = -Gdx.input.getDeltaY(1) * degreesPerPixel2

                myCamera.direction.rotate(myCamera.up, deltaX)
                tmp2.set(myCamera.direction).crs(myCamera.up).nor()
                myCamera.direction.rotate(tmp2, deltaY)

                return true
            }
        }

        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        hasMoved = false
        touchPos.set(screenX.toFloat(), height() - screenY.toFloat());

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (hasMoved == false) {
            gameRenderScreen.getObject(screenX, screenY)
        }

        return super.touchUp(screenX, screenY, pointer, button)
    }

    companion object {

        fun width(): Int {
            return Gdx.graphics.width
        }

        fun height(): Int {
            return Gdx.graphics.height
        }

        var hasMoved: Boolean = false
    }
}