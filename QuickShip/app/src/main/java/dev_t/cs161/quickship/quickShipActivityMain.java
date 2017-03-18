package dev_t.cs161.quickship;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class quickShipActivityMain extends Activity implements Runnable {

    Thread thread = null;
    private Point screen = new Point();
    private volatile boolean running;
    private volatile long timeNow;
    private volatile long timePrevFrame = 0;
    private volatile long timeDelta;
    private Float screenWidth;
    private Float screenHeight;
    private volatile quickShipModel mPlayerModel;
    private volatile quickShipModel mOpponentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;
        newGame();
        victorScreen();
    }

    public void trinhScreen() {
        setContentView(R.layout.quickship_choose_mode_screen);
        LinearLayout topLinear = (LinearLayout) findViewById(R.id.choose_mode_top_linear);
        FrameLayout topFrame = (FrameLayout) findViewById(R.id.choose_mode_top_frame);
        quickShipModel playerBoardData = new quickShipModel();
        quickShipModel opponentBoardData = new quickShipModel();
        //
        playerBoardData.getPlayerGameBoard().setOccupied(5, true);
        playerBoardData.getPlayerGameBoard().setOccupied(6, true);
        playerBoardData.getPlayerGameBoard().setOccupied(7, true);
        playerBoardData.getPlayerGameBoard().setOccupied(8, true);
        playerBoardData.getPlayerGameBoard().setOccupied(9, true);
        //
        quickShipViewChooseModeGrid boardScreen = new quickShipViewChooseModeGrid(this, playerBoardData, opponentBoardData);
        topFrame.getLayoutParams().height = Math.round(screenWidth);
        topFrame.addView(boardScreen);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topLinear.setLayoutParams(param);
    }

    public void victorScreen() {
        setContentView(R.layout.quickship_play_mode_screen);
        LinearLayout topLinear = (LinearLayout) findViewById(R.id.play_mode_top_linear);
        FrameLayout topFrame = (FrameLayout) findViewById(R.id.play_mode_top_frame);
        quickShipModel playerBoardData = new quickShipModel();
        quickShipModel opponentBoardData = new quickShipModel();
        //
        playerBoardData.getPlayerGameBoard().setOccupied(5, true);
        playerBoardData.getPlayerGameBoard().setOccupied(6, true);
        playerBoardData.getPlayerGameBoard().setOccupied(7, true);
        playerBoardData.getPlayerGameBoard().setOccupied(8, true);
        playerBoardData.getPlayerGameBoard().setOccupied(9, true);
        //
        quickShipViewPlayModeGrid boardScreen = new quickShipViewPlayModeGrid(this, playerBoardData, opponentBoardData);
        topFrame.getLayoutParams().height = Math.round(screenWidth);
        topFrame.addView(boardScreen);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topLinear.setLayoutParams(param);
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
        quickShipModelBoard player1Board = mPlayerModel.getPlayerGameBoard();
        quickShipModelBoard player2Board = mOpponentModel.getPlayerGameBoard();
        mPlayerModel.setPlayerGameBoard(player1Board);
        mOpponentModel.setOpponentGameBoard(player2Board);
        running = true;
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