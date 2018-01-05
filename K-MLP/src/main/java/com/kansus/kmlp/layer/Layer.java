package com.kansus.kmlp.layer;

import com.kansus.kmlp.neuron.Neuron;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a layer of a multilayer perceptron neural network.
 */
public abstract class Layer {

    private String mName;

    private List<Neuron> mNeurons = new ArrayList<>();

    /**
     * Constructor of this class.
     *
     * @param name            The name of this layer.
     * @param numberOfNeurons The amount of neurons in this layer.
     * @param numberOfInputs  The amount of inputs the neurons of this layer have.
     */
    public Layer(String name, int numberOfNeurons, int numberOfInputs) {
        mName = name;
        initialize(numberOfNeurons, numberOfInputs);
    }

    /**
     * Initializes this layer.
     *
     * @param numberOfNeurons The amount of neurons in this layer.
     * @param numberOfInputs  The amount of inputs the neurons of this layer have.
     */
    public abstract void initialize(int numberOfNeurons, int numberOfInputs);

    /**
     * Returns the name of this layer.
     *
     * @return The name of this layer.
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the name of this layer.
     *
     * @param name The new name of this layer.
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Returns the amount of neurons in this layer.
     *
     * @return The amount of neurons in this layer.
     */
    public int getNeuronsCount() {
        return this.mNeurons.size();
    }

    /**
     * Returns a list with all the neurons of this layer.
     *
     * @return A list with all the neurons of this layer.
     */
    public List<Neuron> getNeurons() {
        return mNeurons;
    }

    /**
     * Adds a new neuron to this layer.
     *
     * @param neuron         The neuron to be added.
     * @param numberOfInputs The amount of inputs of the neuron.
     */
    public void addNeuron(Neuron neuron, int numberOfInputs) {
        neuron.initialize(numberOfInputs);
        neuron.setLayer(this);
        this.mNeurons.add(neuron);
        neuron.setId(this.getNeuronsCount() - 1);
    }

    /**
     * Returns a string with the weights of all neurons of this layer.
     *
     * @return A string with the weights of all neurons of this layer.
     */
    public String getNeuronsWeights() {
        StringBuilder weights = new StringBuilder();

        for (Neuron n : this.mNeurons) {
            for (int i = 0; i < n.getWeights().length; i++) {
                weights.append(n.getWeights()[i]).append(";");
            }
        }

        return weights.toString();
    }
}
