package com.kansus.mlp;

/**
 * This class represents an input neuron of a multilayer perceptron neural network.
 */
public class InputNeuron extends Neuron {

    @Override
    public void calculateOutput() {
        // In input neurons, the output is the received input
        this.setOutput(this.getInputTerminalValue(0));
    }

    @Override
    public void computeError() {
        // Neurons of the input layer do not have error
    }
}