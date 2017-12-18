package com.kansus.kstrainer;

import com.kansus.kstrainer.model.TrainingConfig;
import com.kansus.kstrainer.util.FileUtils;
import com.kansus.kstrainer.util.Log;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class TrainNetworkCommand implements Command {

    private String[] args;

    public TrainNetworkCommand(String[] args) {
        this.args = args;
    }

    @Override
    public void execute() {
        if (!validate()) {
            return;
        }

        try {
            File configFile = new File(args[2]);
            TrainingConfig trainingConfig = FileUtils.loadTrainingConfiguration(configFile);
            NeuralNetworkFacade trainer = new NeuralNetworkFacade();

            FileWriter fw = new FileWriter(new File(configFile.getParent(), "log.txt"));
            Log.setWriter(new BufferedWriter(fw));

            if (args[1].equals("pixels")) {
                trainer.trainPixelsNetwork(trainingConfig);
            } else if (args[1].equals("strokes")) {
                trainer.trainStrokesNetwork(trainingConfig);
            }

            Log.closeWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean validate() {
        if (args.length != 3) {
            System.err.println("<ERROR>   Incorrect number of parameters.");
            System.err.println("<INFO>    Command syntax: train <neural-network> <config-file>");
            return false;
        }

        if (!Utils.equalsAny(args[1], ValidationUtils.validNeuralNetworks)) {
            System.err.println("<ERROR>   Invalid neural network: " + args[1]);
            return false;
        }

        return true;
    }
}
