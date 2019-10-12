package com.example.bookingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddRemovePortfolioImages_SubActivity_ShopActivity extends AppCompatActivity {

    final int IMG_REQ=10;
    ArrayList<Bitmap> ImagesLoadedFromMediaStore=new ArrayList<>();
    CustomRecyclerVAdapterPortfolioImages customRecyclerVAdapterPortfolioImages;
    RecyclerView PortfolioImagesRecyclerView;
    Button AddImages;

    static Response.Listener<JSONObject> VolleyListener;
    static Response.ErrorListener VolleyErrorListener;
    static RequestQueue RequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_portfolio_images__sub__shop);
        customRecyclerVAdapterPortfolioImages=new CustomRecyclerVAdapterPortfolioImages(ImagesLoadedFromMediaStore, this);
        PortfolioImagesRecyclerView=findViewById(R.id.RecyclerView_PortfolioImages);
        PortfolioImagesRecyclerView.setAdapter(customRecyclerVAdapterPortfolioImages);
        PortfolioImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        AddImages=findViewById(R.id.AddPortfolioImages_AddRemoveSubActivity);
        AddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetImages();
            }
        });
        VolleyListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        };
        VolleyErrorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
        RequestQueue=Volley.newRequestQueue(this);
    }
void GetImages(){
    Intent GetImagesIntent=new Intent();
    GetImagesIntent.setType("image/*");
    GetImagesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    GetImagesIntent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(GetImagesIntent, IMG_REQ);
    Log.v("VolleyReceived", "Up ToGet Images");
}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQ && resultCode==RESULT_OK && data!=null){
            if(data.getClipData() != null) {

                int count = data.getClipData().getItemCount();
                for(int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    try {
                       PushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
               UpdateTheRecyclerView();//the update should happen when we receive them from server

            }
        } else if(data.getData() != null) {

            String imagePath = data.getData().getPath();

            try {
                PushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imagePath)));

            } catch (IOException e) {
                e.printStackTrace();
            }
            UpdateTheRecyclerView();//the update should happen when we receive them from server
        }

    }
    void UpdateTheRecyclerView(){
        //the update should happen when we receive them from server
        customRecyclerVAdapterPortfolioImages=new CustomRecyclerVAdapterPortfolioImages(ImagesLoadedFromMediaStore, this);
        PortfolioImagesRecyclerView.swapAdapter(customRecyclerVAdapterPortfolioImages, true);
    }
    public static void RemoveImageFromServer(int Index){
        Map<String,Object> map=new HashMap<>();
        map.put("Request","RemovePortfolioImage");
        map.put("ImageLink","Put The URL of Image To Be Removed Here");
        JSONObject Data=new JSONObject(map);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(, Data, VolleyListener, VolleyErrorListener);
        RequestQueue.add(jsonObjectRequest);
    }

    public static void PushImageToServer(Bitmap ImageToBeAdded){
        Map<String,Object> map=new HashMap<>();
        map.put("Request","AddPortfolioImage");
        map.put("ImageAsString",ConvertBitmapToString(ImageToBeAdded));
        JSONObject Data=new JSONObject(map);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(, Data, VolleyListener, VolleyErrorListener);
        RequestQueue.add(jsonObjectRequest);
    }
   static String ConvertBitmapToString(Bitmap image){

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP,85,byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
}
