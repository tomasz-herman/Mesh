package com.hermant.graphics.models;

import org.joml.*;

public class Vertex {
    public Vector3f position;
    public Vector3f normal;
    public Vector3f tangent;
    public Vector3f binormal;
    public Vector2f texture;
    public Vector2i screen;
    public Vector4f transformedPosition = new Vector4f();
    public Vector4f transformedPositionEyeSpace = new Vector4f();
    public Vector3f transformedNormal = new Vector3f();

    public void transform(Matrix4f MVP, Matrix4f modelViewMatrix, Matrix3f normalMatrix, int right, int bottom){
        normalMatrix.transform(normal.x, normal.y, normal.z, transformedNormal);
        transformedNormal.normalize();
        MVP.transform(position.x, position.y, position.z, 1.0f, transformedPosition);
        modelViewMatrix.transform(position.x, position.y, position.z, 1.0f, transformedPositionEyeSpace);
        transformedPosition.set((((transformedPosition.x / transformedPosition.w + 1) / 2) * right), (((-transformedPosition.y / transformedPosition.w + 1) / 2) * bottom), transformedPosition.z / transformedPosition.w, 1.0f / transformedPosition.w);
        screen = new Vector2i((int) transformedPosition.x, (int) transformedPosition.y);
    }
}
