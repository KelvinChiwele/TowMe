package com.techart.towme;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;

public class TowMe extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}