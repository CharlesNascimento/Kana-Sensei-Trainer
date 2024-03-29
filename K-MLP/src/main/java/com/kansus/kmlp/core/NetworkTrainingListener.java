package com.kansus.kmlp.core;

/**
 * A listener that notifies its creator about changes in the neural network training progress.
 */
public interface NetworkTrainingListener {

    /**
     * Invoked when a training has started.
     */
    void onTrainingStarted();

    void onTrainingProgressChanged(int epochs, double error);

    void onTrainingCompleted(int epochs, double error, long totalTime);
}
