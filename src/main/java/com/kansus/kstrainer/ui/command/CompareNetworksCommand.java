package com.kansus.kstrainer.ui.command;

import com.kansus.kstrainer.core.Project;
import com.kansus.kstrainer.core.Test;
import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.logging.Log;
import com.kansus.kstrainer.model.Character;
import com.kansus.kstrainer.model.EvaluationResult;
import com.kansus.kstrainer.ui.EvaluationResultsTable;
import com.kansus.kstrainer.util.FileUtils;
import com.kansus.kstrainer.util.Utils;
import com.kansus.kstrainer.util.ValidationUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompareNetworksCommand implements Command {

    private String[] args;

    public CompareNetworksCommand(String[] args) {
        this.args = args;
    }

    @Override
    public void execute() {
        if (!validate()) {
            return;
        }

        List<List<EvaluationResult>> networksResults = evaluateAllNetworks();
        List<EvaluationResultsTable> resultsTables = buildResultsTables(networksResults);
        logAllTables(resultsTables);
        logFinalResults(networksResults);
    }

    private List<List<EvaluationResult>> evaluateAllNetworks() {
        List<List<EvaluationResult>> networksResults = new ArrayList<>();
        Collection<Project> allProjects = Workspace.getInstance().getProjects().values();
        String testName = args[1];

        for (Project project : allProjects) {
            EvaluateNetworkCommand evaluateNetworkCommand = new EvaluateNetworkCommand(args);
            Test test = project.getTestNamed(testName);
            List<EvaluationResult> result = evaluateNetworkCommand.evaluate(test);

            networksResults.add(result);
        }

        return networksResults;
    }

    private List<EvaluationResultsTable> buildResultsTables(List<List<EvaluationResult>> networksResults) {
        List<EvaluationResultsTable> resultsTables = new ArrayList<>();

        for (int i = 0; i < networksResults.get(0).size(); i++) {
            List<EvaluationResult> resultsOfCharacter = new ArrayList<>();

            for (List<EvaluationResult> networkResult : networksResults) {
                EvaluationResult characterResult = networkResult.get(i);
                resultsOfCharacter.add(characterResult);
            }

            resultsTables.add(new EvaluationResultsTable(resultsOfCharacter));
        }
        return resultsTables;
    }

    private void logAllTables(List<EvaluationResultsTable> resultsTables) {
        File comparisonResultDir = Workspace.getInstance().getComparisonResultDirectory();

        for (EvaluationResultsTable table : resultsTables) {
            try {
                Character character = table.getItems().get(0).getCharacter();
                String sample = FileUtils.getFilenameWithoutExtension(table.getItems().get(0).getSample());
                Log.setFile(new File(comparisonResultDir, sample + ".txt"));

                Log.writeln("Comparison results for sample " + sample + " -> " + character);
                Log.writeln(table.toString());

                Log.saveFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void logFinalResults(List<List<EvaluationResult>> networksResults) {
        try {
            File comparisonResultDir = Workspace.getInstance().getComparisonResultDirectory();
            Log.setFile(new File(comparisonResultDir, "results.txt"));

            for (List<EvaluationResult> networkResult : networksResults) {
                String network = networkResult.get(0).getNetwork().getRootDirectory().getName();
                int matchesCount = 0;

                for (EvaluationResult characterResult : networkResult) {
                    if (characterResult.isCorrect()) {
                        matchesCount++;
                    }
                }

                int misses = (networkResult.size() - matchesCount);
                double accuracy = ((double) matchesCount / (double) networkResult.size()) * 100;
                Log.writeln("Network: " + network + " | Matches: " + matchesCount + " | Misses: " + misses + " | Accuracy: " + accuracy);
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
            System.err.println("<INFO>    Command syntax: evaluate-network pixels <network-config-file>");
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