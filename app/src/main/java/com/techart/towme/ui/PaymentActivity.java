package com.techart.towme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.firebase.database.ServerValue;
import com.techart.towme.R;
import com.techart.towme.constants.Constants;
import com.techart.towme.constants.FireBaseUtils;
import com.techart.towme.enums.Status;
import com.techart.towme.model.Order;
import com.techart.towme.model.RaveResponse;

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
        orderUrl = getIntent().getStringExtra("orderUrl");

        Button pay = findViewById(R.id.pay);
        TextView textViewAmount = findViewById(R.id.textViewAmount);

        TextView textFixedAmount = findViewById(R.id.tv_fixed);
        TextView textVariable = findViewById(R.id.tv_variable);
        amountDue(order, textFixedAmount, textVariable);
        if (amountDue > 0) {
            updateOrder();
            textViewAmount.setText("ZMW " + amountDue);
            pay.setOnClickListener(view -> makePayment(amountDue));
        } else {
            textViewAmount.setTextSize(18);
            textViewAmount.setTextColor(getResources().getColor(R.color.red));
            textViewAmount.setText("Total Amount not computed");
        }
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

    private void amountDue(Order order, TextView textFixedAmount, TextView textVariable) {
        try {
            double totalUnitCharge = order.getUnitCharge() * order.getQuantity();
            BigDecimal bigDecimal = BigDecimal.valueOf(totalUnitCharge);
            bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            totalUnitCharge = bigDecimal.doubleValue();

            amountDue = order.getFixedCharge() + totalUnitCharge;

            textFixedAmount.setText("Fixed Charge\r\t\t-\r\t\t\t\t" + order.getFixedCharge() + " * 1    \r\t\t= ZMW " + order.getFixedCharge());
            textVariable.setText("Unit Charge  \r\t\t-\r\t\t\t\t" + order.getUnitCharge() + " * " + order.getQuantity() + "\r\t\t= ZMW " + amountDue);
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
        values.put(Constants.STATUS, Status.PENDING_DELIVERY);
        FireBaseUtils.mDatabaseOrder.child(orderUrl).setValue(values);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            RaveResponse raveResponse;
            ObjectMapper mapper = new ObjectMapper();
            Log.d("MESSAGE", "Message ==========>" + message);
            try {
                if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                    raveResponse = mapper.readValue(message, RaveResponse.class);
                    Log.d("RESULT_SUCCESS", "Message ==========>" + raveResponse);
                    postConfirmOrder();
                } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                    raveResponse = mapper.readValue(message, RaveResponse.class);
                    Log.d("RESULT_ERROR", "Message ==========>" + raveResponse);
                } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                    raveResponse = mapper.readValue(message, RaveResponse.class);
                    Log.d("RESULT_CANCELLED", "Message ==========>" + raveResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}