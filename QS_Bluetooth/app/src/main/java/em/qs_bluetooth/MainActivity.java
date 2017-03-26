package em.qs_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.content.IntentFilter;




public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter btAdapter;
    public TextView tv_status;
    public Button btn_connect;
    public Button btn_disconnect;
    public Button btn_hostGame, btn_joinGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    private void setupUI() {
        final TextView tv_status = (TextView) findViewById(R.id.tv_status);
        final Button btn_connect = (Button) findViewById(R.id.btn_connect);
        final Button btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        final Button btn_hostGame = (Button) findViewById(R.id.btn_hostGame);
        final Button btn_joinGame = (Button) findViewById(R.id.btn_joinGame);


        //btn_disconnect.setVisibility(View.GONE);

        //experimenting with bluetooth
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                //if null, device doesn't support bluetooth
                if( btAdapter == null) {
                    tv_status.setText("Device doesn't support bluetooth.");
                }else if (btAdapter.isEnabled()) {
                    String deviceName = btAdapter.getName();
                    tv_status.setText("Bluetooth Enabled\nConnected Device: " + deviceName);
                    btn_connect.setVisibility(View.GONE);
                    btn_disconnect.setVisibility(View.VISIBLE);
                } else {
                    tv_status.setText("Bluetooth is not on.");
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    int REQUEST_ENABLE_BT = 1;
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT );
                    //need to add listener to main detect bluetooth enabled automatically
                    //BroadcastReceiver
                }

            }
        });

        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btAdapter != null) {
                    btAdapter.disable();
                    tv_status.setText("Bluetooth is now off.");
                    btn_disconnect.setVisibility(View.GONE);
                    btn_connect.setVisibility(View.VISIBLE);
                    btn_hostGame.setVisibility(View.VISIBLE);
                    btn_joinGame.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_hostGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                tv_status.setText("Attempting to Host Game...");
                btn_joinGame.setVisibility(View.GONE);
                //startActivity(discoverableIntent);
                startActivityForResult(discoverableIntent, 1);
            }
        });

        btn_joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btAdapter == null) {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();
                    if( ! btAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        int REQUEST_ENABLE_BT = 1;
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }

                if(btAdapter.isDiscovering())
                    btAdapter.cancelDiscovery();
                //TODO connect to device in discovery mode
                btAdapter.startDiscovery();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);

            }
        });

        //TODO once devices are connected, start threads to allow data to flow between devices.
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

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
}
