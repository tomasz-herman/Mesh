package com.hermant.graphics.lights;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class PointLight extends Light {
    private Vector4f position;
    private Vector4f positionEyeSpace;

    public PointLight(){
        super();
        this.position = new Vector4f(0, 0, 0, 1.0f);
        this.positionEyeSpace = new Vector4f();
    }

    public PointLight(float x, float y, float z){
        super();
        this.position = new Vector4f(x, y, z, 1.0f);
        this.positionEyeSpace = new Vector4f();
    }

    public Vector4f getPosition() {
        return position;
    }

    public void calculatePositionEyeSpace(Matrix4f viewMatrix){
        position.mul(viewMatrix, positionEyeSpace);
    }

    public Vector4f getPositionEyeSpace(){
        return positionEyeSpace;
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector4f(x, y, z, position.w);
    }
}
