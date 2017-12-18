package com.kansus.kstrainer;

import com.kansus.kstrainer.model.TrainingConfig;
import com.kansus.kstrainer.util.FileUtils;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

import java.io.File;

class EvaluateNetworkCommand implements Command {

    private String[] args;

    public EvaluateNetworkCommand(String[] args) {
        this.args = args;
    }

    @Override
    public void execute() {
        if (!validate()) {
            return;
        }

        if (args[1].equals("pixels")) {
            File configFile = new File(args[2]);
            TrainingConfig trainingConfig = FileUtils.loadTrainingConfiguration(configFile);

            NeuralNetworkFacade neuralNetworkFacade = new NeuralNetworkFacade();
            neuralNetworkFacade.evaluatePixelsNetwork(trainingConfig);
        } else if (args[1].equals("strokes")) {
            File configFile = new File(args[2]);
            TrainingConfig trainingConfig = FileUtils.loadTrainingConfiguration(configFile);

            NeuralNetworkFacade neuralNetworkFacade = new NeuralNetworkFacade();
            neuralNetworkFacade.evaluateStrokesNetwork(trainingConfig);
        }
    }

    @Override
    public boolean validate() {
        if (args.length != 3) {
            System.err.println("<ERROR>   Incorrect number of parameters.");
            System.err.println("<INFO>    Command syntax: evaluate-network pixels <network-config-file>");
            return false;
        }

        if (!Utils.equalsAny(args[1], ValidationUtils.validNeuralNetworks)) {
            System.err.println("<ERROR>   Invalid neural network: " + args[1]);
            return false;
        }

        return true;
    }
}
