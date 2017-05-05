package dev_t.cs161.quickship;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import static java.lang.Math.abs;

// Used to detect swiping motion at the play mode screen while at the player grid screen
public class quickShipLayoutPlayModePlayer extends LinearLayout {

    private Point screen = new Point();
    private Float screenWidth;
    private Float screenHeight;
    private Float swipeThreshold;
    private float initialX;
    private float finalX;
    private quickShipActivityMain mMainActivity;

    public quickShipLayoutPlayModePlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
                    mMainActivity.playModeSwitchToOpponentGrid(null);
                } else if (abs(initialX - finalX) > swipeThreshold) {
                    mMainActivity.playModeSwitchToPlayerGrid(null);
                }
                break;
        }
        return true;
    }
}