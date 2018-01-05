package com.kansus.kstrainer.model;

import com.kansus.kstrainer.core.Project;

import java.io.File;

public class EvaluationResult {

    private File sample;
    private Character character;
    private Project network;
    private boolean isCorrect;
    private double highestOutput;
    private Character highestOutputCharacter;
    private double expectedCharacterOutput;
    private double outputsMean;
    private double[] outputs;

    public EvaluationResult(File sample, Character character, Project network) {
        this.sample = sample;
        this.character = character;
        this.network = network;
    }

    public Character getCharacter() {
        return character;
    }

    public File getSample() {
        return sample;
    }

    public Project getNetwork() {
        return network;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public double getHighestOutput() {
        return highestOutput;
    }

    public Character getHighestOutputCharacter() {
        return highestOutputCharacter;
    }

    public double getExpectedCharacterOutput() {
        return expectedCharacterOutput;
    }

    public double getOutputsMean() {
        return outputsMean;
    }

    public double[] getOutputs() {
        return outputs;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public void setNetwork(Project network) {
        this.network = network;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public void setHighestOutput(double highestOutput) {
        this.highestOutput = highestOutput;
    }

    public void setHighestOutputCharacter(Character highestOutputCharacter) {
        this.highestOutputCharacter = highestOutputCharacter;
    }

    public void setExpectedCharacterOutput(double expectedCharacterOutput) {
        this.expectedCharacterOutput = expectedCharacterOutput;
    }

    public void setOutputsMean(double outputsMean) {
        this.outputsMean = outputsMean;
    }

    public void setOutputs(double[] outputs) {
        this.outputs = outputs;
    }
}
