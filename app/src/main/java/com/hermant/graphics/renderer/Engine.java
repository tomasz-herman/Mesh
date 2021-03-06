package com.hermant.graphics.renderer;

import com.hermant.graphics.cameras.Camera;
import com.hermant.graphics.scene.Scene;
import org.joml.Vector4f;

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
        setDisplayFPS(canvas::setInfo);
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
                displayFPS.accept(frames + " fps, " + updates + " ups, " + currentShadingFunction);
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

        var stone = scene.getGameObjects().get(2);
        var position = stone.getPosition();
        position.rotateAxis(delta, 0, 1, 0);
        stone.setRotation(stone.getRotation().x, stone.getRotation().y - (float)Math.toDegrees(delta), stone.getRotation().z);

        var spotlight = scene.getLightSetup().getSpotLights().get(0);
        var direction = spotlight.getDirection();
        var lightPosition = spotlight.getPosition();
        var newDirection = new Vector4f(
                lightPosition.x - position.x,
                lightPosition.y - position.y,
                lightPosition.z - position.z,
                0.0f).normalize();
        direction.set(newDirection);

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
        if(roll){
            camera.rotate(0, 0, -delta * 20);
        }
        if(llor){
            camera.rotate(0, 0, delta * 20);
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
    private boolean roll = false;
    private boolean llor = false;

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
    public void mouseMoved(MouseEvent mouseEvent) { }

    @Override
    public void keyTyped(KeyEvent keyEvent) { }

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
        else if(keyEvent.getKeyCode() == KeyEvent.VK_Z){
            roll = true;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_X){
            llor = true;
        }

    }

    private String currentShadingFunction = "Phong";
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
        else if(keyEvent.getKeyCode() == KeyEvent.VK_Z){
            roll = false;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_X){
            llor = false;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_1){
            renderer.setRenderFunction(renderer::renderTriangleWireframe);
            currentShadingFunction = "Wireframe";
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_2){
            renderer.setRenderFunction(renderer::renderTriangleSuperFlat);
            currentShadingFunction = "SuperFlat";
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_3){
            renderer.setRenderFunction(renderer::renderTriangleFlat);
            currentShadingFunction = "Flat";
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_4){
            renderer.setRenderFunction(renderer::renderTriangleGouraud);
            currentShadingFunction = "Gouraud";
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_5){
            renderer.setRenderFunction(renderer::renderTrianglePhong);
            currentShadingFunction = "Phong";
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_6){
            renderer.setRenderFunction(renderer::renderTrianglePhongSpecularPhong);
            currentShadingFunction = "Phong + specular";
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_C){
            scene.changeCamera();
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
    public void mouseReleased(MouseEvent mouseEvent) { }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) { }

    @Override
    public void mouseExited(MouseEvent mouseEvent) { }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int rotation = e.getWheelRotation();
        float fov = renderer.getFov();
        fov -= (float) Math.toRadians(rotation);
        if(fov < (float) Math.toRadians(10f))fov = (float) Math.toRadians(10f);
        if(fov > (float) Math.toRadians(175f))fov = (float) Math.toRadians(175f);
        renderer.setFov(fov);
    }
}
