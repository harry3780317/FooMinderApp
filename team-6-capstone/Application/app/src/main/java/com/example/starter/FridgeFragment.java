package com.example.starter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FridgeFragment extends Fragment {

    private static final String TAG = "FridgeFragment";
    Switch ble_switch, list_switch;;
    ListView deviceList;

    private static final int RQS_ENABLE_BLUETOOTH = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    public static BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    ArrayList<BluetoothDevice> listBluetoothDevice = new ArrayList<>();
    ArrayAdapter<BluetoothDevice> adapterLeScanResult;

    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fridge, container, false);

        ble_switch = (Switch) rootView.findViewById(R.id.ble_switch);
        list_switch = (Switch) rootView.findViewById(R.id.list_switch);
        deviceList = (ListView) rootView.findViewById(R.id.list);

        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            BluetoothManager mBluetoothManager = (BluetoothManager)getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
        else {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "BluetoothLE is not supported", Toast.LENGTH_LONG).show();
        }

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not supported", Toast.LENGTH_LONG).show();
        }
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                ble_switch.setChecked(false);
            }
            else {
                ble_switch.setChecked(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        ble_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getActivity(), "Turning on Bluetooth", Toast.LENGTH_LONG).show();
                        // turn on Bluetooth
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, RQS_ENABLE_BLUETOOTH);
                    }
                    else {
                        Toast.makeText(getActivity(), "Bluetooth is turned on", Toast.LENGTH_LONG).show();
                    }
                }
                if (!b) {
                    mBluetoothAdapter.disable();
                }
            }
        });

        list_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (mBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getActivity(), "Start scanning for device", Toast.LENGTH_LONG).show();
                        scanLeDevice(true);
                    }
                    else {
                        Toast.makeText(getActivity(), "Turn on Bluetooth to scan device", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    scanLeDevice(false);
                    deviceList.setAdapter(null);
                    Toast.makeText(getActivity(), "Stop scanning for device", Toast.LENGTH_LONG).show();
                    if (!mBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getActivity(), "Turn on Bluetooth to scan device", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        listBluetoothDevice = new ArrayList<>();

        adapterLeScanResult = new ArrayAdapter<BluetoothDevice>(getContext(), android.R.layout.simple_list_item_1, listBluetoothDevice) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                BluetoothDevice device = listBluetoothDevice.get(position);
                if (view == null)
                    view = getActivity().getLayoutInflater().inflate(R.layout.device_list_item, parent, false);
                TextView text1 = view.findViewById(R.id.text1);
                TextView text2 = view.findViewById(R.id.text2);
                if(device.getName() == null || device.getName().isEmpty())
                    text1.setText("<unnamed>");
                else
                    text1.setText(device.getName());
                text2.setText(device.getAddress());
                return view;
            }
        };

        deviceList.setAdapter(adapterLeScanResult);

        mHandler = new Handler();

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mBluetoothLeScanner.stopScan(scanCallback);

                BluetoothDevice device = listBluetoothDevice.get(i);
                Bundle args = new Bundle();
                args.putString("device", device.getAddress());
                Fragment fragment = new SensorFragment();
                fragment.setArguments(args);
                getFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment, "senserData")
                                    .addToBackStack(null)
                                    .commit();
            }
        });

        return rootView;
    }

    /*
    to call startScan (ScanCallback callback),
    Requires BLUETOOTH_ADMIN permission.
    Must hold ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission to get results.
     */
    private void scanLeDevice(boolean enable) {
        if (enable) {
            listBluetoothDevice.clear();
            adapterLeScanResult.notifyDataSetChanged();
            deviceList.invalidateViews();

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.stopScan(scanCallback);
                    deviceList.invalidateViews();

                    Toast.makeText(getActivity(), "Scan timeout", Toast.LENGTH_LONG).show();
                }
            }, SCAN_PERIOD);

            mBluetoothLeScanner.startScan(scanCallback);
        } else {
            mBluetoothLeScanner.stopScan(scanCallback);
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            addBluetoothDevice(result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for(ScanResult result : results){
                addBluetoothDevice(result.getDevice());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(getActivity(), "onScanFailed: " + String.valueOf(errorCode), Toast.LENGTH_LONG).show();
        }

        private void addBluetoothDevice(BluetoothDevice device){
            if(!listBluetoothDevice.contains(device)){
                listBluetoothDevice.add(device);
                adapterLeScanResult.notifyDataSetChanged();
                deviceList.invalidateViews();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RQS_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_CANCELED) {
            getActivity().finish();
            return;
        }

        getBluetoothAdapterAndLeScanner();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(),
                    "bluetoothManager.getAdapter()==null",
                    Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getBluetoothAdapterAndLeScanner(){
        // Get BluetoothAdapter and BluetoothLeScanner.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

//    Button btn_done, btn_showdata;
//    ListView deviceList;
//    Switch ble_switch, list_switch;
////    TextView status;
////    TextView dataWindow;
//
//    BluetoothAdapter mBluetoothAdapter;
//    BluetoothDevice[] mDeviceArray;
//    Set<BluetoothDevice> pairedDevices;
//
//    SendReceive sendReceive;
//
//    static final int STATE_LISTENING = 1;
//    static final int STATE_CONNECTING = 2;
//    static final int STATE_CONNECTED = 3;
//    static final int STATE_CONNECTION_FAILED = 4;
//    static final int STATE_MESSAGE_RECEIVED = 5;
//
//    private static final String APP_NAME = "FooMinder";
//    // random generated UUID
//    private static final UUID MY_UUID = UUID.fromString("b1128468-a646-4038-90d6-a320ddb78b77");
//
//    private static final int REQUEST_ENABLE = 1;
//
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_fridge, container, false);
//
//        deviceList = (ListView) rootView.findViewById(R.id.list);
//        btn_showdata = (Button) rootView.findViewById(R.id.btn_showdata);
////        status = (TextView) rootView.findViewById(R.id.ble_status);
////        dataWindow = (TextView) rootView.findViewById(R.id.dataWindow);
//
//        ble_switch = (Switch) rootView.findViewById(R.id.ble_switch);
//        list_switch = (Switch) rootView.findViewById(R.id.list_switch);
//
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        checkBLEadapter();
//        bluetoothSwitchMethod();
//        deivceListMethod();
//        connectDeviceMethod();
//
//        btn_showdata.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.fragment_container, new SensorFragment());
//                ft.commit();
//            }
//        });
//
//        return rootView;
//    }
//
//    private void checkBLEadapter() {
//        // If the adapter is null, then Bluetooth is not supported
//        if (mBluetoothAdapter == null) {
//            FragmentActivity activity = getActivity();
//            Toast.makeText(activity, "Bluetooth is not supported", Toast.LENGTH_LONG).show();
////            activity.finish();
//        }
//        else {
//            if (!mBluetoothAdapter.isEnabled()) {
//                ble_switch.setChecked(false);
////                status.setText("Please turn on Bluetooth");
//            }
//            else {
//                ble_switch.setChecked(true);
////                status.setText("Bluetooth is already turned on");
//            }
//        }
//    }
//
//    private void bluetoothSwitchMethod() {
//        ble_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    if (!mBluetoothAdapter.isEnabled()) {
//                        Toast.makeText(getActivity(), "Turning on Bluetooth", Toast.LENGTH_LONG).show();
//                        // turn on Bluetooth
//                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(intent, REQUEST_ENABLE);
////                        status.setText("Turned on");
//                    }
//                    else {
//                        Toast.makeText(getActivity(), "Bluetooth is turned on", Toast.LENGTH_LONG).show();
////                        status.setText("Turned on");
//                    }
//                }
//                if (!b) {
//                    mBluetoothAdapter.disable();
////                    status.setText("Turned off");
//                }
//            }
//        });
//    }
//
//    private void deivceListMethod() {
//        list_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    if (mBluetoothAdapter.isEnabled()) {
//                        getPairedList();
//                    }
//                    else {
//                        Toast.makeText(getActivity(), "Turn on Bluetooth to get paired device", Toast.LENGTH_LONG).show();
//                    }
//                }
//                else {
//                    deviceList.setAdapter(null);
//                    if (!mBluetoothAdapter.isEnabled()) {
//                        Toast.makeText(getActivity(), "Turn on Bluetooth to get paired device", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });
//    }
//
//    private void getPairedList() {
//        // list paired devices
//        pairedDevices = mBluetoothAdapter.getBondedDevices();
//
//        if (pairedDevices == null || pairedDevices.size() == 0) {
//            Toast.makeText(getActivity(), "No paired device, open bluetooth settings to pair your device", Toast.LENGTH_LONG).show();
////            status.setText("No paired device");
//        }
//        else {
//            Toast.makeText(getActivity(), "Listing paired devices", Toast.LENGTH_LONG).show();
//            ArrayList<String> devices = new ArrayList<String>();
//
//            int index = 0;
//            mDeviceArray = new BluetoothDevice[pairedDevices.size()];
//
//            for (BluetoothDevice bt : pairedDevices) {
//                mDeviceArray[index] = bt;
//                devices.add(bt.getName());
//                index++;
//            }
//
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, devices);
//            deviceList.setAdapter(arrayAdapter);
//        }
//    }
//
//    private void connectDeviceMethod() {
//        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (mBluetoothAdapter.isDiscovering()) {
//                    Toast.makeText(getActivity(), "Quit discovery", Toast.LENGTH_LONG).show();
//                    mBluetoothAdapter.cancelDiscovery();
//                }
//
//                ClientClass clientClass = new ClientClass(mDeviceArray[i]);
//                clientClass.start();
//
////                status.setText("Connecting");
//            }
//        });
//    }

//    private class ServerClass extends Thread {
//
//        private BluetoothServerSocket serverSocket;
//
//        public ServerClass() {
//            try {
//                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void run() {
//            BluetoothSocket socket = null;
//
//            while (socket == null) {
//                try {
//                    Message message = Message.obtain();
//                    message.what = STATE_CONNECTING;
//                    handler.sendMessage(message);
//
//                    socket = serverSocket.accept();
//                } catch (IOException e) {
//                    e.printStackTrace();
//
//                    Message message = Message.obtain();
//                    message.what = STATE_CONNECTION_FAILED;
//                    handler.sendMessage(message);
//                }
//
//                if (socket != null) {
//
//                    Message message = Message.obtain();
//                    message.what = STATE_CONNECTED;
//                    handler.sendMessage(message);
//
//                    sendReceive = new SendReceive(socket);
//                    sendReceive.start();
//
//                    break;
//                }
//            }
//        }
//    }

//    private class ClientClass extends Thread {
//
//        private BluetoothDevice device;
//        private BluetoothSocket socket;
//
//        public ClientClass (BluetoothDevice device1) {
//            device = device1;
//
//            try {
//                socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void run() {
//            try {
//                socket.connect();
//
//                Message message = Message.obtain();
//                message.what = STATE_CONNECTED;
//                handler.sendMessage(message);
//
//                sendReceive = new SendReceive(socket);
//                sendReceive.start();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//
//                Message message = Message.obtain();
//                message.what = STATE_CONNECTION_FAILED;
//                handler.sendMessage(message);
//            }
//        }
//    }

//    private class SendReceive extends Thread {
//
//        private final BluetoothSocket bluetoothSocket;
//        private final InputStream inputStream;
//        private final OutputStream outputStream;
//
//        public SendReceive (BluetoothSocket socket) {
//            bluetoothSocket = socket;
//            InputStream tempIn = null;
//            OutputStream tempOut = null;
//
//            try {
//                tempIn = bluetoothSocket.getInputStream();
//                tempOut = bluetoothSocket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            inputStream = tempIn;
//            outputStream = tempOut;
//        }
//
//        public void run() {
//
//            byte[] buffer = new byte[1024];
//            int bytes;
//
//            while (true) {
//                try {
//                    bytes = inputStream.read(buffer);
//                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void write(byte[] bytes) {
//            try {
//                outputStream.write(bytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message message) {
//            switch (message.what) {
//                case STATE_LISTENING:
////                    status.setText("Listening");
//                    break;
//
//                case STATE_CONNECTING:
////                    status.setText("Connecting");
//                    break;
//
//                case STATE_CONNECTED:
////                    status.setText("Connected");
//                    break;
//
//                case STATE_CONNECTION_FAILED:
////                    status.setText("Connection Failed");
//                    break;
//
//                case STATE_MESSAGE_RECEIVED:
//                    byte[] readBuffer = (byte[]) message.obj;
//                    String tempMsg = new String(readBuffer, 0, message.arg1);
////                    dataWindow.setText(tempMsg);
//                    break;
//            }
//            return true;
//        }
//    });
}
