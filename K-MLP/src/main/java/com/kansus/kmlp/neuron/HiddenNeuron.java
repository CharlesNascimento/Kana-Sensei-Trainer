package com.kansus.kmlp.neuron;

import com.kansus.kmlp.layer.HiddenLayer;
import com.kansus.kmlp.layer.OutputLayer;

/**
 * This class represents an intermediary neuron of a multilayer perceptron neural network.
 */
public class HiddenNeuron extends Neuron {

    @Override
    public void computeError() {
        HiddenLayer hiddenLayer = (HiddenLayer) this.getLayer();
        OutputLayer outputLayer = (OutputLayer) hiddenLayer.getOutputLayer();

        double error = outputLayer.getWeightedErrorSum(this.getId()) * (1 - (Math.pow(this.getOutput(), 2)));
        this.setError(error);
    }
}