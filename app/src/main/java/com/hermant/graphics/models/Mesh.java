package com.hermant.graphics.models;

import org.joml.AABBf;
import org.joml.Vector3f;

import java.util.List;

public class Mesh {

    private final List<Triangle> triangles;
    private final List<Vertex> vertices;
    private Material material;
    private AABBf aabb;

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public Mesh(List<Triangle> triangles, List<Vertex> vertices) {
        this.triangles = triangles;
        this.vertices = vertices;
        calculateAABB();
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    private void calculateAABB(){
        float minX, minY, minZ;
        float maxX, maxY, maxZ;
        minX = minY = minZ = Float.MAX_VALUE;
        maxX = maxY = maxZ = Float.MIN_VALUE;
        for (Vertex vertex : vertices) {
            Vector3f pos = vertex.position;
            if(pos.x < minX) minX = pos.x;
            if(pos.y < minY) minY = pos.y;
            if(pos.z < minZ) minZ = pos.z;
            if(pos.x > maxX) maxX = pos.x;
            if(pos.y > maxY) maxY = pos.y;
            if(pos.z > maxZ) maxZ = pos.z;
        }
        aabb = new AABBf(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public AABBf getAABB() {
        return aabb;
    }
}
