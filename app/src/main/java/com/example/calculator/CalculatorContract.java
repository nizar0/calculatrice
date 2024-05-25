package com.example.calculator;

import android.provider.BaseColumns;

public final class CalculatorContract {

    private CalculatorContract() {}

    public static class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_EXPRESSION = "expression";
        public static final String COLUMN_RESULT = "result";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                    HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                    HistoryEntry.COLUMN_EXPRESSION + " TEXT," +
                    HistoryEntry.COLUMN_RESULT + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HistoryEntry.TABLE_NAME;
}
