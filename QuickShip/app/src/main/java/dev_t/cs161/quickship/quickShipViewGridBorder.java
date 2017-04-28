package dev_t.cs161.quickship;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class quickShipViewGridBorder extends View {

    private Point screen = new Point();
    private Float screenWidth;
    private Float boardGridFrameStartX;
    private Float boardGridFrameStartY;
    private Float boardGridFrameEndX;
    private Float boardGridFrameEndY;
    private Paint boardGridFrameBorderPaint;
    private Float boardGridFrameBorderStrokeWidth;
    private Float boardGridFrameMargin;
    private Paint titlePaint;
    private Float mTitleHeight;
    private quickShipActivityMain mContext;
<<<<<<< HEAD
    private int mFrameColor;

    public quickShipViewGridBorder(quickShipActivityMain context, int frameColor) {
=======
    private int boardType;

    public quickShipViewGridBorder(quickShipActivityMain context, int boardType) {
>>>>>>> origin/master
        super(context);
        mContext = context;
        mFrameColor = frameColor;
        Display display = context.getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        calculateBoardGUIPositions();
        this.boardType = boardType;
    }

    public void calculateBoardGUIPositions() {
        boardGridFrameBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridFrameBorderPaint.setStyle(Paint.Style.STROKE);
        int dpSize =  2;
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics() ;
        boardGridFrameBorderStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, dm);
        boardGridFrameBorderPaint.setStrokeWidth(boardGridFrameBorderStrokeWidth);
<<<<<<< HEAD
        boardGridFrameBorderPaint.setColor(mFrameColor);
=======
        //boardGridFrameBorderPaint.setColor(Color.BLACK);

        switch (boardType){
            case 0: boardGridFrameBorderPaint.setColor(Color.BLACK);
                    break;
            case 1: boardGridFrameBorderPaint.setColor(mContext.getResources().getColor(R.color.play_mode_opponent_grid));
                    break;
            case 2: boardGridFrameBorderPaint.setColor(mContext.getResources().getColor(R.color.play_mode_player_grid));
                    break;
            default: boardGridFrameBorderPaint.setColor(Color.WHITE);
                    break;
        }
>>>>>>> origin/master

        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(16 * getResources().getDisplayMetrics().density);

        screenWidth = (float) screen.x;

        boardGridFrameMargin = (screenWidth - (screenWidth * (float) 0.9)) / 2;

        mTitleHeight = titlePaint.getTextSize();

        boardGridFrameStartX = boardGridFrameMargin;
        boardGridFrameStartY = boardGridFrameMargin + mTitleHeight;
        boardGridFrameEndX = boardGridFrameMargin + (screenWidth * (float) 0.9);
        boardGridFrameEndY = boardGridFrameMargin + (screenWidth * (float) 0.9);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(boardGridFrameStartX, boardGridFrameStartY, boardGridFrameEndX, boardGridFrameEndY, boardGridFrameBorderPaint);
    }

}