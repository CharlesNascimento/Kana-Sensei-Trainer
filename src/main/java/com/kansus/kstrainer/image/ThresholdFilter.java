package com.kansus.kstrainer.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ThresholdFilter implements ImageFilter {

    private int threshold = 210;

    public ThresholdFilter(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage filtered = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color c = new Color(image.getRGB(i, j));
                int mean = (c.getRed() + c.getGreen() + c.getBlue()) / 3;

                if (mean > threshold) {
                    filtered.setRGB(i, j, Color.white.getRGB());
                }
            }
        }

        return filtered;
    }
}