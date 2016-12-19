package com.example.android.readfromfirebase;

/**
 * Created by TroysMacBook on 12/18/16.
 */

public class ScheduleMeetup {

    private String message;
    private double lat;
    private double longitude;

    public ScheduleMeetup(){

    }


    public ScheduleMeetup(double lat, double longitude, String message  ){
        this.lat = lat;
        this.longitude=longitude;
        this.message=message;
    }

    public double getlat() {
        return lat;
    }

    public void setlat(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }




    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }






}
