package com.techart.towme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btStartOrderProcess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        btStartOrderProcess = findViewById(R.id.bt_start_order);

        btStartOrderProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderActivity = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(orderActivity);

            }
        });
    }
}