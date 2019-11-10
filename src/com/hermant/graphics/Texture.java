package com.hermant.graphics;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Texture {

    private int width, height;
    private int[] pixels;

    public Texture(String path) throws IOException {
        PNGDecoder decoder = new PNGDecoder(new FileInputStream(path));
        ByteBuffer buf = ByteBuffer.allocateDirect(3 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 3, PNGDecoder.Format.RGB);
        width = decoder.getWidth();
        height = decoder.getHeight();
        pixels = new int[width * height];
        for (int i = 0; i < width * height; i++)
            pixels[i] = ((buf.get(3 * i) << 16) & 0xff0000) | ((buf.get(3 * i + 1) << 8) & 0xff00) | ((buf.get(3 * i + 2)) & 0xff);
    }

    public Texture(int width, int height, Color3f color){
        int rgb = color.getRGB();
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
        Arrays.fill(pixels, rgb);
    }

    public int getSampleNearestNeighbor(float x, float y){
        int W = (int)(x * width);
        int H = (int)(y * height);
        return get(W, H);
    }

    public int getSampleBilinearInterpolation(float x, float y){
        x *= width;
        y*= height;
        int x0 = (int)x, y0 = (int)y;
        float dx = x-x0, dy = y-y0, omdx = 1.0f - dx, omdy = 1.0f - dy;
        return Color3f.add(
                Color3f.add(Color3f.mul(new Color3f(get(x0, y0)), omdx*omdy), Color3f.mul(new Color3f(get(x0, y0 + 1)), omdx*dy)),
                Color3f.add(Color3f.mul(new Color3f(get(x0 + 1, y0)), dx*omdy), Color3f.mul(new Color3f(get(x0 + 1, y0 + 1)), dx*dy)))
                .getRGB();
    }

    public int get(int width, int height){
        width = Math.max(Math.min(width, this.width - 1), 0);
        height = Math.max(Math.min(height, this.height - 1), 0);
        return pixels[height * this.width + width];
    }

    private void set(int width, int height, int color){
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

    public Texture rescale(int width, int height){
        Texture result = new Texture(width, height, new Color3f(0));
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                result.set(i, j, getSampleBilinearInterpolation((i + 0.5f) / width, (j + 0.5f) / height));
            }
        }
        return result;
    }
}
