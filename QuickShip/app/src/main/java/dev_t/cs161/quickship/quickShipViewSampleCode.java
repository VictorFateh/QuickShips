package dev_t.cs161.quickship;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class quickShipViewSampleCode extends SurfaceView {

    private Point screen = new Point();
    private SurfaceHolder surfaceHolder;
    private volatile boolean held = true;
    private volatile Float start_x, start_y;
    private volatile Float end_x, end_y;
    private Canvas canvas;
    private Paint clearButtonPaint;
    private Paint clearButtonBorderPaint;
    private Paint textPaint;
    private String clearButtonText = "Clear";
    private float buttonWidth;
    private float textSize;
    private float buttonXcoord;
    private float buttonYcoord;

    public quickShipViewSampleCode(Context context) {
        super(context);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        initializeValues();
    }

    public void initializeValues() {
        surfaceHolder = getHolder();
        clearButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clearButtonPaint.setStyle(Paint.Style.FILL);
        clearButtonPaint.setColor(Color.parseColor("#1f64d3"));
        clearButtonBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clearButtonBorderPaint.setStyle(Paint.Style.FILL);
        clearButtonBorderPaint.setColor(Color.BLACK);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textSize = textPaint.getTextSize();
        buttonWidth = textPaint.measureText(clearButtonText);
        buttonXcoord = (screen.x / 2) - (buttonWidth / 2);
        buttonYcoord = screen.y - (textSize * 2);
    }

    public void render() {
        try {
            synchronized (surfaceHolder) {
                canvas = surfaceHolder.lockCanvas();
                if (surfaceHolder.getSurface().isValid()) {
                    if (!held) {
                        float left = buttonXcoord - 10;
                        float top = buttonYcoord - textSize - 10;
                        float right = buttonXcoord + buttonWidth + 10;
                        float bottom = buttonYcoord + 10;
                        if ((end_x > left) && (end_x < right) && (end_y > top) && (end_y < bottom)) {
                            canvas.drawColor(Color.WHITE);
                        }
                        else {
                            Paint paint = new Paint();
                            paint.setColor(Color.BLACK);
                            paint.setStyle(Paint.Style.FILL_AND_STROKE);
                            paint.setStrokeWidth(10);
                            canvas.drawLine(start_x, start_y, end_x, end_y, paint);
                        }
                    }
                    canvas.drawRect(buttonXcoord - 10, buttonYcoord - textSize - 10, buttonXcoord + buttonWidth + 10, buttonYcoord + 10, clearButtonBorderPaint);
                    canvas.drawRect(buttonXcoord - 4, buttonYcoord - textSize - 4, buttonXcoord + buttonWidth + 4, buttonYcoord + 4, clearButtonPaint);
                    canvas.drawText(clearButtonText, buttonXcoord, buttonYcoord, textPaint);
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
                held = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
            default:
        }
        return true;
    }
}