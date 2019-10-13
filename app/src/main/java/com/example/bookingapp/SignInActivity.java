package com.example.bookingapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.SignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    Response.Listener<JSONObject> volleyListener;
    Response.ErrorListener volleyErrorListener;
    final String SignInURL="http://192.168.43.139:81/Business.php";

    TextView DiamondText;
    ImageView DiamondImage;

    EditText emailAddressEditText;
    EditText passwordEditText;
    Button SignInButton;

    TextView ErrorText;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        DiamondImage=findViewById(R.id.diamondImageView);
        DiamondText=findViewById(R.id.diamondTextView);
        ErrorText=findViewById(R.id.ErrorTextView_SignInActivity);
        progressBar=findViewById(R.id.progressBar_SignInActicity);
        SignInButton=findViewById(R.id.signIn);

        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

        volleyListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("VolleyReceived","Volley Received SignIn Activity "+response.toString());
                if(response.has("SignIn")){
                    try {
                        if(response.getJSONObject("SignIn").getString("Successful").equals("true")){
                            SuccessfulSignIn(response.getJSONObject("SignIn").getString("Token"));
                        }else if(response.getJSONObject("SignIn").getString("Successful").equals("false")){
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


                if(error.networkResponse==null){

                    ErrorText.setText("Couldn't Reach The Server Please Check Your Internet Connection");
                    ErrorText.setVisibility(View.VISIBLE);


                }
                else{
                    Log.v("VolleyError","VolleyError in SignUpActivity "+error.toString());

                    ErrorText.setText("Connection Timed Out");
                    ErrorText.setVisibility(View.VISIBLE);


                }

                TurnOffProgressBar();
            }
        };
        requestQueue= Volley.newRequestQueue(this);
        emailAddressEditText=findViewById(R.id.signInEmail);
        passwordEditText=findViewById(R.id.signInPassword);


    }
void TurnOnProgressBar(){
    progressBar.setVisibility(View.VISIBLE);


    DiamondText.setVisibility(View.GONE);
    DiamondImage.setVisibility(View.GONE);
    emailAddressEditText.setVisibility(View.GONE);
    passwordEditText.setVisibility(View.GONE);
    SignInButton.setVisibility(View.GONE);
    ErrorText.setVisibility(View.GONE);

}
void TurnOffProgressBar(){

    progressBar.setVisibility(View.GONE);

    DiamondText.setVisibility(View.VISIBLE);
    DiamondImage.setVisibility(View.VISIBLE);
    emailAddressEditText.setVisibility(View.VISIBLE);
    passwordEditText.setVisibility(View.VISIBLE);
    SignInButton.setVisibility(View.VISIBLE);

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
        TurnOnProgressBar();
    }

    void SuccessfulSignIn(String TokenReceived){

        Intent goToShopActivityIntent=new Intent(this, ShopActivity.class);
        goToShopActivityIntent.putExtra("Token", TokenReceived);
        startActivity(goToShopActivityIntent);
    }
    void  NotSuccessful(){
        TurnOffProgressBar();
        ErrorText.setText("Email Address or Password is Incorrect");
        ErrorText.setVisibility(View.VISIBLE);

    }
}
