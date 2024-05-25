package com.example.calculator;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;


public class HistoryActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    List<String> historyDataList;
    CalculatorDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView historyListView = findViewById(R.id.historyListView);
        Button btnDeleteAllHistory = findViewById(R.id.btnDeleteAllHistory);

        historyDataList = new ArrayList<>();

        dbHelper = new CalculatorDatabaseHelper(this);
        loadHistoryData();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyDataList);
        historyListView.setAdapter(adapter);

        btnDeleteAllHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllHistory();
            }
        });
    }

    private void loadHistoryData() {
        historyDataList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                CalculatorContract.HistoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String expression = cursor.getString(cursor.getColumnIndexOrThrow(CalculatorContract.HistoryEntry.COLUMN_EXPRESSION));
            double result = cursor.getDouble(cursor.getColumnIndexOrThrow(CalculatorContract.HistoryEntry.COLUMN_RESULT));
            String historyItem = expression + " = " + result;
            historyDataList.add(historyItem);
        }
        cursor.close();
    }

    private void deleteAllHistory() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete(CalculatorContract.HistoryEntry.TABLE_NAME, null, null);

        if (deletedRows > 0) {
            historyDataList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Tout l'historique supprimé", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Aucun historique à supprimer", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
