package dev_t.cs161.quickship;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class quickShipActivityGameMode extends Activity implements Runnable {

    Thread thread = null;
    private Point screen = new Point();
    private FrameLayout mainView;
    private quickShipViewBoard boardScreen;
    private volatile boolean running;
    private volatile long timeNow;
    private volatile long timePrevFrame = 0;
    private volatile long timeDelta;
    private Float screenWidth;
    private Float screenHeight;
    private quickShipModel mPlayerModel;
    private quickShipModel mOpponentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;
        newGame();
        buildBoardScreen();
    }

    public void buildBoardScreen() {
        mainView = new FrameLayout(this);
        mainView.setBackgroundColor(Color.parseColor("#1f64d3"));
        quickShipViewBoardOption gameWidgets = new quickShipViewBoardOption(this);
        boardScreen = new quickShipViewBoard(this, gameWidgets, mPlayerModel, "Your Board");
        gameWidgets.attachViewBoard(boardScreen);
        mainView.addView(boardScreen);
        mainView.addView(gameWidgets);
        setContentView(mainView);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
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

    public void newGame() {
        mPlayerModel = new quickShipModel();
        mOpponentModel = new quickShipModel();
        quickShipBoard player1Board = mPlayerModel.getPlayerGameBoard();
        quickShipBoard player2Board = mOpponentModel.getPlayerGameBoard();
        mPlayerModel.copyOpponentGameBoard(player2Board);
        mOpponentModel.copyOpponentGameBoard(player1Board);
        mPlayerModel.printMap_debug();
        mOpponentModel.printMap_debug();
        running = true;
    }

    public void switchActivity() {
        Intent intent = new Intent(this, quickShipActivitySampleMode.class);
        startActivity(intent);
    }

    public void switchActivity2() {
        mainView = new FrameLayout(this);
        quickShipViewSampleCodeButtons gameWidgets = new quickShipViewSampleCodeButtons(this);
        quickShipViewSampleCode tempScreen = new quickShipViewSampleCode(this);
        mainView.addView(tempScreen);
        mainView.addView(gameWidgets);
        setContentView(mainView);
    }

    public void switchActivity3() {
        mainView = new FrameLayout(this);
        mainView.setBackgroundColor(Color.parseColor("#ffff"));
        quickShipViewBoardOption gameWidgets = new quickShipViewBoardOption(this);
        boardScreen = new quickShipViewBoard(this, gameWidgets, mPlayerModel, "Your Board");
        gameWidgets.attachViewBoard(boardScreen);
        mainView.addView(boardScreen);
        mainView.addView(gameWidgets);
        setContentView(mainView);
    }

    @Override
    public void run() {
//        while (running) {
//            //limit the frame rate to maximum 60 frames per second (16 miliseconds)
//            //limit the frame rate to maximum 30 frames per second (32 miliseconds)
//            timeNow = System.currentTimeMillis();
//            timeDelta = timeNow - timePrevFrame;
//            if (timeDelta < 32) {
//                try {
//                    Thread.sleep(32 - timeDelta);
//                } catch (InterruptedException e) {
//
//                }
//            }
//            timePrevFrame = System.currentTimeMillis();
//            boardScreen.render();
//        }
    }
}