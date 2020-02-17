package com.example.bookingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    Response.Listener<JSONObject> volleyListener;
    Response.ErrorListener volleyErrorListener;
    final String SIGN_IN_URL ="http://192.168.43.139:8888/Business.php";

    TextView diamondText;
    ImageView diamondImage;

    EditText emailAddressEditText;
    EditText passwordEditText;
    Button signInButton;

    TextView errorText;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser!=null){
            successfulSignIn("Token Firebase");
        }




        diamondImage =findViewById(R.id.diamondImageView);
        diamondText =findViewById(R.id.diamondTextView);
        errorText =findViewById(R.id.ErrorTextView_SignInActivity);
        progressBar=findViewById(R.id.progressBar_SignInActicity);
        signInButton =findViewById(R.id.signIn);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        volleyListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("VolleyReceived","Volley Received SignIn Activity "+response.toString());
                if(response.has("SignIn")){
                    try {
                        if(response.getJSONObject("SignIn").getString("Successful").equals("True")){
                            successfulSignIn(response.getJSONObject("SignIn").getString("Token"));
                        }else if(response.getJSONObject("SignIn").getString("Successful").equals("False")){
                            notSuccessful();
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

                    errorText.setText("Couldn't Reach The Server Please Check Your Internet Connection");
                    errorText.setVisibility(View.VISIBLE);


                }
                else{
                    Log.v("VolleyError","VolleyError in SignUpActivity "+error.toString());

                    errorText.setText("Connection Timed Out");
                    errorText.setVisibility(View.VISIBLE);


                }

                turnOffProgressBar();
            }
        };
        requestQueue= Volley.newRequestQueue(this);
        emailAddressEditText=findViewById(R.id.signInEmail);
        passwordEditText=findViewById(R.id.signInPassword);


    }
void turnOnProgressBar(){
    progressBar.setVisibility(View.VISIBLE);


    diamondText.setVisibility(View.GONE);
    diamondImage.setVisibility(View.GONE);
    emailAddressEditText.setVisibility(View.GONE);
    passwordEditText.setVisibility(View.GONE);
    signInButton.setVisibility(View.GONE);
    errorText.setVisibility(View.GONE);

}
void turnOffProgressBar(){

    progressBar.setVisibility(View.GONE);

    diamondText.setVisibility(View.VISIBLE);
    diamondImage.setVisibility(View.VISIBLE);
    emailAddressEditText.setVisibility(View.VISIBLE);
    passwordEditText.setVisibility(View.VISIBLE);
    signInButton.setVisibility(View.VISIBLE);

}
    void signIn(){

        String email;
        email=emailAddressEditText.getText().toString();
        String password;
        password=passwordEditText.getText().toString();


        Map<String,Object> map=new HashMap<>();
        map.put("Request","SignIn");
        map.put("EmailAddress", email);
        map.put("Password",password);

        JSONObject data=new JSONObject(map);

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, SIGN_IN_URL, data, volleyListener, volleyErrorListener);
      //  requestQueue.add(request);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    successfulSignIn("Token FireBase");
                }else{
                    notSuccessful();
                }
            }
        });
        turnOnProgressBar();
    }

    void successfulSignIn(String TokenReceived){

        Intent goToShopActivityIntent=new Intent(this, ShopActivity.class);
        goToShopActivityIntent.putExtra("Token", TokenReceived);
        startActivity(goToShopActivityIntent);
    }
    void notSuccessful(){
        turnOffProgressBar();
        errorText.setText("Email Address or Password is Incorrect");
        errorText.setVisibility(View.VISIBLE);

    }
}
