package com.hermant.graphics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {

    private static Map<String, Texture> textures = new HashMap<>();

    public static Texture getTexture(String path) throws IOException {
        Texture texture = textures.get(path);
        if ( texture == null ) {
            texture = new Texture(path);
            System.out.println("[Texture Manager]Loading new texture file: " + path);
            textures.put(path, texture);
        }
        return texture;
    }

}
