package com.kansus.kmlp.activation;

/**
 * Rectified Linear activation function
 */
public class RectifiedLinearActivationFunction implements ActivationFunction {

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateOutput(double summedInput) {
        return Math.max(0, summedInput);
    }
}