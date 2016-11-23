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
	
	private static int lowerValue = 0;

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

	/**
	 * Generates an array with all the pixels of the image normalized (-1 or 1)
	 * ready to by inputed in the neural network.
	 * 
	 * @param image Image to be normalized.
	 * @param convolveImage Whether the image should be convolved before
	 *            normalization.
	 * @param negative Whether the normalization values (-1 and 1) will be
	 *            inverted.
	 * @return An array with -1s and 1s representing the pixels of the image.
	 */
	public static int[] normalizePixels(BufferedImage image, boolean convolveImage, boolean negative) {
		if (convolveImage) {
			image = convolveImage(image);
		}

		int[] normalizedPixels = image.getRGB(0, 0, 32, 32, null, 0, 32);

		for (int i = 0; i < normalizedPixels.length; i++) {
			Color c = new Color(normalizedPixels[i]);
			int mean = (c.getRed() + c.getGreen() + c.getBlue()) / 3;

			if (mean < 210) {
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
	public static BufferedImage convolveImage(BufferedImage image) {
		float[] filter = new float[] { 1.0f / 121.0f, 2.0f / 121.0f, 3.0f / 121.0f, 2.0f / 121.0f, 1.0f / 121.0f,
		        2.0f / 121.0f, 7.0f / 121.0f, 11.0f / 121.0f, 7.0f / 121.0f, 2.0f / 121.0f, 3.0f / 121.0f,
		        11.0f / 121.0f, 17.0f / 121.0f, 11.0f / 121.0f, 3.0f / 121.0f, 2.0f / 121.0f, 7.0f / 121.0f,
		        11.0f / 121.0f, 7.0f / 121.0f, 2.0f / 121.0f, 1.0f / 121.0f, 2.0f / 121.0f, 3.0f / 121.0f,
		        2.0f / 121.0f, 1.0f / 121.0f };
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
	 * @param strokes List with the strokes (up to 4) to be normalized.
	 * @param negative Whether the normalization values (-1 and 1) will be
	 *            inverted.
	 * @return An array with -1s and 1s representing the given strokes.
	 */
	public static int[] normalizeStrokes(List<String> strokes, boolean negative) {
		int[] normalizedStrokes = new int[32];
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
	 * @param strokes A string with directions separated by a comma.
	 * @param negative Whether the normalization values (-1 and 1) will be
	 *            inverted.
	 * @return An array with -1s and 1s representing the given strokes.
	 */
	public static int[] normalizeStrokes(String strokes, boolean negative) {
		return normalizeStrokes(Arrays.asList(strokes.replace(" ", "").split(",")), negative);
	}
}
