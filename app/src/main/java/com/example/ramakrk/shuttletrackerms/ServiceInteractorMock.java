package com.example.ramakrk.shuttletrackerms;

import android.content.Context;
import android.location.Location;

import java.util.Calendar;

import models.*;

/**
 * Created by ramakrk on 7/24/2017.
 */
public class ServiceInteractorMock implements ServiceInteractorBase
{
    // Mock data values
    static int[] timeIntervals = {1,2,1,4,3,1,2,1,3,1,4,1,2,4,1,2,3,1,2,1};

    static Double latPosition[]= {
            17.429809,
            17.430382,
            17.430761,
            17.431222,
            17.431744,
            17.432425,
            17.432967,
            17.433356,
            17.433745,
            17.434180,
            17.434497,
            17.434871,
            17.435147,
            17.435449,
            17.435638,
            17.435853,
            17.436047,
            17.436285,
            17.436515,
            17.436689
    };
    static Double longPosition[]={
            78.343194,
            78.343419,
            78.343623,
            78.343945,
            78.344267,
            78.344777,
            78.345174,
            78.345319,
            78.345560,
            78.345801,
            78.345967,
            78.346219,
            78.346428,
            78.346600,
            78.346723,
            78.346852,
            78.346943,
            78.347072,
            78.347198,
            78.347292
    };

    public int busCurrentLocationIndex;

    public ServiceInteractorMock()
    {
        busCurrentLocationIndex = longPosition.length;
    }

    // Overridden functions

    @Override
    public Location getCurrentLatLongFromGPS(Context context)
    {
        Location fake = new Location("dummy");
        fake.setLatitude(17.428499);
        fake.setLongitude(78.341483);
        return fake;
    }

    @Override
    public LocationData getLocationDataFromDB(final String busRoute)
    {
        while(busCurrentLocationIndex > 0){
            busCurrentLocationIndex--;
            Calendar tmp = Calendar.getInstance();
            tmp.add(Calendar.SECOND, timeIntervals[busCurrentLocationIndex]);

            return new  LocationData(new Coordinate(latPosition[busCurrentLocationIndex],
                                                longPosition[busCurrentLocationIndex]),
                                        "5",
                                        tmp.getTime());
        }

        return null;
    }

    @Override
    public boolean giveLocationDataToDB(LocationData location)
    {
        return false;
    }


}
