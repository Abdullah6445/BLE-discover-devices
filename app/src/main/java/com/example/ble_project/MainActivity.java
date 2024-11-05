package com.example.ble_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 100;

    Button enableBluetoothButton, disableBluetoothButton;   //blueTooth ko Enable or Disable karne wale button
    Button boundedBloothDevicesButton; // pehle se mojood devices ko display krne wala button
    Button discoverBloothDevicesButton;   // new devices ko discover krne wala button
    ListView discoverDeviceslistView;  // new devices ko show krne wala listView

    ArrayList<String> newDevicesList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    BluetoothAdapter myBluetoothAdapter; // mobile ki bluetooth functionality ko access krne me help krta hai



    ListView boundedBloothDeviceslistView; // pehle se mojood devices ko display krne wala listview

    Intent btEnablingIntent;
    int requestCodeEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        enableBluetoothButton = findViewById(R.id.enable_Bluetooth_btn);
        disableBluetoothButton = findViewById(R.id.disable_Bluetooth_btn);
        boundedBloothDevicesButton = findViewById(R.id.scanPaired_BluetoothDevices_btn);
        boundedBloothDeviceslistView = findViewById(R.id.alreadyPairedDeviceslistViewId);
        discoverBloothDevicesButton = findViewById(R.id.scanNew_BluetoothDevices_btn);
        discoverDeviceslistView = findViewById(R.id.newDeviceslistView);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeEnable = 1;

        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSIONS);

        } else {
            // Permissions already granted, proceed with Bluetooth setup
            bluetoothOnMethod();
            bluetoothOFFMethod();
            scanAlreadyBloothDevicesButtonMethod();
            discoverNewDevices();
        }
    }


    // agr starting me user permissions ko deny kar de usky liay

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                bluetoothOnMethod();
                bluetoothOFFMethod();
                scanAlreadyBloothDevicesButtonMethod();
                discoverNewDevices();
            } else {
                Toast.makeText(this, "Permissions are required for Bluetooth functionality", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void bluetoothOnMethod() {
        enableBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
                } else {
                    if (!myBluetoothAdapter.isEnabled()) {
                        startActivityForResult(btEnablingIntent, requestCodeEnable);
                        Toast.makeText(getApplicationContext(), "Bluetooth enabled successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void bluetoothOFFMethod() {
        disableBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                if (myBluetoothAdapter.isEnabled()) {
                    myBluetoothAdapter.disable();
                }
            }
        });
    }



    private void scanAlreadyBloothDevicesButtonMethod() {
        boundedBloothDevicesButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {


                if (!myBluetoothAdapter.isEnabled()){

                    Toast.makeText(getApplicationContext(), "Check device BlueToothth.", Toast.LENGTH_SHORT).show();

                }
                else{
                    Set<BluetoothDevice> bt = myBluetoothAdapter.getBondedDevices();
                    String[] boundedDeviceName = new String[bt.size()];
                    int index = 0;

                    if (bt.size() > 0) {
                        for (BluetoothDevice device : bt) {
                            boundedDeviceName[index] = device.getName();
                            index++;
                        }
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, boundedDeviceName);
                    boundedBloothDeviceslistView.setAdapter(arrayAdapter);

                }

                }
        });
    }


    private void discoverNewDevices() {
        discoverBloothDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_CODE_PERMISSIONS);
                    return;
                }

                // Cancel any ongoing discovery before starting a new one
                if (myBluetoothAdapter.isDiscovering()) {
                    myBluetoothAdapter.cancelDiscovery();
                }

                boolean isDiscoveryStarted = myBluetoothAdapter.startDiscovery();
                if (isDiscoveryStarted) {
                    Toast.makeText(getApplicationContext(), "Discovering new devices...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Check Location Discovery failed to start.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, newDevicesList);
        discoverDeviceslistView.setAdapter(arrayAdapter);
    }



    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    if (ActivityCompat.checkSelfPermission(MainActivity. this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    newDevicesList.add(device.getName()  != null ? device.getName() : "Unknown Device");
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

}

