package com.hermant.gui;

import com.hermant.graphics.renderer.Engine;
import com.hermant.graphics.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Window extends JFrame implements KeyListener {

    private static final int MINIMUM_WIDTH = 320;
    private static final int MINIMUM_HEIGHT = 240;
    private static final GraphicsDevice DEVICE =
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getScreenDevices()[0];

    private Engine engine;
    private String title;
    private boolean fullscreen = false;

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
        addKeyListener(this);
    }

    public void setText(String text){
        setTitle(title + " " + text);
    }

    public void setScene(Scene scene){
        engine.setScene(scene);
    }

    public void start(){
        engine.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }


    private int lastWidth, lastHeight;
    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
        if(e.getKeyCode() == KeyEvent.VK_F){
            dispose();
            if(fullscreen){
                DEVICE.setFullScreenWindow(null);
                setExtendedState(NORMAL);
                setUndecorated(false);
                setSize(lastWidth, lastHeight);
                setLocationRelativeTo(null);
                setVisible(true);
            } else {
                lastWidth = getWidth();
                lastHeight = getHeight();
                setUndecorated(true);
                DEVICE.setFullScreenWindow(this);
                setVisible(true);
            }
            fullscreen = !fullscreen;
        }
    }
}
