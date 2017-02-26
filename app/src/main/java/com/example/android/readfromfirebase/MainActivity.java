package com.example.android.readfromfirebase;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private FirebaseDatabase meetUpDatabase;
    private DatabaseReference meetUpDatabaseReference;
    private EditText latitude;
    private EditText longitude;
    private EditText message;
    private double latNum;
    private double longNum;
    private Query query;
    private ChildEventListener mChildEventListener;
    private DatabaseReference ref;
    private String key;  //id of new location set
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private TextView txtOutput;
    private double currenrtLatitude;
    private double currentLongitude;
    private MeetupAdapter mMessageAdapter;
    private ListView mMessageListView;
    private Location mLastLocation;
    private List<ScheduleMeetup> friendlyMessages;
    private DatabaseReference messageDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        if (meetUpDatabase == null) {
          //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            meetUpDatabase = FirebaseDatabase.getInstance();
            meetUpDatabaseReference = meetUpDatabase.getReference().child("meetUpLocation");
            DatabaseReference meetUpDatabaseLocation = meetUpDatabase.getReference().child("locations");

            String oneLocation="-Kdrc93Ug0cWZfCMYt31";
        //how to delete a location in firebase
            meetUpDatabaseReference.child(oneLocation).removeValue();
            meetUpDatabaseLocation.child(oneLocation).removeValue();
            final String location[] = {"-Kdrc93Ug0cWZfCMYt31","-Kdrc9FbYjOoARZOJDEx","-Kdrc9q5mGDUWcmyNM3Y","-Kdrc9wy8Z-WnJFBjBUJ","-KdrcA2idmhc_AGwKC6J","-KdrcA7sgPrBUsmcdyi2","-KdrcAETm8FXWO3lz4sq"};

            for(int x=0;x<location.length;x++) {
                final int y=x;
                meetUpDatabaseReference.child(location[x]).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            System.out.println("Listen Still there"+location[y]);

                        } else {
                            System.out.println("Listen not there"+location[y]);
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        //    messageDelete.attachDatabaseReadListener();
        }


       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);


   //     meetUpDatabase = FirebaseDatabase.getInstance();
    //    meetUpDatabaseReference = meetUpDatabase.getReference().child("meetUpLocation");

        ref = meetUpDatabase.getReference().child("locations");
        geoFire = new GeoFire(ref);

        //    query = meetUpDatabase.getReference().child("meetUpLocation").orderByChild("lat").equalTo(60);

        Button send = (Button) findViewById(R.id.sendDataButton);
        message = (EditText) findViewById(R.id.message);
        mMessageListView = (ListView) findViewById(R.id.messageListView);

        geoQuery = geoFire.queryAtLocation(new GeoLocation(currenrtLatitude, currentLongitude), 1.0);

        // Initialize message ListView and its adapter
        friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MeetupAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);


        send.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                ScheduleMeetup meetup = new ScheduleMeetup(currenrtLatitude, currentLongitude, message.getText().toString());
                DatabaseReference newMeetup = meetUpDatabaseReference.push();
                newMeetup.setValue(meetup);

                key = newMeetup.getKey();  //sets key for message in the location section

                geoFire.setLocation(key, new GeoLocation(currenrtLatitude, currentLongitude));
                // geoFire.removeLocation("firebase-hq");

                //clear input boxes
                message.setText("");
            }
        });
        // attachDatabaseReadListener();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();


        //adds event listener to listen for when the device enters the target zone, uses geofire
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            public Map<String, Marker> markers;
            private GoogleMap map;

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));

                //get reference to meetUpLocations and get message using key from the geoQuery
                meetUpDatabase.getReference("meetUpLocation")
                        .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ScheduleMeetup meetUp = dataSnapshot.getValue(ScheduleMeetup.class);
                        //     txtOutput.setText(meetUp.getMessage());
                        System.out.println("query has been triggered: " + meetUp.getMessage()+" "+"KEY: "+dataSnapshot.getKey());
                        mMessageAdapter.add(meetUp);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //   String message = query.getRef().orderByChild("message").toString();


                //  meetUpDatabaseReference.child("key").child("message").;
                //  txtOutput.setText(message);

                // Add a new marker to the map
                //       Marker marker = this.map.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));
                //     this.markers.put(key, marker);


            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
                new AlertDialog.Builder(getBaseContext())
                        .setTitle("Error")
                        .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


    }

    protected void onStop() {
        super.onStop();
     //   this.geoQuery.removeAllListeners();
        mGoogleApiClient.disconnect();
        friendlyMessages.clear();
        mMessageAdapter.clear();
        mMessageListView.deferNotifyDataSetChanged();

    }

    public void attachDatabaseReadListener() {

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ScheduleMeetup meetUp = dataSnapshot.getValue(ScheduleMeetup.class);
                    txtOutput.setText(meetUp.getMessage());
                    //System.out.println("data: " + meetUp.getMessage());
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    System.out.println("Message reomoved.");

                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            query.addChildEventListener(mChildEventListener);
        }


    }

    public void sendData(View view) {

    }

    public void getData(View view) {


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        //get current location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        currenrtLatitude = mLastLocation.getLatitude();
        currentLongitude = mLastLocation.getLongitude();
        System.out.println("lat and long on start: "+currenrtLatitude+" "+currentLongitude);


    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
      //  txtOutput.setText(Double.toString(location.getLatitude()));
        currenrtLatitude = location.getLatitude();
        currentLongitude=location.getLongitude();

        geoQuery.setCenter(new GeoLocation(currenrtLatitude,currentLongitude));
        geoQuery.setRadius(5);
        System.out.println("lat and long: "+currenrtLatitude+" "+currentLongitude);
    }
}
