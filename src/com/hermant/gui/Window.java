package com.hermant.gui;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private Layout layout;
    private String title;
    public Window(String title, int width, int height){
        this.title = title;
        layout = new Layout();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setContentPane(layout.getMain());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(title);
        setSize(new Dimension(width, height));
        setMinimumSize(new Dimension(640, 480));
        setResizable(false);
        setLocationRelativeTo(null);
        pack();
        layout.initializeTerxtureCanvases();
        pack();
        setVisible(true);
        getContentPane().setBackground(Color.BLACK);
        layout.setDisplayFPS(this::setText);
        pack();
        revalidate();
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
