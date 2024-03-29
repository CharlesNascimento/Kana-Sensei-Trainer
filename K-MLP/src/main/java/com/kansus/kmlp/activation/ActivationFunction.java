package com.kansus.kmlp.activation;

/**
 * Neural network's activation function interface.
 */
public interface ActivationFunction {

    /**
     * Performs calculation based on the sum of input neurons output.
     *
     * @param summedInput neuron's sum of outputs respectively inputs for the connected
     *                    neuron
     * @return Output's calculation based on the sum of inputs
     */
    double calculateOutput(double summedInput);
}