package com.example.android.readfromfirebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase meetUpDatabase;
    private DatabaseReference meetUpDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        meetUpDatabase = FirebaseDatabase.getInstance();
        meetUpDatabaseReference = meetUpDatabase.getReference();

    }


    public void getData(View view){
        System.out.println("Got Data");

    }

}
