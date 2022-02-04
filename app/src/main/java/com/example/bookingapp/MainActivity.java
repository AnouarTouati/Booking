package com.example.bookingapp;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bookingapp.signup.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         if(FirebaseAuth.getInstance().getCurrentUser()!=null){
             finish();
             CommonMethods.successfulSignIn(getApplicationContext(),FirebaseAuth.getInstance().getCurrentUser(),MainActivity.this);
         }
       else{
             Button signInButton=findViewById(R.id.signIn);
             Button signUpButton=findViewById(R.id.signUp);

             signUpButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Intent goToSignUpActivity=new Intent(MainActivity.this, SignUpActivity.class);
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
    }

}
