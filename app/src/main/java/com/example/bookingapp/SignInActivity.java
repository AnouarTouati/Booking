package com.example.bookingapp;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookingapp.signup.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    TextView diamondText;
    ImageView diamondImage;

    EditText emailAddressEditText;
    EditText passwordEditText;
    Button signInButton;
    TextView signUpQuestion;

    TextView errorText;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getViewsReferences();
        setUpViews();
    }

    private void getViewsReferences(){
        signUpQuestion=findViewById(R.id.signUpQuestion);
        diamondImage =findViewById(R.id.diamondImageView);
        diamondText =findViewById(R.id.diamondTextView);
        errorText =findViewById(R.id.ErrorTextView_SignInActivity);
        progressBar=findViewById(R.id.progressBar_SignInActicity);
        signInButton =findViewById(R.id.signIn);
        emailAddressEditText=findViewById(R.id.signInEmail);
        passwordEditText=findViewById(R.id.signInPassword);
    }

    private void setUpViews(){
        signUpQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSignUpActivity=new Intent(SignInActivity.this, SignUpActivity.class);
                startActivityForResult(goToSignUpActivity, CommonMethods.KILL_ACTIVITY_REQ);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CommonMethods.KILL_ACTIVITY_REQ){
            if(resultCode==RESULT_OK){
                finish();
            }
        }
    }

    void signIn(){

        turnOnProgressBar();

        String email;
        email=emailAddressEditText.getText().toString();
        String password;
        password=passwordEditText.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   CommonMethods.successfulSignIn(getApplicationContext(),task.getResult().getUser(),SignInActivity.this);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try{
                    throw Objects.requireNonNull(e);
                }
                catch (FirebaseAuthException ee){

                    switch (ee.getErrorCode()){

                        case  "ERROR_USER_DISABLED"  : Toast.makeText(getApplicationContext(),"Your account has been disabled",Toast.LENGTH_LONG).show(); notSuccessful("Your account has been disabled");break;
                        case  "ERROR_USER_NOT_FOUND" : Toast.makeText(getApplicationContext(),"This account doesn't exist",Toast.LENGTH_LONG).show();notSuccessful("This account doesn't exist");break;
                        case  "ERROR_WRONG_PASSWORD" : Toast.makeText(getApplicationContext(),"Invalid Password",Toast.LENGTH_LONG).show();notSuccessful("Invalid Password");break;
                        default: Toast.makeText(getApplicationContext(),"Something went wrong and we couldn't sign you in",Toast.LENGTH_LONG).show();
                            Log.e("AppFilter",e.getMessage() +" the  cause is "+e.getCause() );
                            notSuccessful("Something went wrong and we couldn't sign you in");
                            break;

                    }
                }
                catch (Exception eee) {

                    Log.e("AppFilter",eee.getMessage() +" the  cause is "+eee.getCause() );
                    Toast.makeText(getApplicationContext(),"Something went wrong and we couldn't sign you in",Toast.LENGTH_LONG).show();
                    notSuccessful("Something went wrong and we couldn't sign you in");
                    turnOffProgressBar();
                }

            }
        });
    }

    void turnOnProgressBar(){
        progressBar.setVisibility(View.VISIBLE);


        diamondText.setVisibility(View.GONE);
        diamondImage.setVisibility(View.GONE);
        emailAddressEditText.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.GONE);
        signInButton.setVisibility(View.GONE);
        signUpQuestion.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);

    }
    void notSuccessful(String message){
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);

    }
    void turnOffProgressBar(){
        progressBar.setVisibility(View.GONE);

        diamondText.setVisibility(View.VISIBLE);
        diamondImage.setVisibility(View.VISIBLE);
        emailAddressEditText.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);
        signUpQuestion.setVisibility(View.VISIBLE);
    }

}
