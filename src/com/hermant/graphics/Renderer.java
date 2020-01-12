package com.hermant.graphics;

import org.joml.*;

import java.lang.Math;
import java.util.List;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 1f;
    private static final float Z_FAR = 1000.f;

    private final static int THREADS = Runtime.getRuntime().availableProcessors();

    private final Transformation transformation = new Transformation();

    private Canvas canvas;

    public void setRenderFunction(RenderFunction renderFunction) {
        this.renderFunction = renderFunction;
    }

    private RenderFunction renderFunction = this::renderTriangle;

    public Renderer(Canvas canvas) {
        this.canvas = canvas;
    }

    public void renderScene(Scene scene) {
        canvas.clear();
        List<GameObject> objects = scene.getGameObjects();
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, canvas.getWidth(), canvas.getHeight(), Z_NEAR, Z_FAR);
        Matrix4f viewMatrix = transformation.getViewMatrix(scene.getCamera());
        for (GameObject object : objects) {
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(object, viewMatrix);
            Matrix3f normalMatrix = transformation.getNormalMatrix(modelViewMatrix);
            Matrix4f MVP = transformation.getModelViewProjectionMatrix(modelViewMatrix, projectionMatrix);
            FrustumIntersection intersection = new FrustumIntersection(MVP);
            if(object.getModel().getMeshes().size() > THREADS){
                object.getModel().getMeshes().parallelStream().forEach(mesh -> {
                    AABBf box = mesh.getAABB();
                    if(!intersection.testAab(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) return;
                    if(mesh.getVertices().size() > THREADS << 2)
                        mesh.getVertices().parallelStream().forEach(vertex -> vertex.transform(MVP, normalMatrix, canvas.getWidth(), canvas.getHeight()));
                    else
                        mesh.getVertices().forEach(vertex -> vertex.transform(MVP, normalMatrix, canvas.getWidth(), canvas.getHeight()));
                    if(mesh.getTriangles().size() > THREADS << 2)
                        mesh.getTriangles().parallelStream().forEach(triangle -> renderFunction.render(triangle, mesh.getMaterial(), scene.getLight()));
                    else
                        mesh.getTriangles().forEach(triangle -> renderFunction.render(triangle, mesh.getMaterial(), scene.getLight()));
                });
            } else {
                object.getModel().getMeshes().forEach(mesh -> {
                    AABBf box = mesh.getAABB();
                    if(!intersection.testAab(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) return;
                    if(mesh.getVertices().size() > THREADS << 2)
                        mesh.getVertices().parallelStream().forEach(vertex -> vertex.transform(MVP, normalMatrix, canvas.getWidth(), canvas.getHeight()));
                    else
                        mesh.getVertices().forEach(vertex -> vertex.transform(MVP, normalMatrix, canvas.getWidth(), canvas.getHeight()));
                    if(mesh.getTriangles().size() > THREADS << 2)
                        mesh.getTriangles().parallelStream().forEach(triangle -> renderFunction.render(triangle, mesh.getMaterial(), scene.getLight()));
                    else
                        mesh.getTriangles().forEach(triangle -> renderFunction.render(triangle, mesh.getMaterial(), scene.getLight()));
                });
            }
        }
        canvas.repaint();
    }

    @FunctionalInterface
    public interface RenderFunction {
        void render(Triangle t, Material m, Light l);
    }

    public void renderTriangleWireframe(Triangle t, Material m, Light l){
        if(t.a.transformed.z > -1 && t.b.transformed.z > -1 && t.c.transformed.z > -1 & t.a.transformed.z < 1 && t.b.transformed.z < 1 && t.c.transformed.z < 1){
            drawLine(t.a.screen, t.b.screen);
            drawLine(t.c.screen, t.b.screen);
            drawLine(t.a.screen, t.c.screen);
        }
    }

    private static int orientation(Vector2i a, Vector2i b, Vector2i c) {
        return (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*((c.x-a.x));
    }

    private static int min(int a, int b, int c){
        return Math.min(a, Math.min(b, c));
    }

    private static int max(int a, int b, int c){
        return Math.max(a, Math.max(b, c));
    }


    public void renderTriangle(Triangle t, Material m, Light l){
        Vector2i v0 = t.c.screen, v1 = t.b.screen, v2 = t.a.screen;
        if(v0.x < 0 || v1.x < 0 || v2.x < 0 || v0.y < 0 || v1.y < 0 || v2.y < 0 || v0.y > canvas.getHeight() || v1.y > canvas.getHeight() || v2.y > canvas.getHeight() || v0.x > canvas.getWidth() || v1.x > canvas.getWidth() || v2.x > canvas.getWidth())return;
        int minX = min(v0.x, v1.x, v2.x);
        int minY = min(v0.y, v1.y, v2.y);
        int maxX = max(v0.x, v1.x, v2.x);
        int maxY = max(v0.y, v1.y, v2.y);

        if(maxY < 0) return;
        if(maxX < 0) return;
        if(minX > canvas.getWidth()) return;
        if(minY > canvas.getHeight()) return;

        int area = orientation(v0, v1, v2);
        if(area < 1) return;

        // Clip against screen bounds
        minX = Math.max(minX, 0);
        minY = Math.max(minY, 0);
        maxX = Math.min(maxX, canvas.getWidth() - 1);
        maxY = Math.min(maxY, canvas.getHeight() - 1);


        Vector2f texA = new Vector2f(t.a.texture.x * t.a.transformed.w, t.a.texture.y * t.a.transformed.w);
        Vector2f texB = new Vector2f(t.b.texture.x * t.b.transformed.w, t.b.texture.y * t.b.transformed.w);
        Vector2f texC = new Vector2f(t.c.texture.x * t.c.transformed.w, t.c.texture.y * t.c.transformed.w);

        // Triangle setup
        int A01 = v0.y - v1.y, B01 = v1.x - v0.x;
        int A12 = v1.y - v2.y, B12 = v2.x - v1.x;
        int A20 = v2.y - v0.y, B20 = v0.x - v2.x;


        // Rasterize
        Vector2i p = new Vector2i(minX, minY);

        int w0_row = orientation(v1, v2, p);
        int w1_row = orientation(v2, v0, p);
        int w2_row = orientation(v0, v1, p);

        for (p.y = minY; p.y <= maxY; p.y++) {
            // Determine barycentric coordinates
            int w0 = w0_row;
            int w1 = w1_row;
            int w2 = w2_row;
            for (p.x = minX; p.x <= maxX; p.x++) {

                // If p is on or inside all edges, render pixel.
                if ((w0 | w1 | w2) >= 0){


                    float f0 = (float)w0 / area;
                    float f1 = (float)w1 / area;
                    float f2 = 1.0f - f0 - f1;

                    float z = 1 / (f0 * t.c.transformed.w + f1 * t.b.transformed.w + f2 * t.a.transformed.w);

                    Vector2f tex = new Vector2f(z * (texC.x * f0 + texB.x * f1 + texA.x * f2),
                            z * (texC.y * f0 + texB.y * f1 + texA.y * f2));

                    if(tex.x < 0) tex.x -= (int) tex.x - 1;
                    if(tex.y < 0) tex.y -= (int) tex.y - 1;
                    if(tex.x > 1) tex.x -= (int) tex.x;
                    if(tex.y > 1) tex.y -= (int) tex.y;

                    float depth = (f0 * t.c.transformed.z + f1 * t.b.transformed.z + f2 * t.a.transformed.z);
                    if(m.getDiffuseTexture() == null || depth > 1 || depth < -1)continue;
                    canvas.setPixel(p.x, p.y, m.getDiffuseTexture().getSampleNearestNeighbor(tex.x, tex.y), depth);
                }

                // One step to the right
                w0 += A12;
                w1 += A20;
                w2 += A01;
            }
            // One row step
            w0_row += B12;
            w1_row += B20;
            w2_row += B01;
        }
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

    private static float pow(float a, int b){
        float result = 1.0f;
        while(b > 0){
            if((b & 1) == 1) result *= a;
            b = b >> 1;
            a *= a;
        }
        return result;
    }

}
