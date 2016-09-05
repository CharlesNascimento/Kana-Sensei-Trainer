package com.kansus.mlp;

/**
 * A listener that notifies its creator about changes in the neural network training progress.
 */
public interface NetworkTrainingProgressListener {

    /**
     * Invoked when a training has started.
     */
    void onTrainingStarted();

    void onTrainingProgressChanged(int progress, int epochs, double error);

    void onTrainingCompleted(int epochs, double error, long totalTime);
}
