package com.kansus.kstrainer.ui.command;

import com.kansus.kstrainer.NeuralNetworkFacade;
import com.kansus.kstrainer.core.Test;
import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

public class EvaluateNetworkCommand implements Command {

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
            String testName = args[2];
            Test test = Workspace.getInstance().getCurrentProject().getTestNamed(testName);

            NeuralNetworkFacade neuralNetworkFacade = new NeuralNetworkFacade();
            neuralNetworkFacade.evaluate(test);
        } else if (args[1].equals("strokes")) {
            String testName = args[2];
            Test test = Workspace.getInstance().getCurrentProject().getTestNamed(testName);

            NeuralNetworkFacade neuralNetworkFacade = new NeuralNetworkFacade();
            neuralNetworkFacade.evaluateStrokesNetwork(test);
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

    private static boolean validateEvaluateNetworkArgs(String[] args) {
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
