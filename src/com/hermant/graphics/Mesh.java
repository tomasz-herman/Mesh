package com.hermant.graphics;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class Mesh {

    private Texture texture;
    private Texture normals;
    private final List<Triangle> triangles;
    private final List<Vertex> vertices;
    public float ambient;
    public float diffuse;
    public float specular;
    public int specularExponent;

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public void transform(Matrix4f mvp, Viewport viewport){
        for (Vertex vertex : vertices) {
            Vector3f vec = new Vector3f(vertex.position).mulPosition(mvp);
            vertex.screen = new Vector2i((int) ((vec.x + 1) / 2 * viewport.left),
                    (int) ((vec.y + 1) / 2 * viewport.bottom));
        }
    }

    public Mesh(Texture texture, Texture normals, List<Triangle> triangles, List<Vertex> vertices) {
        this.texture = texture;
        this.normals = normals;
        this.triangles = triangles;
        this.vertices = vertices;
    }

    public Texture getTexture() {
        return texture;
    }

    public Texture getNormals() {
        return normals;
    }

    public void setAmbient(float ambient) {
        this.ambient = ambient;
        for (Triangle triangle : triangles) triangle.ambient = ambient;
    }

    public void setDiffuse(float diffuse) {
        this.diffuse = diffuse;
        for (Triangle triangle : triangles) triangle.diffuse = diffuse;
    }

    public void setSpecular(float specular) {
        this.specular = specular;
        for (Triangle triangle : triangles) triangle.specular = specular;
    }

    public void setSpecularExponent(int specularExponent) {
        this.specularExponent = specularExponent;
        for (Triangle triangle : triangles) triangle.specularExponent = specularExponent;

    }

    public void randomize() {
        Random random = new Random();
        for (Triangle triangle : triangles) {
            triangle.ambient = random.nextFloat() / 4;
            triangle.diffuse = random.nextFloat();
            triangle.specular = random.nextFloat();
            triangle.specularExponent = random.nextInt(100 + 1);
        }
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setNormals(Texture normals) {
        this.normals = normals;
    }
}
