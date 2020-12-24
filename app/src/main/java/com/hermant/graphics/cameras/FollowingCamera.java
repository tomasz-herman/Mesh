package com.hermant.graphics.cameras;

import com.hermant.graphics.scene.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FollowingCamera implements Camera {

    private GameObject following;
    private Vector3f position;
    private Vector3f up = new Vector3f(0, 1, 0);

    public FollowingCamera(Vector3f position, GameObject following){
        this.position = position;
        this.following = following;
    }

    @Override
    public Matrix4f setupViewMatrix(Matrix4f viewMatrix) {
        viewMatrix.identity();
        viewMatrix.lookAt(position, following.getPosition(), up);
        return viewMatrix;
    }

    @Override
    public void rotate(float dx, float dy, float dz) {

    }

    @Override
    public void move(float dx, float dy, float dz) {

    }
}
