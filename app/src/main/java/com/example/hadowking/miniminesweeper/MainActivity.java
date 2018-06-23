package com.example.hadowking.miniminesweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    RadioGroup radioGroup;

    Button start;

    TextView scoreTextView;

    public static final int EASY = 0;
    public static final int MEDIUM = 1;
    public static final int HARD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = findViewById(R.id.radioGroup);

        start = findViewById(R.id.startButton);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, GameActivity.class);

                int id = radioGroup.getCheckedRadioButtonId();

                int difiiculty = MEDIUM;

                switch (id){

                    case R.id.easyRadio : difiiculty = EASY; break;

                    case R.id.mediumRadio : difiiculty = MEDIUM; break;

                    case R.id.hardRadio : difiiculty = HARD; break;

                }

                i.putExtra("difficulty", difiiculty);

                startActivity(i);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        showLastScore();
    }

    private void showLastScore() {

        SharedPreferences sharedPreferences = getSharedPreferences("minesweeper",MODE_PRIVATE);

        int score = sharedPreferences.getInt("score", 0);

        scoreTextView = findViewById(R.id.scoreTextView);

        scoreTextView.setText("Last Score: " + score);

    }
}
