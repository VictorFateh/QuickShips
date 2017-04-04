package dev_t.cs161.quickship;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class quickShipViewChooseModeGrid extends View {

    private Point screen = new Point();
    private volatile boolean held;
    private volatile boolean held2;
    private volatile Float initialX, initialY;
    private volatile Float endX, endY;
    private Float screenWidth;
    private Float screenHeight;
    private Paint boardGridFramePaint;
    private Float boardGridFrameStartX;
    private Float boardGridFrameStartY;
    private Float boardGridFrameEndX;
    private Float boardGridFrameEndY;
    private Float boardGridCellWidth;
    private Float boardGridCellHeight;
    private Paint boardGridLinePaint;
    private int boardGridLinePaintStrokeWidth;
    private Float boardGridFrameDividerX[];
    private Float boardGridFrameDividerY[];
    private volatile int currentIndex;
    private volatile int selectedIndex;
    private volatile int anchorOffset;
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
    private quickShipModelBoard mTemporaryBoard;
    private FrameLayout mChooseModeFrameLayout;
    private ImageView mTempShipSpot;
    private Orientation mCurrentOrientation;
    private FrameLayout.LayoutParams mTempShipSpotLayoutParam;
    private quickShipActivityMain mMainActivity;
    private boolean mShipSelected;
    private ShipType mShipSelectedShipType;
    private Bitmap[] mShipBitmaps;

    public quickShipViewChooseModeGrid(quickShipActivityMain context, quickShipModel playerBoardData, FrameLayout chooseModeFrameLayout, ImageView tempShipSpot) {
        super(context);
        mMainActivity = context;
        mGameModel = playerBoardData;
        mChooseModeFrameLayout = chooseModeFrameLayout;
        mTempShipSpot = tempShipSpot;
        Display display = context.getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        initializeValues();
        calculateBoardGUIPositions();
    }

    public void initializeValues() {
        mTitle = getContext().getResources().getString(R.string.choose_mode_grid_title);
        held = false;
        held2 = false;
        currentIndex = -1;
        anchorOffset = 0;

        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(16 * getResources().getDisplayMetrics().density);

        boardGridFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridFramePaint.setStyle(Paint.Style.FILL);
        boardGridFramePaint.setColor(mMainActivity.getResources().getColor(R.color.choose_mode_grid));

        boardGridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridLinePaint.setStyle(Paint.Style.STROKE);
        boardGridLinePaintStrokeWidth = 1;
        boardGridLinePaint.setStrokeWidth(boardGridLinePaintStrokeWidth);
        boardGridLinePaint.setColor(mMainActivity.getResources().getColor(R.color.choose_mode_grid_line));

        boardGridSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridSelectedPaint.setStyle(Paint.Style.FILL);
        boardGridSelectedPaint.setColor(mMainActivity.getResources().getColor(R.color.choose_mode_cell_selected));
        boardGridFrameDividerX = new Float[11];
        boardGridFrameDividerY = new Float[11];
        mCurrentOrientation = Orientation.HORIZONTAL;
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

        boardGridSelectedStartX = boardGridFrameDividerX[0];
        boardGridSelectedStartY = boardGridFrameDividerY[0];

        mTempShipSpotLayoutParam = new FrameLayout.LayoutParams(0, 0);
        mTempShipSpot.setBackgroundColor(mMainActivity.getResources().getColor(R.color.choose_mode_ship_selected));
        mShipSelected = false;
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

        for (int i = 0; i < 100; i++) {
            if (mGameModel.getPlayerGameBoard().isAnchor(i)) {
                quickShipModelBoardSlot anchorShip = mGameModel.getPlayerGameBoard().getShipSlotAtIndex(i);
                Bitmap tempBitmap = getGenerateBitmap(anchorShip.getShipType(), anchorShip.getOrientation());
                float[] tempXYcoord = getIndexXYCanvasBox(anchorShip.getAnchorIndex(), anchorShip.getShipType(), anchorShip.getOrientation());
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                paint.setDither(true);
                canvas.drawBitmap(tempBitmap, null, new RectF(tempXYcoord[0], tempXYcoord[1], tempXYcoord[2], tempXYcoord[3]), paint);
            }
        }
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
                float heldX = event.getX();
                float heldY = event.getY();
                if (held && mShipSelected && currentIndex != -1) {
                    if (!held2) {
                        selectedIndex = calculateCellTouched(heldX, heldY);
                        anchorOffset = calculateValidIndexForTouchDrag(selectedIndex);
                        if (anchorOffset != -1) {
                            held2 = true;
                        }
                    } else {
                        selectedIndex = calculateCellTouched(heldX, heldY) - anchorOffset;
                        if (selectedIndex >= 0 && selectedIndex < 100) {
                            if (!mGameModel.getPlayerGameBoard().isCollisionExist(selectedIndex, mShipSelectedShipType, mCurrentOrientation)) {
                                int tempIndex = calculateBestPlacement(selectedIndex);
                                if (tempIndex != -1) {
                                    currentIndex = tempIndex;
                                    calculateSelectedRect(currentIndex);
                                    setTempShipVisibility();
                                }
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                held = false;
                held2 = false;
                endX = event.getX();
                endY = event.getY();
                if (endX >= boardGridFrameStartX && endX <= boardGridFrameEndX && endY >= boardGridFrameStartY && endY <= boardGridFrameEndY && abs(endX - initialX) < 5 && abs(endY - initialY) < 5) {
                    selectedIndex = calculateCellTouched(endX, endY);
                    if (mShipSelected && selectedIndex >= 0 && selectedIndex < 100) {
                        if (!mGameModel.getPlayerGameBoard().isCollisionExist(selectedIndex, mShipSelectedShipType, mCurrentOrientation)) {
                            currentIndex = calculateBestPlacement(selectedIndex);
                            calculateSelectedRect(currentIndex);
                            setTempShipVisibility();
                            mMainActivity.setChooseModeRotateBtnStatus(true);
                            mMainActivity.setChooseModePlaceBtnStatus(true);
                        }
                    } else if (!mShipSelected) {
                        if (mGameModel.getPlayerGameBoard().isOccupied(selectedIndex)) {
                            setShipSelected(mGameModel.getPlayerGameBoard().getShipType(selectedIndex));
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
            default:
        }
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

    // Returns an array where array[0] = x, array[1] = y
    public float[] getIndexXYCoord(int index) {
        int xIndex = index % 10;
        index = index / 10;
        int yIndex = index % 10;
        float[] returnArray = new float[4];
        returnArray[0] = boardGridFrameDividerX[xIndex];
        returnArray[1] = boardGridFrameDividerY[yIndex];
        returnArray[2] = boardGridFrameDividerX[xIndex + 1];
        returnArray[3] = boardGridFrameDividerY[yIndex + 1];
        return returnArray;
    }

    // Returns an array where array[0] = startingX, array[1] = startingY, array[2] = endingX, array[4] = endingY
    public float[] getIndexXYCanvasBox(int index, ShipType shipType, Orientation orientation) {
        int xIndex = index % 10;
        index = index / 10;
        int yIndex = index % 10;
        float[] returnArray = new float[4];
        returnArray[0] = boardGridFrameDividerX[xIndex];
        returnArray[1] = boardGridFrameDividerY[yIndex];
        if (orientation.equals(Orientation.VERTICAL)) {
            switch (shipType) {
                case TWO:
                    returnArray[2] = boardGridFrameDividerX[xIndex + 1];
                    returnArray[3] = boardGridFrameDividerY[yIndex + 2];
                    break;

                case THREE_A:
                    returnArray[2] = boardGridFrameDividerX[xIndex + 1];
                    returnArray[3] = boardGridFrameDividerY[yIndex + 3];
                    break;

                case THREE_B:
                    returnArray[2] = boardGridFrameDividerX[xIndex + 1];
                    returnArray[3] = boardGridFrameDividerY[yIndex + 3];
                    break;

                case FOUR:
                    returnArray[2] = boardGridFrameDividerX[xIndex + 1];
                    returnArray[3] = boardGridFrameDividerY[yIndex + 4];
                    break;

                case FIVE:
                    returnArray[2] = boardGridFrameDividerX[xIndex + 1];
                    returnArray[3] = boardGridFrameDividerY[yIndex + 5];
                    break;
            }
        } else {
            switch (shipType) {
                case TWO:
                    returnArray[2] = boardGridFrameDividerX[xIndex + 2];
                    returnArray[3] = boardGridFrameDividerY[yIndex + 1];
                    break;

                case THREE_A:
                    returnArray[2] = boardGridFrameDividerX[xIndex + 3];
                    returnArray[3] = boardGridFrameDividerY[yIndex + 1];
                    break;

                case THREE_B:
                    returnArray[2] = boardGridFrameDividerX[xIndex + 3];
                    returnArray[3] = boardGridFrameDividerY[yIndex + 1];
                    break;

                case FOUR:
                    returnArray[2] = boardGridFrameDividerX[xIndex + 4];
                    returnArray[3] = boardGridFrameDividerY[yIndex + 1];
                    break;

                case FIVE:
                    returnArray[2] = boardGridFrameDividerX[xIndex + 5];
                    returnArray[3] = boardGridFrameDividerY[yIndex + 1];
                    break;
            }
        }
        return returnArray;
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

    public void setOrientation() {
        //Log.d("DEBUG", "Current Index: " + currentIndex);
        boolean validRotation = false;
        int xIndex = currentIndex % 10;
        int index2 = currentIndex / 10;
        int yIndex = index2 % 10;
        int newIndex = currentIndex;
        if (mCurrentOrientation.equals(Orientation.VERTICAL)) {
            while (xIndex >= 0) {
                if (validRotation(newIndex)) {
                    validRotation = true;
                    //Log.d("DEBUG", "New Index: "+newIndex);
                    break;
                }
                xIndex--;
                newIndex = (yIndex * 10) + xIndex;
                //Log.d("DEBUG", "Changing: "+newIndex);
            }
        } else {
            while (yIndex >= 0) {
                if (validRotation(newIndex)) {
                    validRotation = true;
                    //Log.d("DEBUG", "New Index: "+newIndex);
                    break;
                }
                yIndex--;
                newIndex = (yIndex * 10) + xIndex;
                //Log.d("DEBUG", "Changing Index: "+newIndex);
            }
        }
        if (validRotation) {
            if (mCurrentOrientation.equals(Orientation.VERTICAL)) {
                mCurrentOrientation = Orientation.HORIZONTAL;
            } else {
                mCurrentOrientation = Orientation.VERTICAL;
            }
            currentIndex = newIndex;
            calculateSelectedRect(currentIndex);
            setTempShipVisibility();
        } else {

        }
    }

    public boolean validRotation(int index) {
        boolean validRotation = true;
        int xIndex = index % 10;
        int index2 = index / 10;
        int yIndex = index2 % 10;
        if (mCurrentOrientation.equals(Orientation.VERTICAL)) {
            switch (mShipSelectedShipType) {
                case TWO:
                    if (xIndex > 8 || (xIndex + 1 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 1))) {
                        validRotation = false;
                    }
                    break;

                case THREE_A:
                    if (xIndex > 7 || ((xIndex + 1 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 1)) || (xIndex + 2 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 2)))) {
                        validRotation = false;
                    }
                    break;

                case THREE_B:
                    if (xIndex > 7 || ((xIndex + 1 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 1)) || (xIndex + 2 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 2)))) {
                        validRotation = false;
                    }
                    break;

                case FOUR:
                    if (xIndex > 6 || ((xIndex + 1 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 1)) || (xIndex + 2 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 2)) || (xIndex + 3 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 3)))) {
                        validRotation = false;
                    }
                    break;

                case FIVE:
                    if (xIndex > 5 || ((xIndex + 1 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 1)) || (xIndex + 2 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 2)) || (xIndex + 3 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 3)) || (xIndex + 4 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 4)))) {
                        validRotation = false;
                    }
                    break;
            }
        } else {
            switch (mShipSelectedShipType) {
                case TWO:
                    if (yIndex > 8 || (yIndex + 1 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 10))) {
                        validRotation = false;
                    }
                    break;

                case THREE_A:
                    if (yIndex > 7 || ((yIndex + 1 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 10)) || (yIndex + 2 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 20)))) {
                        validRotation = false;
                    }
                    break;

                case THREE_B:
                    if (yIndex > 7 || ((yIndex + 1 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 10)) || (yIndex + 2 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 20)))) {
                        validRotation = false;
                    }
                    break;

                case FOUR:
                    if (yIndex > 6 || ((yIndex + 1 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 10)) || (yIndex + 2 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 20)) || (yIndex + 3 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 30)))) {
                        validRotation = false;
                    }
                    break;

                case FIVE:
                    if (yIndex > 5 || ((yIndex + 1 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 10)) || (yIndex + 2 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 20)) || (yIndex + 3 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 30)) || (yIndex + 4 < 10 && mGameModel.getPlayerGameBoard().isOccupied(index + 40)))) {
                        validRotation = false;
                    }
                    break;
            }
        }
        return validRotation;
    }

    public Bitmap getGenerateBitmap(ShipType shipType, Orientation orientation) {
        int tempHeight;
        int tempWidth;
        Bitmap returnBitmap = null;
        if (orientation.equals(Orientation.VERTICAL)) {
            switch (shipType) {
                case TWO:
                    tempHeight = Math.round(2 * boardGridCellHeight);
                    tempWidth = Math.round(boardGridCellHeight);
                    returnBitmap = mMainActivity.scaleDownDrawableImage(R.drawable.ship_size2_vertical, tempHeight, tempWidth);
                    break;

                case THREE_A:
                    tempHeight = Math.round(3 * boardGridCellHeight);
                    tempWidth = Math.round(boardGridCellHeight);
                    returnBitmap = mMainActivity.scaleDownDrawableImage(R.drawable.ship_size3_a_vertical, tempHeight, tempWidth);
                    break;

                case THREE_B:
                    tempHeight = Math.round(3 * boardGridCellHeight);
                    tempWidth = Math.round(boardGridCellHeight);
                    returnBitmap = mMainActivity.scaleDownDrawableImage(R.drawable.ship_size3_b_vertical, tempHeight, tempWidth);
                    break;

                case FOUR:
                    tempHeight = Math.round(4 * boardGridCellHeight);
                    tempWidth = Math.round(boardGridCellHeight);
                    returnBitmap = mMainActivity.scaleDownDrawableImage(R.drawable.ship_size4_vertical, tempHeight, tempWidth);
                    break;

                case FIVE:
                    tempHeight = Math.round(5 * boardGridCellHeight);
                    tempWidth = Math.round(boardGridCellHeight);
                    returnBitmap = mMainActivity.scaleDownDrawableImage(R.drawable.ship_size5_vertical, tempHeight, tempWidth);
                    break;
            }
        } else {
            switch (shipType) {
                case TWO:
                    tempHeight = Math.round(boardGridCellHeight);
                    tempWidth = Math.round(2 * boardGridCellHeight);
                    returnBitmap = mMainActivity.scaleDownDrawableImage(R.drawable.ship_size2_horizontal, tempHeight, tempWidth);
                    break;

                case THREE_A:
                    tempHeight = Math.round(boardGridCellHeight);
                    tempWidth = Math.round(3 * boardGridCellHeight);
                    returnBitmap = mMainActivity.scaleDownDrawableImage(R.drawable.ship_size3_a_horizontal, tempHeight, tempWidth);
                    break;

                case THREE_B:
                    tempHeight = Math.round(boardGridCellHeight);
                    tempWidth = Math.round(3 * boardGridCellHeight);
                    returnBitmap = mMainActivity.scaleDownDrawableImage(R.drawable.ship_size3_b_horizontal, tempHeight, tempWidth);
                    break;

                case FOUR:
                    tempHeight = Math.round(boardGridCellHeight);
                    tempWidth = Math.round(4 * boardGridCellHeight);
                    returnBitmap = mMainActivity.scaleDownDrawableImage(R.drawable.ship_size4_horizontal, tempHeight, tempWidth);
                    break;

                case FIVE:
                    tempHeight = Math.round(boardGridCellHeight);
                    tempWidth = Math.round(5 * boardGridCellHeight);
                    returnBitmap = mMainActivity.scaleDownDrawableImage(R.drawable.ship_size5_horizontal, tempHeight, tempWidth);
                    break;
            }
        }
        return returnBitmap;
    }

    public void setTempShipVisibility() {
        int tempHeight;
        int tempWidth;
        mTempShipSpotLayoutParam.leftMargin = Math.round(boardGridSelectedStartX);
        mTempShipSpotLayoutParam.topMargin = Math.round(boardGridSelectedStartY);
        if (mCurrentOrientation.equals(Orientation.VERTICAL)) {
            switch (mShipSelectedShipType) {
                case TWO:
                    tempHeight = Math.round(2 * boardGridCellHeight);
                    tempWidth = Math.round(boardGridCellHeight);
                    mTempShipSpotLayoutParam.height = tempHeight;
                    mTempShipSpotLayoutParam.width = tempWidth;
                    mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                    break;

                case THREE_A:
                    tempHeight = Math.round(3 * boardGridCellHeight);
                    tempWidth = Math.round(boardGridCellHeight);
                    mTempShipSpotLayoutParam.height = tempHeight;
                    mTempShipSpotLayoutParam.width = tempWidth;
                    mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                    break;

                case THREE_B:
                    tempHeight = Math.round(3 * boardGridCellHeight);
                    tempWidth = Math.round(boardGridCellHeight);
                    mTempShipSpotLayoutParam.height = tempHeight;
                    mTempShipSpotLayoutParam.width = tempWidth;
                    mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                    break;

                case FOUR:
                    tempHeight = Math.round(4 * boardGridCellHeight);
                    tempWidth = Math.round(boardGridCellHeight);
                    mTempShipSpotLayoutParam.height = tempHeight;
                    mTempShipSpotLayoutParam.width = tempWidth;
                    mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                    break;

                case FIVE:
                    tempHeight = Math.round(5 * boardGridCellHeight);
                    tempWidth = Math.round(boardGridCellHeight);
                    mTempShipSpotLayoutParam.height = tempHeight;
                    mTempShipSpotLayoutParam.width = tempWidth;
                    mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                    break;
            }
        } else {
            switch (mShipSelectedShipType) {
                case TWO:
                    tempHeight = Math.round(boardGridCellHeight);
                    tempWidth = Math.round(2 * boardGridCellHeight);
                    mTempShipSpotLayoutParam.height = tempHeight;
                    mTempShipSpotLayoutParam.width = tempWidth;
                    mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                    break;

                case THREE_A:
                    tempHeight = Math.round(boardGridCellHeight);
                    tempWidth = Math.round(3 * boardGridCellHeight);
                    mTempShipSpotLayoutParam.height = tempHeight;
                    mTempShipSpotLayoutParam.width = tempWidth;
                    mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                    break;

                case THREE_B:
                    tempHeight = Math.round(boardGridCellHeight);
                    tempWidth = Math.round(3 * boardGridCellHeight);
                    mTempShipSpotLayoutParam.height = tempHeight;
                    mTempShipSpotLayoutParam.width = tempWidth;
                    mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                    break;

                case FOUR:
                    tempHeight = Math.round(boardGridCellHeight);
                    tempWidth = Math.round(4 * boardGridCellHeight);
                    mTempShipSpotLayoutParam.height = tempHeight;
                    mTempShipSpotLayoutParam.width = tempWidth;
                    mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                    break;

                case FIVE:
                    tempHeight = Math.round(boardGridCellHeight);
                    tempWidth = Math.round(5 * boardGridCellHeight);
                    mTempShipSpotLayoutParam.height = tempHeight;
                    mTempShipSpotLayoutParam.width = tempWidth;
                    mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                    break;
            }
        }
        mTempShipSpot.setImageBitmap(getGenerateBitmap(mShipSelectedShipType, mCurrentOrientation));
        mTempShipSpot.setVisibility(VISIBLE);
    }

    public void setShipSelected(ShipType shipType) {
        mMainActivity.setChooseModeDoneBtnStatus(false);
        if (currentIndex != -1) {
            mGameModel.getPlayerGameBoard().addShip(currentIndex, mShipSelectedShipType, mCurrentOrientation);
        }
        mShipSelectedShipType = shipType;
        int anchorSpot = mGameModel.getPlayerGameBoard().chooseModeSelectedShip(mShipSelectedShipType);
        if (anchorSpot != -1) {
            quickShipModelBoardSlot anchorShip = mGameModel.getPlayerGameBoard().getShipSlotAtIndex(anchorSpot);
            mGameModel.getPlayerGameBoard().removeShip(anchorSpot);
            currentIndex = anchorSpot;
            calculateSelectedRect(anchorSpot);
            mCurrentOrientation = anchorShip.getOrientation();
            mShipSelected = true;
            setTempShipVisibility();
            mMainActivity.setChooseModeRotateBtnStatus(true);
            mMainActivity.setChooseModePlaceBtnStatus(true);
        } else {
            currentIndex = -1;
            mCurrentOrientation = Orientation.HORIZONTAL;
            mTempShipSpot.setVisibility(INVISIBLE);
        }
        invalidate();
        mShipSelected = true;
    }

    public void deSelectShip() {
        if (currentIndex != -1) {
            mGameModel.getPlayerGameBoard().addShip(currentIndex, mShipSelectedShipType, mCurrentOrientation);
            currentIndex = -1;
        }
        mShipSelected = false;
        mMainActivity.setChooseModeRotateBtnStatus(false);
        mMainActivity.setChooseModePlaceBtnStatus(false);
        if (mGameModel.getPlayerGameBoard().checkAllPlayerShipPlaces()) {
            mMainActivity.setChooseModeDoneBtnStatus(true);
        } else {
            mMainActivity.setChooseModeDoneBtnStatus(false);
        }
        invalidate();
        mTempShipSpot.setVisibility(View.INVISIBLE);
    }


    public static boolean isBetween(float x, float lower, float upper) {
        return lower <= x && x < upper;
    }

    public int calculateBestPlacement(int index) {
        int xIndex = index % 10;
        index = index / 10;
        int yIndex = index % 10;
        int threshold = 0;
        switch (mShipSelectedShipType) {
            case TWO:
                threshold = 10 - 2;
                break;

            case THREE_A:
                threshold = 10 - 3;
                break;

            case THREE_B:
                threshold = 10 - 3;
                break;

            case FOUR:
                threshold = 10 - 4;
                break;

            case FIVE:
                threshold = 10 - 5;
                break;
        }
        if (mCurrentOrientation.equals(Orientation.VERTICAL)) {
            while (yIndex > threshold) {
                yIndex--;
            }
        } else {
            while (xIndex > threshold) {
                xIndex--;
            }
        }
        int returnIndex = ((yIndex * 10) + xIndex);
        if (held2) {
            if (!mGameModel.getPlayerGameBoard().isCollisionExist(returnIndex, mShipSelectedShipType, mCurrentOrientation)) {
                return returnIndex;
            }
            else {
                return -1;
            }
        }
        else {
            return returnIndex;
        }
    }

    public int calculateValidIndexForTouchDrag(int index) {
        int returnInt = -1;
        if (mCurrentOrientation.equals(Orientation.VERTICAL)) {
            switch (mShipSelectedShipType) {
                case TWO:
                    if (index == currentIndex) {
                        return 0;
                    } else if (index == currentIndex + 10) {
                        return 10;
                    }
                    break;

                case THREE_A:
                    if (index == currentIndex) {
                        return 0;
                    } else if (index == currentIndex + 10) {
                        return 10;
                    } else if (index == currentIndex + 20) {
                        return 20;
                    }
                    break;

                case THREE_B:
                    if (index == currentIndex) {
                        return 0;
                    } else if (index == currentIndex + 10) {
                        return 10;
                    } else if (index == currentIndex + 20) {
                        return 20;
                    }
                    break;

                case FOUR:
                    if (index == currentIndex) {
                        return 0;
                    } else if (index == currentIndex + 10) {
                        return 10;
                    } else if (index == currentIndex + 20) {
                        return 20;
                    } else if (index == currentIndex + 30) {
                        return 30;
                    }
                    break;

                case FIVE:
                    if (index == currentIndex) {
                        return 0;
                    } else if (index == currentIndex + 10) {
                        return 10;
                    } else if (index == currentIndex + 20) {
                        return 20;
                    } else if (index == currentIndex + 30) {
                        return 30;
                    } else if (index == currentIndex + 40) {
                        return 40;
                    }
                    break;
            }
        } else {
            switch (mShipSelectedShipType) {
                case TWO:
                    if (index == currentIndex) {
                        return 0;
                    } else if (index == currentIndex + 1) {
                        return 1;
                    }
                    break;

                case THREE_A:
                    if (index == currentIndex) {
                        return 0;
                    } else if (index == currentIndex + 1) {
                        return 1;
                    } else if (index == currentIndex + 2) {
                        return 2;
                    }
                    break;

                case THREE_B:
                    if (index == currentIndex) {
                        return 0;
                    } else if (index == currentIndex + 1) {
                        return 1;
                    } else if (index == currentIndex + 2) {
                        return 2;
                    }
                    break;

                case FOUR:
                    if (index == currentIndex) {
                        return 0;
                    } else if (index == currentIndex + 1) {
                        return 1;
                    } else if (index == currentIndex + 2) {
                        return 2;
                    } else if (index == currentIndex + 3) {
                        return 3;
                    }
                    break;

                case FIVE:
                    if (index == currentIndex) {
                        return 0;
                    } else if (index == currentIndex + 1) {
                        return 1;
                    } else if (index == currentIndex + 2) {
                        return 2;
                    } else if (index == currentIndex + 3) {
                        return 3;
                    } else if (index == currentIndex + 4) {
                        return 4;
                    }
                    break;
            }
        }

        return returnInt;
    }
}