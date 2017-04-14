package dev_t.cs161.quickship;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.abs;

public class quickShipViewPlayModeOpponentGrid extends View {

    private Point screen = new Point();
    private volatile boolean held;
    private volatile Float initialX, initialY;
    private volatile Float endX, endY;
    private Float screenWidth;
    private Float screenHeight;
    private Float swipeThreshold;
    private Paint boardGridFramePaint;
    private Paint boatHitPaint;
    private Paint boatMissPaint;
    private Float boardGridFrameStartX;
    private Float boardGridFrameStartY;
    private Float boardGridFrameEndX;
    private Float boardGridFrameEndY;
    private Float boardGridCellWidth;
    private Float boardGridCellHeight;
    private Paint boardGridLinePaint;
    private Float boardGridLinePaintStrokeWidth;
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
    private quickShipModel mGameModel;
    private quickShipActivityMain mMainActivity;
    private int fireIndex;


    public quickShipViewPlayModeOpponentGrid(quickShipActivityMain context, quickShipModel gameModel) {
        super(context);
        mMainActivity = context;
        mGameModel = gameModel;
        Display display = context.getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        initializeValues();
        calculateBoardGUIPositions();
    }

    public int getFireIndex(){
        return fireIndex;
    }

    public void initializeValues() {
        mTitle = getContext().getResources().getString(R.string.play_mode_grid_opponent_title);
        held = true;
        currentIndex = -1;

        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(16 * getResources().getDisplayMetrics().density);

        boardGridFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridFramePaint.setStyle(Paint.Style.FILL);
        boardGridFramePaint.setColor(mMainActivity.getResources().getColor(R.color.play_mode_opponent_grid));

        boatHitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boatHitPaint.setStyle(Paint.Style.FILL);
        boatHitPaint.setColor(mMainActivity.getResources().getColor(R.color.play_mode_opponent_ship_hit));

        boatMissPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boatMissPaint.setStyle(Paint.Style.FILL);
        boatMissPaint.setColor(mMainActivity.getResources().getColor(R.color.play_mode_opponent_ship_miss));

        boardGridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridLinePaint.setStyle(Paint.Style.STROKE);
        int dpSize =  1;
        DisplayMetrics dm = mMainActivity.getResources().getDisplayMetrics() ;
        boardGridLinePaintStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, dm);
        boardGridLinePaint.setStrokeWidth(boardGridLinePaintStrokeWidth);
        boardGridLinePaint.setColor(mMainActivity.getResources().getColor(R.color.play_mode_opponent_grid_line));

        boardGridSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridSelectedPaint.setStyle(Paint.Style.FILL);
        boardGridSelectedPaint.setColor(mMainActivity.getResources().getColor(R.color.play_mode_player_cell_selected));
        boardGridFrameDividerX = new Float[11];
        boardGridFrameDividerY = new Float[11];
    }

    public void calculateBoardGUIPositions() {
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;

        swipeThreshold = screenWidth * 0.2f;

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

        float[] hitXY;
        float[] missXY;

        mGameModel.getOpponentGameBoard().setOccupied(0, true);
        mGameModel.getOpponentGameBoard().setOccupied(1, true);
        mGameModel.getOpponentGameBoard().setOccupied(2, true);

        //Loop through enemy board and draw hits and misses
        //Hit slots have red circle drawn
        //Slots that have been hit but don't have ships have white circles
        for(int i = 0; i < 100; i++) {
            //If specific index is hit on opponents board paint it as hit
            if(mGameModel.getOpponentGameBoard().isHit(i) && mGameModel.getOpponentGameBoard().isOccupied(i)) {
                hitXY = getIndexXYCoordCircle(i);
                canvas.drawCircle(hitXY[0], hitXY[1], hitXY[2], boatHitPaint);
            }
            //If index was shot at but is not occupied, paint a missed white circle
            else if(mGameModel.getOpponentGameBoard().isHit(i) && !mGameModel.getOpponentGameBoard().isOccupied(i)) {
                missXY = getIndexXYCoordCircle(i);
                canvas.drawCircle(missXY[0], missXY[1], missXY[2], boatMissPaint);
            }
        }

    }

    //Returns Array for drawing circle in middle of grid
    public float[] getIndexXYCoordCircle(int index) {
        int xIndex = index % 10;
        index = index / 10;
        int yIndex = index % 10;
        float[] returnArray = new float[4];
        returnArray[0] = boardGridFrameDividerX[xIndex];
        returnArray[1] = boardGridFrameDividerY[yIndex];
        returnArray[2] = boardGridFrameDividerX[xIndex + 1];
        returnArray[3] = boardGridFrameDividerY[yIndex + 1];

        //Circle X Center
        returnArray[0] = boardGridFrameDividerX[xIndex] + (((boardGridFrameDividerX[xIndex + 1]-boardGridFrameDividerX[xIndex]) / 2));

        //Circle Y Center
        returnArray[1] = boardGridFrameDividerY[yIndex] + (((boardGridFrameDividerY[yIndex + 1]-boardGridFrameDividerY[yIndex]) / 2));

        //Circle Radius
        returnArray[2] = (boardGridFrameDividerX[xIndex + 1] - boardGridFrameDividerX[xIndex]) / 3;

        return returnArray;
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
                if (initialX > endX && abs(initialX - endX) > swipeThreshold) {
                    mMainActivity.playModeSwitchToOptions(null);
                } else if (abs(initialX - endX) > swipeThreshold) {
                    mMainActivity.playModeSwitchToPlayerGrid(null);
                } else {
                    if (endX >= boardGridFrameStartX && endX <= boardGridFrameEndX && endY >= boardGridFrameStartY && endY <= boardGridFrameEndY && abs(endX - initialX) < 5 && abs(endY - initialY) < 5) {
                        selectedIndex = calculateCellTouched(initialX, initialY);
                        if(!mGameModel.getOpponentGameBoard().isHit(selectedIndex)) {
                            if (selectedIndex != currentIndex) {
                                currentIndex = selectedIndex;
                                Log.d("debug", "Index: " + currentIndex);
                                calculateSelectedRect(currentIndex);
                                mMainActivity.setPlayModeFireBtnStatus(true);
                            }
                        }
                        else {
                            deSelectCell();
                        }
                    } else {
                        deSelectCell();
                    }
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
        mMainActivity.setPlayModeFireBtnStatus(false);
        currentIndex = -1;
        boardGridSelectedStartX = null;
        boardGridSelectedStartY = null;
        boardGridSelectedEndX = null;
        boardGridSelectedEndY = null;
    }

    public static boolean isBetween(float x, float lower, float upper) {
        return lower <= x && x < upper;
    }

    public boolean insideBoardGridBound(float x, float y) {
        if (x < boardGridFrameStartX || x > boardGridFrameEndX || y < boardGridFrameStartY || y > boardGridFrameEndY) {
            return false;
        }
        else {
            return true;
        }
    }
}