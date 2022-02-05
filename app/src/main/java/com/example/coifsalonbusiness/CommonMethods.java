package com.example.coifsalonbusiness;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coifsalonbusiness.shop.ShopActivity;
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
