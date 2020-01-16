package com.hermant.gui;

import com.hermant.graphics.Canvas;
import com.hermant.graphics.Scene;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class Layout {

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

    public void start(){
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

    private void update(float delta){
        scene.getCamera().rotate(0, 1 * delta, 0);
    }

    public JPanel getMainPanel() {
        return canvas_panel;
    }

}
