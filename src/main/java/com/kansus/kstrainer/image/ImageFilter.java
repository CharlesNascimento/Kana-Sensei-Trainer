package com.kansus.kstrainer.image;

import java.awt.image.BufferedImage;

public interface ImageFilter {

    BufferedImage apply(BufferedImage image);
}
