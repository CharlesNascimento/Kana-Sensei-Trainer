package com.kansus.kstrainer;

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
	 * Entry point of the application. The following commands and arguments are
	 * supported: <br>
	 * <br>
	 * <b>train >traning-config-file<</b> <br>
	 * <b>evaluate >character-file< >weights-file<</b> <br>
	 * <br>
	 * Examples: <br>
	 * <br>
	 * <b>train training.config</b> <br>
	 * <b>evaluate extracted\char_1.png pixels.mlp</b>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		switch (args[0]) {
		case "train":
			File trainingConfigFile = new File(args[1]);
			if (validateTrainArgs(args.length, trainingConfigFile)) {
				TrainingConfig training = Utils.loadTrainingConfiguration(trainingConfigFile);
				Trainer trainer = new Trainer();
				trainer.train(training);
			}
			break;
		case "evaluate":
			File characterFile = new File(args[1]);
			File weightsFile = new File(args[2]);

			if (validateEvaluateArgs(args.length, characterFile, weightsFile)) {
				try {
					Trainer trainer = new Trainer();
					BufferedImage characterImage = ImageIO.read(characterFile);
					trainer.evaluateCharacter(weightsFile, characterImage);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			System.err.println("<ERROR>   Invalid command.");
			break;
		}

	}

	/**
	 * @param args
	 * @return
	 */
	public static boolean validateTrainArgs(int argsCount, File trainingConfigFile) {
		if (argsCount != 2) {
			System.err.println("<ERROR>   Incorrect number of parameters.");
			System.err.println("<INFO>    Command syntax: train <training-config-file>");
			return false;
		}

		if (!trainingConfigFile.exists()) {
			System.err.println("<ERROR>   Training configuration file not found.");
		}

		return true;
	}

	/**
	 * @param args
	 * @return
	 */
	public static boolean validateEvaluateArgs(int argsCount, File characterFile, File weightsFile) {
		if (argsCount != 3) {
			System.err.println("<ERROR>   Incorrect number of parameters.");
			System.err.println("<INFO>    Command syntax: train <training-config-file>");
			return false;
		}

		if (!characterFile.exists()) {
			System.out.println("<ERROR>   Input image file not found.");
			return false;
		}

		if (!endsWithAny(characterFile.getName(), validFormats)) {
			System.err.println("<ERROR>   " + characterFile.getName()
			        + " is not a supported image file. It should be an image in any of these formats:"
			        + " BMP, JPG, PNG or GIF.");
			return false;
		}

		if (!weightsFile.exists()) {
			System.out.println("<WARNING> Weights file not found, a new file will be created.");
		}

		if (!weightsFile.getName().endsWith("mlp")) {
			System.err.println("<ERROR>   The weights file should be a valid .mlp file.");
			return false;
		}

		return true;
	}

	/**
	 * @param trainingConfig
	 * @return
	 */
	public static boolean validateTrainingsConfigs(TrainingConfig trainingConfig) {
		boolean showedWarning = false;

		if (!trainingConfig.getPixelsWeightsFile().exists() && !showedWarning) {
			System.out.println("<WARNING> Weights file not found, a new file will be created.");
		}

		if (!trainingConfig.getPixelsWeightsFile().getName().endsWith("mlp")) {
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