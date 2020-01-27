package com.hermant.gui;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private static final int MINIMUM_WIDTH = 320;
    private static final int MINIMUM_HEIGHT = 240;

    private Layout layout;
    private String title;
    public Window(String title, int width, int height){
        this.title = title;
        layout = new Layout();
        setContentPane(layout.getMainPanel());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(title);
        setSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
        getContentPane().setBackground(Color.BLACK);
        layout.setDisplayFPS(this::setText);
        addMouseListener(layout);
        addMouseMotionListener(layout);
        addKeyListener(layout);
        addMouseWheelListener(layout);
    }

    public void setText(String text){
        setTitle(title + " " + text);
    }

    public void setCanvas(com.hermant.graphics.Canvas canvas){
        layout.setCanvas(canvas);
    }

    public void setScene(com.hermant.graphics.Scene scene){
        layout.setScene(scene);
    }

    public void setRenderer(com.hermant.graphics.Renderer renderer){
        layout.setRenderer(renderer);
    }

    public void start(){
        layout.start();
    }
}
