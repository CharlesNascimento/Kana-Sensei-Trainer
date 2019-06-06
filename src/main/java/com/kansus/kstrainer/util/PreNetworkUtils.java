package com.kansus.kstrainer.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Class with methods that prepare input values before passing them to the
 * neural network.
 *
 * @author Charles Nascimento
 */
public class PreNetworkUtils {

    private static HashMap<String, Integer> directionsRepresentations = new HashMap<>();

    public static byte lowerValue = -1;

    static {
        directionsRepresentations.put("n", 0);
        directionsRepresentations.put("ne", 1);
        directionsRepresentations.put("e", 2);
        directionsRepresentations.put("se", 3);
        directionsRepresentations.put("s", 4);
        directionsRepresentations.put("sw", 5);
        directionsRepresentations.put("w", 6);
        directionsRepresentations.put("nw", 7);
    }

    private static int[] getPixels(BufferedImage image) {
        int pixels[] = new int[image.getWidth() * image.getHeight()];

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int number = image.getRGB(i, j);
                pixels[i * image.getWidth() + j] = number;
            }
        }

        return pixels;
    }

    /**
     * Generates an array with all the pixels of the image normalized (-1 or 1)
     * ready to by inputted in the neural network.
     *
     * @param image         Image to be normalized.
     * @param convolveImage Whether the image should be convolved before
     *                      normalization.
     * @param negative      Whether the normalization values (-1 and 1) will be
     *                      inverted.
     * @return An array with -1s and 1s representing the pixels of the image.
     */
    public static double[] normalizePixels(BufferedImage image, boolean convolveImage, boolean negative) {
        if (convolveImage) {
            image = convolveImage(image);
        }

        int[] inputPixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        double[] normalizedPixels = new double[inputPixels.length];


        for (int i = 0; i < inputPixels.length; i++) {
            Color c = new Color(inputPixels[i]);
            int mean = (c.getRed() + c.getGreen() + c.getBlue()) / 3;

            if (mean < 200) {
                normalizedPixels[i] = negative ? lowerValue : 1;
            } else {
                normalizedPixels[i] = negative ? 1 : lowerValue;
            }
        }

        return normalizedPixels;
    }

    /**
     * Applies a convolution filter to the specified image.
     *
     * @param image The image to be filtered.
     * @return The convolved image.
     */
    private static BufferedImage convolveImage(BufferedImage image) {
        float[] filter = new float[]{1.0f / 121.0f, 2.0f / 121.0f, 3.0f / 121.0f, 2.0f / 121.0f, 1.0f / 121.0f,
                2.0f / 121.0f, 7.0f / 121.0f, 11.0f / 121.0f, 7.0f / 121.0f, 2.0f / 121.0f, 3.0f / 121.0f,
                11.0f / 121.0f, 17.0f / 121.0f, 11.0f / 121.0f, 3.0f / 121.0f, 2.0f / 121.0f, 7.0f / 121.0f,
                11.0f / 121.0f, 7.0f / 121.0f, 2.0f / 121.0f, 1.0f / 121.0f, 2.0f / 121.0f, 3.0f / 121.0f,
                2.0f / 121.0f, 1.0f / 121.0f};
        int kernelWidth = 5;
        int kernelHeight = 5;
        Kernel kernel = new Kernel(kernelWidth, kernelHeight, filter);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        // bufImg = op.filter(bufImg, null);
        return op.filter(image, null);
    }

    /**
     * Generates an array with the normalization (-1 or 1) of the given strokes.
     *
     * @param strokes  List with the strokes (up to 4) to be normalized.
     * @param negative Whether the normalization values (-1 and 1) will be
     *                 inverted.
     * @return An array with -1s and 1s representing the given strokes.
     */
    public static double[] normalizeStrokes(List<String> strokes, boolean negative) {
        double[] normalizedStrokes = new double[32];
        Arrays.fill(normalizedStrokes, negative ? 1 : lowerValue);

        for (int i = 0; i < strokes.size(); i++) {
            int position = (i * 8) + directionsRepresentations.get(strokes.get(i));
            normalizedStrokes[position] = negative ? lowerValue : 1;
        }

        return normalizedStrokes;
    }

    /**
     * Generates an array with the normalization (-1 or 1) of the given strokes.
     *
     * @param strokes  A string with directions separated by a comma.
     * @param negative Whether the normalization values (-1 and 1) will be
     *                 inverted.
     * @return An array with -1s and 1s representing the given strokes.
     */
    public static double[] normalizeStrokes(String strokes, boolean negative) {
        return normalizeStrokes(Arrays.asList(strokes.replace(" ", "").split(",")), negative);
    }
}
