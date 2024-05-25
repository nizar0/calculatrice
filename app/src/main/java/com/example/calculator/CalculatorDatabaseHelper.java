package com.example.calculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CalculatorDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Calculator.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CalculatorContract.HistoryEntry.TABLE_NAME + " (" +
                    CalculatorContract.HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                    CalculatorContract.HistoryEntry.COLUMN_EXPRESSION + " TEXT," +
                    CalculatorContract.HistoryEntry.COLUMN_RESULT + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CalculatorContract.HistoryEntry.TABLE_NAME;

    public CalculatorDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
