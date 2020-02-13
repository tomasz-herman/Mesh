package com.hermant.graphics.scene;

import com.hermant.graphics.lights.AmbientLight;
import com.hermant.graphics.lights.DirectionalLight;
import com.hermant.graphics.lights.PointLight;
import com.hermant.graphics.lights.SpotLight;
import org.joml.Matrix4f;

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

    public void transform(Matrix4f viewMatrix) {
        for (PointLight pointLight : pointLights) {
            pointLight.calculatePositionEyeSpace(viewMatrix);
        }
        for (SpotLight spotLight : spotLights) {
            spotLight.calculatePositionEyeSpace(viewMatrix);
            spotLight.calculateDirectionEyeSpace(viewMatrix);
        }
        directionalLight.calculateDirectionEyeSpace(viewMatrix);
    }
}
