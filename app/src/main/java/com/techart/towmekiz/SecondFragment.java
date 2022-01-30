package com.techart.towmekiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.towmekiz.constants.Constants;
import com.techart.towmekiz.constants.FireBaseUtils;
import com.techart.towmekiz.databinding.FragmentSecondBinding;
import com.techart.towmekiz.enums.Status;
import com.techart.towmekiz.model.OrderUrl;

import java.util.HashMap;
import java.util.Map;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private String orderUrl;
    private final Map<String, Object> values = new HashMap<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        loadUrl();
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonFirst.setOnClickListener(view1 -> {
            postOrder();
            Intent orderActivity = new Intent(getActivity(), SummaryActivity.class);
            orderActivity.putExtra("orderUrl", orderUrl);
            startActivity(orderActivity);
        });

        binding.chipGroupItem.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip chipAnswer = view.findViewById(checkedId);
                values.put("item", chipAnswer.getText());
            }
        });

        binding.chipGroupLocation.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip chipAnswer = view.findViewById(checkedId);
                values.put("location", chipAnswer.getText());
            }
        });

        binding.chipGroupDirection.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip chipAnswer = view.findViewById(checkedId);
                values.put("direction", chipAnswer.getText());
            }
        });

        binding.chipGroupLane.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip chipAnswer = view.findViewById(checkedId);
                values.put("lane", chipAnswer.getText());
            }
        });
    }

    private void loadUrl() {
        FireBaseUtils.mDatabaseOrderUrl.child(FireBaseUtils.getUiD()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OrderUrl orderUrlObj = dataSnapshot.getValue(OrderUrl.class);
                orderUrl = orderUrlObj.getOrderUrl();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void postOrder() {
        values.put(Constants.ORDER_URL, orderUrl);
        values.put(Constants.STATUS, Status.PENDING_PAYMENT);
        FireBaseUtils.mDatabaseOrderDetails.child(orderUrl).setValue(values);
    }
}