package com.kansus.kstrainer.ui.command;

import com.kansus.kstrainer.NeuralNetworkFacade;
import com.kansus.kstrainer.core.Project;
import com.kansus.kstrainer.core.Test;
import com.kansus.kstrainer.core.Workspace;
import com.kansus.kstrainer.logging.Log;
import com.kansus.kstrainer.model.Character;
import com.kansus.kstrainer.model.EvaluationResult;
import com.kansus.kstrainer.repository.CharacterRepository;
import com.kansus.kstrainer.util.*;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
            NeuralNetworkFacade neuralNetworkFacade = new NeuralNetworkFacade();
            Test test = project.getTestNamed(testName);
            List<EvaluationResult> result = neuralNetworkFacade.evaluatePixelsNetwork(test);

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
                Character character = table.items.get(0).getCharacter();
                String sample = FileUtils.getFilenameWithoutExtension(table.items.get(0).getSample());
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

    class EvaluationResultsTable extends Table<EvaluationResult> {

        public EvaluationResultsTable(List<EvaluationResult> results) {
            super(5 + 92, results.size(), results);
        }

        @Override
        String[] getHeaderColumn() {
            String[] columns = new String[5 + 92];
            List<Character> allCharacters = new CharacterRepository().getAll();

            columns[0] = "Network";
            columns[1] = "Result";
            columns[2] = "Highest output";
            columns[3] = "Expected output";
            columns[4] = "Outputs mean";

            for (int k = 5; k < columns.length; k++) {
                columns[k] = allCharacters.get(k - 5).toString();
            }

            return columns;
        }

        @Override
        String[] getColumn(EvaluationResult item) {
            String[] columns = new String[5 + 92];
            NumberFormat formatter = new DecimalFormat("#0.0000000");

            String highestKana = item.getHighestOutputCharacter().getKana();
            columns[0] = item.getNetwork().getRootDirectory().getName();
            columns[1] = item.isCorrect() ? "OK" : "FAIL";
            columns[2] = formatter.format(item.getHighestOutput()) + " (" + highestKana + ")";
            columns[3] = formatter.format(item.getExpectedCharacterOutput());
            columns[4] = formatter.format(item.getOutputsMean());

            for (int k = 0; k < item.getOutputs().length; k++) {
                columns[k + 5] = String.format("%-16s", formatter.format(item.getOutputs()[k]));
            }

            return columns;
        }
    }

    abstract class Table<T> {

        List<T> items;

        String[][] resultTable;

        public Table(int rows, int columns, List<T> items) {
            this.items = items;
            resultTable = new String[rows][columns + 1];
            buildHeader();
            buildContent();
        }

        public void buildHeader() {
            String[] rows = getHeaderColumn();

            for (int j = 0; j < rows.length; j++) {
                resultTable[j][0] = rows[j];
            }
        }

        public void buildContent() {


            for (int i = 0; i < items.size(); i++) {
                String[] rows = getColumn(items.get(i));

                for (int j = 0; j < rows.length; j++) {
                    resultTable[j][i + 1] = rows[j];
                }
            }
        }

        abstract String[] getHeaderColumn();

        abstract String[] getColumn(T item);

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            builder.append("\n---------------------------- GENERAL ----------------------------\n");

            for (int i = 0; i < resultTable.length; i++) {
                if (i == 5) {
                    builder.append("\n---------------------------- OUTPUTS ----------------------------\n");
                }

                for (int j = 0; j < resultTable[0].length; j++) {
                    builder.append(String.format("%-16s", resultTable[i][j])).append(" | ");
                }

                builder.append("\n");
            }

            return builder.toString();
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