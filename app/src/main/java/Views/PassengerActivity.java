package views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.drive.internal.StringListResponse;
import com.example.ramakrk.shuttletrackerms.ClientBackend;
import com.example.ramakrk.shuttletrackerms.R;
import com.example.ramakrk.shuttletrackerms.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

import models.*;

public class PassengerActivity extends AppCompatActivity {

    private GoogleMap employeeMap;
    private Button trackShuttle;
    private CountDownTimer timerToDisplayStaleness = null;
    Toast mytoast;

    ClientBackend clientBackendPassenger;

    CountDownTimer timerToGetData = null;

    private void getRouteNumber() {
        AlertDialog routeDialog = new AlertDialog.Builder(PassengerActivity.this).create();
        routeDialog.setTitle("Set Route Number");
        String[] availRoutes = {"1","2","3","4","5","6","7","8","9","10",
                                "11","12","13","14","15","16","17","18","19","20","21","22","23","24"};

        final Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,availRoutes);
        spinner.setAdapter(spinnerAdapter);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String getRoute = sharedPreferences.getString("TrackRoute", "1");
        if(Integer.parseInt(getRoute)-1  != 0) {
            spinner.setSelection(Integer.parseInt(getRoute) - 1);
        }
        routeDialog.setView(spinner);
        routeDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "TRACK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Because we may select a new bus route, we cancel the 'Last Updated' timer of the previous root.
                        if(timerToDisplayStaleness!=null) {
                            timerToDisplayStaleness.cancel();
                        }
                        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("TrackRoute",spinner.getSelectedItem().toString());
                        editor.commit();
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(),PassengerActivity.class);
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

    private void UpdateLocationStalenessPeriodically(final int i)
    {
        timerToDisplayStaleness = new CountDownTimer(10000, i) {
            @Override
            public void onTick(long millisUntilFinished) {
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                Long locationStaleness = sharedPreferences.getLong("LocationStaleness",-1);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putLong("LocationStaleness", locationStaleness + i/1000);
                editor.commit();

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
                    stalenessString =   String.format("Location as of %1$02d:%2$02d:%3$02d ago..",hr,min,sec);
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

    private void UpdateBusLocationPeriodically(final String routeNumber)
    {
        timerToGetData = new CountDownTimer(100000,5000)
        {
            int counter = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                LocationData currentLocation = clientBackendPassenger.GetLocationDataFromDB(routeNumber);
                if(currentLocation != null) {
                    Coordinate currentPoint = currentLocation.getPoint();

                    Date registeredTime = currentLocation.getTime();

                    Date currentTime = Utils.getCurrentLocalTime();
                    long differenceInSeconds = getDifference(currentTime, registeredTime);

                    // Use shared memory to store staleness time of location data.
                    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putLong("LocationStaleness", differenceInSeconds);
                    editor.commit();

                    // Update the pin in the map.
                    BitmapDescriptor bitmap;

                    if (currentPoint != null) {
                        employeeMap.clear();
                        LatLng bus = new LatLng(currentPoint.getLatitude(), currentPoint.getLongitude());
                        employeeMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)).position(bus).title(routeNumber));
                        employeeMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bus, 15.0f));
                    } else {
                        Toast.makeText(PassengerActivity.this, "Server didn't return location for bus route " + routeNumber, Toast.LENGTH_SHORT).show();
                    }

                    Location user = clientBackendPassenger.getCurrentLatLongFromGPS(getBaseContext());
                    if (user != null) {
                        LatLng myLocation = new LatLng(user.getLatitude(), user.getLongitude());
                        employeeMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.usericon)).position(myLocation).title("You are here..."));
                    } else {
                        Log.d("0,0", "GPS VALUE IS NULL");
                        /*mytoast=new Toast(PassengerActivity.this);
                        mytoast=Toast.makeText(PassengerActivity.this, " Your location is unavailable. Turn on GPS or Check network connection", Toast.LENGTH_SHORT);
                        mytoast.show();*/
                    }
                }
                else
                {
                    Toast.makeText(PassengerActivity.this, "Server didn't return locationData object " + routeNumber, Toast.LENGTH_SHORT).show();
                }

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
        long gapInSeconds = -1;
        if(late!=null && early!=null) {
            gapInSeconds = (late.getTime() - early.getTime()) / 1000;
        }
        return gapInSeconds;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        initialSetup();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        trackShuttle = (Button) findViewById(R.id.track);
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        clientBackendPassenger = new ClientBackend(getApplicationContext());

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                employeeMap = googleMap;

                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                String RouteNumber = sharedPreferences.getString("TrackRoute","-1");

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putLong("LocationStaleness", -1);
                editor.commit();

                // Call a function to periodically update the bus location on Map.
                UpdateBusLocationPeriodically(RouteNumber);
            }

        });

        trackShuttle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getRouteNumber();
                //timerToGetData.cancel();
            }
        });

        if(timerToDisplayStaleness!=null)
        {
            timerToDisplayStaleness.cancel();
        }
        UpdateLocationStalenessPeriodically(1000);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(PassengerActivity.this,MainPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // clears all previous activities task
         // destroy current activity..
        timerToGetData.cancel();
        timerToGetData=null;
        if(mytoast!=null) {
            mytoast.cancel();
        }
        finish();
        startActivity(intent);
    }

    @Override
    protected void onDestroy()
    {
    if(mytoast!=null) {
        mytoast.cancel();
    }
        super.onDestroy();
    }

    @Override
    protected void onStop () {
        super.onStop();
        if(mytoast!=null) {
            mytoast.cancel();
        }
    }

}
