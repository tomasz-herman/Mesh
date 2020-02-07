package com.hermant.graphics.lights;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class DirectionalLight extends Light{
    private Vector4f direction;
    private Vector4f directionEyeSpace;

    public DirectionalLight(){
        super();
        this.direction = new Vector4f(0, 1, 0, 0.0f).normalize();
        this.directionEyeSpace = new Vector4f();
    }

    public DirectionalLight(float x, float y, float z){
        super();
        this.direction = new Vector4f(x, y, z, 0.0f).normalize();
        this.directionEyeSpace = new Vector4f();
    }

    public Vector4f getDirection() {
        return direction;
    }

    public void calculateDirectionEyeSpace(Matrix4f viewMatrix){
        direction.mul(viewMatrix, directionEyeSpace);
    }

    public Vector4f getDirectionEyeSpace(){
        return directionEyeSpace;
    }

    public void setDirection(float x, float y, float z) {
        this.direction = new Vector4f(x, y, z, direction.w);
    }
}
