package em.qs_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.content.IntentFilter;

import android.widget.ListView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private BluetoothAdapter btAdapter;
    private TextView tv_status;
    private Button btn_connect;
    private Button btn_disconnect;
    private Button btn_hostGame, btn_joinGame;

    private Button btn_joinGameConnect;
    private Button btn_sendMsg;
    private EditText et_msg;

    BluetoothConnectionService mBluetoothConnection;
    BluetoothDevice mBTDevice;
    StringBuilder messages;

    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private DeviceListAdapter mDeviceListAdapter;
    ListView lv_newDevices;



    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_status = (TextView) findViewById(R.id.tv_status);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        btn_hostGame = (Button) findViewById(R.id.btn_hostGame);
        btn_joinGame = (Button) findViewById(R.id.btn_joinGame);
        lv_newDevices = (ListView) findViewById(R.id.lv_newDevices);

        et_msg = (EditText) findViewById(R.id.et_msg);
        btn_joinGameConnect = (Button) findViewById(R.id.btn_joinGameConnect);
        btn_sendMsg = (Button) findViewById(R.id.btn_sendMsg);
        messages = new StringBuilder();

        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, new IntentFilter("incomingMessage"));

        lv_newDevices.setOnItemClickListener(MainActivity.this);

        // check for bonding devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if( ! btAdapter.isEnabled()){
            btn_joinGame.setEnabled(false);
        }
        view_initialState();

        setupUI();
    }

    // IMPORTANT: devices must be paired before running this method
    private void startConnection(){
        //view_connectedState();
        startBTConnection(mBTDevice,MY_UUID_INSECURE);
    }

    // start client method; start connect thread

    private void startBTConnection(BluetoothDevice device, UUID uuid){
        tv_status.setText("Joining the Game Lobby.");
        mBluetoothConnection.startClient(device, uuid);
    }

    public void view_allowJoinGame(){
        btn_joinGame.setVisibility(View.VISIBLE);
        btn_joinGameConnect.setVisibility(View.VISIBLE);
    }
    public void view_initialState(){
        btn_disconnect.setVisibility(View.VISIBLE);
        btn_connect.setVisibility(View.VISIBLE);
        btn_hostGame.setVisibility(View.VISIBLE);
        lv_newDevices.setVisibility(View.VISIBLE);
        btn_joinGame.setVisibility(View.GONE);
        btn_joinGameConnect.setVisibility(View.GONE);
        btn_sendMsg.setVisibility(View.GONE);
        et_msg.setVisibility(View.GONE);
    }
    public void view_connectedState(){
        btn_disconnect.setVisibility(View.VISIBLE);
        btn_connect.setVisibility(View.GONE);
        btn_hostGame.setVisibility(View.GONE);
        btn_joinGame.setVisibility(View.GONE);
        btn_joinGameConnect.setVisibility(View.GONE);
        lv_newDevices.setVisibility(View.GONE);
        btn_sendMsg.setVisibility(View.VISIBLE);
        et_msg.setVisibility(View.VISIBLE);
    }

    void controller_discoverDevices(){
        if(btAdapter.isDiscovering())
            btAdapter.cancelDiscovery();

        //May need Lollipop+ permission check here
        // Bluetooth Tutorial - Discover Devices in Android Studio @3:05

        tv_status.setText("Devices Discovered...");

        //Clear mBTDevices for fresh scan
        mBTDevices.clear();

        // Lollipop+ may need extra manual permissions check
        //checkBTPermissions();

        btAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }
    private void setupUI() {


        // CONNECT BUTTON

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                //if null, device doesn't support bluetooth
                if( btAdapter == null) {
                    tv_status.setText("Device does NOT support Bluetooth.");
                }else if ( ! btAdapter.isEnabled()) {

                    tv_status.setText("Attempting to enable Bluetooth...");

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(mReceiver, filter);

                    int REQUEST_ENABLE_BT = 1;
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT );
                    //need to add listener to main detect bluetooth enabled automatically
                    //BroadcastReceiver
                }

            }
        });


        // DISCONNECT BUTTON

        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btAdapter != null) {


                    //mBTDevices.clear(); //TODO solve fix

                    btAdapter.disable();

                    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(mReceiver, filter);

                    //tv_status.setText("Bluetooth is now off.");
                    view_initialState();
                }
            }
        });

        // HOST GAME BUTTON

        btn_hostGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                tv_status.setText("Attempting to Host Game...");
                //btn_joinGame.setVisibility(View.GONE);
                //mBTDevices.clear(); //TODO solve fix

                IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(mReceiver, filter);

                startActivity(discoverableIntent);

            }
        });

        // JOIN GAME BUTTON

        btn_joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Clean repetative code
                lv_newDevices.setVisibility(View.VISIBLE);
                if(btAdapter == null) {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();
                    if( ! btAdapter.isEnabled()) {
                        tv_status.setText("Attempting to enable Bluetooth...");

                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        int REQUEST_ENABLE_BT = 1;

                        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                        registerReceiver(mReceiver, filter);

                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }else if ( ! btAdapter.isEnabled()) {

                    tv_status.setText("Attempting to enable Bluetooth...");

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    int REQUEST_ENABLE_BT = 1;

                    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(mReceiver, filter);

                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                if(btAdapter.isDiscovering())
                    btAdapter.cancelDiscovery();

                //May need Lollipop+ permission check here
                // Bluetooth Tutorial - Discover Devices in Android Studio @3:05

                tv_status.setText("Devices Discovered...");

                //Clear mBTDevices for fresh scan
                mBTDevices.clear();

                // Lollipop+ may need extra manual permissions check
                //checkBTPermissions();

                btAdapter.startDiscovery();

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);

            }
        });

        btn_joinGameConnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startConnection();
            }

        });

        btn_sendMsg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String full_msg = btAdapter.getName() + ": " + et_msg.getText().toString();
                messages.append(full_msg+"\n");
                tv_status.setText(messages);
                //byte[] bytes = et_msg.getText().toString().getBytes(Charset.defaultCharset());
                byte[] bytes = full_msg.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes);
                et_msg.setText("");//clear message
            }

        });
    }
    private final BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if(intent.getStringExtra("theMsg") != null) {
                String text = intent.getStringExtra("theMsg");
                messages.append(text + "\n");
                tv_status.setText(messages);
            }else if(intent.getBooleanExtra("joinedLobby", false)){
                                view_connectedState();
            }


        }
    };
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


            // Check if bluetooth has been toggled on or off

            if(action.equals(btAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, btAdapter.ERROR);
                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        tv_status.setText("Bluetooth Off.");
                        btn_joinGame.setEnabled(false);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        tv_status.setText("Bluetooth Turning Off...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        String name = btAdapter.getName();
                        String mac = btAdapter.getAddress();
                        tv_status.setText("Bluetooth On.\nDevice name: "+name+"\nDevice MAC: "+mac);
                        btn_joinGame.setEnabled(true);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        tv_status.setText("Bluetooth Turning On...");
                        break;
                }
            }

            // Hosting Game pressed; discoverability enabled

            if(action.equals(btAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, btAdapter.ERROR);
                switch(mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        String name = btAdapter.getName();
                        String mac = btAdapter.getAddress();
                        tv_status.setText("Discoverability Enabled.\nDevice name: "+ btAdapter.getName() +"\nDevice MAC: "+btAdapter.getAddress());
                        controller_discoverDevices();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        tv_status.setText("Discoverability Disabled. Able to receive connections.\nDevice name: "+btAdapter.getName()+"\nDevice MAC: "+btAdapter.getAddress());
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        tv_status.setText("Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        tv_status.setText("Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        tv_status.setText("Connected.");
                        break;
                }
            }

            // DIscover Devices pressed; Discovering Devices enabled

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                /* DEBUGGING
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                tv_status.setText(tv_status.getText().toString() + "\n" +deviceName + " -> " + deviceHardwareAddress);
                */

                mBTDevices.add(device);
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lv_newDevices.setAdapter(mDeviceListAdapter);
            }

            // Discovered device Item pressed; Pairing devices

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // case1: bonded already
                if( device.getBondState() == BluetoothDevice.BOND_BONDED){
                    tv_status.setText("Devices Successfully Paired.");
                    //mBTDevices.clear(); //TODO remove mBTDevices
                    mBTDevice = device; // device it is paired with
                }

                // case 2: creating a bond
                if(device.getBondState() == BluetoothDevice.BOND_BONDING){
                    tv_status.setText("Devices Bonding...");
                }

                // case 3: disconnecting a bond
                if(device.getBondState() == BluetoothDevice.BOND_NONE){
                    tv_status.setText("Device Bond Disconnected.");
                }

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(btAdapter != null)
            btAdapter.cancelDiscovery();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
        unregisterReceiver(msgReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
        //first
        btAdapter.cancelDiscovery();
        tv_status.setText("Discovery Cancelled.\nAttempting to bond");
        String deviceName = mBTDevices.get(i).getName();
        String deviceMAC = mBTDevices.get(i).getAddress();

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            tv_status.setText(tv_status.getText().toString() + " with...\n" +deviceName + " -> " + deviceMAC);



            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            if(mBTDevice != null)
                view_allowJoinGame();
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
            startConnection();
        }

    }

    /*
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }*/
}
