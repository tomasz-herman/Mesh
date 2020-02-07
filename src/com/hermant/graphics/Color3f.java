package com.hermant.graphics;

import org.joml.Vector3f;

public class Color3f {
    public float red, green, blue;

    public Color3f() {
        this.red = 0f;
        this.green = 0f;
        this.blue = 0f;
    }

    public Color3f(float grey) {
        this.red = clamp(grey);
        this.green = clamp(grey);
        this.blue = clamp(grey);
    }

    public Color3f(float red, float green, float blue){
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
    }

    public Color3f(int red, int green, int blue){
        this.red = clamp(red / 255.0f);
        this.green = clamp(green / 255.0f);
        this.blue = clamp(blue / 255.0f);
    }

    public Color3f(int rgb){
        this.red = clamp(((rgb >> 16) & 0xff) / 255.0f);
        this.green = clamp(((rgb >> 8) & 0xff) / 255.0f);
        this.blue = clamp((rgb & 0xff) / 255.0f);
    }

    public Color3f(Color3f other) {
        this.red = other.red;
        this.green = other.green;
        this.blue = other.blue;
    }

    private static float clamp(float x){
        return Math.max( Math.min(x, 1.0f), 0.0f);
    }

    public Color3f clamp(){
        red = clamp(red); green = clamp(green); blue = clamp(blue);
        return this;
    }

    public Color3f mul(float b){
        red *= b; green *= b; blue *= b;
        return this;
    }


    public Color3f div(float b){
        red /= b; green /= b; blue /= b;
        return this;
    }

    public Color3f add(Color3f b){
        red += b.red; green += b.green; blue += b.blue;
        return this;
    }

    public Color3f add(float b){
        red += b; green += b; blue += b;
        return this;
    }

    public Color3f sub(Color3f b){
        red -= b.red; green -= b.green; blue -= b.blue;
        return this;
    }

    public Color3f mul(Color3f b){
        red *= b.red; green *= b.green; blue *= b.blue;
        return this;
    }

    public Color3f div(Color3f b){
        red /= b.red; green /= b.green; blue /= b.blue;
        return this;
    }

    public int getRGB(){
        return ((int)(red * 255.0f + 0.5f) << 16) | ((int)(green * 255.0f + 0.5f) << 8) | (int)(blue * 255.0f + 0.5f);
    }

    public static Color3f mul(Color3f a, Color3f b){
        return new Color3f(a.red * b.red, a.green * b.green, a.blue * b.blue);
    }

    public static Color3f add(Color3f a, Color3f b){
        return new Color3f(a.red + b.red, a.green + b.green, a.blue + b.blue);
    }

    public static Color3f sub(Color3f a, Color3f b){
        return new Color3f(a.red - b.red, a.green - b.green, a.blue - b.blue);
    }

    public static Color3f div(Color3f a, Color3f b){
        return new Color3f(a.red / b.red, a.green / b.green, a.blue / b.blue);
    }

    public static Color3f mul(Color3f a, float b){
        return new Color3f(a.red * b, a.green * b, a.blue * b);
    }

    public static Color3f div(Color3f a, float b){
        return new Color3f(a.red / b, a.green / b, a.blue / b);
    }

    public static Color3f interpolate(Color3f a, Color3f b, Color3f c, Vector3f factors){
        return new Color3f(
                a.red * factors.x + b.red * factors.y + c.red * factors.z,
                a.green * factors.x + b.green * factors.y + c.green * factors.z,
                a.blue * factors.x + b.blue * factors.y + c.blue * factors.z
        );
    }


    @Override
    public String toString() {
        return "Color3f:" + "red=" + red + ", green=" + green + ", blue=" + blue;
    }
}
