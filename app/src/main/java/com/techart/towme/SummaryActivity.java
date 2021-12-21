package com.techart.towme;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.towme.constants.FireBaseUtils;
import com.techart.towme.model.Order;
import com.techart.towme.model.SummaryItem;
import com.techart.towme.ui.SummaryAdaptor;

import java.util.ArrayList;
import java.util.Arrays;

public class SummaryActivity extends AppCompatActivity {
    String orderUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        orderUrl = "-MrLNTQBlx3ClR3xm-r_";
        loadData(orderUrl);
//        displaySummary(new Order());
    }

    private void displaySummary(Order order) {
//        order = new Order("Location", "Service", 0.0, 0.0, "Make", 0.9, 0.8 );
        // Construct the data source

        ArrayList<SummaryItem> arrayOfUsers = new ArrayList<>(
                Arrays.asList(
                        new SummaryItem("LOCATION", order.getLatitude() + " " + order.getLongitude()),
                        new SummaryItem("SERVICE", order.getService()),
                        new SummaryItem("VEHICLE", order.getFixedChargeNarration()),
                        new SummaryItem("CONTACT", "0979407445")));

        // Create the adapter to convert the array to views
        SummaryAdaptor adapter = new SummaryAdaptor(this, arrayOfUsers);

        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lv_items);

        listView.setAdapter(adapter);
    }

    private void loadData(String orderUrl) {
        Log.i("ORDER", orderUrl);

        FireBaseUtils.mDatabaseOrder.child(orderUrl).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                displaySummary(order);
                Log.i("ORDER", order.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}