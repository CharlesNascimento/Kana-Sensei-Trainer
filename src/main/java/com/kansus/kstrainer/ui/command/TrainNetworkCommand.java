package com.kansus.kstrainer.ui.command;

import com.kansus.kmlp.core.MultilayerPerceptron;
import com.kansus.kmlp.core.NetworkTrainingListener;
import com.kansus.kstrainer.NeuralNetworkFacade;
import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.logging.Log;
import com.kansus.kstrainer.model.Character;
import com.kansus.kstrainer.model.NeuralNetworkConfig;
import com.kansus.kstrainer.model.StrokePattern;
import com.kansus.kstrainer.repository.CharacterRepository;
import com.kansus.kstrainer.repository.StrokesRepository;
import com.kansus.kstrainer.util.FileUtils;
import com.kansus.kstrainer.util.PreNetworkUtils;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kansus.kstrainer.util.Utils.addRedundancyTo;
import static com.kansus.kstrainer.util.Utils.concat;
import static com.kansus.kstrainer.util.Utils.scale;

public class TrainNetworkCommand implements Command, NetworkTrainingListener {

    private static final int IMAGE_DIMENSION = 24;

    private String[] args;
    private final CharacterRepository characterRepository;
    private final StrokesRepository strokesRepository;

    public TrainNetworkCommand(String[] args) {
        this.args = args;
        characterRepository = new CharacterRepository();
        strokesRepository = new StrokesRepository();
    }

