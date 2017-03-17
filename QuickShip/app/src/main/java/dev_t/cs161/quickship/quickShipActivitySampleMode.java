package dev_t.cs161.quickship;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class quickShipActivitySampleMode extends Activity {

    private Point screen = new Point();
    private FrameLayout mainView;
    private Float screenWidth;
    private Float screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;
        mainView = new FrameLayout(this);
        buildSampleScreen();
    }

    public void buildSampleScreen() {
        quickShipViewSampleCodeButtons gameWidgets = new quickShipViewSampleCodeButtons(this);
        quickShipViewSampleCode tempScreen = new quickShipViewSampleCode(this);
        mainView.addView(tempScreen);
        mainView.addView(gameWidgets);
        WebView wView = new WebView(this);
        wView.loadUrl("file:///android_asset/oddish.gif");
        wView.setX(0);
        wView.setY(600);
        mainView.addView(wView);
        setContentView(mainView);
    }

    public void switchActivity() {
        Intent intent = new Intent(this, quickShipActivityGameMode.class);
        startActivity(intent);
    }
}