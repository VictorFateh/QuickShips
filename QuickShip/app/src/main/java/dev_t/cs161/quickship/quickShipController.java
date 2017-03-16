package dev_t.cs161.quickship;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class quickShipController extends Activity implements Runnable {

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
    private Button fireMissleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;
        mainView = new FrameLayout(this);
        buildBoardScreen();
        setContentView(mainView);
        newGame();
    }

    public void buildBoardScreen() {
        quickShipViewBoardOption gameWidgets = new quickShipViewBoardOption(this);
        boardScreen = new quickShipViewBoard(this, gameWidgets);
        gameWidgets.attachViewBoard(boardScreen);
        mainView.addView(boardScreen);
        mainView.addView(gameWidgets);
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
        quickShipModel player1 = new quickShipModel();
        quickShipModel player2 = new quickShipModel();
        quickShipBoard player1Board = player1.getPlayerGameBoard();
        quickShipBoard player2Board = player2.getPlayerGameBoard();
        player1.copyOpponentGameBoard(player2Board);
        player2.copyOpponentGameBoard(player1Board);
        player1.printMap_debug();
        player2.printMap_debug();
        running = true;
    }

    @Override
    public void run() {
        while (running) {
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
            boardScreen.render();
        }
    }
}