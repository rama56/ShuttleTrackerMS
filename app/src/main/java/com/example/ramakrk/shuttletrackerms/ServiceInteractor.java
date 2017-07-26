package com.example.ramakrk.shuttletrackerms;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import models.*;

/**
 * Created by ramakrk on 7/24/2017.
 */
public class ServiceInteractor extends Utils
{
    class PutPosition extends AsyncTask<String, String, Void>
    {
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
            final String url = "https://msshuttletracker.herokuapp.com/UpdatePosition.php";
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
            try
            {
                int success = json.getInt(TAG_success);
                if (success == 1)
                {
                    Log.d("t1", "successfully retrieved");
                }
                else
                {
                    // failed to create product
                    String message = json.getString("message");
                    Log.d("t1", "Update failed with error:"+message);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    class GetPosition extends AsyncTask<String, String, Void>
    {
        LocationData returnable;
        String busRoute;
        final String url = "https://msshuttletracker.herokuapp.com/GetPosition.php";

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
                    JSONArray buses = json.getJSONArray("BusInfo");
                    JSONObject bus = buses.getJSONObject(0);

                    String lastUpdatedTime = bus.getString("LastUpdatedTime");

                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = format.parse(lastUpdatedTime);

                    LocationData currentPosition = new LocationData(new Coordinate(bus.getDouble("LatPos"),bus.getDouble("LongPos")), bus.getString("BusNo"), date);

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
