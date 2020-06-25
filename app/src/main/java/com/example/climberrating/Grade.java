package com.example.climberrating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class Grade extends AppCompatActivity {

    EditText problemGradeEditText;
    EditText attemptsEditText;
    EditText repeatsEditText;
    EditText resetGradeEditText;
    TextView gymNameTextView;
    TextView resultTextView;
    Button newGradeBtn;
    Button undoBtn;
    Button setBtn;

    //climberGrade 4 climbing a problemGrade4 -> 50% chance of flash
    float climberGrade;
    float previousGrade;
    HashMap<String,float[]> hmap;
    String gymName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        problemGradeEditText = (EditText) findViewById(R.id.problemGradeEditText);
        attemptsEditText = (EditText) findViewById(R.id.attemptsEditText);
        repeatsEditText = (EditText) findViewById(R.id.repeatsEditText);
        resetGradeEditText = (EditText) findViewById(R.id.resetGradeEditText);
        gymNameTextView = (TextView) findViewById(R.id.gymNameTextView);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        newGradeBtn = (Button) findViewById(R.id.newGradeBtn);
        undoBtn = (Button) findViewById(R.id.undoBtn);
        setBtn = (Button) findViewById(R.id.setBtn);
        hmap = getHashMap("map");
        gymName = getIntent().getStringExtra("id");
        gymNameTextView.setText(gymName);
        loadData();
        updateViews();

        newGradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String problemGradeString = problemGradeEditText.getText().toString();
                String attemptsString = attemptsEditText.getText().toString();
                String repeatsString = repeatsEditText.getText().toString();

                if (problemGradeString.isEmpty() || attemptsString.isEmpty() || repeatsString.isEmpty()) {

                } else {
                    int problemGrade = Integer.parseInt(problemGradeEditText.getText().toString());
                    int attempts = Integer.parseInt(attemptsEditText.getText().toString());
                    int repeats = Integer.parseInt(repeatsEditText.getText().toString());


                    double actualGrade = problemGrade - (repeats / (repeats + 1));
                    double p = 1.0 / (1 + Math.pow(10, actualGrade - climberGrade));
                    previousGrade = climberGrade;
                    // treat it as unsent
                    if(attempts == 0) {
                        climberGrade += 0.1 * (-p);
                    } else {
                        climberGrade += 0.1 * (1.0 / attempts - p);
                    }
                    resultTextView.setText(climberGrade + "");
                    saveData();
                }
            }
        });

        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                climberGrade = previousGrade;
                resultTextView.setText(climberGrade+ "");
                saveData();
            }
        });

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resetGradeString = resetGradeEditText.getText().toString();

                if (resetGradeString.isEmpty()) {

                } else {
                    double resetGrade = Double.parseDouble(resetGradeString);
                    previousGrade = climberGrade;
                    climberGrade = (float) resetGrade;
                    resultTextView.setText(climberGrade + "");
                    saveData();
                }
            }
        });
    }

    public void saveData() {
        hmap.put(gymName, new float[] {climberGrade, previousGrade});
        saveHashMap("map", hmap);
    }

    public void loadData() {
        climberGrade = hmap.get(gymName)[0];
        previousGrade = hmap.get(gymName)[1];
    }

    public void updateViews() {
        resultTextView.setText(climberGrade + "");
    }

    public void saveHashMap(String key, Object obj) {
        SharedPreferences prefs = this.getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString(key,json);
        editor.apply();
    }

    public HashMap<String,float[]> getHashMap(String key) {
        SharedPreferences prefs = this.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key,"");
        java.lang.reflect.Type type = new TypeToken<HashMap<String,float[]>>(){}.getType();
        HashMap<String,float[]> obj = gson.fromJson(json, type);
        return obj;
    }
}