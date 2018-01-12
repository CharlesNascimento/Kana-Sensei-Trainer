package com.kansus.kstrainer;

import com.kansus.kmlp.core.MultilayerPerceptron;
import com.kansus.kmlp.core.NetworkTrainingListener;
import com.kansus.kstrainer.core.Test;
import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.logging.Log;
import com.kansus.kstrainer.model.NeuralNetworkConfig;
import com.kansus.kstrainer.model.StrokePattern;
import com.kansus.kstrainer.util.FileUtils;
import com.kansus.kstrainer.util.PreNetworkUtils;
import com.kansus.kstrainer.util.Utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Facade class which deals with the neural network training and input
 * evaluation.
 *
 * @author Charles Nascimento
 */
public class NeuralNetworkFacade implements NetworkTrainingListener {

    /**
     * Trains the strokes neural network based on the training configuration
     * provided.
     *
     * @param neuralNetworkConfig The training configuration.
     */
    public void trainStrokesNetwork(NeuralNetworkConfig neuralNetworkConfig) {
        Log.writeln("<INFO>    Starting strokes network training.");

        long startTime = System.currentTimeMillis();

        MultilayerPerceptron strokesNeuralNetwork = Utils.createNetworkFromConfig(neuralNetworkConfig);
        boolean negativeNorm = neuralNetworkConfig.isNegativeNormalization();
        loadWeightsFile(strokesNeuralNetwork, neuralNetworkConfig.getWeightsFile());

        for (int i = 0; i < neuralNetworkConfig.getInputs().size(); i++) {
            ArrayList<StrokePattern> strokePatterns = FileUtils.loadStrokePatterns(neuralNetworkConfig.getInputs().get(i));

            for (StrokePattern strokePattern : strokePatterns) {
                double[] expectedOutput = new double[38];
                Arrays.fill(expectedOutput, -1);
                expectedOutput[strokePattern.getId()] = 1;

                double[] normalization = PreNetworkUtils.normalizeStrokes(strokePattern.getPattern(), negativeNorm);
                strokesNeuralNetwork.addPattern("", normalization, expectedOutput);
                Log.writeln("<INFO>    Pattern [" + strokePattern.getPattern() + "] added to the neural network");
            }
        }

        strokesNeuralNetwork.train(this);

        saveWeightsFile(strokesNeuralNetwork, neuralNetworkConfig.getWeightsFile());

        double totalTime = System.currentTimeMillis() - startTime;

        Log.writeln("<INFO>    Command executed in " + totalTime / 1000 + " seconds");
    }

    /**
     * Evaluates the given pixels against the neural network loaded with the
     * weights of the given weights file.
     *
     * @param weightsFile The weights file.
     * @param charImage   The image with the pixels to be evaluated.
     * @return The array of outputs of the neural network.
     */
    public double[] evaluate(File weightsFile, BufferedImage charImage, boolean convolveImage,
                             boolean negativeNormalization) {
        MultilayerPerceptron pixelsNeuralNetwork = new MultilayerPerceptron(1024, 512, 92);
        pixelsNeuralNetwork.loadWeightsFromFile(weightsFile);

        double[] input = PreNetworkUtils.normalizePixels(charImage, convolveImage, negativeNormalization);
        Utils.savePixelsNormalizationToFile(input, new File("E:\\"), "evaluation");
        double[] output = pixelsNeuralNetwork.evaluate(input);
        double outputsSum = 0;

        Log.writeln("<INFO>    Pixels evaluation outputs: ");
        for (int i = 0; i < output.length; i++) {
            Log.writeln("<INFO>    Output[" + i + "] = " + output[i]);
            outputsSum += output[i];
        }

        double highestValue = Utils.highestValueIndex(output);

        Log.writeln("<INFO>    Highest Output: " + highestValue);
        Log.writeln("<INFO>    Outputs Mean: " + outputsSum / output.length);

        return output;
    }

