package com.kansus.kmlp.model;

/**
 * This class represents a pattern that is used to train a neural network.
 */
public class Pattern {

    private String mName;

    private double[] mData;

    private double[] mExpectedOutput;

    /**
     * Constructor of this class.
     *
     * @param name           The name of this pattern.
     * @param data           The data of this pattern.
     * @param expectedOutput The expected output for this pattern.
     */
    public Pattern(String name, double data[], double[] expectedOutput) {
        this.mName = name;
        this.mData = data;
        this.mExpectedOutput = expectedOutput;
    }

    /**
     * Returns the name of this pattern.
     *
     * @return The name of this pattern.
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the data of this pattern.
     *
     * @return The data of this pattern.
     */
    public double[] getData() {
        return mData;
    }

    /**
     * Returns the expected output for this pattern.
     *
     * @return The expected output for this pattern.
     */
    public double[] getExpectedOutput() {
        return mExpectedOutput;
    }
}