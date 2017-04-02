package dev_t.cs161.quickship;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class quickShipActivityMain extends Activity implements Runnable {

    Thread thread = null;
    private Point screen = new Point();
    private quickShipActivityMain mActivityMain;
    private volatile boolean initialBoot;
    private volatile boolean running;
    private volatile long timeNow;
    private volatile long timePrevFrame = 0;
    private volatile long timeDelta;
    private Float screenWidth;
    private Float screenHeight;
    private ViewFlipper mainScreenViewFlipper;
    private ViewFlipper playModeFlipper;
    private volatile quickShipModel mGameModel;
    private volatile quickShipViewChooseModeGrid chooseModeGrid;
    private volatile quickShipViewPlayModePlayerGrid playModePlayerGrid;
    private volatile quickShipViewPlayModeOpponentGrid playModeOpponentGrid;
    private Button mPlayModeFireBtn;
    private Button mPlayerGridBtn;
    private Button mOpponentGridBtn;
    private Button mPlayModeOptionsBtn;
    private Button startGame;
    private FrameLayout mChooseModeFrameLayout;
    private ImageView mSelectedShip;
    private ImageView mShipSize2;
    private ImageView mShipSize3a;
    private ImageView mShipSize3b;
    private ImageView mShipSize4;
    private ImageView mShipSize5;
    private ImageView mTempShipSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;
        initialBoot = true;
        initializeView();
    }

    public void launchStartScreen() {
        mainScreenViewFlipper.setDisplayedChild(0);
        startGame = (Button) findViewById(R.id.start_game_btn);
        startGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newGame();
                mainScreenViewFlipper.setDisplayedChild(2);
            }
        });
    }

    public void initializeView() {
        setContentView(R.layout.quickship_main_screen);
        mActivityMain = this;
        mChooseModeFrameLayout = (FrameLayout) findViewById(R.id.choose_mode);
        mTempShipSpot = (ImageView) findViewById(R.id.temp_ship_spot);
        mPlayModeFireBtn = (Button) findViewById(R.id.play_mode_fire_btn);
        mPlayModeFireBtn.setEnabled(false);

        mPlayerGridBtn = (Button) findViewById(R.id.play_mode_player_grid_btn);
        mPlayerGridBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (playModeFlipper.getDisplayedChild() == 1 || playModeFlipper.getDisplayedChild() == 2) {
                    playModeSwitchToPlayerGrid(null);
                    mOpponentGridBtn.setPressed(false);
                    mPlayModeOptionsBtn.setPressed(false);
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
                if (playModeFlipper.getDisplayedChild() == 0 || playModeFlipper.getDisplayedChild() == 2) {
                    playModeSwitchToOpponentGrid(null);
                    mPlayerGridBtn.setPressed(false);
                    mPlayModeOptionsBtn.setPressed(false);
                    mOpponentGridBtn.setPressed(true);
                }
                return true;

            }
        });

        mPlayModeOptionsBtn = (Button) findViewById(R.id.play_mode_options_btn);
        mPlayModeOptionsBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (playModeFlipper.getDisplayedChild() == 0 || playModeFlipper.getDisplayedChild() == 1) {
                    playModeSwitchToOptions(null);
                    mPlayerGridBtn.setPressed(false);
                    mOpponentGridBtn.setPressed(false);
                    mPlayModeOptionsBtn.setPressed(true);
                }
                return true;

            }
        });

        mShipSize2 = (ImageView) findViewById(R.id.linear_layout_ship_size_2);
        mShipSize3a = (ImageView) findViewById(R.id.linear_layout_ship_size_3_a);
        mShipSize3b = (ImageView) findViewById(R.id.linear_layout_ship_size_3_b);
        mShipSize4 = (ImageView) findViewById(R.id.linear_layout_ship_size_4);
        mShipSize5 = (ImageView) findViewById(R.id.linear_layout_ship_size_5);

        mainScreenViewFlipper = (ViewFlipper) findViewById(R.id.main_screen_view_flipper);
        playModeFlipper = (ViewFlipper) findViewById(R.id.play_mode_view_flipper);

        chooseModeInitializeView();
        playModeInitializeView();
        launchStartScreen();
    }

    public void setPlayModeFireBtnStatus(boolean status) {
        mPlayModeFireBtn.setEnabled(status);
    }

    public void chooseModeInitializeView() {
        LinearLayout topLinear = (LinearLayout) findViewById(R.id.choose_mode_top_linear);
        FrameLayout topFrame = (FrameLayout) findViewById(R.id.choose_mode_top_frame);
        chooseModeGrid = new quickShipViewChooseModeGrid(this, mGameModel, mChooseModeFrameLayout, mTempShipSpot);
        topFrame.getLayoutParams().height = Math.round(screenWidth);
        topFrame.addView(chooseModeGrid);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topLinear.setLayoutParams(param);
    }

    public void playModeInitializeView() {
        LinearLayout topOpponentLinear = (LinearLayout) findViewById(R.id.play_mode_opponent_top_linear);
        FrameLayout topOpponentFrame = (FrameLayout) findViewById(R.id.play_mode_opponent_top_frame);
        playModeOpponentGrid = new quickShipViewPlayModeOpponentGrid(this, mGameModel);
        topOpponentFrame.getLayoutParams().height = Math.round(screenWidth);
        topOpponentFrame.addView(playModeOpponentGrid);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topOpponentLinear.setLayoutParams(param);

        LinearLayout topPlayerLinear = (LinearLayout) findViewById(R.id.play_mode_player_top_linear);
        FrameLayout topPlayerFrame = (FrameLayout) findViewById(R.id.play_mode_player_top_frame);
        playModePlayerGrid = new quickShipViewPlayModePlayerGrid(this, mGameModel);
        topPlayerFrame.getLayoutParams().height = Math.round(screenWidth);
        topPlayerFrame.addView(playModePlayerGrid);
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topPlayerLinear.setLayoutParams(param2);
        playModeFlipper.setDisplayedChild(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!initialBoot) {
            reinitializeUI();
        } else {
            initialBoot = false;
        }
        thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean retry = true;
        running = false;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!initialBoot) {
            reinitializeUI();
        } else {
            initialBoot = false;
        }
    }

    public void reinitializeUI() {
        if (playModeFlipper.getDisplayedChild() == 0) {
            mOpponentGridBtn.setPressed(false);
            mPlayModeOptionsBtn.setPressed(false);
            mPlayerGridBtn.setPressed(true);
        } else if (playModeFlipper.getDisplayedChild() == 1) {
            mPlayModeOptionsBtn.setPressed(false);
            mPlayerGridBtn.setPressed(false);
            mOpponentGridBtn.setPressed(true);
        } else {
            mOpponentGridBtn.setPressed(false);
            mPlayerGridBtn.setPressed(false);
            mPlayModeOptionsBtn.setPressed(true);
        }
    }

    public void newGame() {
        mGameModel = new quickShipModel();
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
        if (playModeFlipper.getDisplayedChild() == 1 || playModeFlipper.getDisplayedChild() == 2) {
            playModeFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));
            playModeFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_right));
            playModeFlipper.setDisplayedChild(0);
            mOpponentGridBtn.setPressed(false);
            mPlayModeOptionsBtn.setPressed(false);
            mPlayerGridBtn.setPressed(true);
        }
    }

    public void playModeSwitchToOpponentGrid(View view) {
        if (playModeFlipper.getDisplayedChild() == 0 || playModeFlipper.getDisplayedChild() == 2) {
            if (playModeFlipper.getDisplayedChild() == 0) {
                playModeFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
                playModeFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_left));
            } else {
                playModeFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_left));
                playModeFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_right));
            }
            playModeFlipper.setDisplayedChild(1);
            mPlayerGridBtn.setPressed(false);
            mPlayModeOptionsBtn.setPressed(false);
            mOpponentGridBtn.setPressed(true);
        }
    }

    public void playModeSwitchToOptions(View view) {
        if (playModeFlipper.getDisplayedChild() == 0 || playModeFlipper.getDisplayedChild() == 1) {
            playModeFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_right));
            playModeFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_left));
            playModeFlipper.setDisplayedChild(2);
            mPlayerGridBtn.setPressed(false);
            mOpponentGridBtn.setPressed(false);
            mPlayModeOptionsBtn.setPressed(true);
        }
    }

    public void setChooseModeSelectedShip(View selectedShip) {
        mShipSize2.setBackgroundColor(0);
        mShipSize3a.setBackgroundColor(0);
        mShipSize3b.setBackgroundColor(0);
        mShipSize4.setBackgroundColor(0);
        mShipSize5.setBackgroundColor(0);

        if (mSelectedShip == null || (selectedShip != null && !mSelectedShip.equals(selectedShip))) {
            selectedShip.setBackgroundColor(getResources().getColor(R.color.choose_mode_ship_selected));
            mSelectedShip = (ImageView) selectedShip;
        }
        else {
            mSelectedShip = null;
        }
    }

    public void setRotation(View button) {
        chooseModeGrid.setOrientation();
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