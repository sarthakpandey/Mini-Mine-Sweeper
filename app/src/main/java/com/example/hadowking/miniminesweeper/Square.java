package com.example.hadowking.miniminesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

public class Square extends Button {

    public static final int MINE = -1;

    private int row;

    private int col;

    private int value = 0;

    private boolean revealed = false;

    private boolean flagged = false;

    public Square(Context context) {
        super(context);

        setPadding(0,0,0,0);
    }

    public void setUp(int row, int col, int value){

        this.row = row;
        this.col = col;
        this.value = value;

        this.revealed = this.flagged = false;

    }

    public void reveal(){

        revealed = true;
        flagged = false;

        invalidate();

    }

    public void setFlag(boolean flag){

        if(!revealed){

            flagged = flag;

            invalidate();

        }

    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isFlagged(){
        return  flagged;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public boolean isMine(){
        return value == MINE;
    }

    public boolean isEmpty(){
        return value == 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(revealed){

            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.reveal_background));

            if(isMine()){
                setText("M");
            }else if(value == 0){
                setText(" ");
            }else{
                setText(String.valueOf(value));
            }
        }else{



            if(flagged){

                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.flag_background));

                setText("!");
            }else{

                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.normal_background));

                setText(" ");
            }

        }
    }
}
