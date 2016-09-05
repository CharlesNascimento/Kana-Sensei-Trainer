package com.kansus.mlp;

/**
 * This class represents the input layer of a multilayer perceptron neural network.
 */
public class InputLayer extends Layer {

    /**
     * Constructor of this class.
     *
     * @param name            The name of this layer.
     * @param numberOfNeurons The amount of neurons in this layer.
     * @param numberOfInputs  The amount of inputs the neurons of this layer have.
     */
    public InputLayer(String name, int numberOfNeurons, int numberOfInputs) {
        super(name, numberOfNeurons, numberOfInputs);
    }

    @Override
    public void initialize(int numberOfNeurons, int numberOfInputs) {
        for (int i = 0; i < numberOfNeurons; i++) {
            this.addNeuron(new InputNeuron(), numberOfInputs);
        }
    }
}