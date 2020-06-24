package com.bigbang.supercoollocation.ui.view;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigbang.supercoollocation.R;
import com.bigbang.supercoollocation.model.data.LocationDetails;
import com.bigbang.supercoollocation.viewmodel.LocationViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;


    private final int PERMISSION_REQUEST = 101;

    private TextView locationPlaceHolder;
    private Button openSettingsButton;

    private LocationManager locationManager;

    private Observer<LocationDetails> locationDetailsObserver;
    private LatLng currentLocation;
    private LocationViewModel locationViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationDetailsObserver = locationDetails -> {
            Log.d("TAG_X", "LOC : " + locationDetails.toString());
            locationPlaceHolder.setText(locationDetails.getResults().get(0).getFormattedAddress() + "");
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLocation).title(locationDetails.getResults().get(0).getFormattedAddress() + ""));
        };

        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationPlaceHolder = findViewById(R.id.location_textview);
        openSettingsButton = findViewById(R.id.settings_button);

        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri packageUri = Uri.fromParts("package", getPackageName(), "Permissions");
                settingsIntent.setData(packageUri);
                startActivity(settingsIntent);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onStart() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission not granted
            requestLocationPermission();
        } else {
            registerLocationListener();
        }
        super.onStart();
        Log.d("TAG_X", "ONRE");
    }

    private void requestLocationPermission() {
        //Request Permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("TAG_X", "ORPR");
        if (requestCode == PERMISSION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerLocationListener();
                    openSettingsButton.setVisibility(View.GONE);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                        requestLocationPermission();
                    else {
                        locationPlaceHolder.setText(R.string.permission_requirement);
                        openSettingsButton.setVisibility(View.VISIBLE);
                    }
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    private void registerLocationListener() {
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10,
                this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //At this point the location has changed
        locationPlaceHolder.setText("Lat : " + location.getLatitude() + " Lng : " + location.getLongitude());
        String latLong = location.getLatitude() + "," + location.getLongitude();
        locationViewModel.getLocationDetails(latLong).observe(this, locationDetailsObserver);


        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}