package com.kansus.kstrainer.ui.command;

import com.kansus.kmlp.core.MultilayerPerceptron;
import com.kansus.kstrainer.NeuralNetworkFacade;
import com.kansus.kstrainer.core.Project;
import com.kansus.kstrainer.core.Test;
import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.logging.Log;
import com.kansus.kstrainer.model.Character;
import com.kansus.kstrainer.model.EvaluationResult;
import com.kansus.kstrainer.model.NeuralNetworkConfig;
import com.kansus.kstrainer.repository.CharacterRepository;
import com.kansus.kstrainer.repository.StrokesRepository;
import com.kansus.kstrainer.util.FileUtils;
import com.kansus.kstrainer.util.PreNetworkUtils;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.kansus.kstrainer.util.Utils.resize;

public class EvaluateNetworkCommand implements Command {

    private static final int IMAGE_DIMENSION = 24;

    private String[] args;
    private final CharacterRepository characterRepository;
    private final StrokesRepository strokesRepository;

    public EvaluateNetworkCommand(String[] args) {
        this.args = args;
        characterRepository = new CharacterRepository();
        strokesRepository = new StrokesRepository();
    }

    @Override
    public void execute() {
        if (!validate()) {
            return;
        }

        if (args[1].equals("pixels")) {
            String testName = args[2];
            Test test = Workspace.getInstance().getCurrentProject().getTestNamed(testName);

            evaluate(test);
        } else if (args[1].equals("strokes")) {
            String testName = args[2];
            Test test = Workspace.getInstance().getCurrentProject().getTestNamed(testName);

            NeuralNetworkFacade neuralNetworkFacade = new NeuralNetworkFacade();
            neuralNetworkFacade.evaluateStrokesNetwork(test);
        }
    }

