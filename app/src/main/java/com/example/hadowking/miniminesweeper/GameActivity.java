package com.example.hadowking.miniminesweeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    LinearLayout rootLayout;

    Button resetButton;

    ProgressBar progressBar;

    private static int NO_OF_ROWS;

    private static int NO_OF_COLS;

    private static int NO_OF_MINES;

    private Square[][] squares;

    private TextView scoreTextView;

    private static final int[][] NEIGHBOUR =
            {

        {1, 0}, {1, 1}, {1, -1}, {0, 1}, {0, -1}, {-1, 0}, {-1, 1}, {-1, -1}

    };

    private int size;

    private int[][] board;

    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();

        int difficulty = intent.getIntExtra("difficulty", MainActivity.MEDIUM);

        switch (difficulty){

            case MainActivity.EASY:
                NO_OF_ROWS = 7;
                NO_OF_COLS = 6;
                NO_OF_MINES = 5;
                break;

            case MainActivity.MEDIUM:
                NO_OF_ROWS = 10;
                NO_OF_COLS = 8;
                NO_OF_MINES = 15;
                break;

            case MainActivity.HARD:
                NO_OF_ROWS = 15;
                NO_OF_COLS = 12;
                NO_OF_MINES = 24;
                break;

        }

        rootLayout = findViewById(R.id.rootLayout);

        scoreTextView = findViewById(R.id.scoreTextView);

        resetButton = findViewById(R.id.resetButton);

        progressBar = findViewById(R.id.progressBar);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        rootLayout.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);

        setUpBoard();

        initGame();

        progressBar.setVisibility(View.GONE);


        rootLayout.setVisibility(View.VISIBLE);

    }

    private void initGame() {
        
        score = 0;
        
        for(int i = 0; i < NO_OF_ROWS; i++){
            for(int j = 0; j < NO_OF_COLS; j++){
                
                board[i][j] = 0;
                
            }
        }
        
        setRandomMines();
        refreshBoard();
        
    }

    private void refreshBoard() {

        for(int i = 0; i < NO_OF_ROWS; i++){
            for(int j = 0; j < NO_OF_COLS; j++){

                squares[i][j].setUp(i, j, board[i][j]);

            }
        }

    }

    private void setRandomMines() {

        int mineCount = 0;

        Random random = new Random();

        while (mineCount < NO_OF_MINES){

            int randomGiver = random.nextInt(NO_OF_COLS * NO_OF_ROWS);

            int r = randomGiver / NO_OF_COLS;

            int c = randomGiver % NO_OF_COLS;

            if(board[r][c] != -1){

                board[r][c] = -1;

                increaseNeighbourValues(r, c);

                mineCount++;

            }

        }

    }

    private void increaseNeighbourValues(int row, int col) {

        for(int i = 0; i < NEIGHBOUR.length; i++){

            int temp[] = NEIGHBOUR[i];

            int neighbourRow = row + temp[0];
            int neighbourCol = col + temp[1];

            if(checkBounds(neighbourRow, neighbourCol) && board[neighbourRow][neighbourCol] != Square.MINE){

                board[neighbourRow][neighbourCol]++;

            }

        }

    }

    private boolean checkBounds(int neighbourRow, int neighbourCol) {

        if(neighbourRow >= 0 && neighbourRow < NO_OF_ROWS && neighbourCol >= 0 && neighbourCol < NO_OF_COLS)
            return  true;

        return false;

    }

    private void setUpBoard() {

        int width = getResources().getDisplayMetrics().widthPixels;

        size = width / NO_OF_COLS;

        squares = new Square[NO_OF_ROWS][NO_OF_COLS];

        board = new int[NO_OF_ROWS][NO_OF_COLS];

        for(int i = 0; i < NO_OF_ROWS; i++){

            LinearLayout row = new LinearLayout(this);

            row.setOrientation(LinearLayout.HORIZONTAL);

            for(int j = 0; j < NO_OF_COLS; j++){

                squares[i][j] = new Square(this);

                LinearLayout.LayoutParams squareParams = new LinearLayout.LayoutParams(size, size);

                squares[i][j].setLayoutParams(squareParams);

                squares[i][j].setOnClickListener(this);

                squares[i][j].setOnLongClickListener(this);
                
                row.addView(squares[i][j]);

            }
            
            rootLayout.addView(row);

        }

    }

    private void reset() {

        rootLayout.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);

        initGame();
        updateScore();

        progressBar.setVisibility(View.GONE);

        rootLayout.setVisibility(View.VISIBLE);

    }

    private void updateScore() {

        scoreTextView.setText("Score: " + score);

    }

    @Override
    public void onClick(View v) {

        Square square = (Square)v;

        if(!square.isRevealed() && !square.isFlagged()){

            square.reveal();

            if(square.isMine()){

                revealAll();
                saveScore();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Oops You Lost !");

                builder.setMessage("Wanna Play Again ? ");

                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(GameActivity.this, MainActivity.class));

                        finish();

                    }
                });

                builder.setNegativeButton("No", null);

                AlertDialog alertDialog = builder.create();

                alertDialog.show();


            }else{

                score++;
                
                if(square.isEmpty()){
                    revealNeighbours(square);
                }

                updateScore();
                
                checkIfGameCompleted();

            }

        }

    }

    private void checkIfGameCompleted() {

        int calculatedValue = (NO_OF_COLS * NO_OF_ROWS) - score;

        if(calculatedValue == NO_OF_MINES){

            revealAll();
            saveScore();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Congrats You Win !");

            builder.setMessage("Wanna Play Again ? ");

            builder.setCancelable(false);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    startActivity(new Intent(GameActivity.this, MainActivity.class));

                    finish();

                }
            });

            builder.setNegativeButton("No", null);

            AlertDialog alertDialog = builder.create();

            alertDialog.show();



        }

    }

    private void revealNeighbours(Square square) {

        int row = square.getRow();

        int col = square.getCol();

        for(int i = 0; i < NEIGHBOUR.length; i++){

            int temp[] = NEIGHBOUR[i];

            int neighbourRow = row + temp[0];
            int neighbourCol = col + temp[1];

            if(checkBounds(neighbourRow, neighbourCol)){

                Square neighbourSquare = squares[neighbourRow][neighbourCol];

                if(!neighbourSquare.isRevealed()){

                    neighbourSquare.reveal();

                    score++;

                    if(neighbourSquare.isEmpty())

                        revealNeighbours(neighbourSquare);

                }

            }

        }

    }

    private void saveScore() {

        getSharedPreferences("minesweeper", MODE_PRIVATE).edit().putInt("score", score).commit();

    }

    private void revealAll() {

        for(int i = 0; i < NO_OF_ROWS; i++){
            for(int j = 0; j < NO_OF_COLS; j++){

                if(!squares[i][j].isRevealed())
                    squares[i][j].reveal();

            }
        }

    }

    @Override
    public boolean onLongClick(View v) {

        Square square = (Square)v;

        square.setFlag(!square.isFlagged());

        return false;
    }
}
