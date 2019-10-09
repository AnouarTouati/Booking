package com.example.bookingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpFrag2 extends Fragment {
    private String FirstName;
    private String LastName;
    private String PhoneNumber;
    private String Email;
    private Boolean isEmployee=false;
    private Boolean isBusinessOwner=false;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.signupfrag2_layout, container,false);
        Button submitFrag2=view.findViewById(R.id.submitFrag2);
        submitFrag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoneFillingFieldsGoNextFrag();
            }
        });
        final CheckBox EmployeeCheckBox=view.findViewById(R.id.employeeFrag2);
        final CheckBox BusinessOwnerCheckBox=view.findViewById(R.id.businessOwnerFrag2);

        EmployeeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BusinessOwnerCheckBox.setChecked(!EmployeeCheckBox.isChecked());
                isEmployee=EmployeeCheckBox.isChecked();
                isBusinessOwner=BusinessOwnerCheckBox.isChecked();


            }
        });

        BusinessOwnerCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmployeeCheckBox .setChecked(!BusinessOwnerCheckBox.isChecked());
                isEmployee=EmployeeCheckBox.isChecked();
                isBusinessOwner=BusinessOwnerCheckBox.isChecked();
            }
        });

        return  view;
    }

    void DoneFillingFieldsGoNextFrag(){
      Boolean SomeThingWentWrong=false;
        EditText FirstNameEditText=view.findViewById(R.id.firstNameFrag2);
        FirstName=FirstNameEditText.getText().toString();
        if(FirstName==null || FirstName.length()==0){
            SomeThingWentWrong=true;
            Toast.makeText(getActivity(), "Please Enter Your First Name", Toast.LENGTH_SHORT).show();
        }

        EditText LastNameEditText=view.findViewById(R.id.lastNameFrag2);
        LastName=LastNameEditText.getText().toString();
        if(LastName==null || LastName.length()==0){
            SomeThingWentWrong=true;
            Toast.makeText(getActivity(), "Please Enter Your Last Name", Toast.LENGTH_SHORT).show();
        }
        EditText PhoneNumberEditText=view.findViewById(R.id.phoneNumberFrag2);
        PhoneNumber=PhoneNumberEditText.getText().toString();
if(!(PhoneNumber.indexOf("0")==0 && (PhoneNumber.indexOf("2")==1||PhoneNumber.indexOf("5")==1 || PhoneNumber.indexOf("6")==1 || PhoneNumber.indexOf("7")==1))){
    SomeThingWentWrong=true;
    Toast.makeText(getActivity(), "Incorrect Phone Number", Toast.LENGTH_SHORT).show();
}
if(!isEmployee &&!isBusinessOwner){
    SomeThingWentWrong=true;
    Toast.makeText(getActivity(), "Please Check Employee/Business Owner", Toast.LENGTH_SHORT).show();
}


if(!SomeThingWentWrong){
    ((SignUpActivity)getActivity()).FirstName=FirstName;
    ((SignUpActivity)getActivity()).LastName=LastName;
    ((SignUpActivity)getActivity()).PhoneNumber=PhoneNumber;
    //if we came here from facebook and email is not available we should send email address to SignUpActivity
    ((SignUpActivity)getActivity()).isEmployee=isEmployee;
    ((SignUpActivity)getActivity()).isBusinessOwner=isBusinessOwner;
    ((SignUpActivity)getActivity()).TurnOnProgressBar();
    SignUpActivity.SendToServerToCheckAndRegister(2);
   // ((SignUpActivity)getActivity()).SetCurrentItemViewPager(2);
}

    }
}
