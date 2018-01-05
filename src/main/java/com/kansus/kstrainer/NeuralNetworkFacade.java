package com.kansus.kstrainer;

import com.kansus.kstrainer.core.Project;
import com.kansus.kstrainer.core.Test;
import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.logging.Log;
import com.kansus.kmlp.core.MultilayerPerceptron;
import com.kansus.kmlp.core.NetworkTrainingListener;
import com.kansus.kstrainer.model.Character;
import com.kansus.kstrainer.model.EvaluationResult;
import com.kansus.kstrainer.model.NeuralNetworkConfig;
import com.kansus.kstrainer.model.StrokePattern;
import com.kansus.kstrainer.repository.CharacterRepository;
import com.kansus.kstrainer.repository.StrokesRepository;
import com.kansus.kstrainer.util.FileUtils;
import com.kansus.kstrainer.util.PreNetworkUtils;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

import javax.imageio.ImageIO;
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

    private static final int IMAGE_DIMENSION = 16;
    private String currentTraining;

    private final CharacterRepository characterRepository;
    private final StrokesRepository strokesRepository;

    public NeuralNetworkFacade() {
        characterRepository = new CharacterRepository();
        strokesRepository = new StrokesRepository();
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

        if (Workspace.getInstance().getCurrentProject().getRootDirectory().getName().equals("K")) {
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
                //int[] pixelsInputC = PreNetworkUtils.normalizePixels(charImage, true,                        neuralNetworkConfig.isNegativeNormalization());
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

                int charId = Integer.parseInt(folderCharacters.get(j).getName().replace(".", ";").split(";")[0]);
                expectedOutput[charId - 1] = 1;

                pixelsNeuralNetwork.addPattern("", pixelsInput, expectedOutput);
                pixelsNeuralNetwork.addPattern("", pixelsInput1, expectedOutput);
                pixelsNeuralNetwork.addPattern("", pixelsInput2, expectedOutput);
                pixelsNeuralNetwork.addPattern("", pixelsInput3, expectedOutput);
                //pixelsNeuralNetwork.addPattern("", pixelsInputC, expectedOutput);
                Log.writeln("<INFO>    Pattern " + folderCharacters.get(j).getName() + " added to the neural network");
            }
        }

        currentTraining = "Pixels network";
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

        if (Workspace.getInstance().getCurrentProject().getRootDirectory().getName().equals("K")) {
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
                charImage = resize(charImage, IMAGE_DIMENSION, IMAGE_DIMENSION);
                String sampleName = FileUtils.getFilenameWithoutExtension(folderCharacters.get(j));
                int charId = Integer.parseInt(sampleName.split("-")[0]);
                Character character = characterRepository.getById(charId);

                // Pixels
                double[] pixelsInput = PreNetworkUtils.normalizePixels(charImage, isConvolve, isNegative);
                Utils.savePixelsNormalizationToFile(pixelsInput, normalizationSubFolder, sampleName);

                // Strokes
                StrokePattern strokePattern = strokesRepository.getById(character.getStrokesPattern());
                double[] normalization = PreNetworkUtils.normalizeStrokes(strokePattern.getPattern(), isNegative);
                normalization = addRedundancyTo(normalization, 7);

                // Both
                double[] input = concat(pixelsInput, normalization);

                // Expected output
                double[] expectedOutput = new double[92];
                Arrays.fill(expectedOutput, -1);
                expectedOutput[charId - 1] = 1;

                pixelsNeuralNetwork.addPattern(sampleName, input, expectedOutput);
                Log.writeln("<INFO>    Sample " + sampleName + " added to the neural network");
            }
        }

        currentTraining = "Pixels network";
        pixelsNeuralNetwork.train(this);

        saveWeightsFile(pixelsNeuralNetwork, neuralNetworkConfig.getWeightsFile());

        double totalTime = System.currentTimeMillis() - startTime;

        Log.writeln("<INFO>    Command executed in " + totalTime / 1000 + " seconds");
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private static double[] concat(double[] array1, double[] array2) {
        double[] c = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, c, 0, array1.length);
        System.arraycopy(array2, 0, c, array1.length, array2.length);
        return c;
    }

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
                double[] expectedOutput = new double[37];
                Arrays.fill(expectedOutput, -1);
                expectedOutput[strokePattern.getId()] = 1;

                double[] normalization = PreNetworkUtils.normalizeStrokes(strokePattern.getPattern(), negativeNorm);
                strokesNeuralNetwork.addPattern("", normalization, expectedOutput);
                Log.writeln("<INFO>    Pattern [" + strokePattern.getPattern() + "] added to the neural network");
            }
        }

        currentTraining = "Strokes network";
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
     * scale image
     *
     * @param sbi       image to scale
     * @param imageType type of image
     * @param dWidth    width of destination image
     * @param dHeight   height of destination image
     * @param fWidth    x-factor for transformation / scaling
     * @param fHeight   y-factor for transformation / scaling
     * @return scaled image
     */
    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();

            g.setPaint(Color.white);
            g.fillRect(0, 0, dWidth, dHeight);

            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);

            double x = dWidth / 2 - sbi.getWidth() * fWidth / 2;
            double y = dHeight / 2 - sbi.getHeight() * fHeight / 2;

            at.translate(x, y);
            g.drawImage(sbi, at, null);
        }
        return dbi;
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

        if (project.getRootDirectory().getName().equals("PX-0") || project.getRootDirectory().getName().equals("K")) {
            PreNetworkUtils.lowerValue = 0;
        } else {
            PreNetworkUtils.lowerValue = -1;
        }

        for (File imageFile : samplesDirImages) {
            try {
                BufferedImage charImage = ImageIO.read(imageFile);

                double[] input = PreNetworkUtils.normalizePixels(
                        charImage,
                        true,
                        neuralNetworkConfig.isNegativeNormalization()
                );

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

        if (project.getRootDirectory().getName().equals("PX-0") || project.getRootDirectory().getName().equals("K")) {
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

                // Strokes
                StrokePattern strokePattern = strokesRepository.getById(character.getStrokesPattern());
                double[] normalization = PreNetworkUtils.normalizeStrokes(strokePattern.getPattern(), isNegative);
                normalization = addRedundancyTo(normalization, 7);

                // Both
                double[] input = concat(pixelsInput, normalization);

                double[] output = pixelsNeuralNetwork.evaluate(input);
                results.add(createEvaluationResultFor(imageFile, project, output));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    private double[] addRedundancyTo(double[] input, int level) {
        double[] output = new double[input.length * (level + 1)];

        for (int i = 0; i < input.length; i++) {
            int start = i * (level + 1);
            int end = start + level + 1;

            for (int j = start; j < end; j++) {
                output[j] = input[i];
            }
        }

        return output;
    }

    public static void main(String[] args) {
        double[] input = {1, 2, 3, 4, 5, 6, 7, 8};
        NeuralNetworkFacade neuralNetworkFacade = new NeuralNetworkFacade();
        input = neuralNetworkFacade.addRedundancyTo(input, 3);

        for (double d : input) {
            System.out.println(d);
        }
    }

    private EvaluationResult createEvaluationResultFor(File sample, Project project, double[] output) {
        double outputsSum = 0;
        int characterId = Integer.parseInt(sample.getName().split("-")[0]);
        Character character = characterRepository.getById(characterId);
        List<Character> allCharacters = characterRepository.getAll();

        Log.writeln("");
        Log.writeln("<INFO>    Pixels evaluation outputs for sample "  + sample + " -> " + character);
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

    public void evaluateCosineSimilarity() {
        /*File a = new File(Workspace.getInstance().getCurrentProject().getTestNamed("Default").getInputDirectory(), "1.png");
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int[] anorm = PreNetworkUtils.normalizePixels(bi, false, false);

        double sim = Utils.cosineSimilarity(input, anorm);
        System.out.println("Cosine Similarity: " + sim);
        System.out.println("Rating: " + Utils.rangeToRange(sim, -1, 1, 0, 10));*/
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
        Log.writeln("<INFO>    " + currentTraining + " training started...");
    }

    @Override
    public void onTrainingProgressChanged(int epochs, double error) {
        Log.writeln("<INFO>    " + currentTraining + " training - " + "Current epoch: " + epochs + " | Current error: "
                + error);
    }

    @Override
    public void onTrainingCompleted(int epochs, double error, long totalTime) {
        Log.writeln("<INFO>    " + currentTraining + " training finished in " + totalTime + "ms | Total of epochs: "
                + epochs + " | Error: " + error);
    }
}
