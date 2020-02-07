package com.hermant.graphics;

import org.joml.Matrix4f;
import org.joml.Vector4f;

@Deprecated
public class Light {
    private Color3f color;
    private Vector4f position;
    private Vector4f positionEyeSpace;

    public Light(float x, float y, float z, boolean pointLight){
        if(pointLight) {
            this.position = new Vector4f(x, y, z, 1.0f);
        }else{
            this.position = new Vector4f(x, y, z, 0.0f);
        }
        this.positionEyeSpace = new Vector4f();
        this.color = new Color3f(1.0f, 1.0f, 1.0f);
    }

    public void setPointLight(boolean pointLight){
        if(pointLight)position.w = 1.0f;
        else position.w = 0.0f;
    }

    public Vector4f getPosition() {
        return position;
    }

    public void calculatePositionEyeSpace(Matrix4f modelViewMatrix){
        position.mul(modelViewMatrix, positionEyeSpace);
    }

    public Vector4f getPositionEyeSpace(){
        return positionEyeSpace;
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector4f(x, y, z, position.w);
    }

    public Color3f getColor() {
        return color;
    }

    public void setColor(float r, float g, float b) {
        this.color = new Color3f(r, g, b);
    }
}
