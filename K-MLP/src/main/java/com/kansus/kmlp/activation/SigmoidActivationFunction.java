package com.kansus.kmlp.activation;

/**
 * Sigmoid activation function. Calculation is based on:
 * <p>
 * y = 1/(1+ e^(-slope*x))
 */
public class SigmoidActivationFunction implements ActivationFunction {

    /**
     * Slope parameter
     */
    private double slope = 1d;

    /**
     * Creates a Sigmoid function with a slope parameter.
     *
     * @param slope slope parameter to be set
     */
    public SigmoidActivationFunction(double slope) {
        this.slope = slope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateOutput(double summedInput) {
        double denominator = 1 + Math.exp(-slope * summedInput);
        return (1d / denominator);
    }
}