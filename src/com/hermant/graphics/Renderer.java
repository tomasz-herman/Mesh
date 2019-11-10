package com.hermant.graphics;

import org.joml.Interpolationf;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.w3c.dom.Text;

import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 1f;
    private static final float Z_FAR = 1000.f;

    private final static int THREADS = Runtime.getRuntime().availableProcessors();

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREADS);

    private Canvas canvas;

    public void setRenderFunction(RenderFunction renderFunction) {
        this.renderFunction = renderFunction;
    }

    private RenderFunction renderFunction = this::renderTriangle;

    public Renderer(Canvas canvas) {
        this.canvas = canvas;
        edgeTable = new Edge[THREADS][canvas.getHeight()];
    }

    public void renderScene(Scene scene) {
        canvas.clear();
        Mesh mesh = scene.getMesh();
        CountDownLatch latch = new CountDownLatch(mesh.getTriangles().size());
        for (Triangle triangle : mesh.getTriangles()) {
            executor.submit(() -> {
                try {
                    int thread = (int) (Thread.currentThread().getId() % THREADS);
                    renderFunction.render(triangle, mesh.getTexture(), mesh.getNormals(), scene.getLight(), thread);
                } catch (Exception e){
                    e.printStackTrace();
                }
                latch.countDown();
                Thread.currentThread().interrupt();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Triangle triangle : mesh.getTriangles()) {
            canvas.setPixel(triangle.a.screen.x, triangle.a.screen.y, 0xffff0000);
            canvas.setPixel(triangle.b.screen.x, triangle.b.screen.y, 0xffff0000);
            canvas.setPixel(triangle.c.screen.x, triangle.c.screen.y, 0xffff0000);
        }

        canvas.repaint();
    }

    @FunctionalInterface
    public interface RenderFunction {
        void render(Triangle t, Texture texture, Texture normal, Light light, int thread);
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
            for (int j = 0; j < activeEdges.size() >> 1; j++) {
                int from = (int)activeEdges.get(2 * j).xMin;
                int to = (int)activeEdges.get(2 * j + 1).xMin;
                for (int k = from; k < to; k++) {
                    canvas.setPixel(k, i, shadeFast(t, k, i, a, b, c));
                    //canvas.setPixel(k, i, texture.getSampleNearestNeighbor((float)k/ canvas.getWidth(), (float)i / canvas.getHeight()));
                }
                j++;
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
            for (int j = 0; j < activeEdges.size() >> 1; j++) {
                int from = (int)activeEdges.get(2 * j).xMin;
                int to = (int)activeEdges.get(2 * j + 1).xMin;
                for (int k = from; k < to; k++) {
                    canvas.setPixel(k, i, shadeHybrid(t, k, i, texA, texB, texC, normA, normB, normC, light));
                    //canvas.setPixel(k, i, texture.getSampleNearestNeighbor((float)k/ canvas.getWidth(), (float)i / canvas.getHeight()));
                }
                j++;
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
        double cos = toLight.reflect(norm, new Vector3f()).dot(0, -1, 0);
        Color3f specular = cos < 1e-6 ?
                new Color3f(0) :
                Color3f.mul(objectColor, t.specular).mul((float) Math.pow(cos, t.specularExponent));
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
            for (int j = 0; j < activeEdges.size() >> 1; j++) {
                int from = (int)activeEdges.get(2 * j).xMin;
                int to = (int)activeEdges.get(2 * j + 1).xMin;
                for (int k = from; k < to; k++) {
                    canvas.setPixel(k, i, shade(t, k, i, texture, normals, light));
                }
                j++;
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
        double cos = toLight.reflect(norm, new Vector3f()).dot(0, -1, 0);
        Color3f specular = cos < 1e-6 ?
                new Color3f(0) :
                Color3f.mul(objectColor, t.specular).mul((float) Math.pow(cos, t.specularExponent));
        Color3f diffuse = objectColor.mul(toLight.dot(norm)).mul(t.diffuse);
        return diffuse.add(ambient).add(specular).clamp();
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
