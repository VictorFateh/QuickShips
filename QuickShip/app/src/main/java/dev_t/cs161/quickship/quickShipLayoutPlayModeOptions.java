package dev_t.cs161.quickship;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import static java.lang.Math.abs;

public class quickShipLayoutPlayModeOptions extends LinearLayout {

    private Point screen = new Point();
    private Float screenWidth;
    private Float screenHeight;
    private Float swipeThreshold;
    private float initialX;
    private float finalX;
    private Context mContext;
    private quickShipActivityMain mMainActivity;

    public quickShipLayoutPlayModeOptions(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        if (!isInEditMode()) {
            mMainActivity = (quickShipActivityMain) context;
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            display.getSize(screen);
            screenWidth = (float) screen.x;
            screenHeight = (float) screen.y;
            swipeThreshold = screenWidth * 0.1f;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                finalX = touchevent.getX();
                if (initialX > finalX && abs(initialX - finalX) > swipeThreshold) {
                    mMainActivity.playModeSwitchToOptions(null);
                } else if (abs(initialX - finalX) > swipeThreshold) {
                    mMainActivity.playModeSwitchToOpponentGrid(null);
                }
                break;
        }
        return true;
    }
}