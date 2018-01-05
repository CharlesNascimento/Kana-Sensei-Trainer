package com.kansus.kmlp.neuron;

import com.kansus.kmlp.activation.ActivationFunction;
import com.kansus.kmlp.core.ActivationFunctionInjector;
import com.kansus.kmlp.layer.Layer;

import java.util.Random;

/**
 * This class represents a neuron of a multilayer perceptron neural network.
 */
public abstract class Neuron {

    private Layer mLayer;

    private int mId;

    private double[] mInputTerminals;
    private double[] mWeights;

    private double mExpectedOutput;
    private double mOutput;

    private double mError;

    private ActivationFunction activationFunction = new ActivationFunctionInjector().getCurrent();

    /**
     * Initializes this neuron.
     *
     * @param numberOfInputs The amount of inputs of this neuron.
     */
    public void initialize(int numberOfInputs) {
        this.mInputTerminals = new double[numberOfInputs];
        this.mWeights = new double[numberOfInputs];
    }

    /**
     * Returns the unique identifier of this neuron.
     *
     * @return The unique identifier of this neuron.
     */
    public final int getId() {
        return mId;
    }

    /**
     * Sets the unique identifier of this neuron.
     *
     * @param id The unique identifier of this neuron.
     */
    public final void setId(int id) {
        this.mId = id;
    }

    /**
     * Returns the error of this neuron.
     *
     * @return The error of this neuron.
     */
    public double getError() {
        return this.mError;
    }

    /**
     * Sets the error of this neuron.
     *
     * @param error The error of this neuron.
     */
    public final void setError(double error) {
        this.mError = error;
    }

    /**
     * Return the layer this neuron belongs to.
     *
     * @return The layer this neuron belongs to.
     */
    public Layer getLayer() {
        return mLayer;
    }

    /**
     * Sets the layer this neuron belongs to.
     *
     * @param layer The layer this neuron belongs to.
     */
    public void setLayer(Layer layer) {
        this.mLayer = layer;
    }

    /**
     * Returns the expected output for this neuron.
     *
     * @return The expected output for this neuron.
     */
    public double getExpectedOutput() {
        return mExpectedOutput;
    }

    /**
     * Sets the expected output for this neuron.
     *
     * @param expectedOutput The expected output for this neuron.
     */
    public void setExpectedOutput(double expectedOutput) {
        this.mExpectedOutput = expectedOutput;
    }

    /**
     * Returns the output of this neuron.
     *
     * @return The output of this neuron.
     */
    public double getOutput() {
        return this.mOutput;
    }

    protected void setOutput(double output) {
        this.mOutput = output;
    }

    /**
     * Sets the weights of the connections of this neuron.
     *
     * @param weights The new weights of the connections of this neuron.
     */
    public void setWeights(double[] weights) {
        this.mWeights = weights;
    }

    /**
     * Gets the weights of the connections of this neuron.
     *
     * @return The weights of the connections of this neuron.
     */
    public double[] getWeights() {
        return mWeights;
    }

    /**
     * Defines the value of an input terminal of this neuron.
     *
     * @param index The index of the terminal of this neuron.
     * @param value The value.
     */
    public void setInputTerminalValue(int index, double value) {
        this.mInputTerminals[index] = value;
    }

    /**
     * Returns the value of the specified input terminal of this neuron.
     *
     * @param index The index of the input terminal.
     * @return The value of the specified input terminal of this neuron.
     */
    public double getInputTerminalValue(int index) {
        return this.mInputTerminals[index];
    }

    /**
     * Calculates the output of this neuron. It's done by applying the activation function
     * over the sum of the multiplication between the input terminals and its respective weights.
     */
    public void calculateOutput() {
        double dotProductSum = 0;

        for (int i = 0; i < this.mInputTerminals.length; i++) {
            dotProductSum += (this.mInputTerminals[i] * this.mWeights[i]);
        }

        this.setOutput(activationFunction.calculateOutput(dotProductSum / (mLayer.getNeuronsCount() * 2)));
    }

    /**
     * Assigns random weights to the connections of this neuron.
     */
    public void resetWeights() {
        Random rand = new Random(System.currentTimeMillis());

        for (int i = 0; i < this.mWeights.length; i++) {
            this.mWeights[i] = rand.nextDouble();
        }
    }

    /**
     * Adjusts the weights of this neuron applying the function (wij += n*ej*xi) over every input
     * terminal.
     *
     * @param learningRate The learning rate of this neural network.
     */
    public void adjustWeights(double learningRate) {
        double[] newWeights = new double[this.mInputTerminals.length];

        for (int i = 0; i < this.mInputTerminals.length; i++) {
            newWeights[i] = this.getWeights()[i] + (learningRate * this.getError() * this.mInputTerminals[i]);
        }

        this.setWeights(newWeights);
    }

    /**
     * Calculates the error of this neuron.
     */
    public abstract void computeError();
}