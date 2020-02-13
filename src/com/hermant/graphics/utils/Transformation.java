package com.hermant.graphics.utils;

import com.hermant.graphics.cameras.Camera;
import com.hermant.graphics.scene.GameObject;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f modelMatrix;

    private final Matrix3f normalMatrix;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f modelViewProjectionMatrix;


    public Transformation() {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        normalMatrix  = new Matrix3f();
        modelViewMatrix = new Matrix4f();
        modelViewProjectionMatrix = new Matrix4f();
    }


    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix(Camera camera) {
        return camera.setupViewMatrix(viewMatrix);
    }

    public Matrix4f getModelMatrix(GameObject gameObject) {
        Vector3f rotation = gameObject.getRotation();
        modelMatrix.identity().translate(gameObject.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameObject.getScale());

        return modelMatrix;
    }

    public Matrix3f getNormalMatrix(Matrix4f modelViewMatrix){
        normalMatrix.set(modelViewMatrix);
        normalMatrix.invert();
        normalMatrix.transpose();
        return normalMatrix;
    }

    public Matrix4f getModelViewMatrix(GameObject gameObject, Matrix4f viewMatrix) {
        Vector3f rotation = gameObject.getRotation();
        modelViewMatrix.identity().translate(gameObject.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameObject.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }

    public Matrix4f getModelViewProjectionMatrix(Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        modelViewProjectionMatrix.set(modelViewMatrix);
        Matrix4f projCurr = new Matrix4f(projectionMatrix);
        return projCurr.mul(modelViewProjectionMatrix);
    }

}
