package com.example.bookingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;

import com.example.bookingapp.shop.ShopActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public  class CommonMethods {



    public static final int KILL_ACTIVITY_REQ=12;
    public static final int LOCATION_REQ = 10;
   public static void successfulSignIn(Context mContext, FirebaseUser firebaseUser, AppCompatActivity callingClass){

        Intent goToShopActivityIntent=new Intent(mContext, ShopActivity.class);
        goToShopActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        ShopActivity.setFirebaseUser(firebaseUser);
        mContext.startActivity(goToShopActivityIntent);
        callingClass.finish();
    }
    @SuppressLint("MissingPermission")
    public void findLocationUsingGPS(Context mContext, FusedLocationProviderClient fusedLocationProviderClient, LocationCallback activityToCallback) {

        if (hasPermissionToAccessLocationData(mContext)) {

            LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                alertBuilder.setMessage("In the Next Screen Allow this Application to Use Location Services");
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                        activityToCallback.comingBackFromLocationSettings = true;
                    }
                });
                alertBuilder.show();

            } else {

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setExpirationDuration(40000);
                locationRequest.setNumUpdates(1);

                com.google.android.gms.location.LocationCallback locationCallback = new com.google.android.gms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        activityToCallback.locationResultsCallback(locationResult.getLastLocation());
                    }
                };

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        }
        else {
            askForPermissionToAccessLocationData(mContext);
        }

    }
    private Boolean hasPermissionToAccessLocationData(Context mContext){
        return (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED);
    }
    private void askForPermissionToAccessLocationData(Context mContext){
        ActivityCompat.requestPermissions(
                (Activity) mContext,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET},LOCATION_REQ);
    }

}
