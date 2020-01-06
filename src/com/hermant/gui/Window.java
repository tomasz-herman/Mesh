package com.hermant.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Window extends JFrame {
    private Layout layout;
    private String title;
    public Window(String title, int width, int height){
        this.title = title;
        layout = new Layout();
        setContentPane(layout.getMain());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(title);
        setSize(new Dimension(width, height));
        setMinimumSize(new Dimension(640, 480));
        setResizable(true);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
        getContentPane().setBackground(Color.BLACK);
        layout.setDisplayFPS(this::setText);
        pack();
        revalidate();
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                System.out.println("Resized");
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
        });
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
