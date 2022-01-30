package com.techart.towmekiz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.techart.towmekiz.constants.Constants;
import com.techart.towmekiz.constants.FireBaseUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    private final LocationListener mLocationListener = location -> {

    };
    int LOCATION_REFRESH_TIME = 15000; // 15 seconds to update
    int LOCATION_REFRESH_DISTANCE = 500; // 500 meters to update

    private GoogleMap mMap;
    private LatLng lastLocation = new LatLng(-15.6026746, 28.3380676);
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FloatingActionButton fab;
    private boolean isFindMe;
    private List<Address> addresses;
    LocationManager mLocationManager;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        isFindMe = getIntent().getBooleanExtra(Constants.IS_FIND_ME, false);
        String orderUrl = getIntent().getStringExtra("orderUrl");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            updateOrder(orderUrl);
            Intent orderActivity = new Intent(MapsActivity.this, DetailsActivity.class);
            orderActivity.putExtra("orderUrl", orderUrl);
            Log.e("MapsActivity", orderUrl);
            startActivity(orderActivity);
            finish();
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        mMap = googleMap;

        if (!isFindMe) {
            // Add a marker in Sydney and move the camera
            LatLng lusaka = new LatLng(-15.6026746, 28.3380676);

            mMap.addMarker(new MarkerOptions()
                    .position(lusaka)
                    .title("Marker in Lusaka")
                    .draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lusaka));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(8));


            mMap.setOnMarkerClickListener(this);


            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(@NotNull Marker arg0) {
                }

                @Override
                public void onMarkerDragEnd(@NotNull Marker arg0) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                    lastLocation = arg0.getPosition();
                }

                @Override
                public void onMarkerDrag(@NotNull Marker arg0) {
                }
            });
        } else {
            getPermission();
        }
    }


    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    private void getPermission() {
        //get location
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);

            getLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
            Toast.makeText(this,
                    "Denied ",
                    Toast.LENGTH_LONG).show();
        }
    }


    private void getLocation() {
        //get location
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(lastLocation));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(8));

                        locationDetails();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
            Toast.makeText(this,
                    "Denied ",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            onPermissionDenied();
        }
    }

    private void locationDetails() {
        DialogInterface.OnClickListener dialogClickListener =
                (dialog, button) -> {
                    if (button == DialogInterface.BUTTON_POSITIVE) {
                    }
                    if (button == DialogInterface.BUTTON_NEGATIVE) {
                        dialog.dismiss();
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CONFIRM YOUR LOCATION!")
                .setMessage("You are located in " + addresses.get(0).getCountryName() + " " + addresses.get(0).getLocality() + " " + addresses.get(0).getFeatureName() + " " + addresses.get(0).getAdminArea())
                .setPositiveButton("CONFIRM", dialogClickListener)
                .setNegativeButton("DECLINE", dialogClickListener)
                .setCancelable(false)
                .show();
    }

    private void onPermissionDenied() {
        DialogInterface.OnClickListener dialogClickListener =
                (dialog, button) -> {
                    if (button == DialogInterface.BUTTON_POSITIVE) {
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
                    }
                    if (button == DialogInterface.BUTTON_NEGATIVE) {
                        dialog.dismiss();
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("YOU NEED TO ALLOW ACCESS TO YOUR LOCATION")
                .setMessage("Without this permission your location will not be accessed")
                .setPositiveButton("ALLOW", dialogClickListener)
                .setNegativeButton("DENY", dialogClickListener)
                .show();
    }

    private void updateOrder(String orderUrl) {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.LATITUDE, lastLocation.latitude);
        values.put(Constants.LONGITUDE, lastLocation.longitude);
        FireBaseUtils.mDatabaseOrder.child(orderUrl).updateChildren(values);
    }
}