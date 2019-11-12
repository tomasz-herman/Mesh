package com.hermant.gui;

import javax.swing.*;
import java.util.Objects;

public class ResolutionChooserForm {
    private JButton start;
    private JComboBox<ComboItem> resolutions;
    private JPanel main;

    private ResolutionChooser chooser;

    public int width = 768;
    public int height = 768;

    private String[] res = new String[]{
            "160 x 160", "240 x 160", "240 x 240", "240 x 240", "320 x 240", "320 x 320", "480 x 320",
            "480 x 480", "640 x 480", "720 x 480", "600 x 600", "800 x 600", "960 x 600", "720 x 720",
            "960 x 720", "1280 x 720", "768 x 768", "1024 x 768", "1360 x 768", "800 x 800",
            "1280 x 800", "900 x 900", "1200 x 900", "1440 x 900", "1600 x 900", "1024 x 1024",
            "1280 x 1024", "1360 x 1024", "1050 x 1050", "1680 x 1050", "1080 x 1080", "1440 x 1080",
            "1920 x 1080", "1152 x 1152", "1536 x 1152", "2048 x 1152", "1536 x 1152", "1200 x 1200",
            "1600 x 1200", "1920 x 1200", "1440 x 1440", "1920 x 1440", "2560 x 1440", "3440 x 1440",
            "2160 x 2160", "2880 x 2160", "3840 x 2160", "2400 x 2400", "3200 x 2400", "3840 x 2400"
    };

    public ResolutionChooserForm(){
        start.addActionListener(e -> chooser.dispose());
        for (int i = 0; i < res.length; i++) {
            resolutions.addItem(new ComboItem(i, res[i]));
        }
        resolutions.addActionListener(e -> {
            String text = Objects.requireNonNull(resolutions.getSelectedItem()).toString();
            var tokens = text.split(" ");
            width = Integer.parseInt(tokens[0]);
            height = Integer.parseInt(tokens[2]);
        });
        resolutions.setSelectedIndex(16);
    }

    public JPanel getMain() {
        return main;
    }

    public void setChooser(ResolutionChooser chooser) {
        this.chooser = chooser;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static class ComboItem {

        private final int value;
        private final String label;

        public ComboItem(int value, String label) {
            this.value = value;
            this.label = label;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
