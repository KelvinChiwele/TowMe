package com.techart.towmekiz.ui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.techart.towmekiz.R;

import org.jetbrains.annotations.NotNull;

public final class OrderViewHolder extends RecyclerView.ViewHolder {
    public TextView tvTrip;
    public TextView tvStatus;
    public TextView tvAmount;
    public TextView tvTime;
    public Button btAccept;
    public ImageView ivImage;

    public OrderViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        tvTrip = itemView.findViewById(R.id.tv_trip);
        tvStatus = itemView.findViewById(R.id.tv_status);
        tvAmount = itemView.findViewById(R.id.tv_amount);
        tvTime = itemView.findViewById(R.id.tv_time);
        btAccept = itemView.findViewById(R.id.bt_accept);
        ivImage = itemView.findViewById(R.id.iv_type);
    }

    public void setIvImage(Context context, String image) {
        Glide.with(context)
                .load(image)
                .apply(RequestOptions.circleCropTransform())
                .into(ivImage);
    }

    public void setIvImage(Context context, int image) {
        Glide.with(context)
                .load(image)
                .apply(RequestOptions.circleCropTransform())
                .into(ivImage);
    }

    public void setBtAcceptVisibility(int visibity) {
        btAccept.setVisibility(visibity);
    }
}
