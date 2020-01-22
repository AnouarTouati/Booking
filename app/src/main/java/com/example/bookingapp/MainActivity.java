package com.example.bookingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    Response.Listener<String> volleyStringListener;
    Response.ErrorListener volleyErrorListener;


    private final int IMG_REQ = 1;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button signUpButton=findViewById(R.id.signUp);
        Button signInButton=findViewById(R.id.signIn);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSignUpActivity=new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(goToSignUpActivity);

            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSignInActivity=new Intent(MainActivity.this, SignInActivity.class);
                startActivity(goToSignInActivity);
            }
        });


    }




    public void selectAnImage() {
        Intent chooseImageIntent = new Intent();
        chooseImageIntent.setType("image/*");
        chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooseImageIntent, IMG_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





}
