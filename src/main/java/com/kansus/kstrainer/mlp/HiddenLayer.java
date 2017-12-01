package com.kansus.kstrainer.mlp;

/**
 * This class represents an intermediary layer of a multilayer perceptron neural network.
 */
public class HiddenLayer extends Layer {

    private Layer mOutputLayer;

    /**
     * Constructor of this class.
     *
     * @param name            The name of this layer.
     * @param numberOfNeurons The amount of neurons in this layer.
     * @param numberOfInputs  The amount of inputs the neurons of this layer have.
     */
    public HiddenLayer(String name, int numberOfNeurons, int numberOfInputs) {
        super(name, numberOfNeurons, numberOfInputs);
    }

    @Override
    public void initialize(int numberOfNeurons, int numberOfInputs) {
        this.getNeurons().clear();

        for (int i = 0; i < numberOfNeurons; i++) {
            this.addNeuron(new HiddenNeuron(), numberOfInputs);
        }
    }

    /**
     * Sets the output layer.
     *
     * @param outputLayer The new output layer.
     */
    public void setOutputLayer(Layer outputLayer) {
        mOutputLayer = outputLayer;
    }

    /**
     * Returns the output layer.
     *
     * @return The output layer.
     */
    public Layer getOutputLayer() {
        return mOutputLayer;
    }
}