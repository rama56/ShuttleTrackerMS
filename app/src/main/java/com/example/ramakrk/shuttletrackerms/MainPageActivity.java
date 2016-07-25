package com.example.ramakrk.shuttletrackerms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainPageActivity extends AppCompatActivity {

    Button passengerButton;
    Button driverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        passengerButton = (Button) findViewById(R.id.navigatePassenger);
        driverButton = (Button) findViewById(R.id.navigateDriver);

        passengerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passengerIntent = new Intent(v.getContext(),MainActivity.class);
                startActivityForResult(passengerIntent,0);

            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
