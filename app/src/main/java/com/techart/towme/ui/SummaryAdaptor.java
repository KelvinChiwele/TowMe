package com.techart.towme.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.techart.towme.R;
import com.techart.towme.model.SummaryItem;

import java.util.ArrayList;

public class SummaryAdaptor extends ArrayAdapter<SummaryItem> {
    public SummaryAdaptor(Context context, ArrayList<SummaryItem> summaryItems) {
        super(context, 0, summaryItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SummaryItem summaryItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_summary_item, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.titleView);
        TextView tvDesc = (TextView) convertView.findViewById(R.id.descView);
        // Populate the data into the template view using the data object
        tvTitle.setText(summaryItem.getTitle());
        tvDesc.setText(summaryItem.getDescription());
        // Return the completed view to render on screen
        return convertView;
    }
}