    /**
     * Evaluates the given pixels against the neural network loaded with the
     * weights of the given weights file.
     *
     * @param test The test data.
     */
    public void evaluateStrokesNetwork(Test test) {
        NeuralNetworkConfig neuralNetworkConfig = Workspace.getInstance().getCurrentProject().getConfiguration();

        File testResultsDir = test.getOutputDirectory();
        FileUtils.mkDir(testResultsDir);

        boolean negativeNormalization = neuralNetworkConfig.isNegativeNormalization();

        MultilayerPerceptron strokesNeuralNetwork = Utils.createNetworkFromConfig(neuralNetworkConfig);
        strokesNeuralNetwork.loadWeightsFromFile(neuralNetworkConfig.getWeightsFile());

        for (int i = 0; i < neuralNetworkConfig.getInputs().size(); i++) {
            ArrayList<StrokePattern> strokePatterns = FileUtils.loadStrokePatterns(neuralNetworkConfig.getInputs().get(i));

            for (StrokePattern strokePattern : strokePatterns) {
                try {
                    String strokes = strokePattern.getPattern();
                    FileWriter fw = new FileWriter(new File(testResultsDir, strokes + ".txt"));
                    Log.setWriter(new BufferedWriter(fw));

                    Log.writeln("Starting evaluation for the [" + strokes + "] pattern...");

                    double[] input = PreNetworkUtils.normalizeStrokes(strokes, negativeNormalization);
                    double[] output = strokesNeuralNetwork.evaluate(input);

                    Log.writeln("<INFO>    Strokes evaluation outputs: ");
                    for (int j = 0; j < output.length; j++) {
                        Log.writeln("<INFO>    Output[" + j + "] = " + output[j]);
                    }

                    double expectedClassOutput = output[strokePattern.getId()];
                    double rating0To10 = Utils.rangeToRange(expectedClassOutput, -1, 1, 0, 10);

                    Log.writeln("<INFO>    Expected Class Output: " + expectedClassOutput);
                    Log.writeln("<INFO>    Expected Class Rating: " + rating0To10);
                    Log.newLine();

                    Log.saveFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Evaluates the given strokes against the neural network loaded with the
     * weights of the given weights file.
     *
     * @param weightsFile The weights file.
     * @param strokes     A list of strokes to be evaluated.
     * @return The array of outputs of the neural network.
     */
    public double[] evaluate(File weightsFile, List<String> strokes) {
        System.out.print("Exists: " + weightsFile.exists());
        MultilayerPerceptron strokesNeuralNetwork = new MultilayerPerceptron(32, 64, 37);
        strokesNeuralNetwork.loadWeightsFromFile(weightsFile);

        double[] input = PreNetworkUtils.normalizeStrokes(strokes, false);
        double[] output = strokesNeuralNetwork.evaluate(input);

        Log.writeln("<INFO>    Strokes evaluation outputs: ");
        for (int i = 0; i < output.length; i++) {
            Log.writeln("<INFO>    Output[" + i + "] = " + output[i]);
        }

        double highestValue = Utils.highestValueIndex(output);
        double rating0To10 = Utils.rangeToRange(highestValue, -1, 1, 0, 10);

        Log.writeln("<INFO>    Highest Output: " + highestValue);
        Log.writeln("<INFO>    Highest Final Rating: " + rating0To10);
        return output;
    }

    /**
     * Loads the weights in the file to the neural network.
     *
     * @param network     The neural network.
     * @param weightsFile The weights file.
     */
    private void loadWeightsFile(MultilayerPerceptron network, File weightsFile) {
        if (!weightsFile.exists()) {
            try {
                weightsFile.createNewFile();
                String currentTrainingWeightsFile = weightsFile.getPath();
                Log.writeln("<INFO>    Weights file \"" + currentTrainingWeightsFile + "\" created.");
            } catch (IOException e) {
                e.printStackTrace();
                Log.writeln("<ERROR>   " + e.getMessage());
            }
        } else {
            if (weightsFile.length() != 0) {
                String currentTrainingWeightsFile = weightsFile.getPath();
                network.loadWeightsFromFile(weightsFile);
                Log.writeln("<INFO>    Weights file \"" + currentTrainingWeightsFile + "\" loaded.");
            }
        }
    }

    /**
     * Saves the weights of the given neural network to a file.
     *
     * @param network     The neural network.
     * @param weightsFile The weights file.
     */
    private void saveWeightsFile(MultilayerPerceptron network, File weightsFile) {
        network.saveWeightsToFile(weightsFile);
        Log.writeln("<INFO>    Weights file \"" + weightsFile.getPath() + "\" saved.");
    }

    @Override
    public void onTrainingStarted() {
        Log.writeln("<INFO>    Strokes network training started...");
    }

    @Override
    public void onTrainingProgressChanged(int epochs, double error) {
        Log.writeln("<INFO>    Strokes network training - " + "Current epoch: " + epochs + " | Current error: "
                + error);
    }

    @Override
    public void onTrainingCompleted(int epochs, double error, long totalTime) {
        Log.writeln("<INFO>    Strokes network training finished in " + totalTime + "ms | Total of epochs: "
                + epochs + " | Error: " + error);
    }
}
