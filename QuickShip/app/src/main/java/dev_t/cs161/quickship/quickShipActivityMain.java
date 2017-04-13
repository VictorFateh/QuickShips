package dev_t.cs161.quickship;

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
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Random;
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
    private EditText mSplashScreenPlayerName;
    private String mPlayerName;
    private BluetoothAdapter btAdapter;
    private StringBuilder messages;
    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private BluetoothConnectionService mBluetoothConnection;
    private DeviceListAdapter mDeviceListAdapter;
    private BluetoothDevice mBTDevice;
    private ListView mDevicesListView;
    private TextView mChooseModeChatMessageLog;
    private static final UUID MY_UUID_INSECURE = UUID.randomUUID();

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
        SharedPreferences preferences = getSharedPreferences("quickShipSettings", MODE_PRIVATE);
        mPlayerName = preferences.getString("playerName", "Player1");
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
                    if (!btAdapter.isEnabled()) {
                        toast_displayMessage("Attempting to enable Bluetooth...");

                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                        IntentFilter BlueToothfilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                        registerReceiver(mReceiver, BlueToothfilter);

                        int REQUEST_ENABLE_BT = 1;
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                    if (btAdapter.isEnabled()) {
                        if (!playerNameCheck.equals(mPlayerName)) {
                            SharedPreferences preferences = getSharedPreferences("quickShipSettings", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("playerName", playerNameCheck);
                            editor.commit();
                        }

                        Toast.makeText(mActivityMain, "Listing Nearby Devices...", Toast.LENGTH_SHORT).show();

                        Intent discoverableIntent =
                                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);

                        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                        registerReceiver(mReceiver, filter);

                        startActivity(discoverableIntent);

                        func_alertDisplayBTDevices();
                    }
                }
            }
        });
    }

    public void initializeView() {
        setContentView(R.layout.quickship_main_screen);
        mActivityMain = this;

        mSplashScreenPlayerName = (EditText) findViewById(R.id.splash_screen_player_name);
        mSplashScreenPlayerName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager inputManager = (InputMethodManager) mActivityMain.getSystemService(mActivityMain.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(mActivityMain.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });


        mChooseModeFrameLayout = (FrameLayout) findViewById(R.id.choose_mode);
        mSplashScreenFrameLayout = (FrameLayout) findViewById(R.id.splash_screen);
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

        mChooseModeChatMessageLog = (TextView) findViewById(R.id.edit_text_chat_log);

        startGame = (Button) findViewById(R.id.start_game_btn);

        blueToothInitializeObjects();

        launchStartScreen();
    }

    public void blueToothInitializeObjects() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            startGame.setEnabled(false);
            AlertDialog alertDialog = new AlertDialog.Builder(mActivityMain).create();
            alertDialog.setTitle("Unsupported Game");
            alertDialog.setMessage("Device does NOT support Bluetooth");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                  new DialogInterface.OnClickListener() {
                                      public void onClick(DialogInterface dialog, int which) {
                                          dialog.dismiss();
                                          // Temporary show the chooes mode even though there's no bluetooth
                                          newGame();
                                          mainScreenViewFlipper.setDisplayedChild(1);
                                      }
                                  });
            alertDialog.show();
        }
        messages = new StringBuilder();
        mDevicesListView = new ListView(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(quickShipDock, new IntentFilter("quickShipCargo"));
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
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
        mGameModel = new quickShipModel(mPlayerName);
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
        // temporary setting the opponent board to what we set in choose mode for the player
        // used for testing since we don't have bluetooth yet
        mGameModel.setOpponentGameBoard(mGameModel.getPlayerGameBoard());

        mainScreenViewFlipper.setDisplayedChild(2);
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
        o2.inScaled = false;
        o2.inSampleSize = inSampleSize;
        b = BitmapFactory.decodeResource(getResources(), res, o2);

        return b;
    }

    private void toast_displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver quickShipDock = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras().getParcelable("quickShipPackage") != null) {
                quickShipBluetoothPacketsToBeSent data = intent.getExtras().getParcelable("quickShipPackage");
                String text = data.getChatMessage();
                messages.append(text + "\n");
            } else if (intent.getBooleanExtra("joinedLobby", false)) {
                //TODO Display User who has joined

            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Check if bluetooth has been toggled on or off
            if (action.equals(btAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, btAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        toast_displayMessage("Bluetooth Off.");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        toast_displayMessage("Bluetooth Turning Off...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        String name = btAdapter.getName();
                        String mac = btAdapter.getAddress();
                        toast_displayMessage("Bluetooth On.\nDevice name: " + name + "\nDevice MAC: " + mac);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        toast_displayMessage("Bluetooth Turning On...");
                        break;
                }
            }

            // Hosting Game pressed; discoverability enabled
            if (action.equals(btAdapter.ACTION_SCAN_MODE_CHANGED)) {
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

            // Discover Devices pressed; Discovering Devices enabled
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d("Discovered Device: ", "" + device.getName());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.quickship_device_adapter_view, mBTDevices);
                mDevicesListView.setAdapter(mDeviceListAdapter);
                mDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        btAdapter.cancelDiscovery();
                        String deviceName = mBTDevices.get(i).getName();
                        String deviceMAC = mBTDevices.get(i).getAddress();
                        //dialog.dismiss();
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            toast_displayMessage("Attempting to bond with...\n" + deviceName + "\n" + deviceMAC);
                            mBTDevices.get(i).createBond();
                            mBTDevice = mBTDevices.get(i);
                            mBluetoothConnection = new BluetoothConnectionService(mActivityMain);
                            startConnection();
                        }
                    }
                });
            }

            // Discovered device Item pressed; Pairing devices
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // case1: bonded already
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    toast_displayMessage("Connection Established!");
                    mBTDevice = device; // device it is paired with
                    // Make new game. Show choose mode screen
                    newGame();
                    mainScreenViewFlipper.setDisplayedChild(1);
                }

                // case 2: creating a bond
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    toast_displayMessage("Devices Bonding...");
                }

                // case 3: disconnecting a bond
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    toast_displayMessage("Device Bond Disconnected.");
                }

            }
        }
    };

    private void startConnection() {
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }

    private void startBTConnection(BluetoothDevice device, UUID uuid) {
        mBluetoothConnection.startClient(device, uuid);
    }

    private void func_alertDisplayBTDevices() {
        final AlertDialog.Builder ad_setName = new AlertDialog.Builder(mActivityMain);
        ad_setName.setTitle("Nearby Bluetooth Devices");
        ad_setName.setMessage("Select a Device...");
        //final ListView lv_devices = new ListView(MainActivity.this);
        mDevicesListView.setAdapter(mDeviceListAdapter);

        ad_setName.setView(mDevicesListView);
        ad_setName.setCancelable(true);
        ad_setName.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
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
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);

                func_alertDisplayBTDevices();
            }
        });
        ad_setName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((ViewGroup) mDevicesListView.getParent()).removeView(mDevicesListView);
                dialogInterface.cancel();
            }
        });

        final AlertDialog dialog = ad_setName.create();
        dialog.show();
        mDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btAdapter.cancelDiscovery();
                String deviceName = mBTDevices.get(i).getName();
                String deviceMAC = mBTDevices.get(i).getAddress();
                //dialog.dismiss();
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    toast_displayMessage("Attempting to bond with...\n" + deviceName + "\n" + deviceMAC);

                    mBTDevices.get(i).createBond();

                    mBTDevice = mBTDevices.get(i);
                    mBluetoothConnection = new BluetoothConnectionService(mActivityMain);
                    startConnection();
                }

                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btAdapter != null)
            btAdapter.cancelDiscovery();
        // Don't forget to unregister the ACTION_FOUND receiver.
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
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