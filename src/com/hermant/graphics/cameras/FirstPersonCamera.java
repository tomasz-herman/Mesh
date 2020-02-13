package com.hermant.graphics.cameras;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FirstPersonCamera implements Camera {

    public FirstPersonCamera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    private Vector3f position;
    private Vector3f rotation;

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    @Override
    public void move(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x -= (float)Math.sin(Math.toRadians(rotation.y)) * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x -= (float)Math.sin(Math.toRadians(rotation.y - 90.0f)) * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90.0f)) * offsetX;
        }
        position.y += offsetY;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    @Override
    public void rotate(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }

    @Override
    public Matrix4f setupViewMatrix(Matrix4f viewMatrix) {
        viewMatrix.identity();
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
                .rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
        viewMatrix.translate(-position.x, -position.y, -position.z);
        return viewMatrix;
    }
}
