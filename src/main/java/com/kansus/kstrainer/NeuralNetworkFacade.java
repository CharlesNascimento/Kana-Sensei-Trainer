package com.kansus.kstrainer;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.kansus.kstrainer.core.Project;
import com.kansus.kstrainer.core.Test;
import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.mlp.MultilayerPerceptron;
import com.kansus.kstrainer.mlp.NetworkTrainingListener;
import com.kansus.kstrainer.model.StrokePattern;
import com.kansus.kstrainer.model.TrainingConfig;
import com.kansus.kstrainer.util.FileUtils;
import com.kansus.kstrainer.util.Log;
import com.kansus.kstrainer.util.PreNetworkUtils;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

/**
 * Facade class which deals with the neural network training and input
 * evaluation.
 *
 * @author Charles Nascimento
 */
public class NeuralNetworkFacade implements NetworkTrainingListener {

    private MultilayerPerceptron pixelsNeuralNetwork;
    private MultilayerPerceptron strokesNeuralNetwork;

    private String currentTraining;

    /**
     * Trains the pixels neural network based on the training configuration
     * provided.
     *
     * @param trainingConfig The training configuration.
     * @throws IOException When there is something wrong with the input files.
     */
    public void trainPixelsNetwork(TrainingConfig trainingConfig) throws IOException {
        Log.write("<INFO>    Starting pixels network training.");

        long startTime = System.currentTimeMillis();

        String rootFolder = trainingConfig.getConfigFile().getParent() + File.separator;
        pixelsNeuralNetwork = Utils.createNetworkFromConfig(trainingConfig);
        loadWeightsFile(pixelsNeuralNetwork, trainingConfig.getWeightsFile());

        ArrayList<File> inputs = trainingConfig.getInputs();
        File normalizationFolder = new File(rootFolder + "Intermediate\\Normalization");
        FileUtils.mkDir(normalizationFolder);

        for (int i = 0; i < inputs.size(); i++) {
            File currentPatternsFolder = trainingConfig.getInputs().get(i).getAbsoluteFile();

            List<File> folderCharacters = Arrays.asList(currentPatternsFolder.listFiles());
            File normalizationSubFolder = new File(normalizationFolder, currentPatternsFolder.getName());
            FileUtils.mkDir(normalizationSubFolder);

            for (int j = 0; j < folderCharacters.size(); j++) {
                BufferedImage charImage = ImageIO.read(folderCharacters.get(j));
                int[] pixelsInput = PreNetworkUtils.normalizePixels(charImage, trainingConfig.isConvolveImage(),
                        trainingConfig.isNegativeNormalization());
                Utils.savePixelsNormalizationToFile(pixelsInput, normalizationSubFolder,
                        folderCharacters.get(j).getName());

                int[] expectedOutput = new int[92];
                Arrays.fill(expectedOutput, -1);

                int charId = Integer.parseInt(folderCharacters.get(j).getName().replace(".", ";").split(";")[0]);
                expectedOutput[charId - 1] = 1;

                pixelsNeuralNetwork.addPattern("", pixelsInput, expectedOutput);
                Log.write("<INFO>    Pattern " + folderCharacters.get(j).getName() + " added to the neural network");
            }
        }

        currentTraining = "Pixels network";
        pixelsNeuralNetwork.train(this);

        saveWeightsFile(pixelsNeuralNetwork, trainingConfig.getWeightsFile());

        double totalTime = System.currentTimeMillis() - startTime;

        Log.write("<INFO>    Command executed in " + totalTime / 1000 + " seconds");
    }

