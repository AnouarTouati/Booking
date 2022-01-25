package com.example.bookingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;

import com.example.bookingapp.shop.ShopActivity;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public  class CommonMethods {



    static final int KILL_ACTIVITY_REQ=12;
    static String convertBitmapToString(Bitmap image){

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP,85,byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    static Bitmap convertStringToBitmap(String image) {

        byte[] bytes;
        bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


   public static void successfulSignIn(Context mContext, FirebaseUser firebaseUser, AppCompatActivity callingClass){

        Intent goToShopActivityIntent=new Intent(mContext, ShopActivity.class);
        goToShopActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        ShopActivity.setFirebaseUser(firebaseUser);
        mContext.startActivity(goToShopActivityIntent);
        callingClass.finish();
    }
}
