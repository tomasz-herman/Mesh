package com.hermant.graphics.cameras;

import com.hermant.graphics.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ThirdPersonCamera implements Camera {
    private GameObject thirdPerson;
    private float distance;
    private Vector3f up = new Vector3f(0, 1, 0);

    public ThirdPersonCamera(GameObject thirdPerson, float distance){
        this.distance = distance;
        this.thirdPerson = thirdPerson;
    }

    @Override
    public Matrix4f setupViewMatrix(Matrix4f viewMatrix) {
        Vector3f cameraPosition = new Vector3f(thirdPerson.getPosition());
        cameraPosition.sub(
                (float)Math.cos(Math.toRadians(thirdPerson.getRotation().x)) * (float)Math.cos(Math.toRadians(thirdPerson.getRotation().y)) * distance,
                (float)Math.sin(Math.toRadians(thirdPerson.getRotation().x)) * distance - distance,
                (float)Math.cos(Math.toRadians(thirdPerson.getRotation().x)) * (float)Math.sin(Math.toRadians(thirdPerson.getRotation().y)) * distance
        );
        viewMatrix.identity();
        viewMatrix.lookAt(cameraPosition, thirdPerson.getPosition(), up);
        return viewMatrix;
    }

    @Override
    public void rotate(float dx, float dy, float dz) {

    }

    @Override
    public void move(float dx, float dy, float dz) {

    }
}
