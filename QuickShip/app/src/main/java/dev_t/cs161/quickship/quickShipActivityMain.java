package dev_t.cs161.quickship;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
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
    private ImageView mTempSelectedShip;
    private ImageView mShipSize2;
    private int mShipSize2width;
    private int mShipSize2height;
    private ImageView mShipSize3a;
    private int mShipSize3awidth;
    private int mShipSize3aheight;
    private ImageView mShipSize3b;
    private int mShipSize3bwidth;
    private int mShipSize3bheight;
    private ImageView mShipSize4;
    private int mShipSize4width;
    private int mShipSize4height;
    private ImageView mShipSize5;
    private int mShipSize5width;
    private int mShipSize5height;
    private Button mRotateBtn;
    private Button mPlaceBtn;
    private Button mDoneBtn;

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
        FrameLayout quickship_background = (FrameLayout) findViewById(R.id.quickship_background);
        BitmapDrawable background = new BitmapDrawable(scaleDownDrawableImage(R.drawable.ocean_top, Math.round(screenHeight), Math.round(screenWidth)));
        quickship_background.setBackgroundDrawable(background);
                startGame = (Button) findViewById(R.id.start_game_btn);
        startGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newGame();
                // Change to 1 to show the play mode screen instead
                mainScreenViewFlipper.setDisplayedChild(2);
            }
        });
    }

    public void initializeView() {
        setContentView(R.layout.quickship_main_screen);
        mActivityMain = this;
        mChooseModeFrameLayout = (FrameLayout) findViewById(R.id.choose_mode);
        mTempSelectedShip = (ImageView) findViewById(R.id.temp_ship_spot);
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

        mainScreenViewFlipper = (ViewFlipper) findViewById(R.id.main_screen_view_flipper);
        playModeFlipper = (ViewFlipper) findViewById(R.id.play_mode_view_flipper);

        mShipSize2 = (ImageView) findViewById(R.id.image_view_ship_size_2);
        mShipSize3a = (ImageView) findViewById(R.id.image_view_ship_size_3_a);
        mShipSize3b = (ImageView) findViewById(R.id.image_view_ship_size_3_b);
        mShipSize4 = (ImageView) findViewById(R.id.image_view_ship_size_4);
        mShipSize5 = (ImageView) findViewById(R.id.image_view_ship_size_5);

        mRotateBtn = (Button) findViewById(R.id.choose_mode_rotate_button);
        mPlaceBtn = (Button) findViewById(R.id.choose_mode_place_button);
        mDoneBtn = (Button) findViewById(R.id.choose_mode_done_button);

        launchStartScreen();
    }

    // This is required to avoid out of memory issues from loading large images
    public void loadChooseModeBitmaps(String tag, int layoutHeight, int layoutWidth) {

        switch (tag) {
            case "image_view_ship_size_2":
                mShipSize2.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size2_horizontal, layoutHeight, layoutWidth));
                break;

            case "image_view_ship_size_3_a":
                mShipSize3a.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size3_a_horizontal, layoutHeight, layoutWidth));
                break;

            case "image_view_ship_size_3_b":
                mShipSize3b.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size3_b_horizontal, layoutHeight, layoutWidth));
                break;

            case "image_view_ship_size_4":
                mShipSize4.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size4_horizontal, layoutHeight, layoutWidth));
                break;

            case "image_view_ship_size_5":
                mShipSize5.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size5_horizontal, layoutHeight, layoutWidth));
                break;

            case "splash_screen_parent":
                ImageView quickship_logo_img = (ImageView) findViewById(R.id.quickship_logo_img);
                quickship_logo_img.setImageBitmap(scaleDownDrawableImage(R.drawable.quickship_splashscreen, layoutHeight, layoutWidth));
                break;

            case "team_logo_parent":
                ImageView company_logo = (ImageView) findViewById(R.id.company_logo);
                company_logo.setImageBitmap(scaleDownDrawableImage(R.drawable.company_logo_black, layoutHeight, layoutWidth));
                break;
        }
    }

    public void setPlayModeFireBtnStatus(boolean status) {
        mPlayModeFireBtn.setEnabled(status);
    }

    public void setChooseModeRotateBtnStatus(boolean status) {
        mRotateBtn.setEnabled(status);
    }

    public void setChooseModePlaceBtnStatus(boolean status) {
        mPlaceBtn.setEnabled(status);
    }

    public void setChooseModeDoneBtnStatus(boolean status) {
        mDoneBtn.setEnabled(status);
    }

    public void chooseModeInitializeView() {
        LinearLayout topLinear = (LinearLayout) findViewById(R.id.choose_mode_top_linear);
        FrameLayout topFrame = (FrameLayout) findViewById(R.id.choose_mode_top_frame);
        chooseModeGrid = new quickShipViewChooseModeGrid(this, mGameModel, mChooseModeFrameLayout, mTempSelectedShip);
        topFrame.getLayoutParams().height = Math.round(screenWidth);
        topFrame.addView(chooseModeGrid);
        FrameLayout topFrameBorder = (FrameLayout) findViewById(R.id.choose_mode_top_frame_border);
        topFrameBorder.addView(new quickShipViewGridBorder(this));
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
        FrameLayout topOpponentFrameBorder = (FrameLayout) findViewById(R.id.play_mode_opponent_top_frame_border);
        topOpponentFrameBorder.addView(new quickShipViewGridBorder(this));

        LinearLayout topPlayerLinear = (LinearLayout) findViewById(R.id.play_mode_player_top_linear);
        FrameLayout topPlayerFrame = (FrameLayout) findViewById(R.id.play_mode_player_top_frame);
        playModePlayerGrid = new quickShipViewPlayModePlayerGrid(this, mGameModel);
        topPlayerFrame.getLayoutParams().height = Math.round(screenWidth);
        topPlayerFrame.addView(playModePlayerGrid);
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topPlayerLinear.setLayoutParams(param2);
        FrameLayout topPlayerFrameBorder = (FrameLayout) findViewById(R.id.play_mode_player_top_frame_border);
        topPlayerFrameBorder.addView(new quickShipViewGridBorder(this));
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
        chooseModeInitializeView();
        playModeInitializeView();
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
            String shipTag = (String) mSelectedShip.getTag();
            switch (shipTag) {
                case "image_view_ship_size_2":
                    chooseModeGrid.setShipSelected(ShipType.TWO);
                    break;

                case "image_view_ship_size_3_a":
                    chooseModeGrid.setShipSelected(ShipType.THREE_A);
                    break;

                case "image_view_ship_size_3_b":
                    chooseModeGrid.setShipSelected(ShipType.THREE_B);
                    break;

                case "image_view_ship_size_4":
                    chooseModeGrid.setShipSelected(ShipType.FOUR);
                    break;

                case "image_view_ship_size_5":
                    chooseModeGrid.setShipSelected(ShipType.FIVE);
                    break;
            }
        } else {
            mSelectedShip = null;
            chooseModeGrid.deSelectShip();
        }
    }

    public void placeButton(View button) {
        mShipSize2.setBackgroundColor(0);
        mShipSize3a.setBackgroundColor(0);
        mShipSize3b.setBackgroundColor(0);
        mShipSize4.setBackgroundColor(0);
        mShipSize5.setBackgroundColor(0);
        mSelectedShip = null;
        chooseModeGrid.deSelectShip();
    }

    public void doneButton(View button) {
        mainScreenViewFlipper.setDisplayedChild(1);
        // temporary setting the opponent board to what we set in choose mode for the player
        // used for testing since we don't have bluetooth yet
        mGameModel.setOpponentGameBoard(mGameModel.getPlayerGameBoard());
        reinitializeUI();
    }

    public void setRotation(View button) {
        chooseModeGrid.setOrientation();
    }

    public Bitmap scaleDownDrawableImage(int res, int reqHeight, int reqWidth) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(), res, o);

        int inSampleSize = 1;

        if (o.outHeight > reqHeight || o.outWidth > reqWidth) {

            final int halfHeight = o.outHeight / 2;
            final int halfWidth = o.outWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        //o2.inScaled = false;
        o2.inSampleSize = inSampleSize;
        b = BitmapFactory.decodeResource(getResources(), res, o2);

        return b;
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