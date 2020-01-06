package com.hermant;

import com.hermant.graphics.*;
import com.hermant.graphics.Renderer;
import com.hermant.gui.Window;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.List;

public class Main {

    public static int DEFAULT_HEIGHT = 720;
    public static int DEFAULT_WIDTH = 1280;

    public static void main(String[] args) {
        Texture texture = null;
        Texture normals = null;
        try {
            texture = new Texture("./res/textures/wall.png");
            normals = new Texture("./res/textures/wall_normal.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert texture != null;
        assert normals != null;
        Window window = new Window("Mesh", DEFAULT_WIDTH, DEFAULT_HEIGHT);
        Canvas canvas = new Canvas(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.setCanvas(canvas);
        window.pack();
        Renderer renderer = new Renderer(canvas);
        Scene scene = new Scene();
        scene.setLight(new Light(0, 0, 0, true));
        scene.setGameObject(List.of(new GameObject(new Model(List.of(MeshGenerator.cube())), new Vector3f(0, 0, 0), new Vector3f(0,0,0), 1f)));
        scene.setCamera(new Camera(new Vector3f(0, 0, 6), new Vector3f(0, 0f, 0)));
        window.setScene(scene);
        window.setRenderer(renderer);
        window.start();
    }


}
