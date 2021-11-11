package com.techart.towme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.techart.towme.constants.Constants;

public class LocationQueryActivity extends AppCompatActivity {
    Button btPickLocation;
    Button btFindMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_query);

        btPickLocation = findViewById(R.id.bt_pick_location);
        btFindMe = findViewById(R.id.bt_find_me);

        btFindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderActivity = new Intent(LocationQueryActivity.this, MapsActivity.class);
                orderActivity.putExtra(Constants.IS_FIND_ME, true);
                startActivity(orderActivity);
            }
        });

        btPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderActivity = new Intent(LocationQueryActivity.this, MapsActivity.class);
                orderActivity.putExtra(Constants.IS_FIND_ME, false);
                startActivity(orderActivity);
            }
        });


    }
}