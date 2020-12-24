package com.hermant.graphics.scene;

import com.hermant.graphics.cameras.Camera;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<GameObject> objects;
    private LightSetup lightSetup;
    private List<Camera> cameras = new ArrayList<>();
    private Camera camera;
    private int cameraIndex;

    public Camera getCamera() {
        return camera;
    }

    public void addCamera(Camera camera) {
        if(this.camera == null){
            this.camera = camera;
            cameraIndex = 0;
        }
        cameras.add(camera);
    }

    public void changeCamera() {
        if(cameras.isEmpty()) return;
        cameraIndex++;
        if(cameraIndex >= cameras.size()) cameraIndex = 0;
        camera = cameras.get(cameraIndex);
    }

    public void setGameObject(List<GameObject> objects) {
        this.objects = objects;
    }

    public List<GameObject> getGameObjects() {
        return objects;
    }

    public LightSetup getLightSetup() {
        return lightSetup;
    }

    public void setLightSetup(LightSetup lightSetup) {
        this.lightSetup = lightSetup;
    }
}
