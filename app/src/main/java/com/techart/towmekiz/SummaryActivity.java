package com.techart.towmekiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.towmekiz.constants.FireBaseUtils;
import com.techart.towmekiz.model.Order;
import com.techart.towmekiz.model.Profile;
import com.techart.towmekiz.model.SummaryItem;
import com.techart.towmekiz.model.Vehicle;
import com.techart.towmekiz.ui.SummaryAdaptor;

import java.util.ArrayList;
import java.util.Arrays;

public class SummaryActivity extends AppCompatActivity {
    private String orderUrl;
    private Order order;
    private Vehicle vehicle;
    private Profile profile = Profile.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        orderUrl = getIntent().getStringExtra("orderUrl");
        getOrderDetails(orderUrl);
        getVehicleDetails(orderUrl);
        profile = Profile.getInstance();
        Button confirmOrder = findViewById(R.id.bt_confirm_order);

        confirmOrder.setOnClickListener(view -> {
            Intent orderActivity = new Intent(SummaryActivity.this, PaymentActivity.class);
            orderActivity.putExtra("order", order);
            orderActivity.putExtra("orderUrl", orderUrl);
            orderActivity.putExtra("phoneNumber", profile);
            startActivity(orderActivity);
        });
    }

    private void displaySummary() {
        ArrayList<SummaryItem> arrayOfUsers = new ArrayList<>(
                Arrays.asList(
                        new SummaryItem("SERVICE", order.getService() + ""),
                        new SummaryItem("VEHICLE", vehicle.getColor() + " " + vehicle.getMake() + " " + vehicle.getModel()),
                        new SummaryItem("ADDITIONAL INFORMATION", order.getAdditionalInformation()),
                        new SummaryItem("CONTACT", profile.getFirstName() + " " + profile.getLastName() + "\r\n" + profile.getPhoneNumber() + "\r\n" + profile.getEmail())));

        // Create the adapter to convert the array to views
        SummaryAdaptor adapter = new SummaryAdaptor(this, arrayOfUsers);

        // Attach the adapter to a ListView
        ListView listView = findViewById(R.id.lv_items);

        listView.setAdapter(adapter);
    }

    private void getOrderDetails(String orderUrl) {
        FireBaseUtils.mDatabaseOrder.child(orderUrl).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order = dataSnapshot.getValue(Order.class);
                if (profile != null && vehicle != null) {
                    displaySummary();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getVehicleDetails(String orderUrl) {
        FireBaseUtils.mDatabaseVehicle.child(orderUrl).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vehicle = dataSnapshot.getValue(Vehicle.class);
                if (profile != null && order != null) {
                    displaySummary();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}