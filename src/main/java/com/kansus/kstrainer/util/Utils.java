package com.kansus.kstrainer.util;

import com.kansus.kmlp.core.MultilayerPerceptron;
import com.kansus.kstrainer.model.NeuralNetworkConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class with utility methods.
 *
 * @author Charles Nascimento
 */
public class Utils {

    /**
     * Returns the index of the highest value in the given array.
     *
     * @param array The array.
     * @return The index of the highest value of the array.
     */
    public static int highestValueIndex(double[] array) {
        int highestValueIndex = 0;

        for (int i = 0; i < array.length; i++) {
            if (array[i] > array[highestValueIndex]) {
                highestValueIndex = i;
            }
        }

        return highestValueIndex;
    }

    /**
     * Creates a new neural network based on the given training configuration
     * object.
     *
     * @param neuralNetworkConfig The training configuration object.
     * @return A new neural network.
     */
    public static MultilayerPerceptron createNetworkFromConfig(NeuralNetworkConfig neuralNetworkConfig) {
        int inputNeuronsCount = neuralNetworkConfig.getInputNeuronsCount();
        int hiddenNeuronsCount = neuralNetworkConfig.getHiddenNeuronsCount();
        int outputNeuronsCount = neuralNetworkConfig.getOutputNeuronsCount();

        MultilayerPerceptron network = new MultilayerPerceptron(
                inputNeuronsCount,
                hiddenNeuronsCount,
                outputNeuronsCount
        );

        network.setLearningRate(neuralNetworkConfig.getLearningRate());
        network.setMaxEpochs(neuralNetworkConfig.getMaxEpochs());
        network.setMinimumError(neuralNetworkConfig.getMinimumError());

        if (neuralNetworkConfig.getWeightsFile().exists()) {
            network.loadWeightsFromFile(neuralNetworkConfig.getWeightsFile());
        }

        return network;
    }

    /**
     * Checks whether the specified text ends with any of the valid values.
     *
     * @param text        The text.
     * @param validValues A list of valid values.
     * @return Whether the text ends with any of the valid values.
     */
    public static boolean endsWithAny(String text, ArrayList<String> validValues) {
        for (String s : validValues) {
            if (text.endsWith(s)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether the specified text is equal to any of the valid values.
     *
     * @param text        The text.
     * @param validValues A list of valid values.
     * @return Whether the text is equal to any of the valid values.
     */
    public static boolean equalsAny(String text, ArrayList<String> validValues) {
        for (String value : validValues) {
            if (text.equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Saves an image normalization to a new image file.
     *
     * @param normalization Normalization (array of -1 and 1 values).
     * @param folder        The folder where to save the image.
     * @param fileName      The name of the image file.
     */
    public static void savePixelsNormalizationToFile(double[] normalization, File folder, String fileName) {
        final int IMAGE_WIDTH = (int) Math.sqrt(normalization.length);
        final int IMAGE_HEIGHT = (int) Math.sqrt(normalization.length);
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
        int x = 0, y = 0;

        for (int i = 0; i < normalization.length; i++) {
            if (normalization[i] == 1) {
                image.setRGB(x, y, 0x000000);
            } else {
                image.setRGB(x, y, 0xffffff);
            }

            x++;
            if (x == IMAGE_WIDTH) {
                x = 0;
                y++;
            }
        }

        try {
            ImageIO.write(image, "png", new File(folder, fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a number inside a certain range of values to another range,
     * while keeping the ratio.
     *
     * @param value  The number to be converted.
     * @param oldMin The minimum value of the old range.
     * @param oldMax The maximum value of the old range.
     * @param newMin The minimum value of the new range.
     * @param newMax The minimum value of the new range.
     * @return A number in the new range.
     */
    public static double rangeToRange(double value, double oldMin, double oldMax, double newMin, double newMax) {
        return ((value - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
    }

    /**
     * Returns the extension of the given file.
     *
     * @param file The file.
     * @return The extension of the file.
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();

        int i = fileName.lastIndexOf('.');

        if (i > 0) {
            return fileName.substring(i + 1);
        } else {
            return "";
        }
    }

    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    /**
     * scale image
     *
     * @param sbi       image to scale
     * @param imageType type of image
     * @param dWidth    width of destination image
     * @param dHeight   height of destination image
     * @param fWidth    x-factor for transformation / scaling
     * @param fHeight   y-factor for transformation / scaling
     * @return scaled image
     */
    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();

            g.setPaint(Color.white);
            g.fillRect(0, 0, dWidth, dHeight);

            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);

            double x = dWidth / 2 - sbi.getWidth() * fWidth / 2;
            double y = dHeight / 2 - sbi.getHeight() * fHeight / 2;

            at.translate(x, y);
            g.drawImage(sbi, at, null);
        }
        return dbi;
    }

    public static double[] concat(double[] array1, double[] array2) {
        double[] c = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, c, 0, array1.length);
        System.arraycopy(array2, 0, c, array1.length, array2.length);
        return c;
    }

    public static double[] addRedundancyTo(double[] input, int level) {
        double[] output = new double[input.length * (level + 1)];

        for (int i = 0; i < input.length; i++) {
            int start = i * (level + 1);
            int end = start + level + 1;

            for (int j = start; j < end; j++) {
                output[j] = input[i];
            }
        }

        return output;
    }

    public static void main(String[] args) {
        double[] input = {1, 2, 3, 4, 5, 6, 7, 8};
        input = addRedundancyTo(input, 3);

        for (double d : input) {
            System.out.println(d);
        }
    }
}