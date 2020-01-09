package com.example.starter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SensorFragment extends Fragment implements ServiceConnection, SerialListener {

    public enum Connected { False, Pending, True }
    private Connected connected = Connected.False;

    private String deviceAddress;
    private SerialSocket socket;
    private SerialService service;
    private boolean initialStart = true;

    private TextView receiveText;
    private TextView tempView;
    private TextView humidityView;
    private TextView alcoholView;

    private ImageView tempStatus;
    private ImageView humidityStatus;
    private ImageView alcoholStatus;

    private float highTemp = 10;
    private float lowTemp = 0;
    private float highHum = 60;
    private float lowHum = 0;
    private float highAlc = 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensordata, container, false);
        receiveText = (TextView) rootView.findViewById(R.id.receiveData);
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());

        tempView = (TextView) rootView.findViewById(R.id.tempView);
        humidityView = (TextView) rootView.findViewById(R.id.humidityView);
        alcoholView = (TextView) rootView.findViewById(R.id.alcoholView);

        tempStatus = (ImageView) rootView.findViewById(R.id.tempStatus);
        humidityStatus = (ImageView) rootView.findViewById(R.id.humidityStatus);
        alcoholStatus = (ImageView) rootView.findViewById(R.id.alcoholStatus);

        return rootView;
    }

    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
        super.onStart();
    }

    @Override
    public void onStop() {
        if(service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        if(initialStart && service !=null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        try {
            getActivity().runOnUiThread(this::disconnect);
        }
        catch (Exception ignored) {}
        super.onPause();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        if(initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            String deviceName = device.getName() != null ? device.getName() : device.getAddress();
            status("connecting...");
            connected = Connected.Pending;
            socket = new SerialSocket();
            service.connect(this, "Connected to " + deviceName);
            socket.connect(getContext(), service, device);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
        socket.disconnect();
        socket = null;
    }

    private void receive(byte[] data) {
        String temp = new String(data);
        if (containsDigit(temp)) {
            if (temp.contains("C") && !temp.contains("BAC") && !temp.contains("CO2")) {
                String temp1 = temp.replaceAll("[C|�|=|e]", "");
                float f = Float.parseFloat(temp1);
                if (f <= highTemp && f >= lowTemp) {
                    tempView.setTextColor(getResources().getColor(R.color.good_status));
                    tempStatus.setImageDrawable(getResources().getDrawable(R.mipmap.smiling_face_with_smiling_eyes));
                }
                else {
                    tempView.setTextColor(getResources().getColor(R.color.caution_status));
                    tempStatus.setImageDrawable(getResources().getDrawable(R.mipmap.white_frowning_face));
                }
                tempView.setText(temp1);
            }
            if (temp.contains("=") && !temp.contains("BAC") && !temp.contains("CO2")) {
                String temp2 = temp.replaceAll("[%|=]","");
                float f = Float.parseFloat(temp2);
                if (f <= highHum && f >= lowHum) {
                    humidityView.setTextColor(getResources().getColor(R.color.good_status));
                    humidityStatus.setImageDrawable(getResources().getDrawable(R.mipmap.smiling_face_with_smiling_eyes));
                }
                else {
                    humidityView.setTextColor(getResources().getColor(R.color.caution_status));
                    humidityStatus.setImageDrawable(getResources().getDrawable(R.mipmap.white_frowning_face));
                }
                humidityView.setText(temp2);
            }
            if (temp.contains("ppm")) {
                String temp3 = temp.replaceAll("[%|BAC|=|ppm|,| ]", "");
                float f = Float.parseFloat(temp3);
                if (f >= highAlc) {
                    alcoholView.setTextColor(getResources().getColor(R.color.caution_status));
                    alcoholStatus.setImageDrawable(getResources().getDrawable(R.mipmap.white_frowning_face));
                }
                else {
                    alcoholView.setTextColor(getResources().getColor(R.color.good_status));
                    alcoholStatus.setImageDrawable(getResources().getDrawable(R.mipmap.smiling_face_with_smiling_eyes));
                }
                alcoholView.setText(temp3);
            }
        }
//        String temp4 = temp.replaceAll("[�]", "");
//        receiveText.append(temp4);
    }

    public final boolean containsDigit(String s) {
        boolean containsDigit = false;

        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsDigit = Character.isDigit(c)) {
                    break;
                }
            }
        }

        return containsDigit;
    }

    private void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str+'\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }
}
