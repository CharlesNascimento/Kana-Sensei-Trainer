package org.kansus.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * Main class of the application.
 * 
 * @author Charles Nascimento
 */
public class Main {

	private static ArrayList<String> validFormats = new ArrayList<>();
	private static ArrayList<String> validCharFamilies = new ArrayList<>();

	static {
		validFormats.add("bmp");
		validFormats.add("jpg");
		validFormats.add("png");
		validFormats.add("gif");

		validCharFamilies.add("x");
		validCharFamilies.add("k");
		validCharFamilies.add("t");
		validCharFamilies.add("s");
		validCharFamilies.add("n");
		validCharFamilies.add("h");
		validCharFamilies.add("m");
		validCharFamilies.add("y");
		validCharFamilies.add("r");
		validCharFamilies.add("wn");
	}

	/**
	 * train <traning-config-file>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (validateArgs(args)) {
			TrainingConfig training = Utils.loadTrainingConfiguration(new File(args[0]));
			Trainer trainer = new Trainer();
			//trainer.train(training);

			try {
				BufferedImage character = ImageIO.read(new File("extracted\\char_2.png"));
				File weightsFile = new File("pixels.mlp");
				trainer.evaluateCharacter(weightsFile, character);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 * @return
	 */
	public static boolean validateArgs(String[] args) {
		if (args.length != 1) {
			System.err.println("<ERROR>   Incorrect number of parameters.");
			System.err.println("<INFO>    Command syntax: train <training-config-file>");
			return false;
		}

		File trainingConfigFile = new File(args[0]);
		if (!trainingConfigFile.exists()) {
			System.err.println("<ERROR>   Training configuration file not found.");
		}

		return true;
	}

	/**
	 * @param trainingConfig
	 * @return
	 */
	public static boolean validateTrainingsConfigs(TrainingConfig trainingConfig) {
		boolean showedWarning = false;

		if (!trainingConfig.getWeightsFile().exists() && !showedWarning) {
			System.out.println("<WARNING> Weights file not found, a new file will be created.");
		}

		if (!trainingConfig.getWeightsFile().getName().endsWith("mlp")) {
			System.err.println("<ERROR>   The weights file should be a valid .mlp file.");
			return false;
		}

		for (TrainingInput trainingInput : trainingConfig.getInputs()) {
			if (!trainingInput.getInputImage().exists()) {
				System.out.println("<ERROR>   Input image file not found.");
				return false;
			}

			if (!endsWithAny(trainingInput.getInputImage().getName(), validFormats)) {
				System.err.println("<ERROR>   " + trainingInput.getInputImage().getName()
				        + " is not a supported image file. It should be an image in any of these formats:"
				        + " BMP, JPG, PNG or GIF.");
				return false;
			}

			if (!equalsAny(trainingInput.getCharFamily(), validCharFamilies)) {
				System.err.println("<ERROR>   " + trainingInput.getCharFamily() + " is not a valid char family."
				        + " Valid character families are " + "x, k, t, s, n, h, m, y, r and wn.");
				return false;
			}
		}

		return true;
	}

	/**
	 * @param text
	 * @param validValues
	 * @return
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
	 * @param text
	 * @param validValues
	 * @return
	 */
	public static boolean equalsAny(String text, ArrayList<String> validValues) {
		for (String value : validValues) {
			if (text.equalsIgnoreCase(value)) {
				return true;
			}
		}

		return false;
	}
}