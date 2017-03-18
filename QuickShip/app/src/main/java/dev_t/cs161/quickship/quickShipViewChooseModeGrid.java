package dev_t.cs161.quickship;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static java.lang.Math.abs;

public class quickShipViewChooseModeGrid extends SurfaceView {

    private Point screen = new Point();
    private SurfaceHolder surfaceHolder;
    private volatile boolean held;
    private Canvas canvas;
    private volatile Float start_x, start_y;
    private volatile Float end_x, end_y;
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
    private quickShipModel mOpponentBoardData;


    public quickShipViewChooseModeGrid(Context context, quickShipModel playerBoardData, quickShipModel opponentBoardData) {
        super(context);
        setWillNotDraw(false);
        setZOrderOnTop(true);
        mplayerBoardData = playerBoardData;
        mOpponentBoardData = opponentBoardData;
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        initializeValues();
        calculateBoardGUIPositions();
    }

    public void initializeValues() {
        mTitle = "Place your ships";
        held = true;
        currentIndex = -1;
        surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(50);

        boardGridFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridFramePaint.setStyle(Paint.Style.FILL);
        boardGridFramePaint.setColor(Color.parseColor("#5a8ddd"));
        boardGridFrameBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridFrameBorderPaint.setStyle(Paint.Style.STROKE);
        boardGridFrameBorderStrokeWidth = 15;
        boardGridFrameBorderPaint.setStrokeWidth(boardGridFrameBorderStrokeWidth);
        boardGridFrameBorderPaint.setColor(Color.BLACK);

        boardGridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridLinePaint.setStyle(Paint.Style.STROKE);
        boardGridLinePaintStrokeWidth = 1;
        boardGridLinePaint.setStrokeWidth(boardGridLinePaintStrokeWidth);
        boardGridLinePaint.setColor(Color.parseColor("#dcdfe5"));

        boardGridSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardGridSelectedPaint.setStyle(Paint.Style.FILL);
        boardGridSelectedPaint.setColor(Color.parseColor("#f4a442"));
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
    public void onDraw(Canvas c) {
        try {
            synchronized (surfaceHolder) {
                canvas = surfaceHolder.lockCanvas();
                if (surfaceHolder.getSurface().isValid()) {
                    canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
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
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                start_x = event.getX();
                start_y = event.getY();
                held = true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                end_x = event.getX();
                end_y = event.getY();
                if (end_x >= boardGridFrameStartX && end_x <= boardGridFrameEndX && end_y >= boardGridFrameStartY && end_y <= boardGridFrameEndY && abs(end_x - start_x) < 5 && abs(end_y - start_y) < 5) {
                    selectedIndex = calculateCellTouched(start_x, start_y);
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