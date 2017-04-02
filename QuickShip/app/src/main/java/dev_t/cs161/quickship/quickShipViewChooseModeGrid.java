package dev_t.cs161.quickship;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
    private quickShipModel mGameModel;
    private quickShipModelBoard mTemporaryBoard;
    private ArrayList<quickShipModelBoardSlot> shipList;
    private quickShipModelBoardSlot currentSelectedPiece;
    private FrameLayout mChooseModeFrameLayout;
    private ImageView mTempShipSpot;
    private Orientation mCurrentOrientation;
    private FrameLayout.LayoutParams mTempShipSpotLayoutParam;


    public quickShipViewChooseModeGrid(Context context, quickShipModel playerBoardData, FrameLayout chooseModeFrameLayout, ImageView tempShipSpot) {
        super(context);
        mContext = context;
        mGameModel = playerBoardData;
        mChooseModeFrameLayout = chooseModeFrameLayout;
        mTempShipSpot = tempShipSpot;
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        initializeValues();
        calculateBoardGUIPositions();
    }

    public void initializeValues() {
        shipList = new ArrayList<>();
        quickShipModelBoardSlot shipSize2 = new quickShipModelBoardSlot();
        shipSize2.setShipType(ShipType.TWO);
        shipSize2.setAnchor(true);
        quickShipModelBoardSlot shipSize3a = new quickShipModelBoardSlot();
        shipSize3a.setShipType(ShipType.THREE_A);
        shipSize3a.setAnchor(true);
        quickShipModelBoardSlot shipSize3b = new quickShipModelBoardSlot();
        shipSize3b.setShipType(ShipType.THREE_B);
        shipSize3b.setAnchor(true);
        quickShipModelBoardSlot shipSize4 = new quickShipModelBoardSlot();
        shipSize4.setShipType(ShipType.FOUR);
        shipSize4.setAnchor(true);
        quickShipModelBoardSlot shipSize5 = new quickShipModelBoardSlot();
        shipSize5.setShipType(ShipType.FIVE);
        shipSize5.setAnchor(true);
        shipList.add(shipSize2);
        shipList.add(shipSize3a);
        shipList.add(shipSize3b);
        shipList.add(shipSize4);
        shipList.add(shipSize5);

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
        mCurrentOrientation = Orientation.VERTICAL;
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
        mTempShipSpotLayoutParam.leftMargin = Math.round(boardGridSelectedStartX);
        mTempShipSpotLayoutParam.topMargin = Math.round(boardGridSelectedStartY);
        mTempShipSpotLayoutParam.height = Math.round(3*boardGridCellHeight);
        mTempShipSpotLayoutParam.width = Math.round(boardGridCellHeight);
        mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
        mTempShipSpot.setAdjustViewBounds(true);
        mTempShipSpot.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
                        //Log.d("debug", "Index: " + currentIndex);
                        calculateSelectedRect(currentIndex);
                        setTempShipVisibility(View.VISIBLE);
                    } else {
                        setTempShipVisibility(View.INVISIBLE);
                    }
                } else {
                    setTempShipVisibility(View.INVISIBLE);
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
        Log.d("debug", "StartX:" + boardGridSelectedStartX + "\n");
        Log.d("debug", "EndX:" + boardGridSelectedEndX + "\n");
        Log.d("debug", "StartY:" + boardGridSelectedStartY + "\n");
        Log.d("debug", "EndY:" + boardGridSelectedEndY + "\n");
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
        if (mCurrentOrientation.equals(Orientation.HORIZONTAL)) {
            mCurrentOrientation = Orientation.VERTICAL;
        }
        else {
            mCurrentOrientation = Orientation.HORIZONTAL;
        }
    }

    public void setTempShipVisibility(int visible) {
        if (visible == View.VISIBLE) {
            mTempShipSpotLayoutParam.leftMargin = Math.round(boardGridSelectedStartX);
            mTempShipSpotLayoutParam.topMargin = Math.round(boardGridSelectedStartY);
            if (mCurrentOrientation.equals(Orientation.VERTICAL)) {
                mTempShipSpotLayoutParam.height = Math.round(3*boardGridCellHeight);
                mTempShipSpotLayoutParam.width = Math.round(boardGridCellHeight);
                mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                mTempShipSpot.setRotation(0);
                mTempShipSpot.setAdjustViewBounds(true);
                mTempShipSpot.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            else {
                mTempShipSpotLayoutParam.height = Math.round(boardGridCellHeight);
                mTempShipSpotLayoutParam.width = Math.round(3*boardGridCellHeight);
                mTempShipSpot.setLayoutParams(mTempShipSpotLayoutParam);
                mTempShipSpot.setRotation(270);
                mTempShipSpot.setAdjustViewBounds(true);
                mTempShipSpot.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }
        mTempShipSpot.setVisibility(visible);
    }

    public static boolean isBetween(float x, float lower, float upper) {
        return lower <= x && x < upper;
    }
}