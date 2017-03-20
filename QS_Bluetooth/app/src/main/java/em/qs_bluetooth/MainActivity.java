package em.qs_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;


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
                startActivity(discoverableIntent);
            }
        });

        btn_joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO connect to device in discovery mode
            }
        });

        //TODO once devices are connected, start threads to allow data to flow between devices.
    }

}
