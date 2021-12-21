package com.techart.towme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.techart.towme.constants.Constants;
import com.techart.towme.constants.FireBaseUtils;
import com.techart.towme.databinding.FragmentFirstBinding;

import java.util.HashMap;
import java.util.Map;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private Map<String, String> vehicleDetails;
    private String orderUrl;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        orderUrl = sharedPreferences.getString("orderUrl", null);

        Toast.makeText(getContext(), orderUrl, Toast.LENGTH_LONG).show();

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
//                if ("make".equals(fieldType)){
//                    binding.spinnerModel.setVisibility(View.VISIBLE);
//                    binding.spinnerYear.setVisibility(View.VISIBLE);
//                    binding.spinnerColor.setVisibility(View.VISIBLE);
//                }
//
//                if ("make".equals(fieldType) && vehicleDetails.containsValue("BMW") ){
//                    populateDropDown(binding.spinnerModel, "model", getResources().getStringArray(R.array.modelBMW));
//                } else if (!data[position].contains("Select")){
//                    populateDropDown(binding.spinnerModel, "model", getResources().getStringArray(R.array.modelToyota));
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void postOrder() {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.MAKE, "BWM");
        values.put(Constants.MODEL, "3 Series");
        values.put(Constants.YEAR, "2010");
        values.put(Constants.COLOR, "Grey");
        values.put(Constants.ORDER_URL, 0);
        FireBaseUtils.mDatabaseOrder.child(orderUrl).setValue(values);
    }

    private void updateOrder(String quantity, String orderUrl) {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.QUANTITY, quantity);
        FireBaseUtils.mDatabaseOrder.child(orderUrl).updateChildren(values);
    }
}