    @Override
    public void execute() {
        if (!validate()) {
            return;
        }

        try {
            NeuralNetworkConfig neuralNetworkConfig = Workspace.getInstance().getCurrentProject().getConfiguration();
            NeuralNetworkFacade trainer = new NeuralNetworkFacade();

            File outputDir = Workspace.getInstance().getCurrentProject().getOutputDirectory();
            FileWriter fw = new FileWriter(new File(outputDir, "log.txt"));
            Log.setWriter(new BufferedWriter(fw));

            if (args[1].equals("pixels")) {
                train(neuralNetworkConfig);
            } else if (args[1].equals("strokes")) {
                trainer.trainStrokesNetwork(neuralNetworkConfig);
            }

            Log.saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Trains the pixels neural network based on the training configuration
     * provided.
     *
     * @param neuralNetworkConfig The training configuration.
     * @throws IOException When there is something wrong with the input files.
     */
    public void trainPixelsNetwork(NeuralNetworkConfig neuralNetworkConfig) throws IOException {
        Log.writeln("<INFO>    Starting pixels network training.");

        long startTime = System.currentTimeMillis();

        String rootFolder = neuralNetworkConfig.getConfigFile().getParent() + File.separator;
        MultilayerPerceptron pixelsNeuralNetwork = Utils.createNetworkFromConfig(neuralNetworkConfig);

        ArrayList<File> inputs = neuralNetworkConfig.getInputs();
        File normalizationFolder = new File(rootFolder + "Intermediate\\Normalization");
        FileUtils.mkDir(normalizationFolder);

        if (Workspace.getInstance().getCurrentProject().getRootDirectory().getName().equals("K") ||
                Workspace.getInstance().getCurrentProject().getRootDirectory().getName().equals("PX-0")) {
            PreNetworkUtils.lowerValue = 0;
        } else {
            PreNetworkUtils.lowerValue = -1;
        }

        for (int i = 0; i < inputs.size(); i++) {
            File currentPatternsFolder = inputs.get(i).getAbsoluteFile();

            List<File> folderCharacters = Arrays.asList(currentPatternsFolder.listFiles());
            File normalizationSubFolder = new File(normalizationFolder, currentPatternsFolder.getName());
            FileUtils.mkDir(normalizationSubFolder);

            for (int j = 0; j < folderCharacters.size(); j++) {
                File charFile = folderCharacters.get(j);
                BufferedImage charImage = ImageIO.read(charFile);

                BufferedImage charImage1 = scale(charImage, BufferedImage.TYPE_BYTE_BINARY, 32, 32, 0.8, 1);
                BufferedImage charImage2 = scale(charImage, BufferedImage.TYPE_BYTE_BINARY, 32, 32, 1, 0.8);
                BufferedImage charImage3 = scale(charImage, BufferedImage.TYPE_BYTE_BINARY, 32, 32, 0.8, 0.8);

                double[] pixelsInput = PreNetworkUtils.normalizePixels(charImage, neuralNetworkConfig.isConvolveImage(),
                        neuralNetworkConfig.isNegativeNormalization());
                double[] pixelsInput1 = PreNetworkUtils.normalizePixels(charImage1, neuralNetworkConfig.isConvolveImage(),
                        neuralNetworkConfig.isNegativeNormalization());
                double[] pixelsInput2 = PreNetworkUtils.normalizePixels(charImage2, neuralNetworkConfig.isConvolveImage(),
                        neuralNetworkConfig.isNegativeNormalization());
                double[] pixelsInput3 = PreNetworkUtils.normalizePixels(charImage3, neuralNetworkConfig.isConvolveImage(),
                        neuralNetworkConfig.isNegativeNormalization());
                //int[] pixelsInputC = PreNetworkUtils.normalizePixels(charImage, true, neuralNetworkConfig.isNegativeNormalization());
                Utils.savePixelsNormalizationToFile(pixelsInput, normalizationSubFolder,
                        folderCharacters.get(j).getName());
                Utils.savePixelsNormalizationToFile(pixelsInput1, normalizationSubFolder,
                        folderCharacters.get(j).getName() + "1");
                Utils.savePixelsNormalizationToFile(pixelsInput2, normalizationSubFolder,
                        folderCharacters.get(j).getName() + "2");
                Utils.savePixelsNormalizationToFile(pixelsInput3, normalizationSubFolder,
                        folderCharacters.get(j).getName() + "3");

                double[] expectedOutput = new double[92];
                Arrays.fill(expectedOutput, -1);

                String sampleName = FileUtils.getFilenameWithoutExtension(charFile);
                int charId = Integer.parseInt(sampleName.split("-")[0]);
                expectedOutput[charId - 1] = 1;

                pixelsNeuralNetwork.addPattern("", pixelsInput, expectedOutput);
                pixelsNeuralNetwork.addPattern("", pixelsInput1, expectedOutput);
                pixelsNeuralNetwork.addPattern("", pixelsInput2, expectedOutput);
                pixelsNeuralNetwork.addPattern("", pixelsInput3, expectedOutput);
                //pixelsNeuralNetwork.addPattern("", pixelsInputC, expectedOutput);
                Log.writeln("<INFO>    Pattern " + folderCharacters.get(j).getName() + " added to the neural network");
            }
        }

        pixelsNeuralNetwork.train(this);

        saveWeightsFile(pixelsNeuralNetwork, neuralNetworkConfig.getWeightsFile());

        double totalTime = System.currentTimeMillis() - startTime;

        Log.writeln("<INFO>    Command executed in " + totalTime / 1000 + " seconds");
    }

    public void train(NeuralNetworkConfig neuralNetworkConfig) throws IOException {
        Log.writeln("<INFO>    Starting pixels network training.");

        long startTime = System.currentTimeMillis();

        String rootFolder = neuralNetworkConfig.getConfigFile().getParent() + File.separator;
        MultilayerPerceptron pixelsNeuralNetwork = Utils.createNetworkFromConfig(neuralNetworkConfig);

        ArrayList<File> inputs = neuralNetworkConfig.getInputs();
        File normalizationFolder = new File(rootFolder + "Intermediate\\Normalization");
        FileUtils.mkDir(normalizationFolder);

        boolean isConvolve = neuralNetworkConfig.isConvolveImage();
        boolean isNegative = neuralNetworkConfig.isNegativeNormalization();

        if (Workspace.getInstance().getCurrentProject().getRootDirectory().getName().equals("PX-0") ||
                Workspace.getInstance().getCurrentProject().getRootDirectory().getName().equals("K")) {
            PreNetworkUtils.lowerValue = 0;
        } else {
            PreNetworkUtils.lowerValue = -1;
        }

        for (int i = 0; i < inputs.size(); i++) {
            File currentPatternsFolder = neuralNetworkConfig.getInputs().get(i).getAbsoluteFile();

            List<File> folderCharacters = Arrays.asList(currentPatternsFolder.listFiles());
            File normalizationSubFolder = new File(normalizationFolder, currentPatternsFolder.getName());
            FileUtils.mkDir(normalizationSubFolder);

            for (int j = 0; j < folderCharacters.size(); j++) {
                BufferedImage charImage = ImageIO.read(folderCharacters.get(j));
                charImage = Utils.resize(charImage, IMAGE_DIMENSION, IMAGE_DIMENSION);
                String sampleName = FileUtils.getFilenameWithoutExtension(folderCharacters.get(j));
                int charId = Integer.parseInt(sampleName.split("-")[0]);
                Character character = characterRepository.getById(charId);

                // Pixels
                double[] pixelsInput = PreNetworkUtils.normalizePixels(charImage, isConvolve, isNegative);
                Utils.savePixelsNormalizationToFile(pixelsInput, normalizationSubFolder, sampleName);
/*
                // Strokes
                StrokePattern strokePattern = strokesRepository.getById(character.getStrokesPattern());
                double[] normalization = PreNetworkUtils.normalizeStrokes(strokePattern.getPattern(), isNegative);
                normalization = addRedundancyTo(normalization, 11);

                // Both
                double[] input = concat(pixelsInput, normalization);*/

                // Expected output
                double[] expectedOutput = new double[92];
                Arrays.fill(expectedOutput, -1);
                expectedOutput[charId - 1] = 1;

                pixelsNeuralNetwork.addPattern(sampleName, pixelsInput, expectedOutput);
                Log.writeln("<INFO>    Sample " + sampleName + " added to the neural network");
            }
        }

        pixelsNeuralNetwork.train(this);

        saveWeightsFile(pixelsNeuralNetwork, neuralNetworkConfig.getWeightsFile());

        double totalTime = System.currentTimeMillis() - startTime;

        Log.writeln("<INFO>    Command executed in " + totalTime / 1000 + " seconds");
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
        Log.writeln("<INFO>    Pixels network training started...");
    }

    @Override
    public void onTrainingProgressChanged(int epochs, double error) {
        Log.writeln("<INFO>    Pixels network training - " + "Current epoch: " + epochs + " | Current error: "
                + error);
    }

    @Override
    public void onTrainingCompleted(int epochs, double error, long totalTime) {
        Log.writeln("<INFO>    Pixels network training finished in " + totalTime + "ms | Total of epochs: "
                + epochs + " | Error: " + error);
    }

    @Override
    public boolean validate() {
        if (args.length != 2) {
            System.err.println("<ERROR>   Incorrect number of parameters.");
            System.err.println("<INFO>    Command syntax: train <neural-network> <config-file>");
            return false;
        }

        if (!Utils.equalsAny(args[1], ValidationUtils.validNeuralNetworks)) {
            System.err.println("<ERROR>   Invalid neural network: " + args[1]);
            return false;
        }

        return true;
    }

    private static boolean validateTrainingsConfigs(NeuralNetworkConfig neuralNetworkConfig) {
        if (!neuralNetworkConfig.getWeightsFile().exists()) {
            System.out.println("<WARNING> Weights file not found, a new file will be created.");
        }

        if (!neuralNetworkConfig.getWeightsFile().getName().endsWith("mlp")) {
            System.err.println("<ERROR>   The weights file should be a valid .mlp file.");
            return false;
        }

        for (File trainingInput : neuralNetworkConfig.getInputs()) {
            if (!trainingInput.exists()) {
                System.out.println("<ERROR>   Input file \'" + trainingInput.getName() + "\' not found.");
                return false;
            }
        }

        return true;
    }
}
