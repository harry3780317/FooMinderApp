package com.example.starter;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingFragment extends Fragment {
    Switch set_sound;
    Switch set_Vibration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        set_sound = (Switch) view.findViewById(R.id.sound);
        set_Vibration=(Switch) view.findViewById(R.id.vibration);

        set_Vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (set_Vibration.isChecked()) {
                    Toast.makeText(getActivity(), "Turning on Vibration", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Vibration is turned off", Toast.LENGTH_LONG).show();
                }
            }
        });

        set_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (set_sound.isChecked()) {
                    Toast.makeText(getActivity(), "Turning on Sound", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Sound is turned off", Toast.LENGTH_LONG).show();
                }
            }
        });



        return view;
    }
}
