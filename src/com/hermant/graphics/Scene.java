package com.hermant.graphics;

import java.util.List;

public class Scene {

    private List<GameObject> objects;
    private Light light;
    private Camera camera;

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setGameObject(List<GameObject> objects) {
        this.objects = objects;
    }

    public List<GameObject> getGameObjects() {
        return objects;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }
}
