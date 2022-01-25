package com.example.bookingapp.signup;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookingapp.R;

public class SignUpFrag1 extends Fragment {
    public  String emailAddress;
    public  String password;
    public  String confirmPassword;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.signupfrag1_layout, container,false);
        Button signUpFrag1Button=view.findViewById(R.id.signUpFrag1);
        signUpFrag1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneFillingFieldsGoNextFrag();
            }
        });

        return view;
    }
    public void doneFillingFieldsGoNextFrag(){

        Boolean SomethingWentWrong=false;

        EditText emailEditText=view.findViewById(R.id.email);
        emailAddress =emailEditText.getText().toString();
        if(emailAddress.contains("@") && emailAddress.contains(".")){
            if(emailAddress.indexOf("@")< emailAddress.indexOf(".")){

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
        password =passwordEditText.getText().toString();
        confirmPassword =confirmPasswordEditText.getText().toString();
        if (password.length()<8){
            Toast.makeText(getActivity(), "Password Must Contain at Least 8 Characters ", Toast.LENGTH_LONG).show();
            SomethingWentWrong=true;
        }else if(!password.contentEquals(confirmPassword)){
            Toast.makeText(getActivity(), "Password Doesn't Match", Toast.LENGTH_LONG).show();
            SomethingWentWrong=true;
        }


        if(!SomethingWentWrong){
            ((SignUpActivity)getActivity()).emailAddress = emailAddress;
            ((SignUpActivity)getActivity()).password = password;

          ((SignUpActivity)getActivity()).createAccount();
        }

    }

}
