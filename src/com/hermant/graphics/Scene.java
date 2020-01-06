package com.hermant.graphics;

public class Scene {

    private GameObject object;
    private Light light;
    private Camera camera;

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setGameObject(GameObject object) {
        this.object = object;
    }

    public GameObject getGameObject() {
        return object;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }
}
