package com.example.ramakrk.shuttletrackerms;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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


    private void getRouteNumber() {
        AlertDialog routeDialog = new AlertDialog.Builder(MainActivity.this).create();
        routeDialog.setTitle("Set Route Number");
        String[] availRoutes = {"1","2","3","4","5"};

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
                // Add a marker in Sydney, Australia, and move the camera.
                LatLng sydney = new LatLng(17.385, 78.486);
                LatLng sydney1 = new LatLng(17.386, 78.489);
                BitmapDescriptor bitmap;
                employeeMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)).position(sydney).title("Marker in Sydney"));
                employeeMap.addMarker(new MarkerOptions().position(sydney1).title("You are here...."));
                employeeMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,15.0f));

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

                
                // Use shared memory to store staleness time of location data.
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt("LocationStaleness",0);
                editor.commit();
            }

            @Override
            public void onFinish()
            {

            }
        }.start();

    }

}
