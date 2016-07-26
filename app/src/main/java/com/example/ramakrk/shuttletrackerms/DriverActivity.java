package com.example.ramakrk.shuttletrackerms;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DriverActivity extends AppCompatActivity {

    private void initialSetup() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String getRoute = sharedPreferences.getString("DriverRoute","1");
        EditText editText = (EditText)findViewById(R.id.driverRoute);
        editText.setText(getRoute);
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

                EditText editText = (EditText)findViewById(R.id.driverRoute);
                String routeID = editText.getText().toString();
                editor.putString("DriverRoute",routeID);
                editor.commit();
                ClientBackend clientBackend = new ClientBackend();
                clientBackend.GiveLocationDataToDBWrapper(getBaseContext(),routeID);
            }
        });

        Button endButon = (Button) findViewById(R.id.endTravel);
        endButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientBackend clientBackend = new ClientBackend();
                clientBackend.StopGivingLocationData();
            }
        });
    }
}
