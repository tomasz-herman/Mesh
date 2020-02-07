package com.hermant.graphics.lights;

import java.util.ArrayList;
import java.util.List;

public final class LightSetup {

    private AmbientLight ambientLight;
    private DirectionalLight directionalLight;
    private List<PointLight> pointLights;
    private List<SpotLight> spotLights;

    public LightSetup(){
        this.ambientLight = new AmbientLight();
        this.directionalLight = new DirectionalLight();
        this.pointLights = new ArrayList<>();
        this.spotLights = new ArrayList<>();
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }
}
