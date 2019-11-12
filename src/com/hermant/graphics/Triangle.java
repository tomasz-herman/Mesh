package com.hermant.graphics;

public class Triangle {
    public Vertex a, b, c;
    public float ambient;
    public float diffuse;
    public float specular;
    public int specularExponent;

    public Triangle(Vertex a, Vertex b, Vertex c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
