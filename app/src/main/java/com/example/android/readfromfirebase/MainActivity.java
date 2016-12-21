package com.example.android.readfromfirebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase meetUpDatabase;
    private DatabaseReference meetUpDatabaseReference;
    private EditText latitude;
    private EditText longitude;
    private EditText message;
    private double latNum;
    private double longNum;
    private Query query;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        meetUpDatabase = FirebaseDatabase.getInstance();
        meetUpDatabaseReference = meetUpDatabase.getReference().child("meetUpLocation");
        query = meetUpDatabase.getReference().child("meetUpLocation").orderByChild("lat").equalTo(60);

        Button send = (Button) findViewById(R.id.sendDataButton);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        message = (EditText) findViewById(R.id.message);



        send.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                String latString = latitude.getText().toString();
                String longString = longitude.getText().toString();


                latNum = Double.parseDouble(latString);
                longNum = Double.parseDouble(longString);

                ScheduleMeetup meetup = new ScheduleMeetup(latNum,longNum,message.getText().toString());
                meetUpDatabaseReference.push().setValue(meetup);

                //clear input boxes
                latitude.setText("");
                longitude.setText("");
                message.setText("");


            }
        });
        attachDatabaseReadListener();


    }

    private void attachDatabaseReadListener() {

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ScheduleMeetup meetUp = dataSnapshot.getValue(ScheduleMeetup.class);
                    System.out.println("data: "+meetUp.getMessage());
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            query.addChildEventListener(mChildEventListener);
        }


    }
    public void sendData(View view){

    }

    public void getData(View view){

    }

}
