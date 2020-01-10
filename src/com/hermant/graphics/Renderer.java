package com.hermant.graphics;


import org.joml.*;

import java.lang.Math;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 10f;
    private static final float Z_FAR = 200.f;

    private final static int THREADS = Runtime.getRuntime().availableProcessors();

    private final Transformation transformation = new Transformation();

    private Canvas canvas;

    public void setRenderFunction(RenderFunction renderFunction) {
        this.renderFunction = renderFunction;
    }

    private RenderFunction renderFunction = this::renderTriangle;

    private Viewport viewport;

    public Renderer(Canvas canvas) {
        this.canvas = canvas;
        edgeTable = new Edge[THREADS][canvas.getHeight()];
        viewport = new Viewport(canvas.getWidth(), canvas.getHeight());
    }

    public void renderScene(Scene scene) {
        canvas.clear();
        List<GameObject> objects = scene.getGameObjects();
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, canvas.getWidth(), canvas.getHeight(), Z_NEAR, Z_FAR);
        Matrix4f viewMatrix = transformation.getViewMatrix(scene.getCamera());
        for (GameObject object : objects) {
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(object, viewMatrix);
            Matrix4f MVP = transformation.getModelViewProjectionMatrix(modelViewMatrix, projectionMatrix);
            for (Mesh mesh : object.getModel().getMeshes()) {
                mesh.getVertices().parallelStream().forEach(vertex -> vertex.transform(MVP, viewport));

                for (Triangle triangle : mesh.getTriangles()) {
                    renderFunction.render(triangle, mesh.getMaterial(), scene.getLight());
                }
            }
        }
        canvas.repaint();
    }

    @FunctionalInterface
    public interface RenderFunction {
        void render(Triangle t, Material m, Light l);
    }

    public void renderTriangleWireframe(Triangle t){
        if(t.a.transformed.z > -1 && t.b.transformed.z > -1 && t.c.transformed.z > -1 & t.a.transformed.z < 1 && t.b.transformed.z < 1 && t.c.transformed.z < 1){
            drawLine(t.a.screen, t.b.screen);
            drawLine(t.c.screen, t.b.screen);
            drawLine(t.a.screen, t.c.screen);
        }
    }

    private static float orientation(Vector2i a, Vector2i b, Vector2i c) {
        return (float)(b.x-a.x)*(float)(c.y-a.y) - (float)(b.y-a.y)*(float)((c.x-a.x));
    }

    private static int min(int a, int b, int c){
        return Math.min(a, Math.min(b, c));
    }

    private static int max(int a, int b, int c){
        return Math.max(a, Math.max(b, c));
    }


    public void renderTriangle(Triangle t, Material m, Light l){
        Vector2i v0 = t.a.screen, v1 = t.b.screen, v2 = t.c.screen;
        int minX = min(v0.x, v1.x, v2.x);
        int minY = min(v0.y, v1.y, v2.y);
        int maxX = max(v0.x, v1.x, v2.x);
        int maxY = max(v0.y, v1.y, v2.y);

        if(maxY < 0) return;
        if(maxX < 0) return;
        if(minX > viewport.right) return;
        if(minY > viewport.bottom) return;

        // Clip against screen bounds
        minX = Math.max(minX, 0);
        minY = Math.max(minY, 0);
        maxX = Math.min(maxX, canvas.getWidth() - 1);
        maxY = Math.min(maxY, canvas.getHeight() - 1);

        float area = orientation(v0, v1, v2);

        // Rasterize
        Vector2i p = new Vector2i();
        for (p.y = minY; p.y <= maxY; p.y++) {
            for (p.x = minX; p.x <= maxX; p.x++) {
                // Determine barycentric coordinates
                float w0 = orientation(v1, v2, p);
                float w1 = orientation(v2, v0, p);
                float w2 = orientation(v0, v1, p);

                // If p is on or inside all edges, render pixel.
                if (w0 >= 0 & w1 >= 0 & w2 >= 0){
                    if((w0 + w1 + w2)<1.0f)continue;
                    float f0 = w0 / area;
                    float f1 = w1 / area;
                    float f2 = w2 / area;

                    Vector2f tex = new Vector2f(t.a.texture.x * f0 + t.b.texture.x * f1 + t.c.texture.x * f2,
                            t.a.texture.y * f0 + t.b.texture.y * f1 + t.c.texture.y * f2);

                    if(tex.x < 0) tex.x -= (int) tex.x - 1;
                    if(tex.y < 0) tex.y -= (int) tex.y - 1;
                    if(tex.x > 1) tex.x -= (int) tex.x;
                    if(tex.y > 1) tex.y -= (int) tex.y;



 //                   float depth = 1.0f / (f0 / t.a.transformed.z + f1 / t.b.transformed.z + f2 / t.c.transformed.z);
                    float depth = (f0 * t.a.transformed.z + f1 * t.b.transformed.z + f2 * t.c.transformed.z);
      //              System.out.println(z);
                    if(m.getDiffuseTexture() == null || depth > 1 || depth < -1)continue;
                    canvas.setPixel(p.x, p.y, m.getDiffuseTexture().getSampleNearestNeighbor(tex.x, tex.y), depth);
                }
            }
        }
    }

    public void renderTriangleTexture(Triangle t, Texture texture, Texture normals, Light light, int thread){
            if(edgeTable[thread].length != canvas.getHeight()) edgeTable[thread] = new Edge[canvas.getHeight()];
            Arrays.fill(edgeTable[thread], null);
            List<Edge> activeEdges = new LinkedList<>();
            int yMin = Math.min(t.a.screen.y, t.b.screen.y);
            if(t.a.screen.y != t.b.screen.y) {
                edgeTable[thread][yMin] = new Edge(t.a.screen, t.b.screen, edgeTable[thread][yMin]);
            }
            yMin = Math.min(t.c.screen.y, t.b.screen.y);
            if(t.c.screen.y != t.b.screen.y) {
                edgeTable[thread][yMin] = new Edge(t.b.screen, t.c.screen, edgeTable[thread][yMin]);
            }
            yMin = Math.min(t.a.screen.y, t.c.screen.y);
            if(t.a.screen.y != t.c.screen.y) {
                edgeTable[thread][yMin] = new Edge(t.c.screen, t.a.screen, edgeTable[thread][yMin]);
            }
            yMin= Math.min(t.b.screen.y, Math.min(0, yMin));
            Color3f a = shadeTexture(t.a, texture);
            Color3f b = shadeTexture(t.b, texture);
            Color3f c = shadeTexture(t.c, texture);
            int yMax = Math.max(Math.max(t.b.screen.y, canvas.getHeight()), Math.max(t.a.screen.y, t.c.screen.y));
            for (int i = yMin; i < yMax; i++) {
                Edge e = edgeTable[thread][i];
                while(e != null){
                    activeEdges.add(e);
                    e = e.next;
                }
                activeEdges.sort(Comparator.comparingDouble(edge -> edge.xMin));
                for (int j = 0; j < activeEdges.size(); j+=2) {
                    int from = (int)activeEdges.get(j).xMin;
                    int to = (int)activeEdges.get(j + 1).xMin;
                    for (int k = from; k < to; k++) {
                        Vector3f factors = Interpolationf.interpolationFactorsTriangle(
                                t.a.screen.x, t.a.screen.y,
                                t.b.screen.x, t.b.screen.y,
                                t.c.screen.x, t.c.screen.y,
                                k, i,
                                new Vector3f()
                        );
                        Vector2f tex = new Vector2f(t.a.texture.x * factors.x + t.b.texture.x * factors.y + t.c.texture.x * factors.z,
                                t.a.texture.y * factors.x + t.b.texture.y * factors.y + t.c.texture.y * factors.z);
                        float depth = t.a.transformed.z * factors.x + t.b.transformed.z * factors.y + t.c.transformed.z * factors.z ;
                        canvas.setPixel(k, i, texture.getSampleNearestNeighbor(tex.x, tex.y), depth);
                    }
                }
                int finalI = i;
                activeEdges.removeIf(edge -> edge.yMax - 1 == finalI);
                for (Edge edge : activeEdges) edge.xMin += edge.dx_dy;
            }

    }

    private static Color3f shadeTexture(Vertex vertex, Texture texture){
        return new Color3f(texture.getSampleNearestNeighbor(vertex.texture.x, vertex.texture.y));
    }

    private void drawLine(Vector2i p1, Vector2i p2){
        int d, dx, dy, ai, bi, xi, yi;
        int x = p1.x, y = p1.y, x2 = p2.x, y2 = p2.y;
        int x1 = x, y1 = y;
        // ustalenie kierunku rysowania
        if (x1 < x2)
        {
            xi = 1;
            dx = x2 - x1;
        }
        else
        {
            xi = -1;
            dx = x1 - x2;
        }
        // ustalenie kierunku rysowania
        if (y1 < y2)
        {
            yi = 1;
            dy = y2 - y1;
        }
        else
        {
            yi = -1;
            dy = y1 - y2;
        }
        // pierwszy piksel
        canvas.setPixel(x, y, 0xffffffff);
        // oś wiodąca OX
        if (dx > dy)
        {
            ai = (dy - dx) * 2;
            bi = dy * 2;
            d = bi - dx;
            // pętla po kolejnych x
            while (x != x2)
            {
                // test współczynnika
                if (d >= 0)
                {
                    x += xi;
                    y += yi;
                    d += ai;
                }
                else
                {
                    d += bi;
                    x += xi;
                }
                canvas.setPixel(x, y, 0xffffffff);
            }
        }
        // oś wiodąca OY
        else
        {
            ai = ( dx - dy ) * 2;
            bi = dx * 2;
            d = bi - dy;
            // pętla po kolejnych y
            while (y != y2)
            {
                // test współczynnika
                if (d >= 0)
                {
                    x += xi;
                    y += yi;
                    d += ai;
                }
                else
                {
                    d += bi;
                    y += yi;
                }
                canvas.setPixel(x, y, 0xffffffff);
            }
        }
    }

    public void renderTriangleFast(Triangle t, Texture texture, Texture normals, Light light, int thread){
        Arrays.fill(edgeTable[thread], null);
        List<Edge> activeEdges = new LinkedList<>();
        int yMin = Math.min(t.a.screen.y, t.b.screen.y);
        if(t.a.screen.y != t.b.screen.y) {
            edgeTable[thread][yMin] = new Edge(t.a.screen, t.b.screen, edgeTable[thread][yMin]);
        }
        yMin = Math.min(t.c.screen.y, t.b.screen.y);
        if(t.c.screen.y != t.b.screen.y) {
            edgeTable[thread][yMin] = new Edge(t.b.screen, t.c.screen, edgeTable[thread][yMin]);
        }
        yMin = Math.min(t.a.screen.y, t.c.screen.y);
        if(t.a.screen.y != t.c.screen.y) {
            edgeTable[thread][yMin] = new Edge(t.c.screen, t.a.screen, edgeTable[thread][yMin]);
        }
        yMin= Math.min(t.b.screen.y, Math.min(0, yMin));
        Color3f a = shade(t, t.a.screen.x, t.a.screen.y, texture, normals, light);
        Color3f b = shade(t, t.b.screen.x, t.b.screen.y, texture, normals, light);
        Color3f c = shade(t, t.c.screen.x, t.c.screen.y, texture, normals, light);
        int yMax = Math.max(Math.max(t.b.screen.y, canvas.getHeight()), Math.max(t.a.screen.y, t.c.screen.y));
        for (int i = yMin; i < yMax; i++) {
            Edge e = edgeTable[thread][i];
            while(e != null){
                activeEdges.add(e);
                e = e.next;
            }
            activeEdges.sort(Comparator.comparingDouble(edge -> edge.xMin));
            for (int j = 0; j < activeEdges.size(); j+=2) {
                int from = (int)activeEdges.get(j).xMin;
                int to = (int)activeEdges.get(j + 1).xMin;
                for (int k = from; k < to; k++) canvas.setPixel(k, i, shadeFast(t, k, i, a, b, c));
            }
            int finalI = i;
            activeEdges.removeIf(edge -> edge.yMax - 1 == finalI);
            for (Edge edge : activeEdges) edge.xMin += edge.dx_dy;
        }
    }

    private static Color3f shadeFast(Triangle t, int xPos, int yPos, Color3f a, Color3f b, Color3f c){
        Vector3f factors = Interpolationf.interpolationFactorsTriangle(
                t.a.screen.x, t.a.screen.y,
                t.b.screen.x, t.b.screen.y,
                t.c.screen.x, t.c.screen.y,
                xPos, yPos,
                new Vector3f()
        );
        return Color3f.interpolate(a, b, c, factors);
    }

    public void renderTriangleHybrid(Triangle t, Texture texture, Texture normals, Light light, int thread){
        Arrays.fill(edgeTable[thread], null);
        List<Edge> activeEdges = new LinkedList<>();
        int yMin = Math.min(t.a.screen.y, t.b.screen.y);
        if(t.a.screen.y != t.b.screen.y) {
            edgeTable[thread][yMin] = new Edge(t.a.screen, t.b.screen, edgeTable[thread][yMin]);
        }
        yMin = Math.min(t.c.screen.y, t.b.screen.y);
        if(t.c.screen.y != t.b.screen.y) {
            edgeTable[thread][yMin] = new Edge(t.b.screen, t.c.screen, edgeTable[thread][yMin]);
        }
        yMin = Math.min(t.a.screen.y, t.c.screen.y);
        if(t.a.screen.y != t.c.screen.y) {
            edgeTable[thread][yMin] = new Edge(t.c.screen, t.a.screen, edgeTable[thread][yMin]);
        }
        yMin= Math.min(t.b.screen.y, Math.min(0, yMin));
        int yMax = Math.max(Math.max(t.b.screen.y, canvas.getHeight()), Math.max(t.a.screen.y, t.c.screen.y));
        Color3f texA = new Color3f(texture.get(t.a.screen.x, t.a.screen.y)).mul(light.getColor());
        Color3f texB = new Color3f(texture.get(t.b.screen.x, t.b.screen.y)).mul(light.getColor());
        Color3f texC = new Color3f(texture.get(t.c.screen.x, t.c.screen.y)).mul(light.getColor());
        Color3f normal = new Color3f(normals.get(t.a.screen.x, t.a.screen.y));
        Vector3f normA = new Vector3f(normal.red * 2 - 1, normal.blue, -normal.green * 2 + 1).normalize();
        normal = new Color3f(normals.get(t.b.screen.x, t.b.screen.y));
        Vector3f normB = new Vector3f(normal.red * 2 - 1, normal.blue, -normal.green * 2 + 1).normalize();
        normal = new Color3f(normals.get(t.c.screen.x, t.c.screen.y));
        Vector3f normC = new Vector3f(normal.red * 2 - 1, normal.blue, -normal.green * 2 + 1).normalize();

        for (int i = yMin; i < yMax; i++) {
            Edge e = edgeTable[thread][i];
            while(e != null){
                activeEdges.add(e);
                e = e.next;
            }
            activeEdges.sort(Comparator.comparingDouble(edge -> edge.xMin));
            for (int j = 0; j < activeEdges.size(); j+=2) {
                int from = (int)activeEdges.get(j).xMin;
                int to = (int)activeEdges.get(j + 1).xMin;
                for (int k = from; k < to; k++)
                    canvas.setPixel(k, i, shadeHybrid(t, k, i, texA, texB, texC, normA, normB, normC, light));
            }
            int finalI = i;
            activeEdges.removeIf(edge -> edge.yMax - 1 == finalI);
            for (Edge edge : activeEdges) edge.xMin += edge.dx_dy;
        }
    }

    private static Color3f shadeHybrid(Triangle t, int xPos, int yPos, Color3f texA, Color3f texB, Color3f texC, Vector3f normA, Vector3f normB, Vector3f normC, Light light){
        Vector3f factors = Interpolationf.interpolationFactorsTriangle(
                t.a.screen.x, t.a.screen.y,
                t.b.screen.x, t.b.screen.y,
                t.c.screen.x, t.c.screen.y,
                xPos, yPos,
                new Vector3f()
        );
        Color3f objectColor = Color3f.interpolate(texA, texB, texC, factors);
        Color3f ambient = Color3f.mul(objectColor, t.ambient);
        Vector3f toLight = light.getPosition().w == 1.0f ?
                new Vector3f(light.getPosition().x - xPos, light.getPosition().y, light.getPosition().z - yPos).normalize() :
                new Vector3f(light.getPosition().x, light.getPosition().y, light.getPosition().z).normalize();
        Vector3f norm = new Vector3f(
                factors.x * normA.x + factors.y * normB.x + factors.z * normC.x,
                factors.x * normA.y + factors.y * normB.y + factors.z * normC.y,
                factors.x * normA.z + factors.y * normB.z + factors.z * normC.z
        );
        float cos = toLight.reflect(norm, new Vector3f()).dot(0, -1, 0);
        Color3f specular = cos < 1e-6 ?
                new Color3f(0) :
                Color3f.mul(objectColor, t.specular).mul(pow(cos, t.specularExponent));
        Color3f diffuse = objectColor.mul(toLight.dot(norm)).mul(t.diffuse);
        return diffuse.add(ambient).add(specular).clamp();
    }

    public void renderTriangle(Triangle t, Texture texture, Texture normals, Light light, int thread){
        Arrays.fill(edgeTable[thread], null);
        List<Edge> activeEdges = new LinkedList<>();
        int yMin = Math.min(t.a.screen.y, t.b.screen.y);
        if(t.a.screen.y != t.b.screen.y) {
            edgeTable[thread][yMin] = new Edge(t.a.screen, t.b.screen, edgeTable[thread][yMin]);
        }
        yMin = Math.min(t.c.screen.y, t.b.screen.y);
        if(t.c.screen.y != t.b.screen.y) {
            edgeTable[thread][yMin] = new Edge(t.b.screen, t.c.screen, edgeTable[thread][yMin]);
        }
        yMin = Math.min(t.a.screen.y, t.c.screen.y);
        if(t.a.screen.y != t.c.screen.y) {
            edgeTable[thread][yMin] = new Edge(t.c.screen, t.a.screen, edgeTable[thread][yMin]);
        }
        yMin= Math.min(t.b.screen.y, Math.min(0, yMin));
        int yMax = Math.max(Math.max(t.b.screen.y, canvas.getHeight()), Math.max(t.a.screen.y, t.c.screen.y));
        for (int i = yMin; i < yMax; i++) {
            Edge e = edgeTable[thread][i];
            while(e != null){
                activeEdges.add(e);
                e = e.next;
            }
            activeEdges.sort(Comparator.comparingDouble(edge -> edge.xMin));
            for (int j = 0; j < activeEdges.size(); j+=2) {
                int from = (int)activeEdges.get(j).xMin;
                int to = (int)activeEdges.get(j + 1).xMin;
                for (int k = from; k < to; k++) canvas.setPixel(k, i, shade(t, k, i, texture, normals, light));
            }
            int finalI = i;
            activeEdges.removeIf(edge -> edge.yMax - 1 == finalI);
            for (Edge edge : activeEdges) edge.xMin += edge.dx_dy;
        }
    }

    private static Color3f shade(Triangle t, int xPos, int yPos, Texture texture, Texture normals, Light light){
        Color3f objectColor = new Color3f(texture.get(xPos, yPos)).mul(light.getColor());
        Color3f ambient = Color3f.mul(objectColor, t.ambient);
        Vector3f toLight = light.getPosition().w == 1.0f ?
                new Vector3f(light.getPosition().x - xPos, light.getPosition().y, light.getPosition().z - yPos).normalize() :
                new Vector3f(light.getPosition().x, light.getPosition().y, light.getPosition().z).normalize();
        Color3f normal = new Color3f(normals.get(xPos, yPos));
        Vector3f norm = new Vector3f(normal.red * 2 - 1, normal.blue, -normal.green * 2 + 1).normalize();
        float cos = toLight.reflect(norm, new Vector3f()).dot(0, -1, 0);
        Color3f specular = cos < 1e-6 ?
                new Color3f(0) :
                Color3f.mul(objectColor, t.specular).mul(pow(cos, t.specularExponent));
        Color3f diffuse = objectColor.mul(toLight.dot(norm)).mul(t.diffuse);
        return diffuse.add(ambient).add(specular).clamp();
    }

    private static float pow(float a, int b){
        float result = 1.0f;
        while(b > 0){
            if((b & 1) == 1) result *= a;
            b = b >> 1;
            a *= a;
        }
        return result;
    }

    private Edge[][] edgeTable;

    private static class Edge {
        private int yMax;
        private float xMin;
        private float dx_dy;
        private Edge next;

        Edge(Vector2i a, Vector2i b, Edge next){
            yMax = Math.max(a.y, b.y);
            xMin = a.y > b.y ? b.x : a.x;
            dx_dy = (float) (a.x - b.x) / (float) (a.y - b.y);
            this.next = next;
        }
    }

}
