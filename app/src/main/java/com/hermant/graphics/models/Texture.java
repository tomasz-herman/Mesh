package com.hermant.graphics.models;

import com.hermant.graphics.utils.Color3f;
import com.hermant.utils.FileUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;

import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture {

    private int width, height;
    private Color3f[] pixels;
    private float[] alphas;
    private int medium;

    public Texture(String path) throws IOException {
        ByteBuffer imageData = FileUtils.ioResourceToByteBuffer(path, 1024);
        ByteBuffer decodedImage;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            decodedImage = stbi_load_from_memory(imageData, w, h, comp, 4);
            this.width = w.get();
            this.height = h.get();
        }
        if (decodedImage == null) throw new IOException();

        pixels = new Color3f[width * height];
        alphas = new float[width * height];
        Color3f medium = new Color3f();

        for (int i = 0; i < width * height; i++){
            pixels[i] = new Color3f(((decodedImage.get(4 * i) << 16) & 0xff0000) | ((decodedImage.get(4 * i + 1) << 8) & 0xff00) | ((decodedImage.get(4 * i + 2)) & 0xff));
            medium.add(pixels[i]);
            alphas[i] = (decodedImage.get(4 * i + 3) & 0xff) / 255.0f;
        }
        medium.div(width * height).clamp();
        this.medium = medium.getRGB();
    }

    public Texture(int width, int height, Color3f color){
        this.width = width;
        this.height = height;
        pixels = new Color3f[width * height];
        IntStream.range(0, width * height).forEach(i -> pixels[i] = new Color3f(color));
    }

    public Color3f getSampleNearestNeighbor(float x, float y){
        int W = (int)(x * width);
        int H = (int)(y * height);
        return getColor(W, H);
    }

    public float getAlpha(float x, float y){
        int W = (int)(x * width);
        int H = (int)(y * height);
        return getAlpha(W, H);
    }

//    public int getSampleBilinearInterpolation(float x, float y){
//        x *= width;
//        y*= height;
//        int x0 = (int)x, y0 = (int)y;
//        float dx = x-x0, dy = y-y0, omdx = 1.0f - dx, omdy = 1.0f - dy;
//        return Color3f.add(
//                Color3f.add(Color3f.mul(new Color3f(get(x0, y0)), omdx*omdy), Color3f.mul(new Color3f(get(x0, y0 + 1)), omdx*dy)),
//                Color3f.add(Color3f.mul(new Color3f(get(x0 + 1, y0)), dx*omdy), Color3f.mul(new Color3f(get(x0 + 1, y0 + 1)), dx*dy)))
//                .getRGB();
//    }

    public Color3f getColor(int width, int height){
        width = Math.max(Math.min(width, this.width - 1), 0);
        height = Math.max(Math.min(height, this.height - 1), 0);
        return pixels[height * this.width + width];
    }

    public float getAlpha(int width, int height){
        width = Math.max(Math.min(width, this.width - 1), 0);
        height = Math.max(Math.min(height, this.height - 1), 0);
        return alphas[height * this.width + width];
    }

    private void setColor(int width, int height, Color3f color){
        width = Math.max(Math.min(width, this.width - 1), 0);
        height = Math.max(Math.min(height, this.height - 1), 0);
        pixels[height * this.width + width] = color;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

//    public Texture rescale(int width, int height){
//        Texture result = new Texture(width, height, new Color3f(0));
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                result.set(i, j, getSampleBilinearInterpolation((i + 0.5f) / width, (j + 0.5f) / height));
//            }
//        }
//        return result;
//    }

    public int getMedium() {
        return medium;
    }
}
