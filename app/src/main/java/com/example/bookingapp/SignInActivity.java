package com.example.bookingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    Response.Listener<JSONObject> volleyListener;
    Response.ErrorListener volleyErrorListener;
    final String SignInURL="http://192.168.43.139:81/ThirdPage.php";

    EditText emailAddressEditText;
    EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        volleyListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("VolleyReceived","Volley Received SignIn Activity "+response.toString());
                if(response.has("SignIn")){
                    try {
                        if(response.getJSONObject("SignIn").getString("Successful").equals("true")){
                            SuccessfulSignIn(response.getJSONObject("SignIn").getString("Token"));
                        }else{
                            NotSuccessful();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        volleyErrorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError","Volley Error  SignIn Activity "+error.toString());
            }
        };
        requestQueue= Volley.newRequestQueue(this);
        emailAddressEditText=findViewById(R.id.signInEmail);
        passwordEditText=findViewById(R.id.signInPassword);

     Button SignInButton=findViewById(R.id.signIn);

     SignInButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             SignIn();
         }
     });
    }

    void SignIn(){

        String Email;
        Email=emailAddressEditText.getText().toString();
        String Password;
        Password=passwordEditText.getText().toString();


        Map<String,Object> map=new HashMap<>();
        map.put("Request","SignIn");
        map.put("EmailAddress", Email);
        map.put("Password",Password);

        JSONObject Data=new JSONObject(map);

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, SignInURL, Data, volleyListener, volleyErrorListener);
        requestQueue.add(request);
    }

    void SuccessfulSignIn(String TokenReceived){

        Intent goToShopActivityIntent=new Intent(this, ShopActivity.class);
        goToShopActivityIntent.putExtra("Token", TokenReceived);
        startActivity(goToShopActivityIntent);
    }
    void  NotSuccessful(){
        Toast.makeText(this, "Failed to sign in", Toast.LENGTH_LONG).show();
    }
}
