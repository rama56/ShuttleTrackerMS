package com.example.ramakrk.shuttletrackerms;

import android.util.Log;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void GiveLocationDataWrapper() throws Exception
    {
        ClientBackend object = new ClientBackend();
        //object.GiveLocationDataToDBWrapper("24");
    }

    @Test
    public void TestGetHttpRequest()
    {
        ClientBackend obj = new ClientBackend();
        ClientBackend.LocationData result = obj.GetLocationDataFromDB("10");
        Log.d("Over", "" + result.point.latitude + result.point.longitude + result.time);
    }

    @Test
    public void TestPutHttpRequest()
    {
        ClientBackend obj = new ClientBackend();

        // new LocationData(new Coordinate(17.43,78.36),"5", parseDate("2016-07-26-19-01-00")
        Date date = ClientBackend.parseDate("2016-07-26-19-01-00");
        ClientBackend.Coordinate point = new ClientBackend.Coordinate(20.5,14.7);
        ClientBackend.LocationData location = new ClientBackend.LocationData(point,"15",date);
        boolean isSuccess = obj.GiveLocationDataToDB(location);
    }

    public void TestPutHttpRequestDirectly()
    {

    }

}