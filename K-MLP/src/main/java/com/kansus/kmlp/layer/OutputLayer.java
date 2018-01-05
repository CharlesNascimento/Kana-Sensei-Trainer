package com.kansus.kmlp.layer;

import com.kansus.kmlp.neuron.Neuron;
import com.kansus.kmlp.neuron.OutputNeuron;

import java.text.DecimalFormat;

/**
 * This class represents the output layer of a multilayer perceptron neural
 * network.
 */
public class OutputLayer extends Layer {

	/**
	 * Constructor of this class.
	 *
	 * @param name The name of this layer.
	 * @param numberOfNeurons The amount of neurons in this layer.
	 * @param numberOfInputs The amount of inputs the neurons of this layer
	 *            have.
	 */
	public OutputLayer(String name, int numberOfNeurons, int numberOfInputs) {
		super(name, numberOfNeurons, numberOfInputs);
	}

	@Override
	public void initialize(int numberOfNeurons, int numberOfInputs) {
		this.getNeurons().clear();

		for (int i = 0; i < numberOfNeurons; i++) {
			this.addNeuron(new OutputNeuron(), numberOfInputs);
		}
	}

	/**
	 * Calculates the sum of the weighted errors of the neurons in this layer,
	 * applying the function sum(ei * wji).
	 *
	 * @param index The index of the connected neuron.
	 * @return the sum of the weighted errors of the neurons in this layer
	 */
	public double getWeightedErrorSum(int index) {
		double sum = 0;

		for (Neuron neuron : this.getNeurons()) {
			sum += neuron.getError() * neuron.getWeights()[index];
		}

		return sum;
	}

	/**
	 * Calculates the mean squared error of this layer, applying the function
	 * 1/2*sum((dj - xj)^2) over all the neurons of this layer.
	 *
	 * @return The mean squared error of this layer.
	 */
	public double getMeanSquaredError() {
		double mse = 0, diff;

		for (Neuron n : this.getNeurons()) {
			diff = n.getExpectedOutput() - n.getOutput();
			mse += Math.pow(diff, 2);
			// System.out.println("expected: " + n.getExpectedOutput() + ",
			// output: " + n.getOutput() + ", mse: " + mse);
		}
		mse = 0.5 * mse;

		// Log.d("Neuron", "getMeanSquaredError " + mse);
		return mse;
	}

	/**
	 * Returns the outputs of all neurons of this layer.
	 *
	 * @return The outputs of all neurons of this layer.
	 */
	public int[] getDiscreteOutput() {
		int[] output = new int[this.getNeuronsCount()];
		int i = 0;

		for (Neuron n : this.getNeurons()) {
			if (n.getOutput() > 0)
				output[i++] = 1;
			else
				output[i++] = -1;
		}

		return output;
	}

	/**
	 * Returns the outputs of all neurons of this layer.
	 *
	 * @return The outputs of all neurons of this layer.
	 */
	public double[] getOutput() {
		double[] output = new double[this.getNeuronsCount()];
		int i = 0;

		for (Neuron n : this.getNeurons()) {
			output[i++] = n.getOutput();
		}

		return output;
	}

	/**
	 * @return A string with the outputs of all neurons of this layer in the
	 *         format #.##.
	 */
	public String getFormattedOutput() {
		String fractionalOutputs = " ( ";
		DecimalFormat format = new DecimalFormat("#.##");

		for (Neuron n : this.getNeurons()) {
			fractionalOutputs += format.format(n.getOutput()) + "; ";
		}

		fractionalOutputs += ")";
		return fractionalOutputs;
	}
}