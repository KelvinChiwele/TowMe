package com.techart.towme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.towme.constants.Constants;
import com.techart.towme.constants.FireBaseUtils;
import com.techart.towme.databinding.FragmentFirstBinding;
import com.techart.towme.model.OrderUrl;

import java.util.HashMap;
import java.util.Map;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private Map<String, String> vehicleDetails;
    private String orderUrl;
    private EditText etQuantity;
    private String quantity;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        loadUrl();

        vehicleDetails = new HashMap<>();
        populateDropDown(binding.spinnerMake, "make", getResources().getStringArray(R.array.make));

        populateDropDown(binding.spinnerYear, "year", getResources().getStringArray(R.array.year));

        populateDropDown(binding.spinnerColor, "color", getResources().getStringArray(R.array.color));
        return binding.getRoot();
    }

    private void populateDropDown(Spinner spinner, String fieldType, final String[] data) {
        ArrayAdapter<String> issuesAdapter = new ArrayAdapter<>(getContext(), R.layout.tv_dropdown, data);
        issuesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issuesAdapter.notifyDataSetChanged();

        spinner.setAdapter(issuesAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleDetails.put(fieldType, data[position]);

                if ("make".equals(fieldType)){
                    binding.spinnerModel.setVisibility(View.VISIBLE);
                    binding.spinnerYear.setVisibility(View.VISIBLE);
                    binding.spinnerColor.setVisibility(View.VISIBLE);
                }

                if ("make".equals(fieldType) && vehicleDetails.containsValue("BMW") ){
                    populateCarDropDown(binding.spinnerModel, "model", getResources().getStringArray(R.array.modelBMW));
                } else if (!data[position].contains("Select") && "make".equals(fieldType)){
                    populateCarDropDown(binding.spinnerModel, "model", getResources().getStringArray(R.array.modelToyota));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void populateCarDropDown(Spinner spinner, String fieldType, final String[] data) {
        ArrayAdapter<String> issuesAdapter = new ArrayAdapter<>(getContext(), R.layout.tv_dropdown, data);
        issuesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issuesAdapter.notifyDataSetChanged();

        spinner.setAdapter(issuesAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleDetails.put(fieldType, data[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 -> {
            if (orderUrl != null) {
                updateOrder();
                postOrder();
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            } else {
                Toast.makeText(getActivity(),
                        "Processing. Please try after 10 seconds. ",
                        Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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


    private void postOrder() {
        vehicleDetails.put(Constants.ORDER_URL, orderUrl);
        FireBaseUtils.mDatabaseVehicle.child(orderUrl).setValue(vehicleDetails);
    }

    private void updateOrder() {
        etQuantity = binding.editTextQuantity;
        quantity = etQuantity.getText().toString();
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.QUANTITY, Double.parseDouble(quantity));
        values.put(Constants.AMOUNT_TO_PAY, Double.parseDouble(quantity));
        FireBaseUtils.mDatabaseOrder.child(orderUrl).updateChildren(values);
    }
}