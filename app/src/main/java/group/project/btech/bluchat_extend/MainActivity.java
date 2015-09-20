package group.project.btech.bluchat_extend;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    public static EditText et1,et2;
    Button b1,b2;
    String mConnectedDeviceName ;
     Context context;
    Thread thread;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));

                            break;

                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    update_ui(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);

                    Toast.makeText(context,mConnectedDeviceName,Toast.LENGTH_SHORT).show();

                    break;
                case Constants.MESSAGE_TOAST:

                    Toast.makeText(context, msg.getData().getString(Constants.TOAST),Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    private void update_ui(String readMessage) {

        et1.setText(readMessage);



    }

    BluetoothChatService mChatService = new BluetoothChatService(this,mHandler);
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c);
        et1 = (EditText)findViewById(R.id.editText);
        et2 = (EditText)findViewById(R.id.editText2);
        b1 = (Button)findViewById(R.id.button19);
        b2 = (Button)findViewById(R.id.button20);
        b1.setOnClickListener(m);
        b2.setOnClickListener(n);
        context = this;


    }



    private void setStatus(String string) {



            this.setTitle(string);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session

                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }


    View.OnClickListener m = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent serverIntent = new Intent(context, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);

        }

         };
    View.OnClickListener n = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            byte[] send = et2.getText().toString().getBytes();
            mChatService.write(send);


        }

    };

}


