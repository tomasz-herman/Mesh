package com.hermant;

import com.hermant.graphics.*;
import com.hermant.graphics.Renderer;
import com.hermant.gui.ResolutionChooser;
import com.hermant.gui.Window;

import java.io.IOException;

public class Main {

    public static int CANVAS_HEIGHT = 768;
    public static int CANVAS_WIDTH = 768;
    public static final int GRID_DENSITY_X = 20;
    public static final int GRID_DENSITY_Y = 20;
    public static final int LIGHT_HEIGHT = 100;
    public static final int LIGHT_RADIUS = 350;

    public static final Object mutex = new Object();

    public static void main(String[] args) {
        Texture texture = null;
        Texture normals = null;
        ResolutionChooser resolution = new ResolutionChooser();
        try {
            synchronized (mutex){
                mutex.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            texture = new Texture("./res/textures/wall.png").rescale(CANVAS_WIDTH, CANVAS_HEIGHT);
            normals = new Texture("./res/textures/wall_normal.png").rescale(CANVAS_WIDTH, CANVAS_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert texture != null;
        assert normals != null;
        Window window = new Window("Meshes", CANVAS_WIDTH, CANVAS_HEIGHT);
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        window.setCanvas(canvas);
        window.pack();
        Renderer renderer = new Renderer(canvas);
        Scene scene = new Scene();
        scene.setLight(new Light(LIGHT_RADIUS, LIGHT_HEIGHT, 0, true));
        Mesh mesh = MeshGenerator.grid(GRID_DENSITY_X, GRID_DENSITY_Y, texture, normals);
        mesh.setAmbient(0.3f);
        mesh.setDiffuse(0.7f);
        mesh.setSpecular(0.7f);
        mesh.setSpecularExponent(30);
        scene.setMesh(mesh);
        window.setScene(scene);
        window.setRenderer(renderer);
        window.start();
    }


}