    /**
     * Trains the strokes neural network based on the training configuration
     * provided.
     *
     * @param trainingConfig The training configuration.
     */
    public void trainStrokesNetwork(TrainingConfig trainingConfig) {
        Log.write("<INFO>    Starting strokes network training.");

        long startTime = System.currentTimeMillis();

        strokesNeuralNetwork = Utils.createNetworkFromConfig(trainingConfig);
        boolean negativeNorm = trainingConfig.isNegativeNormalization();
        loadWeightsFile(strokesNeuralNetwork, trainingConfig.getWeightsFile());

        for (int i = 0; i < trainingConfig.getInputs().size(); i++) {
            ArrayList<StrokePattern> strokePatterns = FileUtils.loadStrokePatterns(trainingConfig.getInputs().get(i));

            for (StrokePattern strokePattern : strokePatterns) {
                int[] expectedOutput = new int[37];
                Arrays.fill(expectedOutput, -1);
                expectedOutput[strokePattern.getId()] = 1;

                int[] normalization = PreNetworkUtils.normalizeStrokes(strokePattern.getName(), negativeNorm);
                strokesNeuralNetwork.addPattern("", normalization, expectedOutput);
                Log.write("<INFO>    Pattern [" + strokePattern.getName() + "] added to the neural network");
            }
        }

        currentTraining = "Strokes network";
        strokesNeuralNetwork.train(this);

        saveWeightsFile(strokesNeuralNetwork, trainingConfig.getWeightsFile());

        double totalTime = System.currentTimeMillis() - startTime;

        Log.write("<INFO>    Command executed in " + totalTime / 1000 + " seconds");
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
        pixelsNeuralNetwork = new MultilayerPerceptron(1024, 512, 92);
        pixelsNeuralNetwork.loadWeightsFromFile(weightsFile);

        int[] input = PreNetworkUtils.normalizePixels(charImage, convolveImage, negativeNormalization);
        Utils.savePixelsNormalizationToFile(input, new File("E:\\"), "evaluation");
        double[] output = pixelsNeuralNetwork.evaluate(input);
        double outputsSum = 0;

        Log.write("<INFO>    Pixels evaluation outputs: ");
        for (int i = 0; i < output.length; i++) {
            Log.write("<INFO>    Output[" + i + "] = " + output[i]);
            outputsSum += output[i];
        }

        double highestValue = Utils.highestValue(output);

        Log.write("<INFO>    Highest Output: " + highestValue);
        Log.write("<INFO>    Outputs Mean: " + outputsSum / output.length);

        return output;
    }

