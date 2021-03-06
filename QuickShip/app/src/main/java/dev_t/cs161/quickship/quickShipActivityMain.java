package dev_t.cs161.quickship;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.PopupWindow.OnDismissListener;
import android.view.View.OnClickListener;

import com.daasuu.library.DisplayObject;
import com.daasuu.library.FPSTextureView;
import com.daasuu.library.callback.AnimCallBack;
import com.daasuu.library.drawer.BitmapDrawer;
import com.daasuu.library.easing.Ease;
import com.daasuu.library.util.Util;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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
    private FrameLayout mSplashScreenFrameLayout;
    private ImageView mSelectedShip;
    private ImageView mTempSelectedShip;
    private ImageView mShipSize2;
    private ImageView mShipSize3a;
    private ImageView mShipSize3b;
    private ImageView mShipSize4;
    private ImageView mShipSize5;
    private Button mRotateBtn;
    private Button mPlaceBtn;
    private Button mDoneBtn;
    private Button mBluetoothEnableButton;
    private EditText mSplashScreenPlayerName;
    private String mPlayerName;
    private BluetoothAdapter btAdapter;
    private StringBuilder messages;
    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private BluetoothConnectionService mBluetoothConnection;
    private DeviceListAdapter mDeviceListAdapter;
    private BluetoothDevice mBTDevice;
    private AlertDialog mBTListViewDialog;
    private ListView mDevicesListView;
    private ScrollView mChooseModeScroller;
    private TextView mChooseModeChatMessageLog;
    private EditText mChooseModeEditTextSend;
    private ScrollView mPlayModeScroller;
    private TextView mPlayModeChatMessageLog;
    private EditText mPlayModeEditTextSend;
    private TextView mPlayModeStatusText;
    private boolean playerChooseModeDone;
    private boolean opponentChooseModeDone;
    private boolean playerTurnDone;
    private boolean opponentTurnDone;
    private boolean gameOver;
    private int playerChosenTarget;
    private int opponentChosenTarget;
    private String playerChosenEmoji;
    private String opponentChosenEmoji = "\uD83D\uDE00";
    private int turnCount;
    private EmojiconsPopup emojiPopup;
    private FPSTextureView mFPSTextureView;
    private debugQuickShipViewPlayModeOpponentGrid testGrid;
    private Bitmap emojiBitmap;
    private Bitmap mHitText;
    private Bitmap mMissText;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screen);
        screenWidth = (float) screen.x;
        screenHeight = (float) screen.y;
        initialBoot = true;
        initializeView();
        emojiPopUpInitializer();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void initializeView() {
        setContentView(R.layout.quickship_main_screen);
        mActivityMain = this;

        mSplashScreenPlayerName = (EditText) findViewById(R.id.splash_screen_player_name);
        mPlayModeStatusText = (TextView) findViewById(R.id.play_mode_status);
        mChooseModeFrameLayout = (FrameLayout) findViewById(R.id.choose_mode);
        mSplashScreenFrameLayout = (FrameLayout) findViewById(R.id.splash_screen);
        mTempSelectedShip = (ImageView) findViewById(R.id.temp_ship_spot);
        mPlayModeFireBtn = (Button) findViewById(R.id.play_mode_fire_btn);
        mPlayModeFireBtn.setEnabled(false);
        mPlayModeFireBtn.setBackgroundResource(R.drawable.firebutton_01_disabled);

        mPlayModeFireBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mPlayModeFireBtn.setBackgroundResource(R.drawable.firebutton_02);
                return false;
            }
        });

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

        mChooseModeScroller = (ScrollView) findViewById(R.id.choose_mode_scroller);
        mChooseModeChatMessageLog = (TextView) findViewById(R.id.edit_text_chat_log);
        mChooseModeEditTextSend = (EditText) findViewById(R.id.edit_text_send_message);

        mPlayModeScroller = (ScrollView) findViewById(R.id.play_mode_scroller);
        mPlayModeChatMessageLog = (TextView) findViewById(R.id.edit_text_chat_log_in_game);
        mPlayModeEditTextSend = (EditText) findViewById(R.id.edit_text_send_message_in_game);

        //Chat Box for Ship Placement Screen
        mChooseModeEditTextSend.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager inputManager = (InputMethodManager) mActivityMain.getSystemService(mActivityMain.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(mActivityMain.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String full_msg = getColoredSpanned(mPlayerName + ": " + mChooseModeEditTextSend.getText().toString(), "#000000");
                    messages.append(full_msg + "<br>");
                    mChooseModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
                    mPlayModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
                    mChooseModeScroller.smoothScrollTo(0, mChooseModeChatMessageLog.getBottom());
                    mPlayModeScroller.smoothScrollTo(0, mPlayModeChatMessageLog.getBottom());

                    quickShipBluetoothPacketsToBeSent data = new quickShipBluetoothPacketsToBeSent(quickShipBluetoothPacketsToBeSent.CHAT, full_msg);
                    //Log.d("Chat Parcel Byte Size: ",""+ParcelableUtil.marshall(data).length); //debugging
                    mBluetoothConnection.write(ParcelableUtil.marshall(data));
                    mChooseModeEditTextSend.setText("");//clear message
                    return true;//em
                }
                return false;
            }
        });

        //Chat Box for Opponent screen
        mPlayModeEditTextSend.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager inputManager = (InputMethodManager) mActivityMain.getSystemService(mActivityMain.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(mActivityMain.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String full_msg = getColoredSpanned(mPlayerName + ": " + mPlayModeEditTextSend.getText().toString(), "#000000");
                    messages.append(full_msg + "<br>");
                    mChooseModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
                    mPlayModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
                    mChooseModeScroller.smoothScrollTo(0, mChooseModeChatMessageLog.getBottom());
                    mPlayModeScroller.smoothScrollTo(0, mPlayModeChatMessageLog.getBottom());

                    quickShipBluetoothPacketsToBeSent data = new quickShipBluetoothPacketsToBeSent(quickShipBluetoothPacketsToBeSent.CHAT, full_msg);
                    mBluetoothConnection.write(ParcelableUtil.marshall(data));
                    mPlayModeEditTextSend.setText("");//clear message

                    return true;
                }
                return false;
            }
        });
        mPlayModeEditTextSend.clearFocus();

        startGame = (Button) findViewById(R.id.start_game_btn);
        mBluetoothEnableButton = (Button) findViewById(R.id.splash_creen_bluetooth_btn);

        otherViewsInitializeObjects();

        blueToothInitializeObjects();

        launchStartScreen();
    }

    public void otherViewsInitializeObjects() {
        mGameModel = new quickShipModel();
        LinearLayout topLinear = (LinearLayout) findViewById(R.id.choose_mode_top_linear);
        FrameLayout topFrame = (FrameLayout) findViewById(R.id.choose_mode_top_frame);
        chooseModeGrid = new quickShipViewChooseModeGrid(this, mGameModel, mChooseModeFrameLayout, mTempSelectedShip);
        topFrame.getLayoutParams().height = Math.round(screenWidth);
        topFrame.addView(chooseModeGrid);
        FrameLayout topFrameBorder = (FrameLayout) findViewById(R.id.choose_mode_top_frame_border);
        topFrameBorder.addView(new quickShipViewGridBorder(this, getResources().getColor(R.color.choose_mode_player_frame_color)));
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topLinear.setLayoutParams(param);

        LinearLayout topOpponentLinear = (LinearLayout) findViewById(R.id.play_mode_opponent_top_linear);
        FrameLayout topOpponentFrame = (FrameLayout) findViewById(R.id.play_mode_opponent_top_frame);
        playModeOpponentGrid = new quickShipViewPlayModeOpponentGrid(this, mGameModel);
        topOpponentFrame.getLayoutParams().height = Math.round(screenWidth);
        topOpponentFrame.addView(playModeOpponentGrid);
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topOpponentLinear.setLayoutParams(param2);
        FrameLayout topOpponentFrameBorder = (FrameLayout) findViewById(R.id.play_mode_opponent_top_frame_border);
        topOpponentFrameBorder.addView(new quickShipViewGridBorder(this, getResources().getColor(R.color.play_mode_opponent_frame_color)));

        LinearLayout topPlayerLinear = (LinearLayout) findViewById(R.id.play_mode_player_top_linear);
        FrameLayout topPlayerFrame = (FrameLayout) findViewById(R.id.play_mode_player_top_frame);
        playModePlayerGrid = new quickShipViewPlayModePlayerGrid(this, mGameModel);
        topPlayerFrame.getLayoutParams().height = Math.round(screenWidth);
        topPlayerFrame.addView(playModePlayerGrid);
        LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Math.round(screenWidth));
        topPlayerLinear.setLayoutParams(param3);
        FrameLayout topPlayerFrameBorder = (FrameLayout) findViewById(R.id.play_mode_player_top_frame_border);
        topPlayerFrameBorder.addView(new quickShipViewGridBorder(this, getResources().getColor(R.color.play_mode_player_frame_color)));
    }

    public void blueToothInitializeObjects() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        messages = new StringBuilder();
        if (btAdapter == null) {
            startGame.setEnabled(false);
            mSplashScreenPlayerName.setVisibility(View.INVISIBLE);//em
            AlertDialog alertDialog = new AlertDialog.Builder(mActivityMain).create();
            alertDialog.setTitle("Unsupported Game");
            alertDialog.setMessage("Device does NOT support Bluetooth");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                  new DialogInterface.OnClickListener() {
                                      public void onClick(DialogInterface dialog, int which) {
                                          dialog.dismiss();
                                          // Temporary show the chooes mode even though there's no bluetooth
                                          // newGame();
                                          // mainScreenViewFlipper.setDisplayedChild(1);
                                      }
                                  });
            //alertDialog.show();
        } else if (!btAdapter.isEnabled()) {
            startGame.setEnabled(false);
            mSplashScreenPlayerName.setVisibility(View.INVISIBLE);//em
            mBluetoothEnableButton.setVisibility(View.VISIBLE);
            AlertDialog alertDialog = new AlertDialog.Builder(mActivityMain).create();
            alertDialog.setTitle("Bluetooth Required");
            alertDialog.setMessage(getResources().getString(R.string.splash_screen_bluetooth_alert_message));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                  new DialogInterface.OnClickListener() {
                                      public void onClick(DialogInterface dialog, int which) {
                                          dialog.dismiss();
                                      }
                                  });
            alertDialog.show();
        }
        mDevicesListView = new ListView(this);
        // Used for receiving quickship parcelables
        //registerReceiver(quickShipDock, new IntentFilter("quickShipCargo"));
        LocalBroadcastManager.getInstance(this).registerReceiver(quickShipDock, new IntentFilter("quickShipCargo"));
        // Used for initial connection of devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBtReceiver, filter);
    }

    public void launchStartScreen() {
        SharedPreferences preferences = getSharedPreferences("quickShipSettings", MODE_PRIVATE);
        mPlayerName = preferences.getString("playerName", "");//em
        mSplashScreenPlayerName.setText(mPlayerName);
        mainScreenViewFlipper.setDisplayedChild(0);
        BitmapDrawable background = new BitmapDrawable(scaleDownDrawableImage(R.drawable.ocean_top, Math.round(screenHeight), Math.round(screenWidth)));
        mSplashScreenFrameLayout.setBackgroundDrawable(background);
        startGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String playerNameCheck = mSplashScreenPlayerName.getText().toString();
                if (playerNameCheck.matches("")) {
                    Toast.makeText(mActivityMain, "Please enter a player name", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    startBTListViewDialog();
                }
            }
        });
    }

    public void startBTListViewDialog() {
        String playerNameCheck = mSplashScreenPlayerName.getText().toString();
        if (!playerNameCheck.equals(mPlayerName)) {
            mPlayerName = playerNameCheck;
            SharedPreferences preferences = getSharedPreferences("quickShipSettings", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("playerName", playerNameCheck);
            editor.commit();
        }

        if (btAdapter.setName("QSBT_" + mPlayerName)) {
            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);

            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(mBtReceiver, filter);

            startActivity(discoverableIntent);

            func_alertDisplayBTDevices();
        }
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
        if(status == false){
            mPlayModeFireBtn.setBackgroundResource(R.drawable.firebutton_01_disabled);
        }
    }

    public void setButtonBack(boolean on){
        if(on == true){
            mPlayModeFireBtn.setBackgroundResource(R.drawable.firebutton_01);
        }
        else {
            mPlayModeFireBtn.setBackgroundResource(R.drawable.firebutton_01_disabled);
        }
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
        gameOver = false;
        turnCount = 1;
        messages.setLength(0);
        playerChooseModeDone = false;
        opponentChooseModeDone = false;
        playerTurnDone = false;
        opponentTurnDone = false;
        mPlayModeStatusText.setVisibility(View.INVISIBLE);
        mGameModel = new quickShipModel();
        chooseModeGrid.setGameModel(mGameModel);
        playModeOpponentGrid.setGameModel(mGameModel);
        playModePlayerGrid.setGameModel(mGameModel);
        chooseModeGrid.invalidate();
        playModeOpponentGrid.invalidate();
        playModePlayerGrid.invalidate();
        mPlayModeStatusText.setText("");
        //mPlayModeFireBtn.setText("Fire!");
        playModeFlipper.setDisplayedChild(1);
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
        if (!playerChooseModeDone) {
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
                        chooseModeGrid.setShipSelected(quickShipModelBoardSlot.TWO);
                        changePlacedShipsBitmaps();
                        mShipSize2.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size2_horizontal, mShipSize2.getHeight(), mShipSize2.getWidth()));
                        break;

                    case "image_view_ship_size_3_a":
                        chooseModeGrid.setShipSelected(quickShipModelBoardSlot.THREE_A);
                        changePlacedShipsBitmaps();
                        mShipSize3a.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size3_a_horizontal, mShipSize3a.getHeight(), mShipSize3a.getWidth()));
                        break;

                    case "image_view_ship_size_3_b":
                        chooseModeGrid.setShipSelected(quickShipModelBoardSlot.THREE_B);
                        changePlacedShipsBitmaps();
                        mShipSize3b.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size3_b_horizontal, mShipSize3b.getHeight(), mShipSize3b.getWidth()));
                        break;

                    case "image_view_ship_size_4":
                        chooseModeGrid.setShipSelected(quickShipModelBoardSlot.FOUR);
                        changePlacedShipsBitmaps();
                        mShipSize4.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size4_horizontal, mShipSize4.getHeight(), mShipSize4.getWidth()));
                        break;

                    case "image_view_ship_size_5":
                        chooseModeGrid.setShipSelected(quickShipModelBoardSlot.FIVE);
                        changePlacedShipsBitmaps();
                        mShipSize5.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size5_horizontal, mShipSize5.getHeight(), mShipSize5.getWidth()));
                        break;
                }
            } else {
                mSelectedShip = null;
                chooseModeGrid.deSelectShip();
            }
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
        changePlacedShipsBitmaps();
    }

    public void changePlacedShipsBitmaps() {
        for(int i = 0; i < 100; i++){
            if(mGameModel.getPlayerGameBoard().getShipSlotAtIndex(i).isOccupied() && mGameModel.getPlayerGameBoard().getShipSlotAtIndex(i).isAnchor()) {
                quickShipModelBoardSlot currentShip = mGameModel.getPlayerGameBoard().getShipSlotAtIndex(i);
                switch (currentShip.getShipType()) {
                    case quickShipModelBoardSlot.TWO:
                        mShipSize2.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size2_01_used, mShipSize2.getHeight(), mShipSize2.getWidth()));
                        break;

                    case quickShipModelBoardSlot.THREE_A:
                        mShipSize3a.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size3_01_used, mShipSize3a.getHeight(), mShipSize3a.getWidth()));
                        break;

                    case quickShipModelBoardSlot.THREE_B:
                        mShipSize3b.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size3_02_used, mShipSize3b.getHeight(), mShipSize3b.getWidth()));
                        break;

                    case quickShipModelBoardSlot.FOUR:
                        mShipSize4.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size4_01_used, mShipSize4.getHeight(), mShipSize4.getWidth()));
                        break;

                    case quickShipModelBoardSlot.FIVE:
                        mShipSize5.setImageBitmap(scaleDownDrawableImage(R.drawable.ship_size5_01_used, mShipSize5.getHeight(), mShipSize5.getWidth()));
                        break;
                }
            }
        }
    }

    public void play_again_btn(View button) {
        mainScreenViewFlipper.setDisplayedChild(2);
    }

    public void doneButton(View button) {
        setChooseModeDoneBtnStatus(false);
        //String x = mGameModel.convertPlayerBoardToGSON();
        byte[] x = mGameModel.convertPlayerBoardToByteArray();
        Log.d("BOARD SIZE: ", "" + x.length);
        //quickShipBluetoothPacketsToBeSent data = new quickShipBluetoothPacketsToBeSent(quickShipBluetoothPacketsToBeSent.SHIPS_PLACED, mGameModel.convertPlayerBoardToGSON());
        quickShipBluetoothPacketsToBeSent data = new quickShipBluetoothPacketsToBeSent(quickShipBluetoothPacketsToBeSent.SHIPS_PLACED, mGameModel.convertPlayerBoardToByteArray());
        Log.d("Final Parcel Size: ", "" + ParcelableUtil.marshall(data).length);
        mBluetoothConnection.write(ParcelableUtil.marshall(data));
        playerChooseModeDone = true;
        checkChooseModeDone("player");
    }

    public void checkChooseModeDone(String status) {
        if (playerChooseModeDone && opponentChooseModeDone) {
            mainScreenViewFlipper.setDisplayedChild(2);
            reinitializeUI();
            String msg = getColoredSpanned("The game has started!", "#eda136");
            messages.append(msg + "<br>");
            mChooseModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
            mPlayModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
            mChooseModeScroller.smoothScrollTo(0, mChooseModeChatMessageLog.getBottom());
            mPlayModeScroller.smoothScrollTo(0, mPlayModeChatMessageLog.getBottom());
        } else {
            if (status.equals("player")) {
                String msg = getColoredSpanned("Ships placed. Waiting for opponent to finish ship placements.", "#eda136");
                messages.append(msg + "<br>");
                mChooseModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
                mPlayModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
                mChooseModeScroller.smoothScrollTo(0, mChooseModeChatMessageLog.getBottom());
                mPlayModeScroller.smoothScrollTo(0, mPlayModeChatMessageLog.getBottom());
            } else {
                String msg = getColoredSpanned("Your opponent has finished placing ships.", "#eda136");
                messages.append(msg + "<br>");
                mChooseModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
                mPlayModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
                mChooseModeScroller.smoothScrollTo(0, mChooseModeChatMessageLog.getBottom());
                mPlayModeScroller.smoothScrollTo(0, mPlayModeChatMessageLog.getBottom());
            }
        }
    }

    //Update opponent grid when user presses fire button
    public void fireOpponentBtn(View v) {

        if (!gameOver) {
            emojiPopup.showAtBottom();
        }
        else {
            // Add bluetooth disconnection code here
            mainScreenViewFlipper.setDisplayedChild(0);
            mBluetoothConnection.disconnect_threads();

        }
    }

    public void checkPlayModeTurnDone(String status) {
        if (playerTurnDone && opponentTurnDone) {
            mGameModel.getPlayerGameBoard().setHit(opponentChosenTarget, true);
            mGameModel.getOpponentGameBoard().setHit(playerChosenTarget, true);
            mGameModel.getPlayerGameBoard().getShipSlotAtIndex(opponentChosenTarget).setEmoji(opponentChosenEmoji);
            mGameModel.getOpponentGameBoard().getShipSlotAtIndex(playerChosenTarget).setEmoji(playerChosenEmoji);
            playModeOpponentGrid.deSelectCell();
            playModePlayerGrid.invalidate();
            playModeOpponentGrid.invalidate();
            String msg = getColoredSpanned("Turn: "+turnCount, "#349edb");
            messages.append(msg + "<br>");
            String msg2 = getColoredSpanned("-------------------------", "#349edb");
            messages.append(msg2 + "<br>");
            if (mGameModel.getOpponentGameBoard().getShipSlotAtIndex(playerChosenTarget).isOccupied()) {
                String msg3 = getColoredSpanned("You hit a ship!", "#db756b");
                messages.append(msg3 + "<br>");
                mPlayModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
            } else {
                String msg3 = getColoredSpanned("You missed!", "#db756b");
                messages.append(msg3 + "<br>");
                mPlayModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
            }
            if (mGameModel.getPlayerGameBoard().getShipSlotAtIndex(opponentChosenTarget).isOccupied()) {
                String msg3 = getColoredSpanned("Your opponent hit your ship!", "#db756b");
                messages.append(msg3 + "<br>");
                mPlayModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
            } else {
                String msg3 = getColoredSpanned("Your opponent missed!", "#db756b");
                messages.append(msg3 + "<br>");
                mPlayModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
            }
            boolean playerGameOver = mGameModel.getPlayerGameBoard().checkGameOver();
            boolean opponentGameOver = mGameModel.getOpponentGameBoard().checkGameOver();
            if (playerGameOver && opponentGameOver) {
                mPlayModeStatusText.setText("Game Ended in a draw!");
                mPlayModeStatusText.setVisibility(View.VISIBLE);
            } else if (playerGameOver) {
                mPlayModeStatusText.setText("You lost!");
                mPlayModeStatusText.setVisibility(View.VISIBLE);
            } else if (opponentGameOver) {
                mPlayModeStatusText.setText("You Won!");
                mPlayModeStatusText.setVisibility(View.VISIBLE);
            }
            if (!playerGameOver && !opponentGameOver) {
                turnCount++;
                playerTurnDone = false;
                opponentTurnDone = false;
                mPlayModeStatusText.setVisibility(View.INVISIBLE);
            } else {
                gameOver = true;
                mPlayModeFireBtn.setText("Play Again");
                mPlayModeFireBtn.setEnabled(true);
            }
        } else {
            mPlayModeFireBtn.setEnabled(false);
            if (status.equals("player")) {
                mPlayModeStatusText.setText("Waiting on opponent...");
                mPlayModeStatusText.setVisibility(View.VISIBLE);
                mPlayModeFireBtn.setBackgroundResource(R.drawable.firebutton_02);
            } else {
                mPlayModeStatusText.setText("Your opponent is done...");
                mPlayModeStatusText.setVisibility(View.VISIBLE);
            }
        }
        mPlayModeScroller.smoothScrollTo(0, mPlayModeChatMessageLog.getBottom());
    }

    public boolean isPlayerChooseModeDone() {
        return playerChooseModeDone;
    }

    public void setPlayerChooseModeDone(boolean playerChooseModeDone) {
        this.playerChooseModeDone = playerChooseModeDone;
    }

    public boolean isOpponentChooseModeDone() {
        return opponentChooseModeDone;
    }

    public void setOpponentChooseModeDone(boolean opponentChooseModeDone) {
        this.opponentChooseModeDone = opponentChooseModeDone;
    }

    public boolean isPlayerTurnDone() {
        return playerTurnDone;
    }

    public void enableBluetooth(View button) {
        toast_displayMessage("Attempting to enable Bluetooth...");

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        IntentFilter BlueToothfilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBtReceiver, BlueToothfilter);

        int REQUEST_ENABLE_BT = 1;
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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
        o2.inScaled = false;
        o2.inSampleSize = inSampleSize;
        b = BitmapFactory.decodeResource(getResources(), res, o2);

        return b;
    }

    private void toast_displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    private final BroadcastReceiver quickShipDock = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras().getParcelable("quickShipPackage") != null) {
                quickShipBluetoothPacketsToBeSent data = intent.getExtras().getParcelable("quickShipPackage");
                int packetType = data.getPacketType();
                Log.d("DEBUG", "PACKETTYPE RECEIVED: " + packetType);
                switch (packetType) {
                    case quickShipBluetoothPacketsToBeSent.CHAT:
                        String text = data.getChatMessage();
                        messages.append(text + "<br>");
                        mChooseModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
                        mPlayModeChatMessageLog.setText(Html.fromHtml(messages.toString()));
                        mChooseModeScroller.smoothScrollTo(0, mChooseModeChatMessageLog.getBottom());
                        mPlayModeScroller.smoothScrollTo(0, mPlayModeChatMessageLog.getBottom());
                        break;
                    case quickShipBluetoothPacketsToBeSent.SHIPS_PLACED:
                        byte[] tempBoard = data.getBoardv2();
                        Log.d("DEBUG - received Board", "" + tempBoard.length);
                        mGameModel.setOpponentBoardFromByteArray(tempBoard);
                        opponentChooseModeDone = true;
                        checkChooseModeDone("opponent");
                        break;

                    case quickShipBluetoothPacketsToBeSent.MOVES:
                        opponentChosenTarget = data.getMovesChosen();
                        opponentChosenEmoji = data.getEmojiType();
                        opponentTurnDone = true;
                        checkPlayModeTurnDone("opponent");
                        break;

                    case quickShipBluetoothPacketsToBeSent.TURN_DONE:
                        break;

                    case quickShipBluetoothPacketsToBeSent.GAME_WON:
                        break;

                    case quickShipBluetoothPacketsToBeSent.QUIT:
                        break;

                    case quickShipBluetoothPacketsToBeSent.NAME_CHANGE:
                        break;

                    case quickShipBluetoothPacketsToBeSent.DISCONNECTED:
                        AlertDialog alertDialog = new AlertDialog.Builder(mActivityMain).create();
                        alertDialog.setTitle("Player Has Disconnected");
                        alertDialog.setMessage("Returning to main screen.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        mainScreenViewFlipper.setDisplayedChild(0);
                        mBluetoothConnection.disconnect_threads();
                        break;
                }
            } else if (intent.getBooleanExtra("startGame", false)) {
                Log.d("MainActivity ->", "startGame triggered.");
                mBTListViewDialog.dismiss();
                newGame();
                mainScreenViewFlipper.setDisplayedChild(1);
                //Notify Second Player to start Game.
                quickShipBluetoothPacketsToBeSent data = new quickShipBluetoothPacketsToBeSent(quickShipBluetoothPacketsToBeSent.TURN_DONE, true);
                mBluetoothConnection.write(ParcelableUtil.marshall(data));
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // Start Game pressed; Discovering Devices
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null && device.getName().contains("QSBT_")) {
                    mBTDevices.add(device);
                }
                Log.d("Discovered Device: ", "" + device.getName());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.quickship_device_adapter_view, mBTDevices);
                mDevicesListView.setAdapter(mDeviceListAdapter);
                mDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        btAdapter.cancelDiscovery();
                        String deviceName = mBTDevices.get(i).getName();
                        String deviceMAC = mBTDevices.get(i).getAddress();
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            toast_displayMessage("Attempting to bond with...\n" + deviceName + "\n" + deviceMAC);
                            if (mBTDevices.get(i).getBondState() == BluetoothDevice.BOND_BONDED) {
                                Log.d("Bonding With", "On click " + mBTDevices.get(i).getName());
                                mBTDevice = mBTDevices.get(i);
                                mBluetoothConnection = new BluetoothConnectionService(mActivityMain);
                                startConnection();
                            }else{
                                mBTDevices.get(i).createBond();
                            }

                        }
                    }
                });
            }
            // Discoverability enabled
            else if (action.equals(btAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, btAdapter.ERROR);
                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        String message = "Discoverability Enabled.\nDevice name: " + btAdapter.getName() + "\nDevice MAC: " + btAdapter.getAddress();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        toast_displayMessage("Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        toast_displayMessage("Connected.");
                        break;
                }
            }
            // Check if bluetooth has been toggled on or off
            else if (action.equals(btAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, btAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        toast_displayMessage("Bluetooth Off.");
                        startGame.setEnabled(false);
                        mSplashScreenPlayerName.setVisibility(View.INVISIBLE);//em
                        mBluetoothEnableButton.setVisibility(View.VISIBLE);
                        AlertDialog alertDialog = new AlertDialog.Builder(mActivityMain).create();
                        alertDialog.setTitle("Bluetooth Required");
                        alertDialog.setMessage(getResources().getString(R.string.splash_screen_bluetooth_alert_message));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                              new DialogInterface.OnClickListener() {
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      dialog.dismiss();
                                                  }
                                              });
                        alertDialog.show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        toast_displayMessage("Bluetooth Turning Off...");
                        startGame.setEnabled(false);
                        mBluetoothEnableButton.setVisibility(View.VISIBLE);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        String name = btAdapter.getName();
                        String mac = btAdapter.getAddress();
                        Toast.makeText(mActivityMain, "Bluetooth On.\nDevice name: " + name + "\nDevice MAC: " + mac, Toast.LENGTH_LONG).show();
                        startGame.setEnabled(true);
                        mSplashScreenPlayerName.setVisibility(View.VISIBLE);//em
                        mBluetoothEnableButton.setVisibility(View.INVISIBLE);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        toast_displayMessage("Bluetooth Turning On...");
                        break;
                }
            }
            // Discovered device Item pressed; Pairing devices
            else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // case1: bonded already
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d("BT Bonding", "BONDED with "+device.getName());
                    AlertDialog alertDialog = new AlertDialog.Builder(mActivityMain).create();
                    alertDialog.setTitle("Devices Successfully Paired");
                    alertDialog.setMessage("Please Reconnect");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    //mBTDevice = device; // device it is paired with
                    //mBluetoothConnection = new BluetoothConnectionService(mActivityMain);
                    //startConnection();
                    //mBTListViewDialog.dismiss();
                    // Make new game. Show choose mode screen
                    //newGame();
                    //mainScreenViewFlipper.setDisplayedChild(1);
                }

                // case 2: creating a bond
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    toast_displayMessage("Pairing Devices...");
                    Log.d("BT Bonding", "Bonding with "+device.getName());
                }

                // case 3: disconnecting a bond
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d("BT Bonding", "Bond NONE with "+device.getName());
                    /*
                    AlertDialog alertDialog = new AlertDialog.Builder(mActivityMain).create();
                    alertDialog.setTitle("Disconnect");
                    alertDialog.setMessage("Device Disconnected");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                          new DialogInterface.OnClickListener() {
                                              public void onClick(DialogInterface dialog, int which) {
                                                  dialog.dismiss();
                                              }
                                          });
                    alertDialog.show();
                    */
                }

            }


        }
    };

    private void startConnection() {
        ((ViewGroup) mDevicesListView.getParent()).removeView(mDevicesListView);
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }

    private void startBTConnection(BluetoothDevice device, UUID uuid) {
        mBluetoothConnection.startClient(device, uuid);
    }

    private void func_alertDisplayBTDevices() {
        if (!btAdapter.isDiscovering())
            btAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBtReceiver, filter);

        final AlertDialog.Builder ad_displayBTDevices = new AlertDialog.Builder(mActivityMain);
        ad_displayBTDevices.setTitle("Nearby Bluetooth Devices");
        ad_displayBTDevices.setMessage("Select a Device...");
        mDevicesListView.setAdapter(mDeviceListAdapter);
        ad_displayBTDevices.setView(mDevicesListView);
        ad_displayBTDevices.setCancelable(true);
        ad_displayBTDevices.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (btAdapter.isDiscovering())
                    btAdapter.cancelDiscovery();

                toast_displayMessage("Refreshing...");

                //Clear mBTDevices for fresh scan
                mBTDevices.clear();

                // Lollipop+ may need extra manual permissions check
                //checkBTPermissions();

                btAdapter.startDiscovery();
                ((ViewGroup) mDevicesListView.getParent()).removeView(mDevicesListView);
                //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                //registerReceiver(mBtReceiver, filter);

                func_alertDisplayBTDevices();
            }
        });
        ad_displayBTDevices.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((ViewGroup) mDevicesListView.getParent()).removeView(mDevicesListView);
                dialogInterface.cancel();
            }
        });
        mBTListViewDialog = ad_displayBTDevices.create();
        mBTListViewDialog.show();
        mDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btAdapter.cancelDiscovery();
                String deviceName = mBTDevices.get(i).getName();
                String deviceMAC = mBTDevices.get(i).getAddress();

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    toast_displayMessage("Attempting to connect with...\n" + deviceName + "\n" + deviceMAC);

                    if(mBTDevices.get(i).createBond()) {
                        Log.d("BT Create Bond", "True");
                        mBTDevice = mBTDevices.get(i);
                        mBluetoothConnection = new BluetoothConnectionService(mActivityMain);
                        startConnection();
                    }
                }

                mBTListViewDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btAdapter != null)
            btAdapter.cancelDiscovery();
        // Don't forget to unregister the ACTION_FOUND receiver.
        if (mBtReceiver != null)
            unregisterReceiver(mBtReceiver);
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

    public String getPlayerChosenEmoji() {
        return playerChosenEmoji;
    }

    public void setPlayerChosenEmoji(String playerChosenEmoji) {
        this.playerChosenEmoji = playerChosenEmoji;
    }

    public String getOpponentChosenEmoji() {
        return opponentChosenEmoji;
    }

    public void setOpponentChosenEmoji(String opponentChosenEmoji) {
        this.opponentChosenEmoji = opponentChosenEmoji;
    }

    public void debugView(View v) {
        setContentView(R.layout.debug_animation_screen);
        FrameLayout debug_screen = (FrameLayout) findViewById(R.id.debug_animation_root);
        testGrid = new debugQuickShipViewPlayModeOpponentGrid(this, mGameModel);
        debug_screen.addView(testGrid);
        FrameLayout debug_border_frame = (FrameLayout) findViewById(R.id.debug_top_frame_border);
        debug_border_frame.addView(new quickShipViewGridBorder(this, getResources().getColor(R.color.play_mode_opponent_frame_color)));
        mFPSTextureView = (FPSTextureView) findViewById(R.id.animation_texture_view);
    }

    public void debugStartAnimationBtn(View v) {
        Float bitmapSize = testGrid.getCellWidth();
        emojiBitmap = textToBitmap("\uD83D\uDCA9", bitmapSize);
        startAnimation(testGrid.getIndexXYCoord(testGrid.getCurrentIndex()));
    }

    public void startAnimation(final float[] slotIndex) {
        if (slotIndex != null) {
            mFPSTextureView.tickStart();
            createHitTextBitmap(slotIndex);
        }
    }

    private void createHitTextBitmap(final float[] slotIndex) {
        final DisplayObject bitmapDisplay = new DisplayObject();

        float initialRotate = (float) randInt(0, 360);

        bitmapDisplay.with(new BitmapDrawer(emojiBitmap).scaleRegistration(emojiBitmap.getWidth() / 2, emojiBitmap.getHeight() / 2))
                .tween()
                .tweenLoop(false)
                .transform(slotIndex[0], slotIndex[1])
                .to(500, slotIndex[0], slotIndex[1], 0, 6f, 6f, 0, Ease.SINE_IN_OUT)
                .waitTime(400)
                .transform(slotIndex[0], slotIndex[1], Util.convertAlphaFloatToInt(1f), 1f, 1f, 0)
                .call(new AnimCallBack() {
                    @Override
                    public void call() {
                        mFPSTextureView.removeChild(bitmapDisplay);
                        spawnRandomEmojis(emojiBitmap, slotIndex);
                    }
                })
                .end();

        mFPSTextureView.addChild(bitmapDisplay);
    }

    public void startEmojiSpawning(final float[] slotIndex) {
        Timer mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < 2; i++) {
                    createParabolicMotionBitmap(emojiBitmap);
                }
            }
        }, 0, 300);
    }

    public void spawnRandomEmojis(final Bitmap mBitmap, final float[] slotIndex) {
        Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            long t0 = System.currentTimeMillis();
            @Override
            public void run() {
                if (System.currentTimeMillis() - t0 > 10 * 1000) {
                    cancel();
                }
                else {
                    int randomAmount = randInt(1,3);
                    for (int i = 0; i < randomAmount; i++) {
                        animateRandomEmoji(mBitmap, slotIndex);
                    }
                }
            }
        }, 0, 300);
    }

    public void animateRandomEmoji(Bitmap mBitmap, final float[] slotIndex) {
        final DisplayObject bitmapDisplay = new DisplayObject();

        float initialRotate = (float) randInt(0, 360);

        bitmapDisplay.with(new BitmapDrawer(mBitmap).scaleRegistration(mBitmap.getWidth() / 2, mBitmap.getHeight() / 2).rotateRegistration(initialRotate, mBitmap.getHeight()/2))
                .tween()
                .tweenLoop(false)
                .transform(slotIndex[0], slotIndex[1], Util.convertAlphaFloatToInt(1f), 1f, 1f, initialRotate)
                .to(500, slotIndex[0], slotIndex[1], 0, 5f, 5f, initialRotate, Ease.SINE_IN_OUT)
                .waitTime(400)
                .transform(slotIndex[0], slotIndex[1], Util.convertAlphaFloatToInt(1f), 1f, 1f, initialRotate)
                .call(new AnimCallBack() {
                    @Override
                    public void call() {
                        mFPSTextureView.removeChild(bitmapDisplay);
                    }
                })
                .end();

        mFPSTextureView.addChild(bitmapDisplay);
    }

    private void createParabolicMotionBitmap(Bitmap mBitmap) {
        final DisplayObject bitmapDisplay = new DisplayObject();

        bitmapDisplay.with(new BitmapDrawer(mBitmap))
                .parabolic()
                .transform(0, mFPSTextureView.getHeight())
                .reboundBottom(false)
                .accelerationX((float) (15 + Math.random() * 7))
                .initialVelocityY((float) (-65 + Math.random() * 15))
                .bottomHitCallback(new AnimCallBack() {
                    @Override
                    public void call() {
                        mFPSTextureView.removeChild(bitmapDisplay);
                    }
                })
                .end();

        mFPSTextureView.addChild(bitmapDisplay);
    }

    public void emojiPopUpInitializer() {
        //FrameLayout root = (FrameLayout) findViewById(R.id.root_frame);
        LinearLayout root = (LinearLayout) findViewById(R.id.play_mode_opponent_top_linear);
        emojiPopup = new EmojiconsPopup(root, this);

        Double widthWithMargin = screenWidth * 0.9;
        Double heightWithMargin = screenHeight - (screenWidth * 0.1);
        emojiPopup.setSize(widthWithMargin.intValue(), heightWithMargin.intValue());

        emojiPopup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                playerChosenEmoji = emojicon.getEmoji();
                Log.d("DEBUG", opponentChosenEmoji);
                emojiPopup.dismiss();

                playerChosenTarget = playModeOpponentGrid.getCurrentIndex();

                quickShipBluetoothPacketsToBeSent data = new quickShipBluetoothPacketsToBeSent(quickShipBluetoothPacketsToBeSent.MOVES, playerChosenTarget, getPlayerChosenEmoji());
                mBluetoothConnection.write(ParcelableUtil.marshall(data));
                playerTurnDone = true;
                checkPlayModeTurnDone("player");

            }
        });
    }

    public static Bitmap textToBitmap(String text, float textWidth) {
        final float testTextSize = 48f;
        TextPaint textBoundPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textBoundPaint.setStyle(Paint.Style.FILL);
        textBoundPaint.setColor(Color.BLACK);
        textBoundPaint.setTextAlign(Paint.Align.LEFT);

        textBoundPaint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        textBoundPaint.getTextBounds(text, 0, text.length(), bounds);

        float calculatedTextSize = (testTextSize * textWidth / bounds.width())-2;
        textBoundPaint.setTextSize(calculatedTextSize);

        StaticLayout mTextLayout = new StaticLayout(text, textBoundPaint, Math.round(textWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        Bitmap b = Bitmap.createBitmap(Math.round(textWidth), mTextLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        c.save();
        c.translate(2, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

    public void setHitText(Bitmap b) {
        mHitText = b;
    }

    public void setMissText(Bitmap b) {
        mMissText = b;
    }

    // Pick a random number
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}