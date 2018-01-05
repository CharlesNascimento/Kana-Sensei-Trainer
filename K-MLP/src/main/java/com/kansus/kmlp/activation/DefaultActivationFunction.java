package com.kansus.kmlp.activation;

/**
 * Sigmoid activation function. Calculation is based on:
 * <p>
 * y = 1/(1+ e^(-slope*x))
 */
public class DefaultActivationFunction implements ActivationFunction {

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateOutput(double summedInput) {
        return ((1 - Math.exp(-2 * summedInput)) / (1 + Math.exp(-2 * summedInput)));
    }
}