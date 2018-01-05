package com.kansus.kmlp.activation;

/**
 * Linear combination activation function implementation, the output unit is
 * simply the weighted sum of its inputs plus a bias term.
 */
public class LinearCombinationFunction implements ActivationFunction {

    /**
     * Bias value
     */
    private double bias;

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateOutput(double summedInput) {
        return summedInput + bias;
    }
}