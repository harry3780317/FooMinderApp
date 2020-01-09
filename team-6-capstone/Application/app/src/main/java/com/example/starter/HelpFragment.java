package com.example.starter;

import android.content.Intent;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.starter.database.FooMinderRepository;

public class HelpFragment extends Fragment {
    FooMinderRepository mFooMinderRepository;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container,false);
        final Button CC_next = (Button) view.findViewById(R.id.cleancache);
        Button RA_next = (Button) view.findViewById(R.id.ResetApplication);
        Button FAQ_next = (Button) view.findViewById(R.id.FASKQ);

        FAQ_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new FAQFragment());
                fr.commit();
            }
        });


        CC_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = getLayoutInflater().inflate(R.layout.fragment_cleancache, null);
                PopupWindow pw = new PopupWindow(popupView,ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT);
                TextView tv = (TextView) popupView.findViewById(R.id.cleancache2);
                pw.setFocusable(true);
                int location[] = new int[2];
                view.getLocationOnScreen(location);
                pw.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 25, 25);
                mFooMinderRepository = new FooMinderRepository(getActivity().getApplication());
                mFooMinderRepository.deleteAll();
            }
        });


        RA_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder altdia= new AlertDialog.Builder(getActivity());
                altdia.setMessage("Do you want to reset?").setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent startIntent = new Intent(getActivity(),MainActivity.class);
                                startActivity(startIntent);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = altdia.create();
                alert.setTitle("Remind");
                alert.show();


            }
        });


        return view;
    }
}