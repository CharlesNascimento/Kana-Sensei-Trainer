package com.kansus.kstrainer.ui;

import java.util.List;

public abstract class Table<T> {

    private List<T> items;

    private String[][] resultTable;

    public Table(int rows, int columns, List<T> items) {
        this.items = items;
        resultTable = new String[rows][columns + 1];
        buildHeader();
        buildContent();
    }

    private void buildHeader() {
        String[] rows = getHeaderColumn();

        for (int j = 0; j < rows.length; j++) {
            resultTable[j][0] = rows[j];
        }
    }

    private void buildContent() {
        for (int i = 0; i < items.size(); i++) {
            String[] rows = getColumn(items.get(i));

            for (int j = 0; j < rows.length; j++) {
                resultTable[j][i + 1] = rows[j];
            }
        }
    }

    public List<T> getItems() {
        return items;
    }

    abstract String[] getHeaderColumn();

    abstract String[] getColumn(T item);

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String lineBreak = System.getProperty("line.separator");

        builder.append(lineBreak);
        builder.append("---------------------------- GENERAL ----------------------------");
        builder.append(lineBreak);

        for (int i = 0; i < resultTable.length; i++) {
            if (i == 5) {
                builder.append(lineBreak);
                builder.append("---------------------------- OUTPUTS ----------------------------");
                builder.append(lineBreak);
            }

            for (int j = 0; j < resultTable[0].length; j++) {
                builder.append(String.format("%-16s", resultTable[i][j])).append(" | ");
            }

            builder.append(lineBreak);
        }

        return builder.toString();
    }
}
