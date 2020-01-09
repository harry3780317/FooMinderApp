package com.example.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class activity_secondary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        Button Next = (Button) findViewById(R.id.confirm);
//        Button Back = (Button) findViewById(R.id.backBtn);

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText Name = (EditText) findViewById(R.id.yourname);
                String userName = Name.getText().toString();
                Intent confirmIntent = new Intent(getApplicationContext(), activity_third.class);
                confirmIntent.putExtra("namekey", userName);
                startActivity(confirmIntent);
            }
        });

//        Back.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent backIntent = new Intent(getApplicationContext(), activity_third.class);
//                startActivity(backIntent);
//            }
//        }));

    }
}
