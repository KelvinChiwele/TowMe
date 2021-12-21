package com.techart.towme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.techart.towme.constants.Constants;
import com.techart.towme.constants.FireBaseUtils;
import com.techart.towme.databinding.FragmentSecondBinding;

import java.util.HashMap;
import java.util.Map;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private String orderUrl;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        orderUrl = sharedPreferences.getString("orderUrl", null);

        Toast.makeText(getContext(), orderUrl, Toast.LENGTH_LONG).show();


        binding = FragmentSecondBinding.inflate(inflater, container, false);


        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 -> {
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            Intent orderActivity = new Intent(getActivity(), SummaryActivity.class);
            orderActivity.putExtra("amount", 15.0);
            startActivity(orderActivity);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void postOrder() {
        Map<String, Object> values = new HashMap<>();
//        values.put(Constants.MAKE, order.getFixedCharge());
//        values.put(Constants.MODEL, order.getTotalUnitCharge());
//        values.put(Constants.YEAR, order);
//        values.put(Constants.COLOR, order.getFixedChargeNarration());
        values.put(Constants.ORDER_URL, orderUrl);
        FireBaseUtils.mDatabaseOrder.child(orderUrl).setValue(values);
    }


}