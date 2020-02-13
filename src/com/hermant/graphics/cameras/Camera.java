package com.hermant.graphics.cameras;

import org.joml.Matrix4f;

public interface Camera {
    Matrix4f setupViewMatrix(Matrix4f viewMatrix);
    void rotate(float dx, float dy, float dz);
    void move(float dx, float dy, float dz);
}
