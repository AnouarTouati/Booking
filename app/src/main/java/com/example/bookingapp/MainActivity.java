package com.example.bookingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.audiofx.DynamicsProcessing;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    static final String URL = "http://www.dztraintrack.tk/index.php";
    ArrayList<Image> ShopsMainImages = new ArrayList<>();
    ArrayList<String> ShopsNames = new ArrayList<>();
    ArrayList<String> ShopsAddresses = new ArrayList<>();

    Response.Listener<String> volleyStringListener;
    Response.ErrorListener volleyErrorListener;
    Response.Listener<JSONObject> volleyJSONObjectListener;

    private final int IMG_REQ = 1;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volleyStringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ServerResponse(response);
            }
        };
        volleyErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ServerResponseError(error.toString());
            }
        };
        volleyJSONObjectListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               ServerResponse(response.toString());

            }
        };

        Button SignUpButton=findViewById(R.id.signUp);
        Button SignInButton=findViewById(R.id.signIn);
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSignUpActivity=new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(goToSignUpActivity);

            }
        });
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSignInAndStoreActivity=new Intent(MainActivity.this,SignInAndStore.class);
                startActivity(goToSignInAndStoreActivity);
            }
        });
Button choose=findViewById(R.id.button4);
choose.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        SelectAnImage();
    }
});
Button check=findViewById(R.id.button5);
check.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        SendImageToServer();
    }
});

    }
private void SendImageToServer(){
     String ImageString= BitmapToString(bitmap);
        JSONObject imageToSend=new JSONObject();

    try {
        imageToSend.put("Image",ImageString );
    } catch (JSONException e) {
        e.printStackTrace();
    }
    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, URL,imageToSend,volleyJSONObjectListener,volleyErrorListener);
    RequestQueue requestQueue=Volley.newRequestQueue(this);
    requestQueue.add(jsonObjectRequest);

}
    private void PostToServer(Map<String, String> DataToSend) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringCommunication stringCommunication = new StringCommunication(Request.Method.POST, URL, DataToSend, volleyStringListener, volleyErrorListener);
        requestQueue.add(stringCommunication);
    }

    private void ServerResponse(String ReceivedData) {



        try {
            JSONObject jsonResponse = new JSONObject(ReceivedData);

            if (jsonResponse.has("Message")) {
                Toast.makeText(this, jsonResponse.getString("Message"), Toast.LENGTH_LONG).show();
            }

            if (jsonResponse.has("Image")) {
                ShowImage(jsonResponse.getString("Image"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void ServerResponseError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    public void SelectAnImage() {
        Intent ChooseImageIntent = new Intent();
        ChooseImageIntent.setType("image/*");
        ChooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(ChooseImageIntent, IMG_REQ);
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


    public String BitmapToString(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
       Toast.makeText(this, "Bitmap Size is "+bitmap.getByteCount(), Toast.LENGTH_LONG).show();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 85, byteArrayOutputStream);
      byte[] byteImage=byteArrayOutputStream.toByteArray();
       Toast.makeText(this, "ByteImage Size is "+byteImage.length, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Base64 encoded Size is "+Base64.encodeToString(byteImage, Base64.DEFAULT).getBytes().length, Toast.LENGTH_LONG).show();
        return  Base64.encodeToString(byteImage, Base64.DEFAULT);
    }

    private Bitmap StringToBitmap(String ImageString) {
        byte[] imageByteArray=Base64.decode(ImageString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        return bitmap;
    }

    private void ShowImage(String ImageString) {

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(StringToBitmap(ImageString));
        imageView.setVisibility(View.VISIBLE);
    }
}
