package em.qs_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.content.IntentFilter;

import android.widget.ListView;
import java.util.ArrayList;




public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private BluetoothAdapter btAdapter;
    private TextView tv_status;
    private Button btn_connect;
    private Button btn_disconnect;
    private Button btn_hostGame, btn_joinGame;

    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private DeviceListAdapter mDeviceListAdapter;
    ListView lv_newDevices;


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




        lv_newDevices.setOnItemClickListener(MainActivity.this);

        // check for bonding devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if( ! btAdapter.isEnabled()){
            btn_joinGame.setEnabled(false);
        }
        setupUI();
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

                    mBTDevices.clear();

                    btAdapter.disable();

                    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(mReceiver, filter);

                    //tv_status.setText("Bluetooth is now off.");
                    btn_disconnect.setVisibility(View.VISIBLE);
                    btn_connect.setVisibility(View.VISIBLE);
                    btn_hostGame.setVisibility(View.VISIBLE);
                    btn_joinGame.setVisibility(View.VISIBLE);
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
                mBTDevices.clear();
                IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//TODO
                registerReceiver(mReceiver, filter);//TODO

                startActivity(discoverableIntent);
                //startActivityForResult(discoverableIntent, 1);
            }
        });

        // JOIN GAME BUTTON

        btn_joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Clean repetative code
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
                //TODO clear mBTDevices for fresh scan
                mBTDevices.clear();

                btAdapter.startDiscovery();

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);

                //TODO Broadcasts when bond state changes (ie:pairing) added @ onCreate in tutorial
                //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                //registerReceiver(mReceiver, filter);
            }
        });

        //TODO once devices are connected, start threads to allow data to flow between devices.
    }


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

            // Hosting Game toggled on or off

            if(action.equals(btAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, btAdapter.ERROR);
                switch(mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        String name = btAdapter.getName();
                        String mac = btAdapter.getAddress();
                        tv_status.setText("Discoverability Enabled.\nDevice name: "+ btAdapter.getName() +"\nDevice MAC: "+btAdapter.getAddress());

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

            // Join Game Discovering Devices

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                tv_status.setText(tv_status.getText().toString() + "\n" +deviceName + " -> " + deviceHardwareAddress);

                mBTDevices.add(device);
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lv_newDevices.setAdapter(mDeviceListAdapter);
            }

            // Join Game Pairing
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // case1: bonded already
                if( device.getBondState() == BluetoothDevice.BOND_BONDED){
                    tv_status.setText("Devices Successfully Paired.");
                    mBTDevices.clear();
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

        }

    }
}
