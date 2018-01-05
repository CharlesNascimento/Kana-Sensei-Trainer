package com.kansus.kstrainer.ui.command;

import com.kansus.kstrainer.NeuralNetworkFacade;
import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.model.NeuralNetworkConfig;
import com.kansus.kstrainer.logging.Log;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TrainNetworkCommand implements Command {

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
            NeuralNetworkConfig neuralNetworkConfig = Workspace.getInstance().getCurrentProject().getConfiguration();
            NeuralNetworkFacade trainer = new NeuralNetworkFacade();

            File outputDir = Workspace.getInstance().getCurrentProject().getOutputDirectory();
            FileWriter fw = new FileWriter(new File(outputDir, "log.txt"));
            Log.setWriter(new BufferedWriter(fw));

            if (args[1].equals("pixels")) {
                trainer.train(neuralNetworkConfig);
            } else if (args[1].equals("strokes")) {
                trainer.trainStrokesNetwork(neuralNetworkConfig);
            }

            Log.saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean validate() {
        if (args.length != 2) {
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

    private static boolean validateTrainingsConfigs(NeuralNetworkConfig neuralNetworkConfig) {
        if (!neuralNetworkConfig.getWeightsFile().exists()) {
            System.out.println("<WARNING> Weights file not found, a new file will be created.");
        }

        if (!neuralNetworkConfig.getWeightsFile().getName().endsWith("mlp")) {
            System.err.println("<ERROR>   The weights file should be a valid .mlp file.");
            return false;
        }

        for (File trainingInput : neuralNetworkConfig.getInputs()) {
            if (!trainingInput.exists()) {
                System.out.println("<ERROR>   Input file \'" + trainingInput.getName() + "\' not found.");
                return false;
            }
        }

        return true;
    }
}
