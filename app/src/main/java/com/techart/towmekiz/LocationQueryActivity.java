package com.techart.towmekiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.techart.towmekiz.constants.Constants;
import com.techart.towmekiz.model.Order;

public class LocationQueryActivity extends AppCompatActivity {
    Button btPickLocation;
    Button btFindMe;

    private Order order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_query);

        order = (Order) getIntent().getSerializableExtra("order");
        String orderUrl = getIntent().getStringExtra("orderUrl");

        btPickLocation = findViewById(R.id.bt_pick_location);
        btFindMe = findViewById(R.id.bt_find_me);

        btFindMe.setOnClickListener(view -> {
            Intent orderActivity = new Intent(LocationQueryActivity.this, MapsActivity.class);
            orderActivity.putExtra(Constants.IS_FIND_ME, true);
            orderActivity.putExtra("order", order);
            orderActivity.putExtra("orderUrl", orderUrl);
            Log.e("LocationQueryActivity", orderUrl);
            startActivity(orderActivity);
        });

        btPickLocation.setOnClickListener(view -> {
            Intent orderActivity = new Intent(LocationQueryActivity.this, MapsActivity.class);
            orderActivity.putExtra(Constants.IS_FIND_ME, false);
            orderActivity.putExtra("order", order);
            orderActivity.putExtra("orderUrl", orderUrl);
            Log.e("LocationQueryActivity", orderUrl);
            startActivity(orderActivity);
        });
    }

}