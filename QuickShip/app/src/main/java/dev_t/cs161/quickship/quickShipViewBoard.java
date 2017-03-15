package dev_t.cs161.quickship;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class quickShipViewBoard extends SurfaceView {

    private Point screen = new Point();
    private SurfaceHolder surfaceHolder;
    private volatile boolean touched = false;
    private volatile boolean held = true;
    private volatile boolean cleared = true;
    private volatile Float start_x, start_y;
    private volatile Float end_x, end_y;
    private Canvas canvas;
    private Paint clearButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint clearButtonBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint();
    private String clearButtonText = "Clear";
    private float w;
    private float textSize;
    private float buttonXcoord;
    private float buttonYcoord;

    public quickShipViewBoard(Context context) {
        super(context);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        surfaceHolder = getHolder();
        clearButtonPaint.setStyle(Paint.Style.FILL);
        clearButtonPaint.setColor(Color.parseColor("#1f64d3"));
        clearButtonBorderPaint.setStyle(Paint.Style.FILL);
        clearButtonBorderPaint.setColor(Color.BLACK);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textSize = textPaint.getTextSize();
        w = textPaint.measureText(clearButtonText);
        buttonXcoord = (screen.x / 2) - (w / 2);
        buttonYcoord = screen.y - (textSize * 2);
    }

    public void render() {
        try {
            synchronized (surfaceHolder) {
                canvas = surfaceHolder.lockCanvas();
                if (surfaceHolder.getSurface().isValid()) {

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
                touched = true;
                start_x = event.getX();
                start_y = event.getY();
                held = true;
                break;
            case MotionEvent.ACTION_MOVE:
                touched = true;
                break;
            case MotionEvent.ACTION_UP:
                touched = false;
                end_x = event.getX();
                end_y = event.getY();
                held = false;
                cleared = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                touched = false;
                break;
            case MotionEvent.ACTION_OUTSIDE:
                touched = false;
                break;
            default:
        }
        return true;
    }
}