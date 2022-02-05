package com.example.coifsalonbusiness.signup;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.coifsalonbusiness.R;

public class SignUpFrag5 extends Fragment {

    View view;
    EditText serviceNameEditText,serviceDurationEditText,servicePriceEditText;
    Button addButton;
    Button finishButton;
    SignUpActivity signUpActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.signupfrag5_layout, container,false);

        getViewsReferences();
        setUpViews();

        return view;
    }
    public SignUpFrag5(SignUpActivity signUpActivity){
        this.signUpActivity=signUpActivity;
    }
    private void getViewsReferences(){
        serviceNameEditText=view.findViewById(R.id.serviceName_Frag5);
        serviceDurationEditText=view.findViewById(R.id.serviceDuration);
        servicePriceEditText=view.findViewById(R.id.servicePrice);
        addButton=view.findViewById(R.id.addButton_Frag5);
        finishButton=view.findViewById(R.id.finishButton_Frag5);
    }
    private void setUpViews(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fieldsAreFilledIn() ) {
                    addServiceToTheList();
                }
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneFillingFieldsGoNextFrag();
            }
        });
    }
    private boolean fieldsAreFilledIn(){
       return !serviceNameEditText.getText().toString().equals("") &&
                !serviceDurationEditText.getText().toString().equals("") &&
                !servicePriceEditText.getText().toString().equals("");
    }
    private void addServiceToTheList(){
        Service_Frag5 service=new Service_Frag5(serviceNameEditText.getText().toString(),
                                                servicePriceEditText.getText().toString(),
                                                serviceDurationEditText.getText().toString());

        signUpActivity.services.add(service);
        clearFields();
    }
    private void clearFields(){
        serviceNameEditText.getText().clear();
        serviceDurationEditText.getText().clear();
        servicePriceEditText.getText().clear();
    }
    void doneFillingFieldsGoNextFrag(){
       signUpActivity.continueSignUp();
    }
}
