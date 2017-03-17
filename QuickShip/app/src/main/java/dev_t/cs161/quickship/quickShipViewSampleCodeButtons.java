package dev_t.cs161.quickship;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class quickShipViewSampleCodeButtons extends LinearLayout {

    private Point screen = new Point();
    private Button switchViewBtn;
    private Float screenWidth;
    private Float screenHeight;
    private Context mContext;
    private quickShipActivitySampleMode currentActivity;

    public quickShipViewSampleCodeButtons(Context context) {
        super(context);
        mContext = context;
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;
        initializeButtons();
    }

    public void initializeButtons() {
        switchViewBtn = new Button(mContext);

        switchViewBtn.setWidth(300);
        switchViewBtn.setText("Switch View");
        switchViewBtn.setX(10);
        switchViewBtn.setY(10);
        switchViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof quickShipActivitySampleMode) {
                    quickShipActivitySampleMode tempActivity = (quickShipActivitySampleMode) mContext;
                    ((quickShipActivitySampleMode) mContext).switchActivity();
                } else if (mContext instanceof quickShipActivityGameMode) {
                    quickShipActivityGameMode tempActivity = (quickShipActivityGameMode) mContext;
                    ((quickShipActivityGameMode) mContext).switchActivity3();
                }
            }
        });
        addView(switchViewBtn);
    }
}
