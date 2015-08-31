package com.burfdevelopment.burfworld;

/**
 * Created by burfies1 on 07/08/15.
 */
public class Constants {

    public static final int chunkSize = 16;
    public static int renderCount;
    public static int cubeCount;

    public static int rayDistance = 40;

    public static float cubeCollisonSize = 0.8f;
    public static float cubeSize = 1.0f; //1

    public static float jumpRate = 4;
    public static float maxJump = 1.5f;
    public static float headHeight = 2.0f;



    public enum BrickState {
        DELETED(-1) , HIDDEN(0) , SHOW(1);

        public int value;

        private BrickState(int value) {
            this.value = value;
        }
    }

    public enum TextureName {
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
