package com.example.coifsalonbusiness;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public abstract class ActivityWithLocation extends AppCompatActivity {
    LocationManager  locationManager;

    protected abstract void onLocationResult(final Location location);
    @SuppressLint("MissingPermission")
    public void findLocationUsingGPS() {
        if(locationManager==null){
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }

        if (hasPermissionToAccessLocationData()) {

            if (isGPSEnabled()) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        onLocationResult(location);
                        locationManager.removeUpdates(this);
                    }
                });

            } else {
                askUserToEnableGPS();
            }
        }
        else {
            askForPermissionToAccessLocationData();
        }

    }
    private boolean isGPSEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    private Boolean hasPermissionToAccessLocationData(){
        return !(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }
    private void askUserToEnableGPS(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("In the Next Screen Allow this Application to Use Location Services");
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, CommonMethods.GPS_ENABLE_REQ);
            }
        });
        alertBuilder.show();

    }
    private void askForPermissionToAccessLocationData(){
        ActivityCompat.requestPermissions(
                (Activity) this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET},CommonMethods.LOCATION_REQ);
    }
    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = true;
        if (requestCode == CommonMethods.LOCATION_REQ) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    granted = false;
                    break;
                }
            }
        }
        if (granted) {
            findLocationUsingGPS();
        }
    }

    @CallSuper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CommonMethods.GPS_ENABLE_REQ){
            if(isGPSEnabled()){
                findLocationUsingGPS();
            }
        }
    }
}
