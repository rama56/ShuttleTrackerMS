package com.example.ramakrk.shuttletrackerms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
                Toast.makeText(MainPageActivity.this,"Tracking shuttle...",Toast.LENGTH_LONG).show();
                Intent passengerIntent = new Intent(v.getContext(),MainActivity.class);
                startActivityForResult(passengerIntent,0);

            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainPageActivity.this,"Broadcasting Location...",Toast.LENGTH_LONG).show();
                Intent driverIntent = new Intent(v.getContext(),DriverActivity.class);
                startActivityForResult(driverIntent,0);
            }
        });

        
    }
}
