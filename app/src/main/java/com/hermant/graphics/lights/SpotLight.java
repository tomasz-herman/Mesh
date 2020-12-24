package com.hermant.graphics.lights;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class SpotLight extends Light {
    private Vector4f direction;
    private Vector4f directionEyeSpace;
    private Vector4f position;
    private Vector4f positionEyeSpace;
    private float cutOff, outerCutOff, epsilon;

    public SpotLight(){
        super();
        this.direction = new Vector4f(0, -1, 0, 0.0f).normalize();
        this.directionEyeSpace = new Vector4f();
        this.position = new Vector4f(0, 0, 0, 1.0f);
        this.positionEyeSpace = new Vector4f();
        this.cutOff = this.outerCutOff = 0;
        this.epsilon = 0;
    }

    public SpotLight(float xPos, float yPos, float zPos, float xDir, float yDir, float zDir, float cutOff, float outerCutOff){
        super();
        this.direction = new Vector4f(xDir, yDir, zDir, 0.0f).normalize();
        this.directionEyeSpace = new Vector4f();
        this.position = new Vector4f(xPos, yPos, zPos, 1.0f);
        this.positionEyeSpace = new Vector4f();
        this.outerCutOff = outerCutOff;
        this.cutOff = cutOff;
        this.epsilon = cutOff - outerCutOff;
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

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
        this.epsilon = cutOff - outerCutOff;
    }

    public float getOuterCutOff() {
        return outerCutOff;
    }

    public void setOuterCutOff(float outerCutOff) {
        this.outerCutOff = outerCutOff;
        this.epsilon = cutOff - outerCutOff;
    }

    public float getEpsilon() {
        return epsilon;
    }
}
