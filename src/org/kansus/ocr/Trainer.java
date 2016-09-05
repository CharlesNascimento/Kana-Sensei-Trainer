package org.kansus.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.kansus.ocr.scanner.CharacterExtractor;

import com.kansus.mlp.MultilayerPerceptron;
import com.kansus.mlp.NetworkTrainingProgressListener;

public class Trainer implements CharacterExtractionListener, NetworkTrainingProgressListener {

	private ArrayList<BufferedImage> extractedCharacters;

	private int extractedCharactersCount;

	private HashMap<String, HashMap<String, ArrayList<Character>>> characters;

	private MultilayerPerceptron pixelsNeuralNetwork;

	private MultilayerPerceptron strokesNeuralNetwork;

	private int currentTrainingIndex;

	private boolean saveExtractedCharsToFile;

	public Trainer() {
		this.characters = Utils.loadCharactersFile();
		pixelsNeuralNetwork = new MultilayerPerceptron(1024, 128, 92);
		strokesNeuralNetwork = new MultilayerPerceptron(32, 16, 92);
	}

	/**
	 * Para cada treinamento, extrair os caracteres da imagem informada; Para
	 * cada caractere extraído da imagem, gerar sua representação, definir a
	 * saída desejada e adicionar o padrão à rede neural; Finalmente, treinar a
	 * rede neural e salvar os pesos da mesma no arquivo de pesos informado.
	 * 
	 * @param training
	 */
	public void train(TrainingConfig training) {
		System.out.println("<INFO>    Starting session of " + training.getInputs().size() + " trainings.");
		long startTime = System.currentTimeMillis();
		CharacterExtractor slicer = new CharacterExtractor();

		ArrayList<TrainingInput> inputs = training.getInputs();
		loadWeightsFile(pixelsNeuralNetwork, training.getWeightsFile());
		// loadWeightsFile(strokesNeuralNetwork, training.getWeightsFile());

		for (int i = 0; i < inputs.size(); i++) {
			currentTrainingIndex = i + 1;
			extractedCharacters = new ArrayList<>();

			TrainingInput currentInput = inputs.get(i);
			saveExtractedCharsToFile = currentInput.isSaveExtractedCharactersToFile();
			slicer.slice(currentInput.getInputImage(), 32, 32, this);
			ArrayList<Character> chars = characters.get(currentInput.getAlphabet()).get(currentInput.getCharFamily());

			for (int j = 0; j < chars.size(); j++) {
				int[] input = PreNetwork.normalizePixels(extractedCharacters.get(j));

				int[] expectedOutput = new int[92];
				Arrays.fill(expectedOutput, -1);
				expectedOutput[chars.get(j).getId() - 1] = 1;

				pixelsNeuralNetwork.addPattern(chars.get(j).getCharacter(), input, expectedOutput);
			}
		}

		pixelsNeuralNetwork.train(this);
		saveWeightsFile(pixelsNeuralNetwork, training.getWeightsFile());
		// saveWeightsFile(strokesNeuralNetwork, training.getWeightsFile());

		double totalTime = System.currentTimeMillis() - startTime;
		System.out.println("<INFO>    Training finished in " + totalTime / 1000 + " seconds");

		int[] input = PreNetwork.normalizePixels(extractedCharacters.get(0));
		double[] output = pixelsNeuralNetwork.evaluate(input);

		System.out.println("<INFO>    Test result: " + outputToCharacter(output));
		for (int i = 0; i < output.length; i++) {
			System.out.println("Output[" + i + "] = " + output[i]);
		}
	}

	private String outputToCharacter(double[] output) {
		int correctOutput = -1;

		for (int i = 0; i < output.length; i++) {
			if (output[i] == 1) {
				correctOutput = i;
				break;
			}
		}

		for (HashMap<String, ArrayList<Character>> families : characters.values()) {
			for (ArrayList<Character> characters : families.values()) {
				for (Character character : characters) {
					if (correctOutput == character.getId() - 1) {
						return character.getCharacter();
					}
				}
			}
		}

		return null;
	}

	public void evaluateCharacter(File weightsFile, BufferedImage charImage) {
		pixelsNeuralNetwork.loadWeightsFromFile(weightsFile);

		int[] input = PreNetwork.normalizePixels(charImage);
		double[] output = pixelsNeuralNetwork.evaluate(input);
		for (int i = 0; i < output.length; i++) {
			System.out.println("Output[" + i + "] = " + output[i]);
		}
		System.out.println("<INFO>    Test result: " + outputToCharacter(output));
	}

	private void loadWeightsFile(MultilayerPerceptron network, File weightsFile) {
		if (!weightsFile.exists()) {
			try {
				weightsFile.createNewFile();
				String currentTrainingWeightsFile = weightsFile.getPath();
				System.out.println("<INFO>    Weights file \"" + currentTrainingWeightsFile + "\" created.");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("<ERROR>   " + e.getMessage());
			}
		} else {
			if (weightsFile.length() != 0) {
				String currentTrainingWeightsFile = weightsFile.getPath();
				network.loadWeightsFromFile(weightsFile);
				System.out.println("<INFO>    Weights file \"" + currentTrainingWeightsFile + "\" loaded.");
			}
		}
	}

	private void saveWeightsFile(MultilayerPerceptron network, File weightsFile) {
		pixelsNeuralNetwork.saveWeightsToFile(weightsFile);
		System.out.println("<INFO>    Weights file \"" + weightsFile.getPath() + "\" saved.");
	}

	@Override
	public void onCharacterExtractionProgressChanged(String message, int progress) {
		System.out.println("<INFO>    Character extraction progress: " + progress + "% | " + message);
	}

	@Override
	public void onCharacterExtracted(BufferedImage image) {
		try {
			extractedCharacters.add(image);

			File extractedCharsDir = new File("extracted");
			if (saveExtractedCharsToFile) {
				if (!extractedCharsDir.exists()) {
					extractedCharsDir.mkdirs();
				}

				extractedCharactersCount++;
				File outputfile = new File(
				        extractedCharsDir + File.separator + "char_" + extractedCharactersCount + ".png");

				ImageIO.write(image, "png", outputfile);
			}
		} catch (IOException ioe) {
			System.err.println("<ERROR>   " + ioe.getMessage());
		}
	}

	@Override
	public void onCharacterExtractionCompleted() {
		System.out.println("<INFO>    Character extraction proccess completed.");
	}

	@Override
	public void onTrainingStarted() {
		System.out.println("<INFO>    Training[" + currentTrainingIndex + "] started...");
	}

	@Override
	public void onTrainingProgressChanged(int progress, int epochs, double error) {
		System.out.println("<INFO>    Training[" + currentTrainingIndex + "] progress: " + progress + " | Epochs: "
		        + epochs + " | Error: " + error);
	}

	@Override
	public void onTrainingCompleted(int epochs, double error, long totalTime) {
		System.out.println("<INFO>    Training[" + currentTrainingIndex + "] finished in " + totalTime
		        + "ms | Total of epochs: " + epochs + " | Error: " + error);
	}
}
