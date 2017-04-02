package dev_t.cs161.quickship;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by trinhnguyen on 4/2/17.
 */

public class quickShipChooseModeLinearLayout extends LinearLayout {
    private quickShipActivityMain mMainActivity;

    public quickShipChooseModeLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mMainActivity = (quickShipActivityMain) context;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!isInEditMode()) {
            mMainActivity.loadChooseModeBitmaps((String) getTag(), getHeight(), getWidth());
        }
    }
}
