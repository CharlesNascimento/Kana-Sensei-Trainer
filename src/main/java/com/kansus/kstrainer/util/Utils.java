package com.kansus.kstrainer.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.kansus.kmlp.core.MultilayerPerceptron;
import com.kansus.kstrainer.model.NeuralNetworkConfig;

/**
 * Class with utility methods.
 *
 * @author Charles Nascimento
 */
public class Utils {

    public static final String currentWorkspace = "E:\\KST";
    public static final String currentNetwork = "E:\\KST";

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

    public static double cosineSimilarity(int[] vectorA, int[] vectorB) {
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
}