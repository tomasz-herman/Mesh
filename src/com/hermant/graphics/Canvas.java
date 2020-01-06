package com.hermant.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Canvas extends JPanel {
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final int[] pixels;
    private final int[] clear;

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        setMinimumSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        clear = new int[pixels.length];
        Arrays.fill(clear, 0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, width, height, null);
    }

    public void setPixel(int x, int y, Color3f color){
        if(x >= width || x < 0 || y >= height || y < 0) return;
        pixels[y * width + x] = color.getRGB();
    }

    public void setPixel(int x, int y, int color){
        if(x >= width || x < 0 || y >= height || y < 0) return;
        pixels[y * width + x] = color;
    }

    public void clear(){
        System.arraycopy(clear, 0, pixels, 0, pixels.length);
    }
}
