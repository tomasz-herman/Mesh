package com.hermant.graphics.lights;

import com.hermant.graphics.utils.Color3f;

public class AmbientLight extends Light {
    public AmbientLight(Color3f color) {
        super(color);
    }

    public AmbientLight() {
        super();
    }
}
