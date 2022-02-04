package com.example.bookingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.location.Location;
import android.location.LocationListener;
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


    public static final int KILL_ACTIVITY_REQ = 12;
    public static final int LOCATION_REQ = 10;
    public static final int GPS_ENABLE_REQ= 11;
    public static final int IMG_REQ=1;

    public static void successfulSignIn(Context mContext, FirebaseUser firebaseUser, AppCompatActivity callingClass) {

        Intent goToShopActivityIntent = new Intent(mContext, ShopActivity.class);
        goToShopActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(goToShopActivityIntent);
        callingClass.finish();
    }
}
