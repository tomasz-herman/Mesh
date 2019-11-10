package com.hermant.gui;

import com.hermant.Main;
import com.hermant.graphics.*;
import com.hermant.graphics.Canvas;
import org.joml.Vector3f;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class Layout {
    private JButton color_texture;
    private JButton color_const;
    private JButton normals_texture;
    private JButton normals_const;
    private JRadioButton slow;
    private JRadioButton hybrid;
    private JRadioButton fast;
    private JSlider kd_slider;
    private JSlider ks_slider;
    private JSlider m_slider;
    private JPanel main;
    private JButton light_color;
    private JRadioButton point;
    private JRadioButton directional;
    private JRadioButton selected;
    private JRadioButton random;
    private JSlider ka_slider;
    private JPanel light_color_panel;
    private JPanel texture_panel;
    private JPanel normals_panel;
    private JPanel canvas_panel;

    private static final int GRAB_TOLERANCE = 8;

    private Scene scene;
    private com.hermant.graphics.Renderer renderer;
    private Canvas canvas;
    private Vector3f lightPos = new Vector3f(Main.LIGHT_RADIUS, Main.LIGHT_HEIGHT, 0);
    private boolean pointLight = true;
    private JFileChooser fileChooser = new JFileChooser();
    private Canvas texture_canvas, normals_canvas;
    private Consumer<String> displayFPS = System.out::println;

    private float ka = 0.3f, ks = 1.0f, kd = 0.7f, m = 30.0f;

    public Layout() {
        fileChooser.setFileFilter(new FileFilter() {

            public String getDescription() {
                return "PNG Images (*.png)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(".png");
                }
            }
        });

        slow.setSelected(true);
        ButtonGroup groupA = new ButtonGroup();
        groupA.add(slow);
        groupA.add(hybrid);
        groupA.add(fast);

        selected.setSelected(true);
        ButtonGroup groupB = new ButtonGroup();
        groupB.add(selected);
        groupB.add(random);

        point.setSelected(true);
        ButtonGroup groupC = new ButtonGroup();
        groupC.add(point);
        groupC.add(directional);

        ka_slider.setValue(30);
        ka_slider.addChangeListener(e -> {
            ka = ka_slider.getValue() / 100.0f;
            if(scene != null) scene.getMesh().setAmbient(ka);
        });

        kd_slider.setValue(70);
        kd_slider.addChangeListener(e -> {
            kd = kd_slider.getValue() / 100.0f;
            if(scene != null) scene.getMesh().setDiffuse(kd);
        });

        ks_slider.setValue(70);
        ks_slider.addChangeListener(e -> {
            ks = ks_slider.getValue() / 100.0f;
            if(scene != null) scene.getMesh().setSpecular(ks);
        });

        m_slider.setMinimum(1);
        m_slider.setValue(30);
        m_slider.addChangeListener(e -> {
            m = (float)m_slider.getValue();
            if(scene != null) scene.getMesh().setSpecularExponent(m);
        });

        random.addActionListener(e -> {
            ka_slider.setEnabled(false);
            kd_slider.setEnabled(false);
            ks_slider.setEnabled(false);
            m_slider.setEnabled(false);
            if(scene != null) scene.getMesh().randomize();
        });

        selected.addActionListener(e -> {
            ka_slider.setEnabled(true);
            kd_slider.setEnabled(true);
            ks_slider.setEnabled(true);
            m_slider.setEnabled(true);
            if(scene != null) {
                scene.getMesh().setAmbient(ka);
                scene.getMesh().setDiffuse(kd);
                scene.getMesh().setSpecular(ks);
                scene.getMesh().setSpecularExponent(m);
            }
        });

        light_color_panel.setBackground(Color.WHITE);
        light_color.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Choose a color", Color.WHITE);
            if(color == null) color = Color.WHITE;
            light_color_panel.setBackground(color);
            scene.getLight().setColor((0.5f + color.getRed()) / 255f, (0.5f + color.getGreen())/ 255f, (0.5f + color.getBlue())/ 255f);
        });

        point.addActionListener(e -> {
            if(scene != null) scene.getLight().setPointLight(pointLight = true);
        });

        directional.addActionListener(e -> {
            if(scene != null) scene.getLight().setPointLight(pointLight = false);
        });

        color_const.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Choose a color", Color.WHITE);
            if(color == null) color = Color.WHITE;
            scene.getMesh().setTexture(new Texture(Main.CANVAS_WIDTH, Main.CANVAS_HEIGHT, new Color3f(color.getRGB())));
            Texture texture = scene.getMesh().getTexture();
            for (int i = 0; i < texture_canvas.getWidth(); i++) {
                for (int j = 0; j < texture_canvas.getHeight(); j++) {
                    texture_canvas.setPixel(i, j, texture.getSampleNearestNeighbor((i + 0.5f)/ texture_canvas.getWidth(), (i + 0.5f)/ texture_canvas.getWidth()));
                }
            }
            texture_canvas.repaint();
        });

        normals_const.addActionListener(e -> {
            scene.getMesh().setNormals(new Texture(Main.CANVAS_WIDTH, Main.CANVAS_HEIGHT, new Color3f(0.5f, 0.5f, 1.0f)));
            Texture texture = scene.getMesh().getNormals();
            for (int i = 0; i < normals_canvas.getWidth(); i++) {
                for (int j = 0; j < normals_canvas.getHeight(); j++) {
                    normals_canvas.setPixel(i, j, texture.getSampleNearestNeighbor((i + 0.5f)/ normals_canvas.getWidth(), (i + 0.5f)/ normals_canvas.getWidth()));
                }
            }
            normals_canvas.repaint();
        });

        color_texture.addActionListener(e -> {
            int returnVal = fileChooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                try {
                    Texture texture = new Texture(file.getPath()).rescale(Main.CANVAS_WIDTH, Main.CANVAS_HEIGHT);
                    for (int i = 0; i < texture_canvas.getWidth(); i++) {
                        for (int j = 0; j < texture_canvas.getHeight(); j++) {
                            texture_canvas.setPixel(i, j, texture.getSampleNearestNeighbor((i + 0.5f)/ texture_canvas.getWidth(), (j + 0.5f)/ texture_canvas.getWidth()));
                        }
                    }
                    texture_canvas.repaint();
                    scene.getMesh().setTexture(texture);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        normals_texture.addActionListener(e -> {
            int returnVal = fileChooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                try {
                    Texture texture = new Texture(file.getPath()).rescale(Main.CANVAS_WIDTH, Main.CANVAS_HEIGHT);
                    for (int i = 0; i < normals_canvas.getWidth(); i++) {
                        for (int j = 0; j < normals_canvas.getHeight(); j++) {
                            normals_canvas.setPixel(i, j, texture.getSampleNearestNeighbor((i + 0.5f)/ normals_canvas.getWidth(), (j + 0.5f)/ normals_canvas.getWidth()));
                        }
                    }
                    normals_canvas.repaint();
                    scene.getMesh().setNormals(texture);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        fast.addActionListener(e -> renderer.setRenderFunction(renderer::renderTriangleFast));
        hybrid.addActionListener(e -> renderer.setRenderFunction(renderer::renderTriangleHybrid));
        slow.addActionListener(e -> renderer.setRenderFunction(renderer::renderTriangle));

    }

    public void setDisplayFPS(Consumer<String> displayFPS) {
        this.displayFPS = displayFPS;
    }

    public void setScene(Scene scene){
        this.scene = scene;
        Texture texture = scene.getMesh().getTexture();
        for (int i = 0; i < texture_canvas.getWidth(); i++) {
            for (int j = 0; j < texture_canvas.getHeight(); j++) {
                texture_canvas.setPixel(i, j, texture.getSampleNearestNeighbor((i + 0.5f)/ texture_canvas.getWidth(), (j + 0.5f)/ texture_canvas.getWidth()));
            }
        }
        texture_canvas.repaint();
        texture = scene.getMesh().getNormals();
        for (int i = 0; i < normals_canvas.getWidth(); i++) {
            for (int j = 0; j < normals_canvas.getHeight(); j++) {
                normals_canvas.setPixel(i, j, texture.getSampleNearestNeighbor((i + 0.5f)/ normals_canvas.getWidth(), (j + 0.5f)/ normals_canvas.getWidth()));
            }
        }
        normals_canvas.repaint();
    }

    public void setRenderer(com.hermant.graphics.Renderer renderer){
        this.renderer = renderer;
    }

    public void setCanvas(Canvas canvas){
        this.canvas = canvas;
        canvas_panel.add(canvas);
    }

    public void initializeTerxtureCanvases(){
        texture_panel.setLayout(new FlowLayout());
        normals_panel.setLayout(new FlowLayout());
        texture_panel.add(texture_canvas = new Canvas(texture_panel.getWidth(), texture_panel.getHeight()));
        normals_panel.add(normals_canvas = new Canvas(normals_panel.getWidth(), normals_panel.getHeight()));
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

    private void update(float delta){
        if(pointLight){
            lightPos.rotateY(delta);
            lightPos.add(Main.CANVAS_WIDTH>>1, 0, Main.CANVAS_HEIGHT>>1);
            scene.getLight().setPosition(lightPos.x, lightPos.y, lightPos.z);
            lightPos.add(-(Main.CANVAS_WIDTH>>1), 0, -Main.CANVAS_HEIGHT>>1);
        } else {
            scene.getLight().setPosition(0, 1,  0);
        }
    }

    public JPanel getMain() {
        return main;
    }

    private class MouseAdapter implements MouseListener, MouseMotionListener, MouseWheelListener {

        private Vertex grabbed = null;

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
            for (Triangle triangle : scene.getMesh().getTriangles()) {
                if(Math.abs(triangle.a.screen.x - e.getX()) < GRAB_TOLERANCE && Math.abs(triangle.a.screen.y - e.getY()) < GRAB_TOLERANCE){
                    grabbed = triangle.a;
                    break;
                }
                if(Math.abs(triangle.b.screen.x - e.getX()) < GRAB_TOLERANCE && Math.abs(triangle.b.screen.y - e.getY()) < GRAB_TOLERANCE){
                    grabbed = triangle.b;
                    break;
                }
                if(Math.abs(triangle.c.screen.x - e.getX()) < GRAB_TOLERANCE && Math.abs(triangle.c.screen.y - e.getY()) < GRAB_TOLERANCE){
                    grabbed = triangle.c;
                    break;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            grabbed = null;
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if(grabbed != null){
                if(e.getX() >= 0 && e.getX() < canvas.getWidth())
                    if(e.getY() >= 0 && e.getY() < canvas.getHeight()) {
                        grabbed.screen.x = e.getX();
                        grabbed.screen.y = e.getY();
                    }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }
    }


}
