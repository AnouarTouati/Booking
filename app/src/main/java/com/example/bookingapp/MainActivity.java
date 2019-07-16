package com.example.bookingapp;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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
            Toast.makeText(this, jsonResponse.getString("Message"), Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void ServerResponseError(String error){
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
