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
    private Button signUpFrag1Button;
    private EditText emailEditText;
    private EditText passwordEditText;
    private  EditText confirmPasswordEditText;
    private final SignUpActivity signUpActivity;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.signupfrag1_layout, container,false);

        getViewsReferences();
        setUpViews();

        return view;
    }
    public SignUpFrag1(SignUpActivity signUpActivity){
        this.signUpActivity=signUpActivity;
    }
    private void getViewsReferences(){
        signUpFrag1Button=view.findViewById(R.id.signUpFrag1);
        emailEditText=view.findViewById(R.id.email);
        passwordEditText=view.findViewById(R.id.password);
        confirmPasswordEditText=view.findViewById(R.id.confirmPassword);
    }
    private void setUpViews(){
        signUpFrag1Button.setOnClickListener(view -> doneFillingFieldsGoNextFrag());
    }
    public void doneFillingFieldsGoNextFrag(){

        String emailAddress = emailEditText.getText().toString();
        if(emailAddress.contains("@") && emailAddress.contains(".")){
            if(!(emailAddress.indexOf("@")< emailAddress.indexOf("."))){
                Toast.makeText(getActivity(), "Bad Email", Toast.LENGTH_LONG).show();
               return;
            }
        }else {
            Toast.makeText(getActivity(), "Bad Email", Toast.LENGTH_LONG).show();
           return;
        }

        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        if (password.length()<8){
            Toast.makeText(getActivity(), "Password Must Contain at Least 8 Characters ", Toast.LENGTH_LONG).show();
           return;
        }else if(!password.contentEquals(confirmPassword)){
            Toast.makeText(getActivity(), "Password Doesn't Match", Toast.LENGTH_LONG).show();
           return;
        }

           signUpActivity.emailAddress = emailAddress;
           signUpActivity.password = password;

           signUpActivity.createAccount();
    }
}
