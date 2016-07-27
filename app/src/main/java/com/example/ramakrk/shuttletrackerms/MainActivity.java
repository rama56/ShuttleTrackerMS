package com.example.ramakrk.shuttletrackerms;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

//import com.google.android.gms.drive.internal.StringListResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private GoogleMap employeeMap;
    private Button trackShuttle;

    private CountDownTimer timerToDisplayStaleness = null;

    private void getRouteNumber() {
        AlertDialog routeDialog = new AlertDialog.Builder(MainActivity.this).create();
        routeDialog.setTitle("Set Route Number");
        String[] availRoutes = {"1","2","3","4","5","6","7","8","9","10",
                                "11","12","13","14","15","16","17","18","19","20","21","22","23","24"};

        final Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,availRoutes);
        spinner.setAdapter(spinnerAdapter);
        routeDialog.setView(spinner);
        routeDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "TRACK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("TrackRoute",spinner.getSelectedItem().toString());
                        editor.commit();
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(intent,0);
                    }
                });
        routeDialog.show();
    }

    private void initialSetup() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String getRoute = sharedPreferences.getString("TrackRoute","");
        if(getRoute.equalsIgnoreCase("")) {
            getRouteNumber();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initialSetup();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trackShuttle = (Button) findViewById(R.id.track);
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                employeeMap = googleMap;

                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                String RouteNumber = sharedPreferences.getString("TrackRoute","1");
                // Call a function to periodically update the bus location on Map.
                UpdateBusLocationPeriodically(RouteNumber);
            }

        });



        // Testing here by Rama. Don't erase for now.
        //ClientBackend obj = new ClientBackend();
        //obj.GiveLocationDataToDBWrapper(getBaseContext(), "24");
        // Testing ends.

        trackShuttle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getRouteNumber();
            }
        });

        UpdateLocationStalenessPeriodically(1000);
    }

    private void UpdateLocationStalenessPeriodically(int i)
    {
        timerToDisplayStaleness = new CountDownTimer(10000000, i) {
            @Override
            public void onTick(long millisUntilFinished) {
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                Long locationStaleness = sharedPreferences.getLong("LocationStaleness",-1);

                String stalenessString;
                if(locationStaleness < 0)
                {
                    stalenessString = "Unknown";
                }
                else if(locationStaleness >= 86400)
                {
                    stalenessString = "More than a day ago.";
                }
                else
                {
                    long diff = locationStaleness;
                    long sec = diff % 60;
                    long min = (diff / 60) % 60;
                    long hr = diff / (60 * 60);
                    stalenessString =   String.format("Updated %1$02d:%2$02d:%3$02d ago..",hr,min,sec);
                }

                TextView object  = (TextView)findViewById(R.id.timeChange);
                object.setText(stalenessString);
            }

            @Override
            public void onFinish() {
                timerToDisplayStaleness.start();
            }
        }.start();

    }

    CountDownTimer timerToGetData = null;

    private void UpdateBusLocationPeriodically(final String routeNumber)
    {
        timerToGetData = new CountDownTimer(100000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                ClientBackend object = new ClientBackend();
                ClientBackend.LocationData currentLocation = object.GetLocationDataFromDB(routeNumber);
                ClientBackend.Coordinate currentPoint = currentLocation.point;
                Date registeredTime = currentLocation.time;
                Date currentTime = ClientBackend.getCurrentLocalTime();

                long differenceInSeconds = getDifference(registeredTime, currentTime);

                // Use shared memory to store staleness time of location data.
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putLong("LocationStaleness",differenceInSeconds);
                editor.commit();

                // Update the pin in the map.
                LatLng bus = new LatLng(currentPoint.latitude, currentPoint.longitude);
                Location user = ClientBackend.getCurrentLatLongFromGPS(getBaseContext());
                LatLng myLocation = new LatLng(user.getLatitude(), user.getLongitude());
                BitmapDescriptor bitmap;
                employeeMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)).position(bus).title(routeNumber));
                employeeMap.addMarker(new MarkerOptions().position(myLocation).title("You are here..."));
                employeeMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bus,15.0f));
            }

            @Override
            public void onFinish()
            {
                timerToGetData.start();
            }
        };
        timerToGetData.start();

    }

    private long getDifference(Date early, Date late)
    {
        long gapInSeconds = 250;
        if(late!=null && early!=null) {
            gapInSeconds = (late.getTime() - early.getTime()) / 1000;
        }
        return gapInSeconds;
    }

}
