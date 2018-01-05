package com.kansus.kstrainer.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kansus.kstrainer.core.Project;
import com.kansus.kstrainer.core.Workspace;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kansus.kstrainer.model.StrokePattern;
import com.kansus.kstrainer.model.Character;
import com.kansus.kstrainer.model.NeuralNetworkConfig;

/**
 * Class with utility methods to deal with files.
 *
 * @author Charles Nascimento
 */
public class FileUtils {

    /**
     * Loads the training configuration file.
     *
     * @param trainingConfigFile The training configuration file.
     * @return All the configuration data in the file.
     */
    public static NeuralNetworkConfig loadTrainingConfiguration(File trainingConfigFile) {
        ArrayList<File> trainingInputs = new ArrayList<>();
        NeuralNetworkConfig neuralNetworkConfig = new NeuralNetworkConfig();

        try {
            JSONParser parser = new JSONParser();
            JSONObject trainingObject = (JSONObject) parser.parse(new FileReader(trainingConfigFile));

            neuralNetworkConfig.setConfigFile(trainingConfigFile);
            Project project = new Project(trainingConfigFile.getParentFile());
            File weightsFile = new File(project.getOutputDirectory(), (String) trainingObject.get("weights_file"));
            neuralNetworkConfig.setWeightsFile(weightsFile);
            int inputNeuronsCount = (int) (long) trainingObject.get("input_neurons_count");
            neuralNetworkConfig.setInputNeuronsCount(inputNeuronsCount);
            int hiddenNeuronsCount = (int) (long) trainingObject.get("hidden_neurons_count");
            neuralNetworkConfig.setHiddenNeuronsCount(hiddenNeuronsCount);
            int outputNeuronsCount = (int) (long) trainingObject.get("output_neurons_count");
            neuralNetworkConfig.setOutputNeuronsCount(outputNeuronsCount);
            double learningRate = (double) trainingObject.get("learning_rate");
            neuralNetworkConfig.setLearningRate(learningRate);
            double minimumError = (double) trainingObject.get("minimum_error");
            neuralNetworkConfig.setMinimumError(minimumError);
            int maxEpochs = (int) (long) trainingObject.get("max_epochs");
            neuralNetworkConfig.setMaxEpochs(maxEpochs);

            Object convolveImageObj = trainingObject.get("convolve_image");
            boolean convolveImage = convolveImageObj != null && (boolean) convolveImageObj;
            neuralNetworkConfig.setConvolveImage(convolveImage);

            Object negativeNormObj = trainingObject.get("negative_normalization");
            boolean negativeNormalization = negativeNormObj != null && (boolean) negativeNormObj;
            neuralNetworkConfig.setNegativeNormalization(negativeNormalization);

            JSONArray inputsArray = (JSONArray) trainingObject.get("inputs");
            String rootFolder = neuralNetworkConfig.getConfigFile().getParent();

            for (Object t : inputsArray) {
                JSONObject inputObject = (JSONObject) t;

                File inputFile = new File(rootFolder, (String) inputObject.get("path"));

                trainingInputs.add(inputFile);
            }

            neuralNetworkConfig.setInputs(trainingInputs);
        } catch (IOException | ParseException ioe) {
            ioe.printStackTrace();
        }

        return neuralNetworkConfig;
    }

    /**
     * Loads a file containing the stroke patterns used to train the neural
     * network.
     *
     * @param strokePatternFiles The file with the stroke patterns.
     * @return All the configurations in the file.
     */
    public static ArrayList<StrokePattern> loadStrokePatterns(File strokePatternsFile) {
        ArrayList<StrokePattern> strokePatterns = new ArrayList<StrokePattern>();

        try {
            JSONParser parser = new JSONParser();
            JSONArray strokePatternsArray = (JSONArray) parser.parse(new FileReader(strokePatternsFile));

            for (Object spo : strokePatternsArray) {
                JSONObject strokePatternsObject = (JSONObject) spo;

                int id = (int) (long) strokePatternsObject.get("id");
                String pattern = (String) strokePatternsObject.get("pattern");

                strokePatterns.add(new StrokePattern(id, pattern, null));
            }
        } catch (IOException | ParseException ioe) {
            ioe.printStackTrace();
        }

        return strokePatterns;
    }

    /**
     * Converts a normalization string (sequence of -1 and 1) into an integer
     * array.
     *
     * @param normalizationString The normalization string.
     * @return An array of integer values.
     */
    public static int[] normalizationStringToArray(String normalizationString) {
        String[] splitedValues = normalizationString.split(",");
        int[] array = new int[splitedValues.length];

        for (int i = 0; i < splitedValues.length; i++) {
            array[i] = Integer.parseInt(splitedValues[i]);
        }

        return array;
    }

    public static void savePatternsToFile(HashMap<String, int[]> patterns) {
        JSONArray patternsJsonArray = new JSONArray();
        int id = 0;

        for (Map.Entry<String, int[]> entry : patterns.entrySet()) {
            JSONObject obj = new JSONObject();
            String entryKey = entry.getKey().replace("[", "").replace("]", "");
            int[] entryValue = entry.getValue();
            StringBuilder normalization = new StringBuilder();

            for (int i = 0; i < entryValue.length; i++) {
                normalization.append(entryValue[i]);

                if (i < entryValue.length - 1) {
                    normalization.append(",");
                }
            }

            obj.put("id", id++);
            obj.put("pattern", entryKey);
            obj.put("normalization", normalization.toString());
            patternsJsonArray.add(obj);
        }

        try {
            FileWriter file = new FileWriter("stroke_patterns.json");
            file.write(patternsJsonArray.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the specified folder if it does not exist.
     *
     * @param normalizationFolder The folder.
     */
    public static void mkDir(File normalizationFolder) {
        if (!normalizationFolder.exists() || !normalizationFolder.isDirectory()) {
            normalizationFolder.mkdirs();
        }
    }

    /**
     * @param imageFile
     * @return
     */
    public static String getFilenameWithoutExtension(File imageFile) {
        return imageFile.getName().substring(0, imageFile.getName().length() - 4);
    }
}