    /**
     * Evaluates the given pixels against the neural network loaded with the
     * weights of the given weights file.
     *
     * @param test The test data.
     */
    public List<EvaluationResult> evaluatePixelsNetwork(Test test) {
        List<EvaluationResult> results = new ArrayList<>();
        File[] samplesDirImages = test.getInputDirectory().listFiles(ValidationUtils.imagesFileFilter);

        if (samplesDirImages == null || samplesDirImages.length == 0) {
            Log.writeln("<WARNING>    No images have been found in the Samples folder.");
            return results;
        }

        Project project = new Project(test.getRootDirectory().getParentFile().getParentFile());
        NeuralNetworkConfig neuralNetworkConfig = project.getConfiguration();
        MultilayerPerceptron pixelsNeuralNetwork = Utils.createNetworkFromConfig(neuralNetworkConfig);

        boolean isConvolve = neuralNetworkConfig.isConvolveImage();
        boolean isNegative = neuralNetworkConfig.isNegativeNormalization();

        if (project.getRootDirectory().getName().equals("PX-0") || project.getRootDirectory().getName().equals("K")) {
            PreNetworkUtils.lowerValue = 0;
        } else {
            PreNetworkUtils.lowerValue = -1;
        }

        for (File imageFile : samplesDirImages) {
            try {
                BufferedImage charImage = ImageIO.read(imageFile);
                double[] input = PreNetworkUtils.normalizePixels(charImage, isConvolve, isNegative);

                String filename = FileUtils.getFilenameWithoutExtension(imageFile);
                Utils.savePixelsNormalizationToFile(input, test.getNormalizationDirectory(), filename);

                double[] output = pixelsNeuralNetwork.evaluate(input);
                results.add(createEvaluationResultFor(imageFile, project, output));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    /**
     * Evaluates the given pixels against the neural network loaded with the
     * weights of the given weights file.
     *
     * @param test The test data.
     */
    public List<EvaluationResult> evaluate(Test test) {
        List<EvaluationResult> results = new ArrayList<>();
        File[] samplesDirImages = test.getInputDirectory().listFiles(ValidationUtils.imagesFileFilter);

        if (samplesDirImages == null || samplesDirImages.length == 0) {
            Log.writeln("<WARNING>    No images have been found in the Samples folder.");
            return results;
        }

        Project project = new Project(test.getRootDirectory().getParentFile().getParentFile());
        NeuralNetworkConfig neuralNetworkConfig = project.getConfiguration();
        MultilayerPerceptron pixelsNeuralNetwork = Utils.createNetworkFromConfig(neuralNetworkConfig);

        boolean isConvolve = neuralNetworkConfig.isConvolveImage();
        boolean isNegative = neuralNetworkConfig.isNegativeNormalization();

        if (project.getRootDirectory().getName().equals("PX-0") || project.getRootDirectory().getName().equals("PX-CN0")) {
            PreNetworkUtils.lowerValue = 0;
        } else {
            PreNetworkUtils.lowerValue = -1;
        }

        for (File imageFile : samplesDirImages) {
            String sampleName = FileUtils.getFilenameWithoutExtension(imageFile);
            int characterId = Integer.parseInt(imageFile.getName().split("-")[0]);
            Character character = characterRepository.getById(characterId);

            try {
                BufferedImage charImage = ImageIO.read(imageFile);
                charImage = resize(charImage, IMAGE_DIMENSION, IMAGE_DIMENSION);

                // Pixels
                double[] pixelsInput = PreNetworkUtils.normalizePixels(charImage, isConvolve, isNegative);
                Utils.savePixelsNormalizationToFile(pixelsInput, test.getNormalizationDirectory(), sampleName);
/*
                // Strokes
                StrokePattern strokePattern = strokesRepository.getById(character.getStrokesPattern());
                double[] normalization = PreNetworkUtils.normalizeStrokes(strokePattern.getPattern(), isNegative);
                normalization = addRedundancyTo(normalization, 11);

                // Both
                double[] input = concat(pixelsInput, normalization);*/

                double[] output = pixelsNeuralNetwork.evaluate(pixelsInput);
                results.add(createEvaluationResultFor(imageFile, project, output));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    private EvaluationResult createEvaluationResultFor(File sample, Project project, double[] output) {
        double outputsSum = 0;
        int characterId = Integer.parseInt(sample.getName().split("-")[0]);
        Character character = characterRepository.getById(characterId);
        List<Character> allCharacters = characterRepository.getAll();

        //output[characterId - 1] = (output[characterId - 1] * 0.8f + 0.9f * 0.2f) / 1;

        Log.writeln("");
        Log.writeln("<INFO>    Pixels evaluation outputs for sample " + sample + " -> " + character);
        for (int i = 0; i < output.length; i++) {
            outputsSum += output[i];
            Log.writeln("<INFO>    Output for " + allCharacters.get(i) + " -> " + output[i]);
        }

        EvaluationResult result = new EvaluationResult(sample, character, project);

        int highestValueIndex = Utils.highestValueIndex(output);
        Character highestOutputCharacter = allCharacters.get(highestValueIndex);
        double highestOutput = output[highestValueIndex];
        double expectedCharacterOutput = output[characterId - 1];
        double outputsMean = outputsSum / output.length;

        result.setHighestOutput(highestOutput);
        result.setHighestOutputCharacter(highestOutputCharacter);
        result.setExpectedCharacterOutput(expectedCharacterOutput);
        result.setCorrect(expectedCharacterOutput == highestOutput);
        result.setOutputsMean(outputsMean);
        result.setOutputs(output);

        Log.writeln("<INFO>    Result: " + (expectedCharacterOutput == highestOutput));
        Log.writeln("<INFO>    Highest Output: " + highestOutput + " (" + result.getHighestOutputCharacter() + ")");
        Log.writeln("<INFO>    Expected Output: " + result.getExpectedCharacterOutput());
        Log.writeln("<INFO>    Outputs Mean: " + outputsMean);

        return result;
    }

    @Override
    public boolean validate() {
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
}
