package com.hermant.graphics;

import java.util.List;

public class Mesh {

    private final List<Triangle> triangles;
    private final List<Vertex> vertices;
    private Material material;

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public Mesh(List<Triangle> triangles, List<Vertex> vertices) {
        this.triangles = triangles;
        this.vertices = vertices;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
