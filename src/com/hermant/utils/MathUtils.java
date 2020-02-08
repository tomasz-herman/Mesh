package com.hermant.utils;

import org.joml.Vector2i;

public class MathUtils {

    public static int min(int a, int b, int c){
        return Math.min(a, Math.min(b, c));
    }

    public static int max(int a, int b, int c){
        return Math.max(a, Math.max(b, c));
    }

    public static float pow(float a, int b){
        float result = 1.0f;
        while(b > 0){
            if((b & 1) == 1) result *= a;
            b = b >> 1;
            a *= a;
        }
        return result;
    }

    public static int orientation(Vector2i a, Vector2i b, Vector2i c) {
        return (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*((c.x-a.x));
    }

}
