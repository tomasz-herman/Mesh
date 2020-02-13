package com.hermant.graphics.models;

import com.hermant.graphics.utils.Color3f;

public class Material {
    public static final Color3f DEFAULT_COLOUR = new Color3f(1.0f, 1.0f, 1.0f);

    private Color3f ambientColour;
    private Color3f diffuseColour;
    private Color3f specularColour;

    private Texture diffuseTexture;
    private Texture specularTexture;
    private Texture ambientTexture;
    private Texture normalsTexture;

    private boolean hasDiffuseTexture;
    private boolean hasSpecularTexture;
    private boolean hasAmbientTexture;
    private boolean hasNormalsTexture;

    private float shininess;

    public Material() {
        this.ambientColour = DEFAULT_COLOUR;
        this.diffuseColour = DEFAULT_COLOUR;
        this.specularColour = DEFAULT_COLOUR;
        diffuseTexture = null;
        specularTexture = null;
        ambientTexture = null;
        normalsTexture = null;
        hasDiffuseTexture = false;
        hasSpecularTexture = false;
        hasAmbientTexture = false;
        hasNormalsTexture = false;
        shininess = 0.0f;
    }

    public Color3f getAmbientColour() {
        return ambientColour;
    }

    public void setAmbientColour(Color3f ambientColour) {
        this.ambientColour = ambientColour;
    }

    public Color3f getDiffuseColour() {
        return diffuseColour;
    }

    public void setDiffuseColour(Color3f diffuseColour) {
        this.diffuseColour = diffuseColour;
    }

    public Color3f getSpecularColour() {
        return specularColour;
    }

    public void setSpecularColour(Color3f specularColour) {
        this.specularColour = specularColour;
    }

    public Texture getDiffuseTexture() {
        return diffuseTexture;
    }

    public void setDiffuseTexture(Texture diffuseTexture) {
        this.diffuseTexture = diffuseTexture;
        hasDiffuseTexture = diffuseTexture != null;
    }

    public Texture getSpecularTexture() {
        return specularTexture;
    }

    public void setSpecularTexture(Texture specularTexture) {
        this.specularTexture = specularTexture;
        hasSpecularTexture = specularTexture != null;

    }

    public Texture getAmbientTexture() {
        return ambientTexture;
    }

    public void setAmbientTexture(Texture ambientTexture) {
        this.ambientTexture = ambientTexture;
        hasAmbientTexture = ambientTexture != null;
    }

    public Texture getNormalsTexture() {
        return normalsTexture;
    }

    public void setNormalsTexture(Texture normalsTexture) {
        this.normalsTexture = normalsTexture;
        hasNormalsTexture = normalsTexture != null;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public boolean hasDiffuseTexture() {
        return hasDiffuseTexture;
    }

    public boolean hasSpecularTexture() {
        return hasSpecularTexture;
    }

    public boolean hasAmbientTexture() {
        return hasAmbientTexture;
    }

    public boolean hasNormalsTexture() {
        return hasNormalsTexture;
    }
}
