package com.hermant.gui;

import com.hermant.graphics.Camera;
import com.hermant.graphics.Canvas;
import com.hermant.graphics.Renderer;
import com.hermant.graphics.Scene;

import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class Engine implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    private Scene scene;
    private Renderer renderer;
    private Canvas canvas;
    private Consumer<String> displayFPS = System.out::println;

    public Engine(int width, int height) {
        canvas = new Canvas(width, height);
        renderer = new Renderer(canvas);
    }

    public void setDisplayFPS(Consumer<String> displayFPS) {
        this.displayFPS = displayFPS;
    }

    public void setScene(Scene scene){
        this.scene = scene;
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
        if(scene == null) return;
        float move = delta * 100;
        Camera camera = scene.getCamera();
        if(up){
            camera.move(0, move, 0);
        }
        if(down){
            camera.move(0, -move, 0);
        }
        if(left){
            camera.move(-move, 0, 0);
        }
        if(right){
            camera.move(move, 0, 0);
        }
        if(forward){
            camera.move(0, 0, -move);
        }
        if(backward){
            camera.move(0, 0, move);
        }
        camera.rotate(deltaY * delta * 20, 0, 0);
        camera.rotate(0, deltaX * delta * 20, 0);
        deltaX = 0;
        deltaY = 0;
    }

    public JPanel getCanvas() {
        return canvas;
    }

    private boolean up = false;
    private boolean down = false;
    private boolean left = false;
    private boolean right = false;
    private boolean forward = false;
    private boolean backward = false;

    private int mouseX;
    private int mouseY;
    private int deltaX;
    private int deltaY;

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        int newMouseX = mouseEvent.getX();
        int newMouseY = mouseEvent.getY();
        deltaX = newMouseX - mouseX;
        deltaY = newMouseY - mouseY;
        mouseX = newMouseX;
        mouseY = newMouseY;
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_W){
            forward = true;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_S){
            backward = true;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_A){
            left = true;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_D){
            right = true;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_Q){
            down = true;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_E){
            up = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_W){
            forward = false;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_S){
            backward = false;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_A){
            left = false;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_D){
            right = false;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_Q){
            down = false;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_E){
            up = false;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_1){
            renderer.setRenderFunction(renderer::renderTriangleWireframe);
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_2){
            renderer.setRenderFunction(renderer::renderTriangleGouraud);
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_3){
            renderer.setRenderFunction(renderer::renderTrianglePhong);
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_4){
            renderer.setRenderFunction(renderer::renderTrianglePhong);
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
