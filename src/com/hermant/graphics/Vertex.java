package com.hermant.graphics;

import org.joml.*;

public class Vertex {
    public Vector3f position;
    public Vector3f normal;
    public Vector3f tangent;
    public Vector3f binormal;
    public Vector2f texture;
    public Vector2i screen;
    public Vector4f transformed;
    public Vector3f transformedNormal;

    public void transform(Matrix4f MVP, Matrix3f normalMatrix, int right, int bottom){
        transformedNormal = normalMatrix.transform(normal.x, normal.y, normal.z, new Vector3f());
        transformed = MVP.transform(position.x, position.y, position.z, 1.0f, new Vector4f());
        transformed.set((((transformed.x / transformed.w + 1) / 2) * right), (((-transformed.y / transformed.w + 1) / 2) * bottom), transformed.z / transformed.w, 1.0f / transformed.w);
        screen = new Vector2i((int)transformed.x, (int)transformed.y);
    }
}
