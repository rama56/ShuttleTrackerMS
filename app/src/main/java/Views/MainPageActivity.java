package views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ramakrk.shuttletrackerms.R;

public class MainPageActivity extends AppCompatActivity {

    Button passengerButton;
    Button driverButton;
    Button routeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        TextView text = (TextView)findViewById(R.id.title);
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottomup);
        text.startAnimation(animation1);
        TextView text1 = (TextView)findViewById(R.id.choosemode);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        text1.startAnimation(animation2);

        passengerButton = (Button) findViewById(R.id.navigatePassenger);
        driverButton = (Button) findViewById(R.id.navigateDriver);
        //routeButton = (Button) findViewById(R.id.routeTrack);

        passengerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected())
                {
                    Toast.makeText(MainPageActivity.this,"Tracking shuttle...",Toast.LENGTH_LONG).show();
                    Intent passengerIntent = new Intent(v.getContext(),PassengerActivity.class);
                    startActivityForResult(passengerIntent,0);
                }
                else
                {
                    //Toast.makeText(MainPageActivity.this,"Check your internet connection",Toast.LENGTH_LONG).show();
                    showAlertDialog();
                }
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()) {
                    Toast.makeText(MainPageActivity.this, "Broadcasting Location...", Toast.LENGTH_LONG).show();
                    Intent driverIntent = new Intent(v.getContext(), DriverActivity.class);
                    startActivityForResult(driverIntent, 0);
                }
                else
                {
                    //Toast.makeText(MainPageActivity.this,"Check your internet connection",Toast.LENGTH_LONG).show();
                    showAlertDialog();
                }

            }
        });

        
    }

    @Override
    public void onResume()
    {
        super.onResume();
        int a = 5;
        //dummy
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    private void showAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainPageActivity.this).create(); //Use context
        alertDialog.setTitle("Error");
        alertDialog.setMessage("No Network Connection");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
