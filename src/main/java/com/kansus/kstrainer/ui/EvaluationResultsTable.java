package com.kansus.kstrainer.ui;

import com.kansus.kstrainer.model.Character;
import com.kansus.kstrainer.model.EvaluationResult;
import com.kansus.kstrainer.repository.CharacterRepository;
import com.kansus.kstrainer.ui.command.CompareNetworksCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class EvaluationResultsTable extends Table<EvaluationResult> {

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
