package dev_t.cs161.quickship;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class quickShipActivityMain extends Activity implements Runnable {

    Thread thread = null;
    private Point screen = new Point();
    private volatile boolean running;
    private volatile long timeNow;
    private volatile long timePrevFrame = 0;
    private volatile long timeDelta;
    private Float screenWidth;
    private Float screenHeight;
    private ViewFlipper mainScreenViewFlipper;
    private ViewFlipper playModeFlipper;
    private volatile quickShipModel mPlayerModel;
    private volatile quickShipModel mOpponentModel;
    private volatile quickShipViewChooseModeGrid chooseModeGrid;
    private volatile quickShipViewPlayModePlayerGrid playModePlayerGrid;
    private volatile quickShipViewPlayModeOpponentGrid playModeOpponentGrid;
    private Button mPlayerGridBtn;
    private Button mOpponentGridBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;
        newGame();
        initializeView();
    }

    public void initializeView() {
        setContentView(R.layout.quickship_main_screen);
        mPlayerGridBtn = (Button) findViewById(R.id.play_mode_player_grid_btn);
        mPlayerGridBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (playModeFlipper.getDisplayedChild() == 0) {
                    playModeSwitchToPlayerGrid(null);
                    mOpponentGridBtn.setPressed(false);
                    mPlayerGridBtn.setPressed(true);
                }
                return true;

            }
        });
        mOpponentGridBtn = (Button) findViewById(R.id.play_mode_opponent_grid_btn);
        mOpponentGridBtn.setPressed(true);
        mOpponentGridBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (playModeFlipper.getDisplayedChild() == 1) {
                    playModeSwitchToOpponentGrid(null);
                    mPlayerGridBtn.setPressed(false);
                    mOpponentGridBtn.setPressed(true);
                }
                return true;

            }
        });
        chooseModeInitializeView();
        playModeInitializeView();
        attachViewFlipperToPlayViews();
    }

    public void attachViewFlipperToPlayViews() {
        mainScreenViewFlipper = (ViewFlipper) findViewById(R.id.main_screen_view_flipper);
        playModeFlipper = (ViewFlipper) findViewById(R.id.play_mode_view_flipper);
    }

    public void chooseModeInitializeView() {
        LinearLayout topLinear = (LinearLayout) findViewById(R.id.choose_mode_top_linear);
        FrameLayout topFrame = (FrameLayout) findViewById(R.id.choose_mode_top_frame);
        chooseModeGrid = new quickShipViewChooseModeGrid(this, mPlayerModel, mOpponentModel);
        topFrame.getLayoutParams().height = Math.round(screenWidth);
        topFrame.addView(chooseModeGrid);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topLinear.setLayoutParams(param);
    }

    public void playModeInitializeView() {
        LinearLayout topOpponentLinear = (LinearLayout) findViewById(R.id.play_mode_opponent_top_linear);
        FrameLayout topOpponentFrame = (FrameLayout) findViewById(R.id.play_mode_opponent_top_frame);
        playModeOpponentGrid = new quickShipViewPlayModeOpponentGrid(this, mPlayerModel, mOpponentModel);
        topOpponentFrame.getLayoutParams().height = Math.round(screenWidth);
        topOpponentFrame.addView(playModeOpponentGrid);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topOpponentLinear.setLayoutParams(param);

        LinearLayout topPlayerLinear = (LinearLayout) findViewById(R.id.play_mode_player_top_linear);
        FrameLayout topPlayerFrame = (FrameLayout) findViewById(R.id.play_mode_player_top_frame);
        playModePlayerGrid = new quickShipViewPlayModePlayerGrid(this, mPlayerModel, mOpponentModel);
        topPlayerFrame.getLayoutParams().height = Math.round(screenWidth);
        topPlayerFrame.addView(playModePlayerGrid);
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topPlayerLinear.setLayoutParams(param2);
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

    public void switchToPlayModeScreen(View view) {
        mainScreenViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
        mainScreenViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_left));
        mainScreenViewFlipper.setDisplayedChild(mainScreenViewFlipper.indexOfChild(findViewById(R.id.play_mode)));
    }

    public void switchToChooseModeScreen(View view) {
        mainScreenViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));
        mainScreenViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_right));
        mainScreenViewFlipper.setDisplayedChild(mainScreenViewFlipper.indexOfChild(findViewById(R.id.choose_mode)));
    }

    public void playModeSwitchToPlayerGrid(View view) {
        if (playModeFlipper.getDisplayedChild() == 0) {
            playModeFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));
            playModeFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_right));
            playModeFlipper.setDisplayedChild(1);
            mOpponentGridBtn.setPressed(false);
            mPlayerGridBtn.setPressed(true);
        }
    }

    public void playModeSwitchToOpponentGrid(View view) {
        if (playModeFlipper.getDisplayedChild() == 1) {
            playModeFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
            playModeFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_left));
            playModeFlipper.setDisplayedChild(0);
            mPlayerGridBtn.setPressed(false);
            mOpponentGridBtn.setPressed(true);
        }
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