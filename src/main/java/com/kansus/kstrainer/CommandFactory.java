package com.kansus.kstrainer;

class CommandFactory {

    Command create(String[] args) {
        switch (args[0]) {
            case "train":
                return new TrainNetworkCommand(args);
            case "evaluate":
                return new EvaluateSimpleCommand(args);
            case "evaluate-network":
                return new EvaluateNetworkCommand(args);
            default:
                System.err.println("<ERROR>   Invalid command.");
                return null;
        }
    }
}
