package views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ramakrk.shuttletrackerms.ClientBackend;
import com.example.ramakrk.shuttletrackerms.R;

public class DriverActivity extends AppCompatActivity
{
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
                Toast.makeText(DriverActivity.this,"Starting to track",Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //showCurrentLocation();
                Checkforconnection();
                Spinner spinner = (Spinner) findViewById(R.id.driverRoute);
                String routeID = spinner.getSelectedItem().toString();
                editor.putString("DriverRoute", routeID);
                editor.commit();
                clientBackendDriver = new ClientBackend(getApplicationContext()); //latitude,longitude);
                clientBackendDriver.GiveLocationDataToDBWrapper(getBaseContext(), routeID);
            }
        });

        Button endButon = (Button) findViewById(R.id.endTravel);
        endButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clientBackendDriver != null) {
                    Toast.makeText(DriverActivity.this,"Stopped tracking",Toast.LENGTH_LONG).show();
                    clientBackendDriver.StopGivingLocationData();
                }
            }
        });
    }

    private void Checkforconnection() {
        Location location = clientBackendDriver.getCurrentLatLongFromGPS(DriverActivity.this);
        if(location==null)
        {
            Log.d("0,0","GPS VALUE IS NULL");
            AlertDialog alertDialog = new AlertDialog.Builder(DriverActivity.this).create(); //Use context
            alertDialog.setTitle("Oops..Check your connection");
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

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(DriverActivity.this,MainPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // clears all previous activities task
        finish();
        startActivity(intent);
    }

}