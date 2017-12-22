package com.kansus.kstrainer.core;

import java.io.File;

public class Test {

    private static final String INPUT_DIRECTORY_NAME = "Input";
    private static final String INTERMEDIATE_DIRECTORY_NAME = "Intermediate";
    private static final String NORMALIZATION_DIRECTORY_NAME = "Normalization";
    private static final String OUTPUT_DIRECTORY_NAME = "Output";

    private File rootDirectory;

    private File inputDirectory;
    private File intermediateDirectory;
    private File normalizationDirectory;
    private File outputDirectory;

    public Test(File rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.inputDirectory = new File(rootDirectory, INPUT_DIRECTORY_NAME);
        this.intermediateDirectory = new File(rootDirectory, INTERMEDIATE_DIRECTORY_NAME);
        this.normalizationDirectory = new File(intermediateDirectory, NORMALIZATION_DIRECTORY_NAME);
        this.outputDirectory = new File(rootDirectory, OUTPUT_DIRECTORY_NAME);
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

    public File getNormalizationDirectory() {
        return normalizationDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }
}
