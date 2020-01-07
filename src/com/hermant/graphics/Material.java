package com.hermant.graphics;

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
    }

    public Texture getSpecularTexture() {
        return specularTexture;
    }

    public void setSpecularTexture(Texture specularTexture) {
        this.specularTexture = specularTexture;
    }

    public Texture getAmbientTexture() {
        return ambientTexture;
    }

    public void setAmbientTexture(Texture ambientTexture) {
        this.ambientTexture = ambientTexture;
    }

    public Texture getNormalsTexture() {
        return normalsTexture;
    }

    public void setNormalsTexture(Texture normalsTexture) {
        this.normalsTexture = normalsTexture;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public boolean hasDiffuseTexture() {
        return diffuseTexture != null;
    }

    public boolean hasSpecularTexture() {
        return specularTexture != null;
    }

    public boolean hasAmbientTexture() {
        return ambientTexture != null;
    }

    public boolean hasNormalsTexture() {
        return normalsTexture != null;
    }
}
