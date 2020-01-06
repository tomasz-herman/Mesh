package com.hermant.graphics;

import org.joml.*;

public class Vertex {
    public Vector3f position;
    public Vector3f normal;
    public Vector3f tangent;
    public Vector3f binormal;
    public Vector2f texture;
    public Vector2i screen;

    public void transform(Matrix4f MVP, Viewport viewport){
        Vector4f transformed = MVP.transform(position.x, position.y, position.z, 1.0f, new Vector4f());
        screen = new Vector2i((int)(((transformed.x / transformed.w + 1) / 2) * viewport.left), (int)((transformed.y / transformed.w + 1) / 2 * viewport.bottom));
    }
}
