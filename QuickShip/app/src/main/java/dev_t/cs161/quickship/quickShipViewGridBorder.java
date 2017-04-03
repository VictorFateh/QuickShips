package dev_t.cs161.quickship;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
    private int boardGridFrameBorderStrokeWidth;
    private Float boardGridFrameMargin;
    private Paint titlePaint;
    private Float mTitleHeight;

    public quickShipViewGridBorder(quickShipActivityMain context) {
        super(context);
        Display display = context.getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        calculateBoardGUIPositions();
    }

    public void calculateBoardGUIPositions() {
        boardGridFrameBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridFrameBorderPaint.setStyle(Paint.Style.STROKE);
        boardGridFrameBorderStrokeWidth = 15;
        boardGridFrameBorderPaint.setStrokeWidth(boardGridFrameBorderStrokeWidth);
        boardGridFrameBorderPaint.setColor(Color.BLACK);

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