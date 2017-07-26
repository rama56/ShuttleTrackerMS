package com.example.ramakrk.shuttletrackerms;

import android.content.Context;
import android.location.Location;

/**
 * Created by ramakrk on 7/24/2017.
 */
public interface ServiceInteractorBase
{
    Location getCurrentLatLongFromGPS(Context context);

    models.LocationData getLocationDataFromDB(final String busRoute);

    boolean giveLocationDataToDB(models.LocationData location);

}
