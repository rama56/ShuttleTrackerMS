package com.example.ramakrk.shuttletrackerms;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Driver;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by ramakrk on 7/25/2016.
 */
public class ClientBackend {

    private static Context contextOfInstantiator;

    public ClientBackend(Context c) {
        this.contextOfInstantiator = c;
    }

    JSONParser jsonParser = new JSONParser();
    final String TAG_success = "success";

//    private enum BusRoute
//    {
//
//    }

    public ClientBackend()
    {

    }

//    public static void showToastMethod() {
//        Toast.makeText(contextOfInstantiator, "Turn on GPS or Check network connection", Toast.LENGTH_SHORT).show();
//    }

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

    public static LocationData CurrentPosition;


    // Methods called by passengers waiting for the bus.
    public LocationData GetLocationDataFromDB(final String busRoute) {

        return new LocationData(new Coordinate(17.43,78.36),"5", parseDate("2016-07-26-19-01-00"));
 /*       final String url = "http://shuttletracker.netau.net/GetPosition.php";
        int retryCount = 5;

        // Create a HTTP request.

        while (retryCount > 0) {
            // Create and perform a HTTP request.
            JSONObject request = new JSONObject();
            GetPosition gp = new GetPosition(busRoute);
            try {
                gp.execute().get(5000, TimeUnit.MILLISECONDS);
                retryCount = 0;
            } catch (Exception e) {
                Log.e("CB", e.getMessage());
                retryCount--;
            }

        }
        return CurrentPosition;
        */


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
                Log.e("GiveLocationDataTimer", "Time left on this tick " + millisUntilFinished);

                // Get GPS data.
                Location location = getCurrentLatLongFromGPS(context);
//
//                double dlongitude = location.getLatitude();  //longitude;
//                double dlatitude = location.getLongitude();  //latitude;
                if (location == null)
                {
                    // DriverActivity dv= new DriverActivity();
                 //latitude;

                    Log.d("0,0", "GPS VALUE IS NULL");
                    //showToastMethod();
                    //showWarningAlert(contextOfInstantiator);
                }
                else
                {
                    double dlongitude = location.getLatitude();  //longitude;
                    double dlatitude = location.getLongitude();  //latitude;
                    String gps = dlatitude + "," + dlongitude;

                    // We now have a non-null location object.
                    //Toast.makeText(,"Got the param"+gps,Toast.LENGTH_LONG).show();

                    Log.d(gps, "GPS Value");

                    // Get Current time
                    Date currentTime = getCurrentLocalTime();

                    GiveLocationDataToDB(new LocationData(new Coordinate(dlatitude, dlongitude), busRoute, currentTime));
                }
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


        /*int retryCount = 5;

        // Create a HTTP request.

        while (retryCount > 0) {
            // Create and perform a HTTP request.
           PutPosition putPosition = new PutPosition(location.busRoute,location.point.latitude,location.point.longitude,location.time);

            try {
                putPosition.execute().get(5000, TimeUnit.MILLISECONDS);
                retryCount = 0;
            } catch (Exception e) {
                Log.e("CB", e.getMessage());
                retryCount--;
            }

        }*/


        return isSuccess;
    }

    public class PutPosition extends AsyncTask<String, String, Void>{
        String busRoute;
        String lat;
        String longitude;
        String upDateTime;

        public PutPosition(String busRoute, Double lat, Double longitutde, Date updateTime){
            this.busRoute = busRoute;
            this.lat = lat.toString();
            this.longitude = longitutde.toString();
            this.upDateTime = updateTime.toString();

        }

        @Override
        protected Void doInBackground(String... params) {
            final String url = "https://shuttletracker.netau.net/UpdatePosition.php";
            java.util.List<NameValuePair> requestParams = new ArrayList<NameValuePair>();

            requestParams.add(new BasicNameValuePair("RouteNo", this.busRoute));
            requestParams.add(new BasicNameValuePair("BusNo", this.busRoute));
            requestParams.add(new BasicNameValuePair("LatPos", this.lat));
            requestParams.add(new BasicNameValuePair("LongPos", this.longitude));
            requestParams.add(new BasicNameValuePair("LastUpdateTime", this.upDateTime));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url,
                    "GET", requestParams);


            // check log cat fro response
            Log.d("Create Response", json.toString());
            try {
                int success = json.getInt(TAG_success);

                if (success == 1) {
                    Log.d("t1", "successfully retrieved");
                    // closing this screen
                    // finish();
                } else {
                    // failed to create product
                    String message = json.getString("message");
                    Log.d("t1", "Update failed with error:"+message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
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

    class GetPosition extends AsyncTask<String, String, Void>
    {
        LocationData returnable;
        String busRoute;
        final String url = "https://shuttletracker.netau.net/GetPosition.php";

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
    private void showWarningAlert(Context context) { //Added argument
        //DriverActivity driverActivity=null;
        AlertDialog alertDialog = new AlertDialog.Builder(context).create(); //Use context
        alertDialog.setTitle("Error,Check connection");
        alertDialog.setMessage("Turn on GPS or Check network Connection");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
