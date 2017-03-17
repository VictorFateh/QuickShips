package dev_t.cs161.quickship;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class quickShipViewBoardOption extends LinearLayout {

    private Point screen = new Point();
    private quickShipViewBoard mboardScreen;
    private Button fireMissleBtn;
    private Float screenWidth;
    private Float screenHeight;
    private Context mContext;

    public quickShipViewBoardOption(Context context) {
        super(context);
        mContext = context;
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;
    }

    public void attachViewBoard(quickShipViewBoard boardScreen) {
        mboardScreen = boardScreen;
        fireMissleBtn = new Button(mContext);

        fireMissleBtn.setWidth(300);
        fireMissleBtn.setEnabled(false);
        fireMissleBtn.setX(screenWidth - mboardScreen.getBoardGridFrameMargin() - 300);
        fireMissleBtn.setY(mboardScreen.getViewHeight());
        fireMissleBtn.setText("Fire Missile");

        addView(fireMissleBtn);

        Button switchView = new Button(mContext);

        switchView.setWidth(300);
        switchView.setX(0);
        switchView.setY(mboardScreen.getViewHeight());
        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof quickShipActivityGameMode) {
                    quickShipActivityGameMode tempActivity = (quickShipActivityGameMode)mContext;
                    ((quickShipActivityGameMode) mContext).switchActivity();
                }
            }
        });
        switchView.setText("Switch View");

        addView(switchView);
    }

    public void setMissileBtnStatus(boolean status) {
        fireMissleBtn.setEnabled(status);
    }
}
