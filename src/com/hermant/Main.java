package com.hermant;

import com.hermant.graphics.*;
import com.hermant.graphics.lights.LightSetup;
import com.hermant.graphics.lights.PointLight;
import com.hermant.gui.Window;
import org.joml.Vector3f;

import java.util.List;

public class Main {

    public static int DEFAULT_HEIGHT = 720;
    public static int DEFAULT_WIDTH = 1280;

    public static void main(String[] args) {
        Model rock = null;
        Model sponza = null;
        try {
            rock = ModelLoader.load("res/models/rock/rock.obj", "res/models/rock");
            sponza = ModelLoader.load("res/models/sponza/sponza.obj", "res/models/sponza");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rock == null)System.exit(2);
        Window window = new Window("Mesh", DEFAULT_WIDTH, DEFAULT_HEIGHT);
        Canvas canvas = new Canvas(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.setCanvas(canvas);
        window.pack();
        Renderer renderer = new Renderer(canvas);
        LightSetup lights = new LightSetup();
        lights.getPointLights().add(new PointLight(0, 100, 0));
        Scene scene = new Scene();
        scene.setLightSetup(lights);
        scene.setGameObject(List.of(new GameObject(sponza, new Vector3f(0, -20, 0), new Vector3f(0,0,0), 0.3f)));
        scene.setCamera(new Camera(new Vector3f(0, 80, 0), new Vector3f(0, 0f, 0)));
        window.setScene(scene);
        window.setRenderer(renderer);
        window.start();
    }


}
