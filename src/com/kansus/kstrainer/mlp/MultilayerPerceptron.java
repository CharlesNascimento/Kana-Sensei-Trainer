package com.kansus.kstrainer.mlp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

// TODO Salvar no arquivo de pesos também a estrutura da rede neural.

/**
 * This class represents a multilayer perceptron neural network.
 */
public class MultilayerPerceptron {

	public static final int KEY_INPUT_LAYER = 0;
	public static final int KEY_HIDDEN_LAYER = 1;
	public static final int KEY_OUTPUT_LAYER = 2;

	private double mLearningRate = 0.05;

	private double mMinimumError = 0.01;

	private int mMaxEpochs = 10000;

	private ArrayList<Pattern> mTrainingSet = new ArrayList<>();

	private ArrayList<Layer> mLayers = new ArrayList<>();

	private NetworkTrainingListener mProgressListener;

	public MultilayerPerceptron(int inputNeurons, int hiddenNeurons, int outputNeurons) {
		InputLayer inputLayer = new InputLayer("input_layer", inputNeurons, 1);
		HiddenLayer hiddenLayer = new HiddenLayer("hidden_layer", hiddenNeurons, inputLayer.getNeuronsCount());
		OutputLayer outputLayer = new OutputLayer("output_layer", outputNeurons, hiddenLayer.getNeuronsCount());

		mLayers.add(inputLayer);
		mLayers.add(hiddenLayer);
		mLayers.add(outputLayer);

		hiddenLayer.setOutputLayer(outputLayer);
	}

	/**
	 * Trains this neural network using the patterns in the training set.
	 */
	public void train() {
		this.backpropagation();
	}

	/**
	 * Trains this neural network using the patterns in the training set.
	 *
	 * @param progressListener A listener that will be invoked whenever there is
	 *            a change in the training progress.
	 */
	public void train(NetworkTrainingListener progressListener) {
		this.mProgressListener = progressListener;
		this.backpropagation();
	}

	/**
	 * Evaluate the input data by obtaining the outputs of the network.
	 *
	 * @param input The data to be evaluated.
	 * @return The outputs of the neural network.
	 */
	public double[] evaluate(int[] input) {
		// Log.d("MLP", "evaluate");
		this.forward(input);
		OutputLayer outputLayer = (OutputLayer) mLayers.get(KEY_OUTPUT_LAYER);
		return outputLayer.getOutput();
	}

	/**
	 * Performs the backpropagation phase of the multilayer perceptron. 1.
	 * Initialize weights and parameters; 2. Repeat until the error is minimal
	 * or a given number of cycles is executed: 2.1. For each pattern in the
	 * training set: 2.1.1. Perform the forward phase so we have the outputs of
	 * the network; 2.1.2. Compare the outputs with the expected outputs; 2.1.3.
	 * Update the weights of the neurons by performing the backward phase.
	 */
	private void backpropagation() {
		int epochs = 1;
		double meanSquaredErrorSum;
		double meanSquaredErrorMean;

		if (mProgressListener != null) {
			mProgressListener.onTrainingStarted();
		}

		long startTime = System.currentTimeMillis();
		this.resetWeights();

		do {
			meanSquaredErrorSum = 0;

			for (Pattern pattern : this.mTrainingSet) {
				this.setupExpectedOutputs(pattern);
				this.forward(pattern.getData());
				this.computeErrors();
				this.backward();
				meanSquaredErrorSum += this.getMeanSquaredError();
				// Log.d("MLP", "meanSquaredErrorSum: " + meanSquaredErrorSum);
			}
			meanSquaredErrorMean = meanSquaredErrorSum / this.mTrainingSet.size();
			epochs++;

			if ((epochs % 10) == 0 && mProgressListener != null) {
				mProgressListener.onTrainingProgressChanged(epochs, meanSquaredErrorMean);
			}
		} while ((this.mMinimumError < meanSquaredErrorMean) && (epochs < this.mMaxEpochs));

		long totalTime = System.currentTimeMillis() - startTime;
		if (mProgressListener != null) {
			mProgressListener.onTrainingCompleted(epochs, meanSquaredErrorMean, totalTime / 1000);
		}
	}

