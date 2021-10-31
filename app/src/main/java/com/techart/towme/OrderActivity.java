package com.techart.towme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.techart.towme.databinding.ActivityOrderBinding;
import com.techart.towme.enums.Unit;
import com.techart.towme.model.Service;

import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityOrderBinding binding;
    private Map<String, Service> serviceList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btStartOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderActivity = new Intent(OrderActivity.this, MapsActivity.class);
                startActivity(orderActivity);
            }
        });

        serviceList.put("bt_battery", new Service("bt_battery", "Battery", 1600.0, 0.0, Unit.STANDARD));
        serviceList.put("bt_fuel", new Service("bt_fuel", "Fuel", 300.0, 18.0, Unit.Ltr));
        serviceList.put("bt_lockout", new Service("bt_lockout", "Lockout", 500.0, 0.0, Unit.STANDARD));
        serviceList.put("bt_tire", new Service("bt_tire","Tire", 350.0, 0.0, Unit.STANDARD));
        serviceList.put("bt_tow", new Service("bt_tow","Tow", 300.0, 10.0, Unit.KM));
        serviceList.put("bt_winch", new Service("bt_winch","Winch", 300.0, 0.0, Unit.STANDARD));
    }

    public void onServiceClick(View view) {
        Service service = new Service();
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
        if (!Unit.STANDARD.equals(service.getUnitOfMeasure())){
            binding.tvExtraCharge.setVisibility(View.VISIBLE);
            binding.tvExtraCharge.setText("+ ZMW " + service.getVariableCharge() + "/" + service.getUnitOfMeasure());
        } else {
            binding.tvExtraCharge.setVisibility(View.GONE);
        }
        binding.tvSelectedService.setText(service.getServiceName());
    }
}