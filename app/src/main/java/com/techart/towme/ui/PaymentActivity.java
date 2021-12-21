package com.techart.towme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.techart.towme.R;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        double amount = getIntent().getExtras().getDouble("amount");

        Button pay = findViewById(R.id.pay);
        TextView textViewAmount = findViewById(R.id.textViewAmount);
        textViewAmount.setText("ZMW " + amount);

        pay.setOnClickListener(view -> makePayment(amount));
    }

    private void makePayment(double amount) {
        new RavePayManager(this)
                .setAmount(amount)
                .setEmail("techartsolz@gmail.com")
                .setCountry("ZM")
                .setCurrency("ZMW")
                .setfName("Kelvin")
                .setlName("Chiwele")
                .setNarration("Fuel")
                .setPublicKey("FLWPUBK_TEST-44076338f78fc7d14b73aa970cfd86d6-X")
                .setEncryptionKey("FLWSECK_TEST7d7ccd9230f5")
                .setTxRef(System.currentTimeMillis() + "Ref")
                .acceptZmMobileMoneyPayments(true)
                .onStagingEnv(true)
                .shouldDisplayFee(true)
                .showStagingLabel(true)
                .initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_LONG).show();
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_LONG).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_LONG).show();
            }
        }
    }
}