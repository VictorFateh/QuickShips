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

public class quickShipLayoutPlayModePlayer extends LinearLayout {

    private ViewFlipper playModeFlipper;
    private Point screen = new Point();
    private Float screenWidth;
    private Float screenHeight;
    private Float swipeThreshold;
    private float initialX;
    private float finalX;
    private Context mContext;

    public quickShipLayoutPlayModePlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;
        swipeThreshold = screenWidth * 0.1f;
    }

    public void attachAttributes(ViewFlipper v) {
        playModeFlipper = v;
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                finalX = touchevent.getX();
                if (initialX > finalX && abs(initialX - finalX) > swipeThreshold && initialX > (screenWidth * 0.8)) {
                    playModeFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_from_right));
                    playModeFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_from_left));
                    playModeFlipper.setDisplayedChild(2);
                } else if (abs(initialX - finalX) > swipeThreshold && initialX < (screenWidth * 0.2)) {
                    playModeFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_from_left));
                    playModeFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_from_right));
                    playModeFlipper.setDisplayedChild(2);
                }
                break;
        }
        return true;
    }
}
