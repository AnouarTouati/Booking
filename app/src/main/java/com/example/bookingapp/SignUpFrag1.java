package com.example.bookingapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpFrag1 extends Fragment {
    public  String EmailAddress;
    public  String Password;
    public  String ConfirmPassword;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.signupfrag1_layout, container,false);
        Button signUpFrag1Button=view.findViewById(R.id.signUpFrag1);
        signUpFrag1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoneFillingFieldsGoNextFrag();
            }
        });

        return view;
    }
    public void DoneFillingFieldsGoNextFrag(){

        Boolean SomethingWentWrong=false;

        EditText emailEditText=view.findViewById(R.id.email);
        EmailAddress=emailEditText.getText().toString();
        if(EmailAddress.contains("@") && EmailAddress.contains(".")){
            if(EmailAddress.indexOf("@")<EmailAddress.indexOf(".")){

            }else{
                Toast.makeText(getActivity(), "Bad Email", Toast.LENGTH_LONG).show();
                SomethingWentWrong=true;
            }
        }else {
            Toast.makeText(getActivity(), "Bad Email", Toast.LENGTH_LONG).show();
            SomethingWentWrong=true;
        }

        EditText passwordEditText=view.findViewById(R.id.password);
        EditText confirmPasswordEditText=view.findViewById(R.id.confirmPassword);
        Password=passwordEditText.getText().toString();
        ConfirmPassword=confirmPasswordEditText.getText().toString();
        if (Password.length()<8){
            Toast.makeText(getActivity(), "Password Must Contain at Least 8 Characters ", Toast.LENGTH_LONG).show();
            SomethingWentWrong=true;
        }else if(!Password.contentEquals(ConfirmPassword)){
            Toast.makeText(getActivity(), "Password Doesn't Match", Toast.LENGTH_LONG).show();
            SomethingWentWrong=true;
        }


        if(!SomethingWentWrong){
            ((SignUpActivity)getActivity()).EmailAddress=EmailAddress;
            ((SignUpActivity)getActivity()).Password=Password;
            ((SignUpActivity)getActivity()).TurnOnProgressBar();
           SignUpActivity.SendToServerToCheckAndRegister(1);
        //    ((SignUpActivity)getActivity()).SetCurrentItemViewPager(1);
        }

    }

}
