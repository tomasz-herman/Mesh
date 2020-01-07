package com.hermant.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Canvas extends JPanel implements ComponentListener {
    private int width;
    private int height;
    private BufferedImage image;
    private int[] pixels;
    private int[] clear;
    private float[] depth;

    public Canvas(int width, int height) {
        setSize(this.width = width, this.height = height);
        initBuffers();
        addComponentListener(this);
    }

    private void initBuffers() {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        clear = new int[pixels.length];
        depth = new float[pixels.length];
        Arrays.fill(clear, 0);
        Arrays.fill(depth, Float.MIN_VALUE);
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

    public void setPixel(int x, int y, Color3f color, float depth){
        if(x >= width || x < 0 || y >= height || y < 0) return;
        if(depth < this.depth[y * width + x]) return;
        else this.depth[y * width + x] = depth;
        pixels[y * width + x] = color.getRGB();
    }

    public void setPixel(int x, int y, int color, float depth){
        if(x >= width || x < 0 || y >= height || y < 0) return;
        if(depth < this.depth[y * width + x]) return;
        else this.depth[y * width + x] = depth;
        pixels[y * width + x] = color;
    }


    public void clear(){
        System.arraycopy(clear, 0, pixels, 0, pixels.length);
        Arrays.fill(depth, Float.MIN_VALUE);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.width = (int)getSize().getWidth();
        this.height = (int)getSize().getHeight();
        initBuffers();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
