package com.hermant.graphics.renderer;

import com.hermant.graphics.utils.Color3f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Canvas extends JPanel implements ComponentListener {
    private static final int CLEAR_COLOR = 0x00000000;
    private static final float CLEAR_DEPTH = 1.0f;

    private int width;
    private int height;
    private BufferedImage image;
    private int[] pixels;
    private float[] depth;
    private String info = "";

    public Canvas(int width, int height) {
        setSize(this.width = width, this.height = height);
        initBuffers();
        addComponentListener(this);
    }

    private void initBuffers() {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        depth = new float[pixels.length];
        Arrays.fill(depth, CLEAR_DEPTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, width, height, null);
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 2.0f);
        g.setFont(newFont);
        g.setColor(Color.white);
        g.drawString(info, 4, 24);
    }

    public void setPixel(int x, int y, Color3f color){
        if(x >= width || x < 0 || y >= height || y < 0) return;
        pixels[y * width + x] = color.getRGB();
    }

    public void setPixel(int x, int y, int color){
        if(x >= width || x < 0 || y >= height || y < 0) return;
        pixels[y * width + x] = color;
    }

    public boolean testDepth(int x, int y, float depth){
        return depth > this.depth[y * width + x];
    }

    public void setPixel(int x, int y, Color3f color, float depth){
        if(depth > this.depth[y * width + x]) return;
        else this.depth[y * width + x] = depth;
        pixels[y * width + x] = color.getRGB();
    }

    public void setPixel(int x, int y, int color, float depth){
        if(depth > this.depth[y * width + x]) return;
        else this.depth[y * width + x] = depth;
        pixels[y * width + x] = color;
    }

    public void setPixelSafe(int x, int y, Color3f color, float depth){
        if(x >= width || x < 0 || y >= height || y < 0) return;
        if(depth > this.depth[y * width + x]) return;
        else this.depth[y * width + x] = depth;
        pixels[y * width + x] = color.getRGB();
    }

    public void setPixelSafe(int x, int y, int color, float depth){
        if(x >= width || x < 0 || y >= height || y < 0) return;
        if(depth > this.depth[y * width + x]) return;
        else this.depth[y * width + x] = depth;
        pixels[y * width + x] = color;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void appendInfo(String info) {
        this.info += info;
    }

    public void clear(){
        Arrays.fill(pixels, CLEAR_COLOR);
        Arrays.fill(depth, CLEAR_DEPTH);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.width = (int)getSize().getWidth();
        this.height = (int)getSize().getHeight();
        initBuffers();
    }

    @Override
    public void componentMoved(ComponentEvent e) { }

    @Override
    public void componentShown(ComponentEvent e) { }

    @Override
    public void componentHidden(ComponentEvent e) { }
}
