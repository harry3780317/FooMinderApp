package com.example.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.room.Room;

import com.example.starter.database.FooMinderRepository;
import com.example.starter.database.FooMinderDatabase;
import com.example.starter.database.entity.FridgeStatusData;
import com.example.starter.database.entity.ReciptData;

public class MainActivity extends AppCompatActivity {

//    Database
    private ReciptData mCurrentReciptData;
    private FridgeStatusData mCurrentFridgeStatusData;
    private FooMinderDatabase mFooMinderDatabase;
    private FooMinderRepository mFooMinderRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button welcomebackBtn = (Button) findViewById(R.id.welcomebackBtn);
//        Button firsttimeuserBtn = (Button) findViewById(R.id.firsttimeuserBtn);
//        Button switchuserBtn = (Button) findViewById(R.id.swtichuserBtn);

//        firsttimeuserBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent startIntent = new Intent(getApplicationContext(), welcome.class);
//                startActivity(startIntent);
//            }
//        });
//
//        switchuserBtn.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent switchIntent = new Intent(getApplicationContext(), activity_secondary.class);
//                startActivity(switchIntent);
//            }
//        }));

        welcomebackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(getApplicationContext(), welcome.class);
                startActivity(backIntent);
            }
        });

//        Initialize the Database
        mFooMinderDatabase = Room.databaseBuilder(this, FooMinderDatabase.class, "FooMinder.db")
                .allowMainThreadQueries()
                .build();

        mFooMinderRepository = new FooMinderRepository(getApplication());
        mCurrentReciptData = new ReciptData(null, 0, 0);
        mCurrentFridgeStatusData = new FridgeStatusData(0, 0, 0, 0);
//        mFooMinderRepository.insert(mCurrentReciptData);
//        mFooMinderRepository.insert(mCurrentFridgeStatusData);

//        insert test
//        ReciptData initial = new ReciptData("Apple", 10, 20);
//        mFooMinderRepository.insert(initial);
//        ReciptData test1 = new ReciptData("Orange", 5, 20);
//        mFooMinderRepository.insert(test1);

//        setter test
//        mCurrentReciptData.setItem("test");
//        mCurrentReciptData.setQuantity(10);
//        mCurrentReciptData.setStoringTime(20);
//        mFooMinderRepository.insert(mCurrentReciptData);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFooMinderDatabase.close();
    }

}
