package com.kansus.kstrainer.model;

import java.io.File;
import java.util.ArrayList;

/**
 * Class that holds the configuration values of a neural network training.
 * 
 * @author Charles Nascimento
 */
public class TrainingConfig {

	private File configFile;
	
	private File weightsFile;

	private int inputNeuronsCount;

	private int hiddenNeuronsCount;

	private int outputNeuronsCount;

	private double learningRate = 0.2;

	private double minimumError = 0.01;

	private int maxEpochs = 50000;
	
	private boolean negativeNormalization = false;
	
	private boolean convolveImage = true;

	private ArrayList<File> inputs;

	public TrainingConfig() {
	}

	public File getConfigFile() {
		return configFile;
	}
	
	public File getWeightsFile() {
		return weightsFile;
	}

	public int getInputNeuronsCount() {
		return inputNeuronsCount;
	}

	public int getHiddenNeuronsCount() {
		return hiddenNeuronsCount;
	}

	public int getOutputNeuronsCount() {
		return outputNeuronsCount;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public double getMinimumError() {
		return minimumError;
	}

	public int getMaxEpochs() {
		return maxEpochs;
	}
	
	public boolean isNegativeNormalization() {
		return negativeNormalization;
	}

	public boolean isConvolveImage() {
		return convolveImage;
	}

	public ArrayList<File> getInputs() {
		return inputs;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}
	
	public void setWeightsFile(File weightsFile) {
		this.weightsFile = weightsFile;
	}

	public void setInputNeuronsCount(int inputNeuronsCount) {
		this.inputNeuronsCount = inputNeuronsCount;
	}

	public void setHiddenNeuronsCount(int hiddenNeuronsCount) {
		this.hiddenNeuronsCount = hiddenNeuronsCount;
	}

	public void setOutputNeuronsCount(int outputNeuronsCount) {
		this.outputNeuronsCount = outputNeuronsCount;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public void setMinimumError(double minimumError) {
		this.minimumError = minimumError;
	}

	public void setMaxEpochs(int maxEpochs) {
		this.maxEpochs = maxEpochs;
	}
	
	public void setNegativeNormalization(boolean negativeNormalization) {
		this.negativeNormalization = negativeNormalization;
	}

	public void setConvolveImage(boolean convolveImage) {
		this.convolveImage = convolveImage;
	}

	public void setInputs(ArrayList<File> inputs) {
		this.inputs = inputs;
	}
}
