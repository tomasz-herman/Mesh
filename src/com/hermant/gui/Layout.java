package com.hermant.gui;

import com.hermant.Main;
import com.hermant.graphics.*;
import com.hermant.graphics.Canvas;
import org.joml.Vector3f;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class Layout {

    private JPanel main;
    private JPanel canvas_panel;

    private Scene scene;
    private com.hermant.graphics.Renderer renderer;
    private Canvas canvas;
    private Consumer<String> displayFPS = System.out::println;

    public Layout() {

    }

    public void setDisplayFPS(Consumer<String> displayFPS) {
        this.displayFPS = displayFPS;
    }

    public void setScene(Scene scene){
          this.scene = scene;
    }

    public void setRenderer(com.hermant.graphics.Renderer renderer){
        this.renderer = renderer;
    }

    public void setCanvas(Canvas canvas){
        this.canvas = canvas;
        canvas_panel.setLayout(new GridLayout());
        canvas_panel.add(canvas);
    }

    public void initializeTextureCanvases(){

    }


    public void start(){
        new MouseAdapter(canvas);
        int frames = 0;
        int updates = 0;
        long time = 0;
        long dt = 1_000_000_000 / 60;
        long currentTime = System.nanoTime();
        while(true){
            long newTime = System.nanoTime();
            long frameTime = newTime - currentTime;
            currentTime = newTime;
            while ( frameTime > 0 )
            {
                long deltaTime = Math.min(frameTime, dt);
                update(deltaTime / 1e9f);
                frameTime -= deltaTime;
                time += deltaTime;
                updates++;
            }
            try {
                SwingUtilities.invokeAndWait(this::render);
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
            }
            frames++;
            if(time > 1e9){
                displayFPS.accept(frames + " fps, " + updates + " ups");
                time = 0;
                frames = 0;
                updates = 0;
            }
        }
    }

    private void render(){
        renderer.renderScene(scene);
    }

    boolean rot = false;
    int i = 0;

    private void update(float delta){
        Vector3f rotation = scene.getGameObject().getRotation();
        if(rot) scene.getGameObject().setRotation(rotation.x, rotation.y + delta*100, rotation.z);
        else scene.getGameObject().setRotation(rotation.x, rotation.y - delta*100, rotation.z);
        i++;
        if(i > 3000) {
            rot = !rot;
            i = 0;
        }
    }

    public JPanel getMain() {
        return main;
    }

    private class MouseAdapter implements MouseListener, MouseMotionListener, MouseWheelListener {

        MouseAdapter(Canvas canvas){
            canvas.addMouseMotionListener(this);
            canvas.addMouseListener(this);
            canvas.addMouseWheelListener(this);
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }
    }


}
