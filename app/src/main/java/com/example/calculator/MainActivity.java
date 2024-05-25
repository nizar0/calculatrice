package com.example.calculator;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tvResult;
    StringBuilder expression;
    ArrayList<String> historyList;
    CalculatorDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        expression = new StringBuilder();
        historyList = new ArrayList<>();
        dbHelper = new CalculatorDatabaseHelper(this);

        Button btn0 = findViewById(R.id.btn0);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnSubtract = findViewById(R.id.btnSubtract);
        Button btnMultiply = findViewById(R.id.btnMultiply);
        Button btnDivide = findViewById(R.id.btnDivide);
        Button btnEquals = findViewById(R.id.btnEquals);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnDecimal = findViewById(R.id.btnDecimal);
        Button btnSquare = findViewById(R.id.btnSquare);
        Button btnSquareRoot = findViewById(R.id.btnSquareRoot);
        Button historyButton = findViewById(R.id.historyButton);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String buttonText = button.getText().toString();

                int id = v.getId();
                if (id == R.id.btnClear) {
                    expression = new StringBuilder();
                } else if (id == R.id.btnDelete) {
                    if (expression.length() > 0)
                        expression.deleteCharAt(expression.length() - 1);
                } else if (id == R.id.btnEquals) {

                    try {
                        double result = evaluate(expression.toString());
                        String resultString = result % 1 == 0 ? String.valueOf((int) result) : String.valueOf(result);
                        saveToDatabase(expression.toString(), resultString); // Save expression and result
                        expression = new StringBuilder(resultString);
                    } catch (Exception e) {
                        expression = new StringBuilder("Error");
                    }
                } else if (id == R.id.btnSquare) {
                    try {
                        double result = Math.pow(Double.parseDouble(expression.toString()), 2);
                        String resultString = result % 1 == 0 ? String.valueOf((int) result) : String.valueOf(result);
                        historyList.add(expression.toString() + "^2 = " + resultString);
                        expression = new StringBuilder(resultString);
                        saveToDatabase(expression.toString(), resultString);
                    } catch (Exception e) {
                        expression = new StringBuilder("Error");
                    }
                } else if (id == R.id.btnSquareRoot) {
                    try {
                        double result = Math.sqrt(Double.parseDouble(expression.toString()));
                        String resultString = result % 1 == 0 ? String.valueOf((int) result) : String.valueOf(result);
                        historyList.add("sqrt(" + expression.toString() + ") = " + resultString);
                        expression = new StringBuilder(resultString);
                        saveToDatabase(expression.toString(), resultString);
                    } catch (Exception e) {
                        expression = new StringBuilder("Error");
                    }
                } else {
                    expression.append(buttonText);
                }
                tvResult.setText(expression.toString());
            }
        };

        btn0.setOnClickListener(onClickListener);
        btn1.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        btn3.setOnClickListener(onClickListener);
        btn4.setOnClickListener(onClickListener);
        btn5.setOnClickListener(onClickListener);
        btn6.setOnClickListener(onClickListener);
        btn7.setOnClickListener(onClickListener);
        btn8.setOnClickListener(onClickListener);
        btn9.setOnClickListener(onClickListener);
        btnAdd.setOnClickListener(onClickListener);
        btnSubtract.setOnClickListener(onClickListener);
        btnMultiply.setOnClickListener(onClickListener);
        btnDivide.setOnClickListener(onClickListener);
        btnEquals.setOnClickListener(onClickListener);
        btnClear.setOnClickListener(onClickListener);
        btnDelete.setOnClickListener(onClickListener);
        btnDecimal.setOnClickListener(onClickListener);
        btnSquare.setOnClickListener(onClickListener);
        btnSquareRoot.setOnClickListener(onClickListener);

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                intent.putStringArrayListExtra("historyList", historyList);
                startActivity(intent);
            }
        });
    }

    private void saveToDatabase(String expression, String result) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CalculatorContract.HistoryEntry.COLUMN_EXPRESSION, expression);
        values.put(CalculatorContract.HistoryEntry.COLUMN_RESULT, result);
        long newRowId = db.insert(CalculatorContract.HistoryEntry.TABLE_NAME, null, values);
    }
    public static double evaluate(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }



}
