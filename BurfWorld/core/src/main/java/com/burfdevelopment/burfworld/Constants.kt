package com.burfdevelopment.burfworld

/**
 * Created by burfies1 on 20/10/2017.
 */
object Constants {

    // todo comment what these do
    @JvmField val chunkSize = 10 // limited to 13 as weird issue?
    @JvmField var renderCount: Int = 0 // how many cubes on screen

    @JvmField val rayDistance = 40

    @JvmField val cubeCollisonSize = 0.8f // ??
    @JvmField val cubeSize = 1.0f // size of cube

    @JvmField val jumpRate = 5f
    @JvmField val maxJump = 1.5f
    @JvmField val headHeight = 2.0f

    // inital height
    @JvmField val startingHeight = 15.0f

    // how many chunks to make chunkArea * chunkArea
    @JvmField val chunkArea = 3

    enum class BrickState  constructor(var value: Int) {
        DELETED(-1), HIDDEN(0), SHOW(1)
    }

    // todo why is this not used
    enum class TextureName {
        SMALL_GREY_BRICKS,
        LARGE_GREY_BRICKS,
        SAMLL_RED_BRICKS,
        GRASS_LOW,
        SNOW,
        SAND,
        BARK,
        LOGS_HORZ,
        WATER,
        TINY_GREY_BRICKS,
        RED_BRICKS,
        LIGHT_GREY_STONE_PATH_1,
        GREY_STONE_PATH,
        GREY_STONE,
        GREY_STONE_2,
        WHITE_STONE,
        DARK_GREY_STONE_PATH,
        GREY_METAL_WALL_1,
        GREY_METAL_WALL_2,
        LIGHT_GREY_STONE_PATH_2,
        VERY_LIGHT_GREY_STONE,
        GREY_STONE_3,
        LIGHT_GREY_STONE_LOGS_HORZ_1,
        GREY_STONE_LOGS_HORZ,
        GREY_STONE_PATH_2,
        GREY_STONE_PATH_3,
        GREY_METAL_MESH_1,
        GREY_METAL_MESH_2,
        DARK_GREY_STONE,
        GREY_METAL_FLOOR_1,
        GREY_METAL_FLOOR_2,
        GREY_METAL_FLOOR_3,
        GREY_METAL_FLOOR_4,
        BROWN_METAL_FLOOR,
        GREY_METAL,
        LIGHT_GREY_STONE_LOGS_HORZ_2,
        BRICK,
        GRASS_NORMAL
    }
}