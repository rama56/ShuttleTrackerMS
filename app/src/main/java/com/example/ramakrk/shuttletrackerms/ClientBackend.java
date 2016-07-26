package com.example.ramakrk.shuttletrackerms;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by ramakrk on 7/25/2016.
 */
public class ClientBackend {

    JSONParser jsonParser = new JSONParser();
    final String TAG_success = "success";

//    private enum BusRoute
//    {
//
//    }

    private CountDownTimer timerForSendingLocationData;

    public class LocationData {
        public LocationData(Coordinate Point, String BusRoute, Date time)
        {
            this.point = Point;
            this.busRoute = BusRoute;
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

    public static LocationData CurrentPosition;


    // Methods called by passengers waiting for the bus.
    public LocationData GetLocationDataFromDB(final String busRoute) {
        final String url = "http://shuttletracker.netau.net/GetPosition.php";

        // Create and perform a HTTP request.
        JSONObject request = new JSONObject();
        GetPosition gp=new GetPosition(busRoute);
        try {
            gp.execute().get(5000,TimeUnit.MILLISECONDS);
        }catch (Exception e){
            Log.e("CB",e.getMessage());
        }
        return CurrentPosition;


//        java.util.List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
//
//        requestParams.add(new BasicNameValuePair("RouteNo", busRoute));
//        requestParams.add(new BasicNameValuePair("BusNo", "busRoute"));
//
//        // getting JSON Object
//        // Note that create product url accepts POST method
//        JSONObject json = jsonParser.makeHttpRequest(url,
//                "GET", requestParams);
//
//
//        // check log cat fro response
//        Log.d("Create Response", json.toString());
//
//        // check for success tag
//        try {
//            int success = json.getInt(TAG_success);
//
//            if (success == 1) {
//                Log.d("t1", "successfully retrieved");
//                JSONArray buses = json.getJSONArray("product");
//                JSONObject bus = buses.getJSONObject(0);
//
//                String lastUpdatedTime = bus.getString("LastUpdatedTime");
//
//                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = format.parse(lastUpdatedTime);
//                CurrentPosition.point = new Coordinate(bus.getDouble("LatPos"),bus.getDouble("LongPos"));
//                CurrentPosition.busRoute =  busRoute;
//                CurrentPosition.time = date;
//                // closing this screen
//                // finish();
//            } else {
//                // failed to create product
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return CurrentPosition;
    }

    // Methods called by bus driver.
    public void GiveLocationDataToDBWrapper(final Context context, final String busRoute) throws SecurityException {
        timerForSendingLocationData = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Get GPS data.
                Log.e("GiveLocationDataTimer","Time left on this tick " + millisUntilFinished);
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

                double longitude, latitude;
                if(location !=null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }

                // Get Current time
                Date currentTime = getCurrentLocalTime();

                GiveLocationDataToDB(new LocationData (new Coordinate(1.1,2.2), busRoute, currentTime));
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
    class GetPosition extends AsyncTask<String, String, Void>
    {
        LocationData returnable;
        String busRoute;
        final String url = "http://shuttletracker.netau.net/GetPosition.php";

        GetPosition(String busRoute)
        {
            this.busRoute=busRoute;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            java.util.List<NameValuePair> requestParams = new ArrayList<NameValuePair>();

            requestParams.add(new BasicNameValuePair("RouteNo", busRoute));
            requestParams.add(new BasicNameValuePair("BusNo", busRoute));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url,
                    "GET", requestParams);


            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_success);

                if (success == 1) {
                    Log.d("t1", "successfully retrieved");
                    JSONArray buses = json.getJSONArray("product");
                    JSONObject bus = buses.getJSONObject(0);

                    String lastUpdatedTime = bus.getString("LastUpdatedTime");

                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = format.parse(lastUpdatedTime);
                    CurrentPosition.point = new Coordinate(bus.getDouble("LatPos"),bus.getDouble("LongPos"));
                    CurrentPosition.busRoute =  busRoute;
                    CurrentPosition.time = date;
                    // closing this screen
                    // finish();
                } else {
                    // failed to create product
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
        }
    }

}

