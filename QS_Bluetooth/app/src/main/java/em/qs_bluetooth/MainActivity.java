package em.qs_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.content.IntentFilter;

import android.widget.ListView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity{
    private ListView lv_devices;
    private BluetoothAdapter btAdapter;
    private TextView tv_status;
    private Button btn_toggleBT;
    private Button btn_connect;

    //private Button btn_joinGameConnect;
    private Button btn_sendMsg;
    private EditText et_msg;

    private Button btn_setName;

    BluetoothConnectionService mBluetoothConnection;
    BluetoothDevice mBTDevice;
    StringBuilder messages;

    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private DeviceListAdapter mDeviceListAdapter;
    //TODO REMOVE ListView lv_newDevices;



    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_status = (TextView) findViewById(R.id.tv_status);
        btn_toggleBT = (Button) findViewById(R.id.btn_toggleBT);
        //btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        //TODO REMOVE lv_newDevices = (ListView) findViewById(R.id.lv_newDevices);

        et_msg = (EditText) findViewById(R.id.et_msg);
        //btn_joinGameConnect = (Button) findViewById(R.id.btn_joinGameConnect);
        btn_sendMsg = (Button) findViewById(R.id.btn_sendMsg);

        btn_setName = (Button) findViewById(R.id.btn_setName);

        messages = new StringBuilder();

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        lv_devices = new ListView(MainActivity.this);

        LocalBroadcastManager.getInstance(this).registerReceiver(quickShipDock, new IntentFilter("quickShipCargo"));

        //lv_newDevices.setOnItemClickListener(MainActivity.this);

        // check for bonding devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        //btAdapter = BluetoothAdapter.getDefaultAdapter(); //TODO remove v1

        if( btAdapter == null){
            func_alertIfBTdoesNOTexist();
        }
        else if( ! btAdapter.isEnabled()){
            btn_toggleBT.setText("Enable Bluetooth");
        }else{
            btn_toggleBT.setText("Disable Bluetooth");
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
        //toast_displayMessage("Joining the Game Lobby.");
        mBluetoothConnection.startClient(device, uuid);
    }

    public void view_initialState(){
        btn_toggleBT.setVisibility(View.VISIBLE);
        btn_connect.setVisibility(View.VISIBLE);
        btn_setName.setVisibility(View.VISIBLE);
        btn_sendMsg.setVisibility(View.GONE);
        et_msg.setVisibility(View.GONE);
    }
    public void view_connectedState(){
        btn_toggleBT.setVisibility(View.VISIBLE);
        btn_connect.setVisibility(View.GONE);
        btn_setName.setVisibility(View.GONE);
        btn_sendMsg.setVisibility(View.GONE);
        et_msg.setVisibility(View.VISIBLE);
    }

    void controller_discoverDevices(){
        if(btAdapter.isDiscovering())
            btAdapter.cancelDiscovery();

        //May need Lollipop+ permission check here
        // Bluetooth Tutorial - Discover Devices in Android Studio @3:05

        //toast_displayMessage("Discovering Devices...");

        //Clear mBTDevices for fresh scan
        mBTDevices.clear();

        // Lollipop+ may need extra manual permissions check
        //checkBTPermissions();

        btAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        func_alertDisplayBTDevices(); //TODO ADDED_HERE
    }
    private void setupUI() {


        // CONNECT BUTTON

        btn_toggleBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if null, device doesn't support bluetooth
                if( btAdapter == null) {
                    func_alertIfBTdoesNOTexist();
                }else if ( ! btAdapter.isEnabled()) {
                    func_enableBT();
                }else{
                    func_disableBT();
                }
            }
        });

        // BROADCAST & CONNECT TO PLAYERS BUTTON

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);


                IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(mReceiver, filter);

                startActivity(discoverableIntent);

            }
        });


        btn_sendMsg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String full_msg = btAdapter.getName() + ": " + et_msg.getText().toString();
                messages.append(full_msg+"\n");
                tv_status.setText(messages);

                quickShipBluetoothPacketsToBeSent data = new quickShipBluetoothPacketsToBeSent(PacketType.CHAT,full_msg);

                //byte[] bytes = full_msg.getBytes(Charset.defaultCharset());

                mBluetoothConnection.write( ParcelableUtil.marshall(data) );
                et_msg.setText("");//clear message
            }

        });

        btn_setName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //TODO Check BT and then Create dialog once enabled
                func_enableBT(); //CHECK BLUETOOTH IS ENABLED OR NAME WONT CHANGE

                AlertDialog.Builder ad_setName = new AlertDialog.Builder(MainActivity.this);
                ad_setName.setTitle("Change Broadcast Name");
                final EditText et_input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                et_input.setHint(btAdapter.getName());
                et_input.setLayoutParams(lp);
                ad_setName.setView(et_input);
                ad_setName.setCancelable(true);
                ad_setName.setPositiveButton("Set New Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = et_input.getText().toString();
                        if( ! newName.isEmpty() ) {
                            tv_status.setText("Changed name to " + newName);
                            if (btAdapter.setName(newName))
                                toast_displayMessage("Changed name to " + newName);
                        }
                    }
                });
                ad_setName.setNegativeButton("Cancel", null);
                AlertDialog dialog = ad_setName.create();
                dialog.show();
            }

        });

        et_msg.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if( actionID == EditorInfo.IME_ACTION_DONE){
                    func_sendMessage();
                    return true;
                }else{
                    return false;
                }
            }
        });
    }

    private void func_alertDisplayBTDevices(){
        //mDeviceListAdapter = new DeviceListAdapter(MainActivity.this, R.layout.device_adapter_view, mBTDevices);
        //lv_newDevices.setAdapter(mDeviceListAdapter);

        final AlertDialog.Builder ad_setName = new AlertDialog.Builder(MainActivity.this);
        ad_setName.setTitle("Nearby Bluetooth Devices");
        ad_setName.setMessage("Select a Device...");
        //final ListView lv_devices = new ListView(MainActivity.this);
        lv_devices.setAdapter(mDeviceListAdapter);

        ad_setName.setView(lv_devices);
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
                ((ViewGroup) lv_devices.getParent()).removeView(lv_devices);
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);

                func_alertDisplayBTDevices();
            }
        });
        ad_setName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((ViewGroup) lv_devices.getParent()).removeView(lv_devices);
                dialogInterface.cancel();
            }
        });
        final AlertDialog dialog = ad_setName.create();
        dialog.show();
        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
                    startConnection();
                }

                dialog.dismiss();
            }
        });
    }
    private void func_sendMessage(){
        String full_msg = btAdapter.getName() + ": " + et_msg.getText().toString();
        messages.append(full_msg+"\n");
        tv_status.setText(messages);

        quickShipBluetoothPacketsToBeSent data = new quickShipBluetoothPacketsToBeSent(PacketType.CHAT,full_msg);

        //byte[] bytes = full_msg.getBytes(Charset.defaultCharset());

        mBluetoothConnection.write( ParcelableUtil.marshall(data) );
        et_msg.setText("");//clear message
    }
    private void func_alertIfBTdoesNOTexist(){
        if( btAdapter == null) {
            toast_displayMessage("Device does NOT support Bluetooth.");
        }
    }
    private void func_enableBT(){
        if ( ! btAdapter.isEnabled()) {
            toast_displayMessage("Attempting to enable Bluetooth...");

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);

            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void func_disableBT(){
        if( btAdapter.isEnabled()){
            btAdapter.disable();

            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);

            view_initialState();

            tv_status.setText("Status.");
        }
    }

    private void toast_displayMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private final BroadcastReceiver quickShipDock = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {




            if(intent.getExtras().getParcelable("quickShipPackage") != null) {
                quickShipBluetoothPacketsToBeSent data = intent.getExtras().getParcelable("quickShipPackage");
                String text = data.getChatMessage();
                messages.append(text + "\n");
                tv_status.setText(messages);
            }else if(intent.getBooleanExtra("joinedLobby", false)){
                //TODO Display User who has joined
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
                        toast_displayMessage("Bluetooth Off.");
                        btn_toggleBT.setText("Enable Bluetooth");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        toast_displayMessage("Bluetooth Turning Off...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        String name = btAdapter.getName();
                        String mac = btAdapter.getAddress();
                        toast_displayMessage("Bluetooth On.\nDevice name: "+name+"\nDevice MAC: "+mac);
                        btn_toggleBT.setText("Disable Bluetooth");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        toast_displayMessage("Bluetooth Turning On...");
                        break;
                }
            }

            // Hosting Game pressed; discoverability enabled

            if(action.equals(btAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, btAdapter.ERROR);
                switch(mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:

                        //toast_displayMessage("Discoverability Enabled.\nDevice name: "+ btAdapter.getName() +"\nDevice MAC: "+btAdapter.getAddress());
                        String message = "Discoverability Enabled.\nDevice name: "+ btAdapter.getName() +"\nDevice MAC: "+btAdapter.getAddress();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        controller_discoverDevices();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        //tv_status.setText("Discoverability Disabled. Able to receive connections.\nDevice name: "+btAdapter.getName()+"\nDevice MAC: "+btAdapter.getAddress());
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        //tv_status.setText("Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        toast_displayMessage("Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        toast_displayMessage("Connected.");
                        break;
                }
            }

            // DIscover Devices pressed; Discovering Devices enabled

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                mBTDevices.add(device);
                Log.d("Discovered Device: ", ""+device.getName());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lv_devices.setAdapter(mDeviceListAdapter);


            }

            // Discovered device Item pressed; Pairing devices

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // case1: bonded already
                if( device.getBondState() == BluetoothDevice.BOND_BONDED){
                    toast_displayMessage("Devices Successfully Paired.");
                    mBTDevice = device; // device it is paired with
                }

                // case 2: creating a bond
                if(device.getBondState() == BluetoothDevice.BOND_BONDING){
                    toast_displayMessage("Devices Bonding...");
                }

                // case 3: disconnecting a bond
                if(device.getBondState() == BluetoothDevice.BOND_NONE){
                    toast_displayMessage("Device Bond Disconnected.");
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
        if(mReceiver != null)
            unregisterReceiver(mReceiver);

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
