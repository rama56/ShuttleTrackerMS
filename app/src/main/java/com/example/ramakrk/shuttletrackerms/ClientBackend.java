package com.example.ramakrk.shuttletrackerms;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.logging.Logger;

/**
 * Created by ramakrk on 7/25/2016.
 */
public class ClientBackend {

//    private enum BusRoute
//    {
//
//    }
    public double latitude;
    public double longitude;
    public ClientBackend(double Clatitude,double Clongitude)
    {
        this.latitude=Clatitude;
        this.longitude=Clongitude;
    }

    private CountDownTimer timerForSendingLocationData;

    private class LocationData {
        public LocationData(Coordinate Point, String BusRoute) {
            this.point = Point;
            this.busRoute = BusRoute;
        }

        Coordinate point;
        String busRoute;    //TODO: Make bus routes as enums instead of strings.

    }

    private class Coordinate {
        public Coordinate(double Latitude, double Longitude) {
            this.latitude = Latitude;
            this.longitude = Longitude;
        }

        double latitude;
        double longitude;
    }


    // Methods called by passengers waiting for the bus.
    public LocationData GetLocationDataFromDB(String busRoute) {
        LocationData returnable = null;

        // Create and perform a HTTP request.


        if (1 == 1 /*if HTTP response is valid*/) {
            // Update returnable variable.
            returnable = new LocationData(new Coordinate(4.5, 6.5), "dummyBus");
        }
        return returnable;
    }

    // Methods called by bus driver.
    public void GiveLocationDataToDBWrapper(final Context context, final String busRoute) throws SecurityException {
        timerForSendingLocationData = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Get GPS data.

                LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                Location location = null;
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        Log.e("ClientBackend","Permission Denied For Fine/Coarse Location");
                        return;
                    }
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                catch (Exception e)
                {
                    Log.e("ClientBackend","Exception" + e);
                }

                double dlongitude = longitude;
                double dlatitude = latitude;
                String gps=longitude+","+latitude;
                //Toast.makeText(,"Got the param"+gps,Toast.LENGTH_LONG).show();
                Log.d(gps,"GPS Value");

                // Get Current time



                GiveLocationDataToDB(busRoute, new LocationData (new Coordinate(1.1,2.2), busRoute));
            }

            @Override
            public void onFinish() {
                timerForSendingLocationData.start();
            }
        }.start();
    }

    public void StopGivingLocationData()
    {
        timerForSendingLocationData.cancel();
    }

    private boolean GiveLocationDataToDB(String busRoute, LocationData location)
    {
        boolean isSuccess = false;
        int retryCount = 5;

        // Create a HTTP request.


        while(retryCount > 0)
        {
            //Perform the HTTP request.

            if(1==1 /* HTTP request succeeds*/)
            {
                isSuccess = true;
            }
            retryCount--;
        }
        return isSuccess;
    }


}
