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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by ramakrk on 7/25/2016.
 */
public class ClientBackend {

//    private enum BusRoute
//    {
//
//    }

    public ClientBackend()
    {

    }

    private CountDownTimer timerForSendingLocationData;

    public class LocationData {
        public LocationData(Coordinate Point, String BusRoute, Date time)
        {
            this.point = Point;
            this.busRoute = BusRoute;
            this.time= time;
        }
        Date time;
        Coordinate point;
        String busRoute;    //TODO: Make bus routes as enums instead of strings.
    }

    public class Coordinate {
        public Coordinate(double Latitude, double Longitude) {
            this.latitude = Latitude;
            this.longitude = Longitude;
        }

        double latitude;
        double longitude;
    }


    // Methods called by passengers waiting for the bus.
    public LocationData GetLocationDataFromDB(String busRoute) {

        return new LocationData(new Coordinate(17.43,78.36),"5", parseDate("2016-07-26-19-01-00"));

        //return new LocationData(new Coordinate(76,54),"5", this.getCurrentLocalTime(300));

        //LocationData returnable = null;

        // Create and perform a HTTP request.


        //if (1 == 1 /*if HTTP response is valid*/) {
            // Update returnable variable.
            //returnable = new LocationData(new Coordinate(4.5, 6.5), "dummyBus");
        //}
        //return returnable;
    }

    private Date parseDate(String date)
    {
       try
       {
           return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").parse(date);
       }
       catch (ParseException e)
       {
           return null;
       }

    }

    // Methods called by bus driver.
    public void GiveLocationDataToDBWrapper(final Context context, final String busRoute) throws SecurityException {
        timerForSendingLocationData = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("GiveLocationDataTimer","Time left on this tick " + millisUntilFinished);

                // Get GPS data.
                Location location = getCurrentLatLongFromGPS(context);

                double dlongitude = location.getLatitude();  //longitude;
                double dlatitude = location.getLongitude();  //latitude;
                String gps = dlatitude + "," + dlongitude;

                //Toast.makeText(,"Got the param"+gps,Toast.LENGTH_LONG).show();

                Log.d(gps,"GPS Value");

                // Get Current time
                Date currentTime = getCurrentLocalTime();

                GiveLocationDataToDB(new LocationData (new Coordinate(dlatitude,dlongitude), busRoute, currentTime));
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

    private boolean GiveLocationDataToDB(LocationData location)
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

    public static Date getCurrentLocalTime()
    {
        Calendar calendarObject = Calendar.getInstance();
        Date currentTime = calendarObject.getTime();
        return currentTime;
    }

    public static Date getCurrentLocalTime(int goBackSeconds)
    {
        Calendar calendarObject = Calendar.getInstance();
        calendarObject.add(Calendar.SECOND, -goBackSeconds);
        Date currentTime = calendarObject.getTime();
        return currentTime;
    }

    public static Location getCurrentLatLongFromGPS(Context context)
    {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Log.e("ClientBackend","Permission Denied For Fine/Coarse Location");
                return null;
            }
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        catch (Exception e)
        {
            Log.e("ClientBackend","Exception" + e);
        }
        return location;
    }

}
