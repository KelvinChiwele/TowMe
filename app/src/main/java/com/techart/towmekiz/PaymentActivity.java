package com.techart.towmekiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.firebase.database.ServerValue;
import com.techart.towmekiz.constants.Constants;
import com.techart.towmekiz.constants.FireBaseUtils;
import com.techart.towmekiz.enums.Answer;
import com.techart.towmekiz.enums.Status;
import com.techart.towmekiz.model.Order;
import com.techart.towmekiz.model.Profile;
import com.techart.towmekiz.model.RaveResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    double amountDue;
    private String orderUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Order order = (Order) getIntent().getSerializableExtra("order");
        Profile profile = (Profile) getIntent().getSerializableExtra("profile");
        orderUrl = getIntent().getStringExtra("orderUrl");

        Button pay = findViewById(R.id.pay);
        TextView textViewAmount = findViewById(R.id.textViewAmount);

        TextView textFixedAmount = findViewById(R.id.tv_fixed);
        TextView textVariable = findViewById(R.id.tv_variable);
        Button buttonHome = findViewById(R.id.bt_home);
        amountDue(order, textFixedAmount, textVariable);
        if (amountDue > 0) {
            updateOrder();
            textViewAmount.setText("ZMW " + amountDue);
            pay.setOnClickListener(view -> makePayment(amountDue, profile, order.getService()));
        } else {
            textViewAmount.setTextSize(18);
            textViewAmount.setTextColor(getResources().getColor(R.color.red));
            textViewAmount.setText("Total Amount not computed");
        }

        buttonHome.setOnClickListener(view -> {
            Intent orderActivity = new Intent(PaymentActivity.this, MainActivity.class);
            startActivity(orderActivity);
        });
    }

    private void makePayment(double amount, Profile profile, String narration) {
        new RavePayManager(this)
                .setAmount(amount)
                .setEmail(profile.getEmail())
                .setCountry("ZM")
                .setCurrency("ZMW")
                .setfName(profile.getFirstName())
                .setlName(profile.getLastName())
                .setNarration("Been payment for " + narration)
                .setPublicKey("FLWPUBK-52aa01a14028a2ea8496ddc5f4a662f8-X")
                .setEncryptionKey("6cae781ca522e969b5619e3a")
                .setTxRef("Txn ID: " + System.currentTimeMillis())
                .setPhoneNumber(profile.getPhoneNumber())
                .acceptZmMobileMoneyPayments(true)
                .withTheme(R.style.MyCustomTheme)
                .shouldDisplayFee(true)
                .onStagingEnv(false)
                .initialize();
    }

    private void amountDue(Order order, TextView textFixedAmount, TextView textVariable) {
        try {
            textFixedAmount.setText("Fixed Charge\r\t\t-\r\t\t\t\t" + order.getFixedCharge() + " * 1    \r\t\t= ZMW " + order.getFixedCharge());
            if (Answer.YES.toString().equals(order.getNeedsQuantity())) {
                amountDue = order.getFixedCharge();
                return;
            }
            double totalUnitCharge = order.getUnitCharge() * order.getQuantity();
            BigDecimal bigDecimal = BigDecimal.valueOf(totalUnitCharge);
            bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            totalUnitCharge = bigDecimal.doubleValue();

            amountDue = order.getFixedCharge() + totalUnitCharge;
            textVariable.setText("Unit Charge  \r\t\t-\r\t\t\t\t" + order.getUnitCharge() + " * " + order.getQuantity() + "\r\t\t= ZMW " + totalUnitCharge);

        } catch (Exception e) {
            amountDue = 0;
        }
    }

    private void updateOrder() {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.AMOUNT_TO_PAY, amountDue);
        FireBaseUtils.mDatabaseOrder.child(orderUrl).updateChildren(values);
    }

    private void postConfirmOrder() {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.AMOUNT_PAID, amountDue);
        values.put(Constants.TRANSACTION_TIME, ServerValue.TIMESTAMP);
        values.put(Constants.ORDER_URL, orderUrl);
        values.put(Constants.STATUS, Status.PAID);
        FireBaseUtils.mDatabaseOrder.child(orderUrl).updateChildren(values);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            RaveResponse raveResponse;
            ObjectMapper mapper = new ObjectMapper();
            try {
                if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                    postConfirmOrder();
//                    raveResponse = mapper.readValue(message, RaveResponse.class);
//                    Log.d("RESULT_SUCCESS", "Message ==========>" + raveResponse.toString());
                    Intent home = new Intent(PaymentActivity.this, MainActivity.class);
                    startActivity(home);

                } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                    raveResponse = mapper.readValue(message, RaveResponse.class);
                    Toast.makeText(this, raveResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                    raveResponse = mapper.readValue(message, RaveResponse.class);
                    Toast.makeText(this, raveResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Transaction could not be completed", Toast.LENGTH_LONG).show();
        }
    }
}