	/**
	 * Performs the forward phase of the multilayer perceptron. 1. The input is
	 * presented to the input layer L[0]; 2. For each L layer after the input
	 * layer: 2.1. After the neurons of the L[i](i>0) layer calculate their
	 * output signals, these serve as input for the calculation of the outputs
	 * of the neurons in the next layer L[i](i + 1); 3. The outputs produced by
	 * the neurons of the output layer are compared to the expected outputs.
	 *
	 * @param data The data to be inputted.
	 */
	private void forward(int[] data) {
		// Log.d("MLP", "forward");
		Layer previousLayer = null;
		int i;

		for (Layer currentLayer : mLayers) {
			i = 0;
			// Log.d("MLP", currentLayer.getName());

			if (currentLayer instanceof InputLayer) {
				for (Neuron neuron : currentLayer.getNeurons()) {
					neuron.setInputTerminalValue(0, data[i++]);
					neuron.calculateOutput();
				}
			} else {
				for (Neuron neuron : currentLayer.getNeurons()) {
					for (Neuron previousNeuron : previousLayer.getNeurons()) {
						neuron.setInputTerminalValue(i++, previousNeuron.getOutput());
					}
					i = 0;
					neuron.calculateOutput();
				}
			}
			previousLayer = currentLayer;
		}
	}

	/**
	 * Performs the backward phase of the multilayer perceptron. 1. From the
	 * output layer to the input layer: 1.1. The nodes of the current layer
	 * adjust their weights in order to reduce their error; 1.2. The error of
	 * the nodes of the hidden layers is calculated using the error of the next
	 * layer nodes connected to it, weighted by the weights of the connections
	 * between them.
	 */
	private void backward() {
		// Log.d("MLP", "backward");
		Layer currentLayer;

		for (int i = (mLayers.size() - 1); i > 0; i--) {
			currentLayer = this.mLayers.get(i);

			for (Neuron neuron : currentLayer.getNeurons()) {
				neuron.adjustWeights(this.mLearningRate);
			}
		}
	}

	/**
	 * Setups the expected outputs of the neurons in the output layer.
	 *
	 * @param pattern The pattern with the expected outputs.
	 */
	private void setupExpectedOutputs(Pattern pattern) {
		// Log.d("MLP", "setupExpectedOutputs");
		OutputLayer outputLayer = (OutputLayer) mLayers.get(KEY_OUTPUT_LAYER);
		int i = 0;

		for (Neuron neuron : outputLayer.getNeurons()) {
			neuron.setExpectedOutput(pattern.getExpectedOutput()[i]);
			i++;
		}
	}

	/**
	 * Calculates the errors of all the neurons of the neural network, starting
	 * from the output layer.
	 */
	private void computeErrors() {
		// Log.d("MLP", "computeErrors");
		Layer currentLayer;

		for (int i = (mLayers.size() - 1); i > 0; i--) {
			currentLayer = this.mLayers.get(i);

			for (Neuron neuron : currentLayer.getNeurons()) {
				neuron.computeError();
			}
		}
	}

	/**
	 * Resets the weights of the hidden and output layers by assigning them
	 * random values.
	 */
	private void resetWeights() {
		// Log.d("MLP", "resetWeights");
		for (Layer layer : this.mLayers) {

			if (!(layer instanceof InputLayer)) {
				for (Neuron neuron : layer.getNeurons()) {
					neuron.resetWeights();
				}
			}
		}
	}

	/**
	 * Adds a new pattern to the training set.
	 *
	 * @param name The name of the pattern.
	 * @param data The pattern data.
	 * @param expectedOutput The expected output of the pattern.
	 */
	public void addPattern(String name, int[] data, int[] expectedOutput) {
		Pattern pattern = new Pattern(name, data, expectedOutput);
		mTrainingSet.add(pattern);
		// System.out.println("Pattern added! " + mTrainingSet.size());
	}

