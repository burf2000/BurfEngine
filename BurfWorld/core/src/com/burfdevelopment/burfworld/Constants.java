package com.burfdevelopment.burfworld;

/**
 * Created by burfies1 on 07/08/15.
 */
public class Constants {

    public static final int chunkSize = 16;
    public static int renderCount;
    public static int cubeCount;

    public static int rayDistance = 80;

    public static float cubeCollisonSize = 0.6f;
    public static float cubeSize = 1.0f; //1

    public static float maxJump = 2;

    public enum BrickState {
        DELETED(-1) , HIDDEN(0) , SHOW(1);

        public int value;

        private BrickState(int value) {
            this.value = value;
        }
    }
}
