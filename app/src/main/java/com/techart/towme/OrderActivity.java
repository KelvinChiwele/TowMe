package com.techart.towme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.firebase.database.ServerValue;
import com.techart.towme.constants.Constants;
import com.techart.towme.constants.FireBaseUtils;
import com.techart.towme.databinding.ActivityOrderBinding;
import com.techart.towme.enums.Status;
import com.techart.towme.enums.Unit;
import com.techart.towme.model.Order;

import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityOrderBinding binding;
    private final Map<String, Order> serviceList = new HashMap<>();
    private final Order order = new Order("bt_tow", "Tow", 300.0, 10.0, Unit.KM);
    Order service;

    private String orderUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btStartOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postOrder();
                setUrl(orderUrl);
                Intent orderActivity = new Intent(OrderActivity.this, LocationQueryActivity.class);
                orderActivity.putExtra("order", order);
                orderActivity.putExtra("orderUrl", orderUrl);
                Log.e("OrderActivity", orderUrl);
                startActivity(orderActivity);
            }
        });

        serviceList.put("bt_battery", new Order("bt_battery", "Battery", 1600.40, 0.0, Unit.STANDARD));
        serviceList.put("bt_fuel", new Order("bt_fuel", "Fuel", 300.50, 18.5, Unit.Ltr));
        serviceList.put("bt_lockout", new Order("bt_lockout", "Lockout", 500.12, 0.0, Unit.STANDARD));
        serviceList.put("bt_tire", new Order("bt_tire", "Tire", 350.90, 0.0, Unit.STANDARD));
        serviceList.put("bt_tow", new Order("bt_tow", "Tow", 300.95, 10.9, Unit.KM));
        serviceList.put("bt_winch", new Order("bt_winch", "Winch", 300.40, 0.0, Unit.STANDARD));
    }

    public void onServiceClick(View view) {
        service = new Order("bt_tow", "Tow", 300.0, 10.0, Unit.KM);
        switch (view.getId()) {
            case R.id.bt_battery:
                service =  serviceList.get("bt_battery");
                break;
            case R.id.bt_fuel:
                service =  serviceList.get("bt_fuel");
                break;
            case R.id.bt_lockout:
                service =  serviceList.get("bt_lockout");
                break;
            case R.id.bt_tire:
                service =  serviceList.get("bt_tire");
                break;
            case R.id.bt_tow:
                service =  serviceList.get("bt_tow");
                break;
            case R.id.bt_winch:
                service =  serviceList.get("bt_winch");
                break;
        }
        //
        binding.tvStartOrder.setVisibility(View.GONE);
        binding.ivGuidance.setVisibility(View.GONE);

        //
        binding.tvSelectedService.setVisibility(View.VISIBLE);
        binding.btStartOrder.setVisibility(View.VISIBLE);
        binding.tvPrice.setVisibility(View.VISIBLE);

        binding.tvPrice.setText("ZMW " + service.getFixedCharge());
        if (!Unit.STANDARD.equals(service.getUnitOfMeasure())) {
            binding.tvExtraCharge.setVisibility(View.VISIBLE);
            binding.tvExtraCharge.setText("+ ZMW " + service.getUnitCharge() + "/" + service.getUnitOfMeasure());
        } else {
            binding.tvExtraCharge.setVisibility(View.GONE);
        }
        binding.tvSelectedService.setText(service.getService());

        order.setService(service.getService());
        order.setFixedChargeNarration("+ ZMW " + service.getFixedCharge());
        order.setTotalUnitChargeNarration("+ ZMW " + service.getUnitCharge() + "/" + service.getUnitOfMeasure());
    }

    private void postOrder() {
        orderUrl = FireBaseUtils.mDatabaseOrder.push().getKey();
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.SERVICE_NAME, service.getService());
        values.put(Constants.FIXED_CHARGE, service.getFixedCharge());
        values.put(Constants.UNIT_CHARGE, service.getUnitCharge());
        values.put(Constants.UNIT_OF_MEASURE, service.getUnitOfMeasure());
        values.put(Constants.QUANTITY, 1.0);
        values.put(Constants.STATUS, Status.IN_PROGRESS);
        values.put(Constants.USER_URL, FireBaseUtils.getUiD());
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        FireBaseUtils.mDatabaseOrder.child(orderUrl).setValue(values);
    }

    private void setUrl(String orderUrl) {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.ORDER_URL, orderUrl);
        FireBaseUtils.mDatabaseOrderUrl.child(FireBaseUtils.getUiD()).setValue(values);
    }
}