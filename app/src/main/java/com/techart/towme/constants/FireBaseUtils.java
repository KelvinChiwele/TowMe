package com.techart.towme.constants;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Has constants for Fire base variable names
 * Created by Kelvin on 11/09/2017.
 */

public final class FireBaseUtils {

    public static String status;
    static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static final DatabaseReference mDatabaseUsers = database.child(Constants.USERS_KEY);
    public static final DatabaseReference mDatabaseVehicle = database.child(Constants.VEHICLE_KEY);
    public static final DatabaseReference mDatabaseOrderUrl = database.child(Constants.ORDER_URL_KEY);
    public static final DatabaseReference mDatabaseMotorist = database.child(Constants.MOTORIST_KEY);
    public static final DatabaseReference mDatabaseOrder = database.child(Constants.ORDER_KEY);
    public static final DatabaseReference mDatabaseOrderDetails = database.child(Constants.ORDER_DETAILS_KEY);
    public static final DatabaseReference mDatabaseTransaction = database.child(Constants.TRANSACTION_KEY);


    private FireBaseUtils() {

    }


    @NonNull
    public static String getEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }


    @NonNull
    public static String getAuthor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getDisplayName(); //(a > b) ? a : b
    }

    @NonNull
    public static String getUiD() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }


    public static void deleteChapter(final String post_key) {
        mDatabaseVehicle.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(post_key)) {
                    mDatabaseVehicle.child(post_key).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
