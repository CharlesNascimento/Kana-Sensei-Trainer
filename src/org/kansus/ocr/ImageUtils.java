package org.kansus.ocr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 */
public class ImageUtils {

    public static Image scaleBitmap(BufferedImage bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > height) {
            int ratio = width / maxWidth;
            width = maxWidth;
            height = height / ratio;
        } else if (height > width) {
            int ratio = height / maxHeight;
            height = maxHeight;
            width = width / ratio;
        } else {
            height = maxHeight;
            width = maxWidth;
        }

        return bitmap.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public static BufferedImage makeSquareBitmap(Image bitmap, int bitmapDimens) {
    	BufferedImage squareBitmap = new BufferedImage(bitmapDimens, bitmapDimens, BufferedImage.TYPE_INT_RGB);
        Graphics canvas = squareBitmap.getGraphics();

        int centerLeft = (bitmapDimens - bitmap.getWidth(null)) / 2;
        int centerTop = (bitmapDimens - bitmap.getHeight(null)) / 2;

        //canvas.drawColor(Color.WHITE);
        canvas.drawImage(bitmap, centerLeft, centerTop, Color.BLACK, null);

        return squareBitmap;
    }
}
