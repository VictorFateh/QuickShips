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

public class quickShipView extends SurfaceView implements Runnable {

    private Point screen = new Point();
    Thread thread = null;
    SurfaceHolder surfaceHolder;
    volatile boolean running = false;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    volatile boolean touched = false;
    volatile boolean held = true;
    volatile boolean preview = false;
    volatile boolean cleared = true;
    volatile Float touched_x, touched_y;
    volatile Float start_x, start_y;
    volatile Float end_x, end_y;
    long timeNow;
    long timePrevFrame = 0;
    long timeDelta;
    Canvas canvas;
    Paint clearButtonPaint = new Paint();
    Paint clearButtonBorderPaint = new Paint();
    Paint textPaint = new Paint();
    String clearButtonText = "Clear";
    float w;
    float textSize;
    float buttonXcoord;
    float buttonYcoord;

    public quickShipView(Context context) {
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

    public void onResumeMySurfaceView() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void onPauseMySurfaceView() {
        boolean retry = true;
        running = false;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void render(Canvas canvas) {
        if (surfaceHolder.getSurface().isValid()) {
            if (!held) {
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setStrokeWidth(10);
                canvas.drawLine(start_x, start_y, end_x, end_y, paint);
            }
            canvas.drawRect(buttonXcoord - 10, buttonYcoord - textSize - 10, buttonXcoord + w + 10, buttonYcoord + 10, clearButtonBorderPaint);
            canvas.drawRect(buttonXcoord - 4, buttonYcoord - textSize - 4, buttonXcoord + w + 4, buttonYcoord + 4, clearButtonPaint);
            canvas.drawText(clearButtonText, buttonXcoord, buttonYcoord, textPaint);
            if (!held && !cleared) {
                float left = buttonXcoord - 10;
                float top = buttonYcoord - textSize - 10;
                float right = buttonXcoord + w + 10;
                float bottom = buttonYcoord + 10;
                if ((end_x > left) && (end_x < right) && (end_y > top) && ( end_y < bottom)) {
//                    Log.d("debug", "end_x: "+end_x);
//                    Log.d("debug", "end_y: "+end_y);
//                    Log.d("debug", "left: "+left);
//                    Log.d("debug", "top: "+top);
//                    Log.d("debug", "right: "+right);
//                    Log.d("debug", "bottom: "+bottom);
                    canvas.drawColor(Color.WHITE);
                    cleared = true;
                }
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            canvas = null;
            //limit the frame rate to maximum 60 frames per second (16 miliseconds)
            //limit the frame rate to maximum 30 frames per second (32 miliseconds)
            timeNow = System.currentTimeMillis();
            timeDelta = timeNow - timePrevFrame;
            if (timeDelta < 32) {
                try {
                    Thread.sleep(32 - timeDelta);
                } catch (InterruptedException e) {

                }
            }
            timePrevFrame = System.currentTimeMillis();

            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    render(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        //touched_x = event.getX();
        //touched_y = event.getY();

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