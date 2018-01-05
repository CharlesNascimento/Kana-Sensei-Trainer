package com.kansus.kstrainer.core;

import com.kansus.kstrainer.model.NeuralNetworkConfig;
import com.kansus.kstrainer.util.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

public class Project {

    private static final String INPUT_DIRECTORY_NAME = "Input";
    private static final String INTERMEDIATE_DIRECTORY_NAME = "Intermediate";
    private static final String OUTPUT_DIRECTORY_NAME = "Output";
    private static final String TEST_DIRECTORY_NAME = "Test";
    public static final String CONFIG_FILE_NAME = "network.json";

    private File rootDirectory;
    private File inputDirectory;
    private File intermediateDirectory;
    private File outputDirectory;
    private File testDirectory;

    private HashMap<String, Test> tests = new HashMap<>();

    private NeuralNetworkConfig neuralNetworkConfig;

    public Project(File rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.inputDirectory = new File(rootDirectory, INPUT_DIRECTORY_NAME);
        this.intermediateDirectory = new File(rootDirectory, INTERMEDIATE_DIRECTORY_NAME);
        this.outputDirectory = new File(rootDirectory, OUTPUT_DIRECTORY_NAME);
        this.testDirectory = new File(rootDirectory, TEST_DIRECTORY_NAME);

        scanTests();
    }

    public NeuralNetworkConfig getConfiguration() {
        if (neuralNetworkConfig != null) return neuralNetworkConfig;

        File configFile = new File(rootDirectory, CONFIG_FILE_NAME);
        neuralNetworkConfig = FileUtils.loadTrainingConfiguration(configFile);
        return neuralNetworkConfig;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public File getInputDirectory() {
        return inputDirectory;
    }

    public File getIntermediateDirectory() {
        return intermediateDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public Test getTestNamed(String name) {
        return tests.get(name);
    }

    private void scanTests() {
        FileFilter filter = File::isDirectory;
        File[] directories = testDirectory.listFiles(filter);

        if (directories == null) return;

        for (File directory : directories) {
            tests.put(directory.getName(), new Test(directory));
        }
    }
}
