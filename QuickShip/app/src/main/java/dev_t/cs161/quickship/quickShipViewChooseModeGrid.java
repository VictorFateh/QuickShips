package dev_t.cs161.quickship;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class quickShipViewChooseModeGrid extends View {

    private Point screen = new Point();
    private Context mContext;
    private volatile boolean held;
    private volatile Float initialX, initialY;
    private volatile Float endX, endY;
    private Float screenWidth;
    private Float screenHeight;
    private Paint boardGridFramePaint;
    private Float boardGridFrameStartX;
    private Float boardGridFrameStartY;
    private Float boardGridFrameEndX;
    private Float boardGridFrameEndY;
    private Paint boardGridFrameBorderPaint;
    private int boardGridFrameBorderStrokeWidth;
    private Float boardGridCellWidth;
    private Float boardGridCellHeight;
    private Paint boardGridLinePaint;
    private int boardGridLinePaintStrokeWidth;
    private Float boardGridFrameDividerX[];
    private Float boardGridFrameDividerY[];
    private int currentIndex;
    private int selectedIndex;
    private Paint boardGridSelectedPaint;
    private Float boardGridSelectedStartX;
    private Float boardGridSelectedStartY;
    private Float boardGridSelectedEndX;
    private Float boardGridSelectedEndY;
    private Float boardGridFrameMargin;
    private Float viewWidth;
    private Float viewHeight;
    private Paint titlePaint;
    private String mTitle;
    private Float mTitleWidth;
    private Float mTitleHeight;
    private Float mTitleX;
    private Float mTitleY;
    private quickShipModel mplayerBoardData;
    private quickShipModelBoard mTemporaryBoard;
    private ArrayList<quickShipModelBoardSlot> shipList;
    private quickShipModelBoardSlot currentSelectedPiece;


    public quickShipViewChooseModeGrid(Context context, quickShipModel playerBoardData) {
        super(context);
        mContext = context;
        mplayerBoardData = playerBoardData;
        mTemporaryBoard = new quickShipModelBoard();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        initializeValues();
        calculateBoardGUIPositions();
    }

    public void initializeValues() {
        shipList = new ArrayList<>();
        quickShipModelBoardSlot piece1 = new quickShipModelBoardSlot();
        piece1.setShipType(ShipType.TWO);
        piece1.setAnchor(true);
        quickShipModelBoardSlot piece2 = new quickShipModelBoardSlot();
        piece2.setShipType(ShipType.THREE_A);
        piece2.setAnchor(true);
        quickShipModelBoardSlot piece3 = new quickShipModelBoardSlot();
        piece3.setShipType(ShipType.THREE_B);
        piece3.setAnchor(true);
        quickShipModelBoardSlot piece4 = new quickShipModelBoardSlot();
        piece4.setShipType(ShipType.FOUR);
        piece4.setAnchor(true);
        quickShipModelBoardSlot piece5 = new quickShipModelBoardSlot();
        piece5.setShipType(ShipType.FIVE);
        piece5.setAnchor(true);
        shipList.add(piece1);
        shipList.add(piece2);
        shipList.add(piece3);
        shipList.add(piece4);
        shipList.add(piece5);

        mTitle = getContext().getResources().getString(R.string.choose_mode_grid_title);
        held = true;
        currentIndex = -1;

        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(16 * getResources().getDisplayMetrics().density);

        boardGridFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridFramePaint.setStyle(Paint.Style.FILL);
        boardGridFramePaint.setColor(mContext.getResources().getColor(R.color.choose_mode_grid));
        boardGridFrameBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridFrameBorderPaint.setStyle(Paint.Style.STROKE);
        boardGridFrameBorderStrokeWidth = 15;
        boardGridFrameBorderPaint.setStrokeWidth(boardGridFrameBorderStrokeWidth);
        boardGridFrameBorderPaint.setColor(Color.BLACK);

        boardGridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridLinePaint.setStyle(Paint.Style.STROKE);
        boardGridLinePaintStrokeWidth = 1;
        boardGridLinePaint.setStrokeWidth(boardGridLinePaintStrokeWidth);
        boardGridLinePaint.setColor(mContext.getResources().getColor(R.color.choose_mode_grid_line));

        boardGridSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridSelectedPaint.setStyle(Paint.Style.FILL);
        boardGridSelectedPaint.setColor(mContext.getResources().getColor(R.color.choose_mode_cell_selected));
        boardGridFrameDividerX = new Float[11];
        boardGridFrameDividerY = new Float[11];
    }

    public void calculateBoardGUIPositions() {
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;

        boardGridFrameMargin = (screenWidth - (screenWidth * (float) 0.9)) / 2;

        mTitleHeight = titlePaint.getTextSize();
        mTitleWidth = titlePaint.measureText(mTitle);
        mTitleX = boardGridFrameMargin;
        mTitleY = mTitleHeight + (mTitleHeight / 2);

        boardGridFrameStartX = boardGridFrameMargin;
        boardGridFrameStartY = boardGridFrameMargin + mTitleHeight;
        boardGridFrameEndX = boardGridFrameMargin + (screenWidth * (float) 0.9);
        boardGridFrameEndY = boardGridFrameMargin + (screenWidth * (float) 0.9);
        float boardGridFrameWidth = boardGridFrameEndX - boardGridFrameStartX;
        float boardGridFrameHeight = boardGridFrameEndY - boardGridFrameStartY;
        boardGridCellWidth = boardGridFrameWidth / 10;
        boardGridCellHeight = boardGridFrameHeight / 10;

        viewWidth = screenWidth;
        viewHeight = boardGridFrameEndY + boardGridFrameMargin;

        float tempDividerX = boardGridFrameStartX;
        for (int i = 0; i < 11; i++) {
            boardGridFrameDividerX[i] = tempDividerX;
            tempDividerX = tempDividerX + boardGridCellWidth;
        }
        float tempDividerY = boardGridFrameStartY;
        for (int i = 0; i < 11; i++) {
            boardGridFrameDividerY[i] = tempDividerY;
            tempDividerY = tempDividerY + boardGridCellHeight;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(mTitle, mTitleX, mTitleY, titlePaint);
        canvas.drawRect(boardGridFrameStartX, boardGridFrameStartY, boardGridFrameEndX, boardGridFrameEndY, boardGridFramePaint);

        float verticalX = boardGridFrameStartX + boardGridCellWidth;
        for (int i = 0; i < 9; i++) {
            canvas.drawLine(verticalX, boardGridFrameStartY, verticalX, boardGridFrameEndY, boardGridLinePaint);
            verticalX = verticalX + boardGridCellWidth;
        }
        float verticalY = boardGridFrameStartY + boardGridCellHeight;
        for (int i = 0; i < 9; i++) {
            canvas.drawLine(boardGridFrameStartX, verticalY, boardGridFrameEndX, verticalY, boardGridLinePaint);
            verticalY = verticalY + boardGridCellHeight;
        }

        if (boardGridSelectedStartX != null && boardGridSelectedEndX != null && boardGridSelectedStartY != null && boardGridSelectedEndY != null) {
            canvas.drawRect(boardGridSelectedStartX, boardGridSelectedStartY, boardGridSelectedEndX, boardGridSelectedEndY, boardGridSelectedPaint);
        }

        canvas.drawRect(boardGridFrameStartX, boardGridFrameStartY, boardGridFrameEndX, boardGridFrameEndY, boardGridFrameBorderPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                held = true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                if (endX >= boardGridFrameStartX && endX <= boardGridFrameEndX && endY >= boardGridFrameStartY && endY <= boardGridFrameEndY && abs(endX - initialX) < 5 && abs(endY - initialY) < 5) {
                    selectedIndex = calculateCellTouched(initialX, initialY);
                    if (selectedIndex != currentIndex) {
                        currentIndex = selectedIndex;
                        Log.d("debug", "Index: " + currentIndex);
                        calculateSelectedRect(currentIndex);
                    } else {
                        deSelectCell();
                    }
                } else {
                    deSelectCell();
                }
                held = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
            default:
        }
        invalidate();
        return true;
    }

    public void calculateSelectedRect(int index) {
        int xIndex = index % 10;
        index = index / 10;
        int yIndex = index % 10;
        boardGridSelectedStartX = boardGridFrameDividerX[xIndex];
        boardGridSelectedEndX = boardGridFrameDividerX[xIndex + 1];
        boardGridSelectedStartY = boardGridFrameDividerY[yIndex];
        boardGridSelectedEndY = boardGridFrameDividerY[yIndex + 1];
    }

    public int calculateCellTouched(float x, float y) {
        int index = 0;
        for (int i = 0; i < 10; i++) {
            if (isBetween(y, boardGridFrameDividerY[i], boardGridFrameDividerY[i + 1])) {
                for (int j = 0; j < 10; j++) {
                    if (isBetween(x, boardGridFrameDividerX[j], boardGridFrameDividerX[j + 1])) {
                        return index;
                    }
                    index++;
                }
            }
            index = index + 10;
        }
        return index;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public float getViewWidth() {
        return viewWidth;
    }

    public float getBoardGridFrameMargin() {
        return boardGridFrameMargin;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void deSelectCell() {
        currentIndex = -1;
        boardGridSelectedStartX = null;
        boardGridSelectedStartY = null;
        boardGridSelectedEndX = null;
        boardGridSelectedEndY = null;
    }

    public static boolean isBetween(float x, float lower, float upper) {
        return lower <= x && x < upper;
    }
}