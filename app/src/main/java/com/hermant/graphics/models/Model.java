package com.hermant.graphics.models;

import java.util.List;

public class Model {

    public Model(List<Mesh> meshes) {
        this.meshes = meshes;
    }

    private List<Mesh> meshes;

    public List<Mesh> getMeshes() {
        return meshes;
    }
}
