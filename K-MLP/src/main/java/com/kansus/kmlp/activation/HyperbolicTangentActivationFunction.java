package com.kansus.kmlp.activation;

/**
 * Sinusoid activation function. Calculation is based on:
 * <p>
 * y = sin(x)
 */
public class HyperbolicTangentActivationFunction implements ActivationFunction {

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateOutput(double summedInput) {
        return Math.tanh(summedInput);
    }
}