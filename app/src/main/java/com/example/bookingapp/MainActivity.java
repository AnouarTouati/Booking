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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final String URL = "http://www.dztraintrack.tk/index.php";
    ArrayList<Image> ShopsMainImages = new ArrayList<>();
    ArrayList<String> ShopsNames = new ArrayList<>();
    ArrayList<String> ShopsAddresses = new ArrayList<>();

    Response.Listener<String> volleyStringListener;
    Response.ErrorListener volleyErrorListener;

    private final int IMG_REQ=1;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
Button ChooseImageButtong=findViewById(R.id.chooseImage);
ChooseImageButtong.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        SelectAnImage();
    }
});

Button UpLoadImageButton=findViewById(R.id.upLoadImage);
UpLoadImageButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        UploadImage();
    }
});

        volleyStringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ServerResponse(response);
            }
        };
volleyErrorListener=new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
ServerResponseError(error.toString());
    }
};
        ShopsNames.add("FirstShop");
        ShopsNames.add("SecondShop");
        ShopsNames.add("ThirdShop");
        ShopsNames.add("FourthShop");
        ShopsNames.add("FifthShop");

        ShopsAddresses.add("Sahel");
        ShopsAddresses.add("SidiDaoud");
        ShopsAddresses.add("Dellys");
        ShopsAddresses.add("Boumerdes");
        ShopsAddresses.add("BabEzzouar");


        RecyclerView recyclerView = findViewById(R.id.recyclerViewResult);
        CustomRecyclerViewAdapter customRecyclerViewAdapter = new CustomRecyclerViewAdapter(this, ShopsMainImages, ShopsNames, ShopsAddresses);
        recyclerView.setAdapter(customRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Map<String,String> data=new HashMap<>();
        data.put("Message", "Hello There");
        PostToServer(data);
    }

    private void PostToServer(Map<String, String> DataToSend) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringCommunication stringCommunication = new StringCommunication(Request.Method.POST, URL, DataToSend, volleyStringListener, volleyErrorListener);
        requestQueue.add(stringCommunication);
    }
    private void ServerResponse(String ReceivedData) {
        try {
            JSONObject jsonResponse = new JSONObject(ReceivedData);
            if(jsonResponse.has("Message")){
                Toast.makeText(this, jsonResponse.getString("Message"), Toast.LENGTH_LONG).show();
            }

            if(jsonResponse.has("Image")){
             ShowImage(jsonResponse.getString("Image"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void ServerResponseError(String error){
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
    public void SelectAnImage(){
Intent ChooseImageIntent=new Intent();
ChooseImageIntent.setType("image/*");
ChooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
startActivityForResult(ChooseImageIntent,IMG_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQ && resultCode==RESULT_OK && data!=null){
            Uri path=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), path);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void UploadImage(){
        Map<String,String> data=new HashMap<>();
        String ImageString=BitmapToString(bitmap);
        data.put("Image",ImageString);
        PostToServer(data);
    }
    private String BitmapToString(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, byteArrayOutputStream);
        byte[] byteImage=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteImage, Base64.DEFAULT);
    }
    private Bitmap StringToBitmap(String ImageString){

     byte[] byteImage=Base64.decode(ImageString, Base64.DEFAULT);
    Bitmap bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
    return bitmap;
    }
    private void ShowImage(String ImageString){

        ImageView imageView=findViewById(R.id.imageView);
        imageView.setImageBitmap(StringToBitmap(ImageString));
        imageView.setVisibility(View.VISIBLE);
    }
}