    /**
     * Evaluates the given pixels against the neural network loaded with the
     * weights of the given weights file.
     *
     * @param trainingConfig The neural network configuration.
     */
    public void evaluatePixelsNetwork(Test test) {
        Project project = Workspace.getInstance().getCurrentProject();
        TrainingConfig trainingConfig = project.getConfiguration();

        FileUtils.mkDir(test.getInputDirectory());
        FileUtils.mkDir(test.getIntermediateDirectory());
        FileUtils.mkDir(test.getOutputDirectory());

        boolean convolveImage = trainingConfig.isConvolveImage();
        boolean negativeNormalization = trainingConfig.isNegativeNormalization();

        pixelsNeuralNetwork = Utils.createNetworkFromConfig(trainingConfig);
        pixelsNeuralNetwork.loadWeightsFromFile(trainingConfig.getWeightsFile());

        File[] samplesDirImages = test.getInputDirectory().listFiles(ValidationUtils.imagesFileFilter);

        if (samplesDirImages == null || samplesDirImages.length == 0) {
            Log.write("<WARNING>    No images have been found in the Evaluation" +
                    "n\\Samples folder, no evaluation could be done.");
            return;
        }

        File a = new File(Workspace.getInstance().getCurrentProject().getTestNamed("Default").getInputDirectory(), "1.png");
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int[] anorm = PreNetworkUtils.normalizePixels(bi, convolveImage, negativeNormalization);

        for (File imageFile : samplesDirImages) {
            try {
                String filename = FileUtils.getFilenameWithoutExtension(imageFile);
                FileWriter fw = new FileWriter(new File(test.getOutputDirectory(), filename + ".txt"));
                Log.setWriter(new BufferedWriter(fw));

                BufferedImage charImage = ImageIO.read(imageFile);
                int[] input = PreNetworkUtils.normalizePixels(charImage, convolveImage, negativeNormalization);
                double sim = Utils.cosineSimilarity(input, anorm);
                System.out.println("Cosine Similarity: " + sim);
                System.out.println("Rating: " + Utils.rangeToRange(sim, -1, 1, 0, 10));
                Utils.savePixelsNormalizationToFile(input, test.getNormalizationDirectory(), filename);
                double[] output = pixelsNeuralNetwork.evaluate(input);
                double outputsSum = 0;

                Log.write("<INFO>    Pixels evaluation outputs for sample \"" + filename + "\": ");
                for (int i = 0; i < output.length; i++) {
                    outputsSum += output[i];
                    Log.write("<INFO>    Output[" + i + "] = " + output[i]);
                }

                double highestValue = Utils.highestValue(output);

                Log.write("<INFO>    Highest Output: " + highestValue);
                Log.write("<INFO>    Outputs Mean: " + outputsSum / output.length + "\n");

                Log.closeWriter();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Evaluates the given pixels against the neural network loaded with the
     * weights of the given weights file.
     *
     * @param trainingConfig The configuration of the neural network.
     */
    public void evaluateStrokesNetwork(Test test) {
        TrainingConfig trainingConfig = Workspace.getInstance().getCurrentProject().getConfiguration();

        File testResultsDir = test.getOutputDirectory();
        FileUtils.mkDir(testResultsDir);

        boolean negativeNormalization = trainingConfig.isNegativeNormalization();

        strokesNeuralNetwork = Utils.createNetworkFromConfig(trainingConfig);
        strokesNeuralNetwork.loadWeightsFromFile(trainingConfig.getWeightsFile());

        for (int i = 0; i < trainingConfig.getInputs().size(); i++) {
            ArrayList<StrokePattern> strokePatterns = FileUtils.loadStrokePatterns(trainingConfig.getInputs().get(i));

            for (StrokePattern strokePattern : strokePatterns) {
                try {
                    String strokes = strokePattern.getName();
                    FileWriter fw = new FileWriter(new File(testResultsDir, strokes + ".txt"));
                    Log.setWriter(new BufferedWriter(fw));

                    Log.write("Starting evaluation for the [" + strokes + "] pattern...");

                    int[] input = PreNetworkUtils.normalizeStrokes(strokes, negativeNormalization);
                    double[] output = strokesNeuralNetwork.evaluate(input);

                    Log.write("<INFO>    Strokes evaluation outputs: ");
                    for (int j = 0; j < output.length; j++) {
                        Log.write("<INFO>    Output[" + j + "] = " + output[j]);
                    }

                    double expectedClassOutput = output[strokePattern.getId()];
                    double rating0To10 = Utils.rangeToRange(expectedClassOutput, -1, 1, 0, 10);

                    Log.write("<INFO>    Expected Class Output: " + expectedClassOutput);
                    Log.write("<INFO>    Expected Class Rating: " + rating0To10);

                    Log.closeWriter();
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
        strokesNeuralNetwork = new MultilayerPerceptron(32, 64, 37);
        strokesNeuralNetwork.loadWeightsFromFile(weightsFile);

        int[] input = PreNetworkUtils.normalizeStrokes(strokes, false);
        double[] output = strokesNeuralNetwork.evaluate(input);

        Log.write("<INFO>    Strokes evaluation outputs: ");
        for (int i = 0; i < output.length; i++) {
            Log.write("<INFO>    Output[" + i + "] = " + output[i]);
        }

        double highestValue = Utils.highestValue(output);
        double rating0To10 = Utils.rangeToRange(highestValue, -1, 1, 0, 10);

        Log.write("<INFO>    Highest Output: " + highestValue);
        Log.write("<INFO>    Highest Final Rating: " + rating0To10);
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
                Log.write("<INFO>    Weights file \"" + currentTrainingWeightsFile + "\" created.");
            } catch (IOException e) {
                e.printStackTrace();
                Log.write("<ERROR>   " + e.getMessage());
            }
        } else {
            if (weightsFile.length() != 0) {
                String currentTrainingWeightsFile = weightsFile.getPath();
                network.loadWeightsFromFile(weightsFile);
                Log.write("<INFO>    Weights file \"" + currentTrainingWeightsFile + "\" loaded.");
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
        Log.write("<INFO>    Weights file \"" + weightsFile.getPath() + "\" saved.");
    }

    @Override
    public void onTrainingStarted() {
        Log.write("<INFO>    " + currentTraining + " training started...");
    }

    @Override
    public void onTrainingProgressChanged(int epochs, double error) {
        Log.write("<INFO>    " + currentTraining + " training - " + "Current epoch: " + epochs + " | Current error: "
                + error);
    }

    @Override
    public void onTrainingCompleted(int epochs, double error, long totalTime) {
        Log.write("<INFO>    " + currentTraining + " training finished in " + totalTime + "ms | Total of epochs: "
                + epochs + " | Error: " + error);
    }
}
