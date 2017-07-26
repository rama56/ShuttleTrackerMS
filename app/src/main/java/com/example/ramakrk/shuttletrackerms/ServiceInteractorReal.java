package com.example.ramakrk.shuttletrackerms;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.json.JSONObject;

import models.*;

/**
 * Created by ramakrk on 7/24/2017.
 */
public class ServiceInteractorReal extends ServiceInteractor implements ServiceInteractorBase
{
    ServiceInteractorReal()
    {
    }

    // Internal classes.

    public Location getCurrentLatLongFromGPS(Context context)
    {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        try
        {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Log.e("ClientBackend", "Permission Denied For Fine/Coarse Location");
                return null;
            }
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Exception e)
        {
            Log.e("ClientBackend", "Exception" + e);
        }
        return location;
    }

    @Override
    public LocationData getLocationDataFromDB(final String busRoute)
    {
        final String url = "https://msshuttletracker.herokuapp.com/GetPosition.php";
        int retryCount = 5;

        LocationData currentPosition = new LocationData();
        // Create a HTTP request.

        while (retryCount > 0) {
            // Create and perform a HTTP request.
            JSONObject request = new JSONObject();
            GetPosition gp = new GetPosition(busRoute);
            try {
                gp.execute().get();
                retryCount = 0;
            } catch (Exception e) {
                Log.e("CB", e.getMessage());
                retryCount--;
            }
        }
        return currentPosition;
    }

    @Override
    public boolean giveLocationDataToDB(LocationData location)
    {
        boolean isSuccess = false;
        int retryCount = 5;
        while (retryCount > 0) {
            // Create and perform a HTTP request.
            ServiceInteractor.PutPosition putPosition =
                    new ServiceInteractor.PutPosition(
                            location.getBusRoute(),
                            location.getPoint().getLatitude(),
                            location.getPoint().getLongitude(),
                            location.getTime());

            try {
                putPosition.execute().get();
                retryCount = 0;
            } catch (Exception e) {
                Log.e("CB", e.getMessage());
                retryCount--;
            }
        }
        return isSuccess;
    }
}
