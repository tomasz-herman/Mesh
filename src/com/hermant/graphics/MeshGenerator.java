package com.hermant.graphics;

import com.hermant.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeshGenerator {

    public static final int GRID_MARGIN = 25;

    public static Mesh grid(int w, int h){
        Vertex[][] vertices = new Vertex[w + 1][h + 1];
        for (int i = 0; i < w + 1; i++) {
            for (int j = 0; j < h + 1; j++) {
                float x = (float) i / w;
                float y = (float) j / h;
                Vertex v = new Vertex();
                v.position = new Vector3f(x * 2 - 1, 0, y * 2 - 1);
                v.normal = new Vector3f(0, 1, 0);
                v.texture = new Vector2f(x, y);
                v.binormal = new Vector3f(1, 0, 0);
                v.tangent = new Vector3f(0, 0, 1);
                v.screen = new Vector2i();
                vertices[i][j] = v;
            }
        }
        List<Triangle> triangles = new ArrayList<>();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                triangles.add(new Triangle(vertices[i][j], vertices[i + 1][j], vertices[i + 1][j + 1]));
                triangles.add(new Triangle(vertices[i][j], vertices[i][j + 1], vertices[i + 1][j + 1]));
            }
        }
        List<Vertex> vertexList = new ArrayList<>();
        for (Vertex[] vertex : vertices) {
            vertexList.addAll(Arrays.asList(vertex));
        }
        return new Mesh(null, null, triangles, vertexList);
    }

    public static Mesh grid(int w, int h, Texture texture, Texture normals){
        Vertex[][] vertices = new Vertex[w + 1][h + 1];
        for (int i = 0; i < w + 1; i++) {
            for (int j = 0; j < h + 1; j++) {
                float x = (float) i / w * (Main.DEFAULT_WIDTH - GRID_MARGIN * 2) + GRID_MARGIN;
                float y = (float) j / h * (Main.DEFAULT_HEIGHT - GRID_MARGIN * 2) + GRID_MARGIN;
                Vertex v = new Vertex();
                v.position = new Vector3f(x, 0, y);
                v.normal = new Vector3f(0, 1, 0);
                v.texture = new Vector2f(x, y);
                v.binormal = new Vector3f(1, 0, 0);
                v.tangent = new Vector3f(0, 0, 1);
                v.screen = new Vector2i((int)x, (int)y);
                vertices[i][j] = v;
            }
        }
        List<Triangle> triangles = new ArrayList<>();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                triangles.add(new Triangle(vertices[i][j], vertices[i + 1][j], vertices[i + 1][j + 1]));
                triangles.add(new Triangle(vertices[i][j], vertices[i][j + 1], vertices[i + 1][j + 1]));
            }
        }
        List<Vertex> vertexList = new ArrayList<>();
        for (Vertex[] vertex : vertices) {
            vertexList.addAll(Arrays.asList(vertex));
        }
        return new Mesh(texture, normals, triangles, vertexList);
    }


    public static Mesh cube(){
        Vertex[][][] vertices = new Vertex[2][2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    int x = i == 0 ? -1 : 1;
                    int y = j == 0 ? -1 : 1;
                    int z = k == 0 ? -1 : 1;
                    Vertex v = new Vertex();
                    v.position = new Vector3f(x, y, z);
                    v.normal = new Vector3f(0, 0, 0);
                    v.texture = new Vector2f(0, 0);
                    v.binormal = new Vector3f(0, 0, 0);
                    v.tangent = new Vector3f(0, 0, 0);
                    v.screen = new Vector2i(0, 0);
                    vertices[i][j][k] = v;
                }
            }
        }
        List<Triangle> triangles = new ArrayList<>();
        triangles.add(new Triangle(vertices[0][0][0], vertices[0][1][0], vertices[0][0][1]));
        triangles.add(new Triangle(vertices[0][1][1], vertices[0][1][0], vertices[0][0][1]));
        triangles.add(new Triangle(vertices[1][0][0], vertices[1][1][0], vertices[1][0][1]));
        triangles.add(new Triangle(vertices[1][1][1], vertices[1][1][0], vertices[1][0][1]));

        triangles.add(new Triangle(vertices[0][0][0], vertices[0][0][1], vertices[1][0][0]));
        triangles.add(new Triangle(vertices[1][0][1], vertices[0][0][1], vertices[1][0][0]));
        triangles.add(new Triangle(vertices[0][1][0], vertices[0][1][1], vertices[1][1][0]));
        triangles.add(new Triangle(vertices[1][1][1], vertices[0][1][1], vertices[1][1][0]));

        triangles.add(new Triangle(vertices[0][0][0], vertices[0][1][0], vertices[1][0][0]));
        triangles.add(new Triangle(vertices[1][1][0], vertices[0][1][0], vertices[1][0][0]));
        triangles.add(new Triangle(vertices[0][0][1], vertices[0][1][1], vertices[1][0][1]));
        triangles.add(new Triangle(vertices[1][1][1], vertices[0][1][1], vertices[1][0][1]));

        List<Vertex> vertexList = new ArrayList<>();
        for (Vertex[][] vertex : vertices) {
            for (Vertex[] ver : vertex) {
                vertexList.addAll(Arrays.asList(ver));
            }
        }
        return new Mesh(null, null, triangles, vertexList);
    }
}
