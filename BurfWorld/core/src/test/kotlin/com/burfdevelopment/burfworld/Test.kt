package com.burfdevelopment.burfworld

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.mockito.Mockito.verify


// TODO https://codingdoodles.com/setting-up-kotlin-junit-mockito-gitlab-ci-for-libgdx-projects/

/**
 * Created by burfies1 on 30/10/2017.
 */
val shapeRenderer : ShapeRenderer = mock()

@Test
fun onDrawDebug_createRect() {
    val SIZE = 10.0f
    drawDebug(shapeRenderer)
    verify(shapeRenderer).rect(1.0f, 1.0f, SIZE, SIZE)
}