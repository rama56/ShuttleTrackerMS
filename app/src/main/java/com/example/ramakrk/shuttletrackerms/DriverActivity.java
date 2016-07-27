package com.example.ramakrk.shuttletrackerms;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v4.widget.Space;

public class DriverActivity extends AppCompatActivity {
    private static final double MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 0.1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds

    protected LocationManager locationManager;
    public double latitude,longitude;

    protected Button retrieveLocationButton;

    ClientBackend clientBackendDriver;

    private void initialSetup() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String getRoute = sharedPreferences.getString("DriverRoute", "1");
        Spinner spinner = (Spinner) findViewById(R.id.driverRoute);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.route_number,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(Integer.parseInt(getRoute)-1  != 0) {
            spinner.setSelection(Integer.parseInt(getRoute) - 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        initialSetup();
        Button startButton = (Button) findViewById(R.id.startTravel);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //showCurrentLocation();

                Spinner spinner = (Spinner) findViewById(R.id.driverRoute);
                String routeID = spinner.getSelectedItem().toString();
                editor.putString("DriverRoute", routeID);
                editor.commit();
                clientBackendDriver = new ClientBackend(); //latitude,longitude);
                clientBackendDriver.GiveLocationDataToDBWrapper(getBaseContext(), routeID);
            }
        });

        Button endButon = (Button) findViewById(R.id.endTravel);
        endButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clientBackendDriver != null) {
                    clientBackendDriver.StopGivingLocationData();
                }
            }
        });
    }

public void onBackPressed()
{
    // code here to show dialog
    super.onBackPressed();  // optional depending on your needs
    finish();
}

}