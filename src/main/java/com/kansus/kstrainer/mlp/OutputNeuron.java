package com.kansus.kstrainer.mlp;

/**
 * This class represents an output neuron of a multilayer perceptron neural network.
 */
public class OutputNeuron extends Neuron {

    @Override
    public void computeError() {
        // ei = (di - xi) * F(yi)
        double error = (this.getExpectedOutput() - this.getOutput()) * (1 - (Math.pow(this.getOutput(), 2)));
        this.setError(error);
    }
}