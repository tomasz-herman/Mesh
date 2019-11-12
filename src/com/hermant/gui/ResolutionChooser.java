package com.hermant.gui;

import com.hermant.Main;

import javax.swing.*;
import java.awt.*;

public class ResolutionChooser extends JFrame {

    ResolutionChooserForm layout;

    public ResolutionChooser(){
        layout = new ResolutionChooserForm();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setContentPane(layout.getMain());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Choose resolution");
        setSize(new Dimension(320, 144));
        setMinimumSize(new Dimension(320, 144));
        setResizable(false);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
        layout.setChooser(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        Main.CANVAS_WIDTH = layout.width;
        Main.CANVAS_HEIGHT = layout.height;
        synchronized (Main.mutex){
            Main.mutex.notify();
        }
    }
}
