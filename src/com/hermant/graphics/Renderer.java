package com.hermant.graphics;


import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;

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

    public void renderTriangleWireframe(Triangle t, Material m, Light l){
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
