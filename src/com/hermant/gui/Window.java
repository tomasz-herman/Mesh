package com.hermant.gui;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private static final int MINIMUM_WIDTH = 320;
    private static final int MINIMUM_HEIGHT = 240;

    private Engine engine;
    private String title;

    public Window(String title, int width, int height){
        this.title = title;
        engine = new Engine(width, height);
        setContentPane(engine.getCanvas());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(title);
        setSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
        getContentPane().setBackground(Color.BLACK);
        engine.setDisplayFPS(this::setText);
        addMouseListener(engine);
        addMouseMotionListener(engine);
        addKeyListener(engine);
        addMouseWheelListener(engine);
    }

    public void setText(String text){
        setTitle(title + " " + text);
    }

    public void setScene(com.hermant.graphics.Scene scene){
        engine.setScene(scene);
    }

    public void start(){
        engine.start();
    }
}
