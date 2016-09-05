package org.kansus.ocr;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Classe com métodos reponsáveis por tratar uma imagem antes dela ser passada à
 * rede neural.
 * 
 * @author Charles Nascimento
 */
public class PreNetwork {

	private static HashMap<String, Integer> directionsRepresentations = new HashMap<>();

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
	 * @return An array with -1s and 1s representing the pixels of the image.
	 */
	public static int[] normalizePixels(BufferedImage image) {
		int[] normalizedPixels = new int[image.getWidth() * image.getHeight()];
		int k = 0;

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				if (image.getRGB(x, y) == 0xFFFFFFFF || image.getRGB(x, y) == 0xFFFFFF) {
					normalizedPixels[k++] = -1;
				} else {
					normalizedPixels[k++] = 1;
				}
			}
		}

		return normalizedPixels;
	}

	/**
	 * Generates an array with the normalization (-1 or 1) of the given strokes.
	 * 
	 * @param strokes List with the strokes (up to 4) to be normalized.
	 * @return An array with -1s and 1s representing the given strokes.
	 */
	public static int[] normalizeStrokes(ArrayList<String> strokes) {
		int[] normalizedStrokes = new int[32];
		Arrays.fill(normalizedStrokes, -1);

		for (int i = 0; i < normalizedStrokes.length; i++) {
			int position = (i * 8) + directionsRepresentations.get(strokes.get(i));
			normalizedStrokes[position] = 1;
		}

		return normalizedStrokes;
	}
}
