package com.hermant.graphics.lights;

import com.hermant.graphics.utils.Color3f;

public abstract class Light {
    private Color3f color;

    public Light(Color3f color) {
        this.color = color;
    }

    public Light() {
        this.color = new Color3f(0.25f);
    }

    public Color3f getColor() {
        return color;
    }

    public void setColor(Color3f color) {
        this.color = color;
    }
}
