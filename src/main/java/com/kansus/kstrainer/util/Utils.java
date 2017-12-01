package com.kansus.kstrainer.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.kansus.kstrainer.mlp.MultilayerPerceptron;
import com.kansus.kstrainer.model.TrainingConfig;

/**
 * Class with utility methods.
 * 
 * @author Charles Nascimento
 */
public class Utils {

	/**
	 * Returns the highest value in the given array.
	 * 
	 * @param array The array.
	 * @return The highest value.
	 */
	public static double highestValue(double[] array) {
		double highestValue = -1;

		for (int i = 0; i < array.length; i++) {
			if (array[i] > highestValue) {
				highestValue = array[i];
			}
		}

		return highestValue;
	}

	/**
	 * Creates a new neural network based on the given training configuration
	 * object.
	 * 
	 * @param trainingConfig The training configuration object.
	 * @return A new neural network.
	 */
	public static MultilayerPerceptron createNetworkFromConfig(TrainingConfig trainingConfig) {
		int inputNeuronsCount = trainingConfig.getInputNeuronsCount();
		int hiddenNeuronsCount = trainingConfig.getHiddenNeuronsCount();
		int outputNeuronsCount = trainingConfig.getOutputNeuronsCount();

		MultilayerPerceptron network = new MultilayerPerceptron(inputNeuronsCount, hiddenNeuronsCount,
		        outputNeuronsCount);
		network.setLearningRate(trainingConfig.getLearningRate());
		network.setMaxEpochs(trainingConfig.getMaxEpochs());
		network.setMinimumError(trainingConfig.getMinimumError());

		return network;
	}

	/**
	 * Checks whether the specified text ends with any of the valid values.
	 * 
	 * @param text The text.
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
	 * @param text The text.
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
	 * @param folder The folder where to save the image.
	 * @param fileName The name of the image file.
	 */
	public static void savePixelsNormalizationToFile(int[] normalization, File folder, String fileName) {
		final int IMAGE_WIDTH = 32;
		final int IMAGE_HEIGHT = 32;
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
	 * @param value The number to be converted.
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
}