	/**
	 * Removes all patterns from the training set.
	 */
	public void clearPatterns() {
		mTrainingSet.clear();
	}

	/**
	 * Redefines the number of neurons in the hidden layer.
	 *
	 * @param newNeuronsCount The new number of neurons in the hidden layer.
	 */
	public void redefineHiddenLayerNeurons(int newNeuronsCount) {
		InputLayer inputLayer = (InputLayer) mLayers.get(KEY_INPUT_LAYER);
		HiddenLayer hiddenLayer = (HiddenLayer) mLayers.get(KEY_HIDDEN_LAYER);
		OutputLayer outputLayer = (OutputLayer) mLayers.get(KEY_OUTPUT_LAYER);

		hiddenLayer.initialize(newNeuronsCount, inputLayer.getNeuronsCount());
		outputLayer.initialize(outputLayer.getNeuronsCount(), newNeuronsCount);
	}

	/**
	 * Saves the weights of the neural network to a file.
	 *
	 * @param file The file in which to save the weights.
	 */
	public void saveWeightsToFile(File file) {
		try {
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));

			for (Layer layer : this.mLayers) {
				if (!(layer instanceof InputLayer)) {
					for (Neuron neuron : layer.getNeurons()) {
						byte[] bytes = Utils.toByteArray(neuron.getWeights());

						output.write(bytes);
					}
				}
			}

			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the neural network's weights from a file.
	 *
	 * @param file The file to be loaded.
	 */
	public void loadWeightsFromFile(File file) {
		try {
			InputStream input = null;
			try {
				byte[] result = new byte[(int) file.length()];
				int totalBytesRead = 0;
				input = new BufferedInputStream(new FileInputStream(file));

				while (totalBytesRead < result.length) {
					int bytesRemaining = result.length - totalBytesRead;

					int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
					if (bytesRead > 0) {
						totalBytesRead = totalBytesRead + bytesRead;
					}
				}

				totalBytesRead = 0;
				for (Layer layer : this.mLayers) {
					if (!(layer instanceof InputLayer)) {
						for (Neuron neuron : layer.getNeurons()) {
							int to = totalBytesRead + (neuron.getWeights().length * 8);
							byte[] buffer = Arrays.copyOfRange(result, totalBytesRead, to);

							double[] weights = Utils.toDoubleArray(buffer);
							neuron.setWeights(weights);
							totalBytesRead += (neuron.getWeights().length * 8);
						}
					}
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the Mean Squared Error of this neural network.
	 *
	 * @return The Mean Squared Error of this neural network.
	 */
	private double getMeanSquaredError() {
		OutputLayer outputLayer = (OutputLayer) mLayers.get(KEY_OUTPUT_LAYER);
		return outputLayer.getMeanSquaredError();
	}

	/**
	 * Returns the learning rate of this neural network.
	 *
	 * @return The learning rate of this neural network.
	 */
	public final double getLearningRate() {
		return mLearningRate;
	}

	/**
	 * Sets the learning rate of this neural network.
	 *
	 * @param learningRate The new learning rate of this neural network.
	 */
	public final void setLearningRate(double learningRate) {
		this.mLearningRate = learningRate;
	}

	/**
	 * Returns the neural network's minimum error.
	 *
	 * @return The neural network's minimum error.
	 */
	public final double getMinimumError() {
		return mMinimumError;
	}

	/**
	 * Sets the neural network's minimum error.
	 *
	 * @param minimumError The new neural network's minimum error.
	 */
	public final void setMinimumError(double minimumError) {
		this.mMinimumError = minimumError;
	}

	/**
	 * Returns the maximum number of epochs of a training in this neural
	 * network.
	 *
	 * @return The maximum number of epochs of a training in this neural
	 *         network.
	 */
	public final int getMaxEpochs() {
		return mMaxEpochs;
	}

	/**
	 * Sets the maximum number of epochs of a training in this neural network.
	 *
	 * @param maxEpochs The new maximum number of epochs of a training in this
	 *            neural network.
	 */
	public final void setMaxEpochs(int maxEpochs) {
		this.mMaxEpochs = maxEpochs;
	}
}