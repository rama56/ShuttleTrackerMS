package com.example.ramakrk.shuttletrackerms;

// Native framework imports.
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

// Native language imports.
import java.util.Date;

// Project Internal imports
import models.*;


/**
 * Created by ramakrk on 7/25/2016.
 */
public class ClientBackend
{
    private static Context contextOfInstantiator;

    private static ServiceInteractorBase backendHelper;

    public ClientBackend(Context context)
    {
        contextOfInstantiator = context;
        ServiceInteractorHelperFactory helperFactory = new ServiceInteractorHelperFactory();
        String dataMode = contextOfInstantiator.getApplicationContext().getString(R.string.data_mode);

        backendHelper = helperFactory.getBackendHelper(dataMode);
    }

    private CountDownTimer timerForSendingLocationData;

    // Methods called by passengers waiting for the bus.
    public LocationData GetLocationDataFromDB(final String busRoute)
    {
        return backendHelper.getLocationDataFromDB(busRoute);
    }


    // Methods called by bus driver.
    public void GiveLocationDataToDBWrapper(final Context context, final String busRoute) throws SecurityException
    {
        timerForSendingLocationData = new CountDownTimer(100000, 000)
        {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("GiveLocationDataTimer", "Time left on this tick " + millisUntilFinished);

                // Get GPS data.
                Location location = getCurrentLatLongFromGPS(context);

                if (location == null)
                {
                    Log.d("0,0", millisUntilFinished + "GPS VALUE IS NULL");
                    Checkforconnection(context);
                }
                else
                {
                    double dlongitude = location.getLatitude();  //longitude;
                    double dlatitude = location.getLongitude();  //latitude;
                    String gps = dlatitude + "," + dlongitude;

                    // We now have a non-null location object.

                    Log.d(gps, "GPS Value");

                    // Get Current time
                    Date currentTime = Utils.getCurrentLocalTime();

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

    //Ported
    public boolean GiveLocationDataToDB(LocationData location)
    {
        return backendHelper.giveLocationDataToDB(location);
    }

    // Ported.
    public Location getCurrentLatLongFromGPS(Context context)
    {
        return backendHelper.getCurrentLatLongFromGPS(context);
    }

    private void Checkforconnection(Context connectioncontext) {
        Location location = backendHelper.getCurrentLatLongFromGPS(connectioncontext);
        if(location==null)
        {
            Log.d("0,0","GPS VALUE IS NULL");
            Toast.makeText(connectioncontext, "Turn on GPS or Check network connection", Toast.LENGTH_SHORT).show();
            AlertDialog alertDialog = new AlertDialog.Builder(connectioncontext).create(); //Use context
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

}
