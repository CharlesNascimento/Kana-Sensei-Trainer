package com.kansus.kstrainer;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.kansus.kstrainer.model.TrainingConfig;
import com.kansus.kstrainer.util.FileUtils;
import com.kansus.kstrainer.util.Log;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

/**
 * Main class of the application.
 * 
 * @author Charles Nascimento
 */
public class Main {

	/**
	 * Entry point of the application. The following commands and arguments are
	 * supported:<br>
	 * <br>
	 * <b>train &ltneural-network&gt &ltconfig-file&gt</b><br>
	 * <b>evaluate pixels &ltweights-file&gt &ltcharacter-file&gt</b><br>
	 * <b>evaluate strokes &ltweights-file&gt&ltstrokes&gt</b><br>
	 * <b>evaluate-network pixels &lnetwork-config-file&gt</b><br>
	 * <br>
	 * Examples:<br>
	 * <br>
	 * <b>train pixels config.json</b><br>
	 * <b>train strokes config.json</b><br>
	 * <b>evaluate pixels pixels.mlp test.jpg</b><br>
	 * <b>evaluate strokes strokes.mlp e,s,s</b>
	 * <b>evaluate-network pixels config.json</b><br>
	 * 
	 * @param args Application arguments.
	 */
	public static void main(String[] args) {
		switch (args[0]) {
		case "train":
			if (validateTrainArgs(args)) {
				try {
					File configFile = new File(args[2]);
					TrainingConfig trainingConfig = FileUtils.loadTrainingConfiguration(configFile);
					NeuralNetworkFacade trainer = new NeuralNetworkFacade();
					
					FileWriter fw = new FileWriter(new File(configFile.getParent(), "log.txt"));
					Log.setWriter(new BufferedWriter(fw));

					if (args[1].equals("pixels")) {
						trainer.trainPixelsNetwork(trainingConfig);
					} else if (args[1].equals("strokes")) {
						trainer.trainStrokesNetwork(trainingConfig);
					}
					
					Log.closeWriter();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case "evaluate":
			if (validateEvaluateArgs(args)) {
				if (args[1].equals("pixels")) {
					File pixelsWightsFile = new File(args[2]);
					File characterFile = new File(args[3]);

					try {
						NeuralNetworkFacade trainer = new NeuralNetworkFacade();
						BufferedImage characterImage = ImageIO.read(characterFile);
						trainer.evaluate(pixelsWightsFile, characterImage, true, false);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					File strokesWeightsFile = new File(args[2]);
					List<String> strokes = Arrays.asList(args[3].split(","));
					NeuralNetworkFacade trainer = new NeuralNetworkFacade();
					trainer.evaluate(strokesWeightsFile, strokes);
				}
			}
			break;
		case "evaluate-network":
			if (validateEvaluateNetworkArgs(args)) {
				if (args[1].equals("pixels")) {
					File configFile = new File(args[2]);
					TrainingConfig trainingConfig = FileUtils.loadTrainingConfiguration(configFile);

					NeuralNetworkFacade neuralNetworkFacade = new NeuralNetworkFacade();
					neuralNetworkFacade.evaluatePixelsNetwork(trainingConfig);
				} else if (args[1].equals("strokes")) {
					File configFile = new File(args[2]);
					TrainingConfig trainingConfig = FileUtils.loadTrainingConfiguration(configFile);

					NeuralNetworkFacade neuralNetworkFacade = new NeuralNetworkFacade();
					neuralNetworkFacade.evaluateStrokesNetwork(trainingConfig);
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
	private static boolean validateTrainArgs(String[] args) {
		if (args.length != 3) {
			System.err.println("<ERROR>   Incorrect number of parameters.");
			System.err.println("<INFO>    Command syntax: train <neural-network> <config-file>");
			return false;
		}

		if (args.length == 3 && !Utils.equalsAny(args[1], ValidationUtils.validNeuralNetworks)) {
			System.err.println("<ERROR>   Invalid neural network: " + args[1]);
			return false;
		}

		return true;
	}

	/**
	 * @param args
	 * @return
	 */
	private static boolean validateEvaluateArgs(String[] args) {
		if (args.length != 4) {
			System.err.println("<ERROR>   Incorrect number of parameters.");
			System.err.println("<INFO>    Command syntax: evaluate <training-config-file>");
			return false;
		}

		if (!Utils.equalsAny(args[1], ValidationUtils.validNeuralNetworks)) {
			System.err.println("<ERROR>   Invalid neural network: " + args[1]);
			return false;
		}

		return true;
	}
	
	/**
	 * @param args
	 * @return
	 */
	private static boolean validateEvaluateNetworkArgs(String[] args) {
		if (args.length != 3) {
			System.err.println("<ERROR>   Incorrect number of parameters.");
			System.err.println("<INFO>    Command syntax: evaluate-network pixels <network-config-file>");
			return false;
		}

		if (!Utils.equalsAny(args[1], ValidationUtils.validNeuralNetworks)) {
			System.err.println("<ERROR>   Invalid neural network: " + args[1]);
			return false;
		}

		return true;
	}

	/**
	 * @param args
	 * @return
	 */
	private static boolean validateEvaluatePixelsArgs(File pixelsWeightsFile, File characterFile) {
		if (!characterFile.exists()) {
			System.out.println("<ERROR>   Input image file not found.");
			return false;
		}

		if (!Utils.endsWithAny(characterFile.getName(), ValidationUtils.validFormats)) {
			System.err.println("<ERROR>   " + characterFile.getName()
			        + " is not a supported image file. It should be an image in any of these formats:"
			        + " BMP, JPG, PNG or GIF.");
			return false;
		}

		if (!pixelsWeightsFile.exists()) {
			System.out.println("<WARNING> Weights file not found.");
			return false;
		}

		if (!pixelsWeightsFile.getName().endsWith("mlp")) {
			System.err.println("<ERROR>   The weights file should be a valid .mlp file.");
			return false;
		}

		return true;
	}

	/**
	 * @param strokesWeightsFile
	 * @param strokes
	 * @return
	 */
	private static boolean validateEvaluateStrokesArgs(File strokesWeightsFile, List<String> strokes) {
		if (!strokesWeightsFile.exists()) {
			System.out.println("<WARNING> Weights file not found.");
			return false;
		}

		if (!strokesWeightsFile.getName().endsWith("mlp")) {
			System.err.println("<ERROR>   The weights file should be a valid .mlp file.");
			return false;
		}

		for (String stroke : strokes) {
			if (!Utils.equalsAny(stroke, ValidationUtils.validDirections)) {
				System.err.println("<ERROR>   Invalid stroke direction: " + stroke);
				return false;
			}
		}

		return true;
	}

	/**
	 * @param trainingConfig
	 * @return
	 */
	private static boolean validateTrainingsConfigs(TrainingConfig trainingConfig) {
		boolean showedWarning = false;

		if (!trainingConfig.getWeightsFile().exists() && !showedWarning) {
			System.out.println("<WARNING> Weights file not found, a new file will be created.");
		}

		if (!trainingConfig.getWeightsFile().getName().endsWith("mlp")) {
			System.err.println("<ERROR>   The weights file should be a valid .mlp file.");
			return false;
		}

		for (File trainingInput : trainingConfig.getInputs()) {
			if (!trainingInput.exists()) {
				System.out.println("<ERROR>   Input file not found.");
				return false;
			}
		}

		return true;
	}
}