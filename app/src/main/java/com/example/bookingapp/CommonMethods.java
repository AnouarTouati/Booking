package com.example.bookingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Base64;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.Context.MODE_PRIVATE;
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

    static String loadJSONFile(String jsonFileName,Context context) {
        String[] mFileList = context.fileList();
        Boolean fileExists = false;

        for (int i = 0; i < mFileList.length; i++) {
            if (jsonFileName.equals(mFileList[i])) {
                fileExists = true;
                break;
            }
        }
        if (fileExists) {
            String jsonAsString = null;

            try {
                FileInputStream fis = context.openFileInput(jsonFileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String text;
                while ((text = br.readLine()) != null) {
                    sb.append(text);
                }
                jsonAsString = sb.toString();

                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonAsString;
        } else {

            FileOutputStream fos = null;
            JSONObject emptyJSONObject = new JSONObject();
            try {

                fos = context.openFileOutput("Data.txt", MODE_PRIVATE);
                fos.write(emptyJSONObject.toString().getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            return emptyJSONObject.toString();
        }
    }

   public static void successfulSignIn(Context mContext, FirebaseUser firebaseUser, AppCompatActivity callingClass){

        Intent goToShopActivityIntent=new Intent(mContext,ShopActivity.class);
        goToShopActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        ShopActivity.setFirebaseUser(firebaseUser);
        mContext.startActivity(goToShopActivityIntent);
        callingClass.finish();
    }
}
