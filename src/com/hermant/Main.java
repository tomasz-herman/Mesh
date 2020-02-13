package com.hermant;

import com.hermant.graphics.*;
import com.hermant.graphics.cameras.FirstPersonCamera;
import com.hermant.graphics.cameras.FollowingCamera;
import com.hermant.graphics.cameras.ThirdPersonCamera;
import com.hermant.graphics.lights.LightSetup;
import com.hermant.graphics.lights.PointLight;
import com.hermant.graphics.lights.SpotLight;
import com.hermant.gui.Window;
import org.joml.Vector3f;

import java.util.List;

public class Main {

    public static int DEFAULT_HEIGHT = 720;
    public static int DEFAULT_WIDTH = 1280;

    public static void main(String[] args) {
        Model rock = null;
        Model sponza = null;
        Model table = null;
        try {
            rock = ModelLoader.load("res/models/rock/rock.obj", "res/models/rock");
            sponza = ModelLoader.load("res/models/sponza/sponza.obj", "res/models/sponza");
            table = ModelLoader.load("res/models/table/table.obj", "res/models/table");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rock == null)System.exit(2);
        Window window = new Window("Mesh", DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.pack();
        LightSetup lights = new LightSetup();
        PointLight pointLight = new PointLight(0, 100, 0);
        pointLight.setColor(new Color3f(255, 0, 0));
        lights.getPointLights().add(pointLight);
        SpotLight spotLight = new SpotLight(0, 100, 0, 0, 1, 0, 0.98f, 0.95f);
        spotLight.setColor(new Color3f(0, 255, 255));
        lights.getSpotLights().add(spotLight);
        Scene scene = new Scene();
        scene.setLightSetup(lights);
        scene.setGameObject(
                List.of(
                        new GameObject(sponza, new Vector3f(0, 0, 0), new Vector3f(0,0,0), 0.3f),
                        new GameObject(table, new Vector3f(0, 0, 0), new Vector3f(0,0,0), 33.3f),
                        new GameObject(rock, new Vector3f(33.3f, 50.0f, 0), new Vector3f(0,-90,0), 4.0f)
                ));
        scene.setCamera(new FirstPersonCamera(new Vector3f(0, 80, 0), new Vector3f(0, 0f, 0)));
        scene.setCamera(new FollowingCamera(new Vector3f(40, 60, 0), scene.getGameObjects().get(2)));
        scene.setCamera(new ThirdPersonCamera(scene.getGameObjects().get(2), 25));
        window.setScene(scene);
        window.start();
    }


}
