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
        setContentPane(layout.getMain());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(title);
        setSize(new Dimension(width, height));
        setMinimumSize(new Dimension(640, 480));
        setResizable(false);
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setLocationRelativeTo(null);
        pack();
        try {
            layout.initializeTerxtureCanvases();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        e.printStackTrace();
        }
        pack();
        setVisible(true);
        layout.setDisplayFPS(this::setText);
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
