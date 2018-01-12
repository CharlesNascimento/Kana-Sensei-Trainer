package com.kansus.kstrainer.ui;

import com.kansus.kstrainer.ui.command.*;

class CommandFactory {

    Command create(String[] args) {
        switch (args[0]) {
            case "train":
                return new TrainNetworkCommand(args);
            case "evaluate":
                return new EvaluateSimpleCommand(args);
            case "evaluate-network":
                return new EvaluateNetworkCommand(args);
            case "compare-networks":
                return new CompareNetworksCommand(args);
            case "generate-references":
                return new GenerateReferencesCommand(args);
            case "evaluate-similarities":
                return new EvaluateSimilaritiesCommand(args);
            default:
                System.err.println("<ERROR>   Invalid command.");
                return null;
        }
    }
}
