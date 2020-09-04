package com.example.bookingapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpFrag2 extends Fragment {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
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
                doneFillingFieldsGoNextFrag();
            }
        });
        final CheckBox employeeCheckBox=view.findViewById(R.id.employeeFrag2);
        final CheckBox businessOwnerCheckBox=view.findViewById(R.id.businessOwnerFrag2);

        employeeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                businessOwnerCheckBox.setChecked(!employeeCheckBox.isChecked());
                isEmployee=employeeCheckBox.isChecked();
                isBusinessOwner=businessOwnerCheckBox.isChecked();


            }
        });

        businessOwnerCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employeeCheckBox .setChecked(!businessOwnerCheckBox.isChecked());
                isEmployee=employeeCheckBox.isChecked();
                isBusinessOwner=businessOwnerCheckBox.isChecked();
            }
        });

        return  view;
    }

    void doneFillingFieldsGoNextFrag(){
      Boolean someThingWentWrong=false;
        EditText firstNameEditText=view.findViewById(R.id.firstNameFrag2);
        firstName =firstNameEditText.getText().toString();
        if(firstName ==null || firstName.length()==0){
            someThingWentWrong=true;
            Toast.makeText(getActivity(), "Please Enter Your First Name", Toast.LENGTH_SHORT).show();
        }

        EditText lastNameEditText=view.findViewById(R.id.lastNameFrag2);
        lastName =lastNameEditText.getText().toString();
        if(lastName ==null || lastName.length()==0){
            someThingWentWrong=true;
            Toast.makeText(getActivity(), "Please Enter Your Last Name", Toast.LENGTH_SHORT).show();
        }
        EditText phoneNumberEditText=view.findViewById(R.id.phoneNumberFrag2);
        phoneNumber =phoneNumberEditText.getText().toString();
if(!(phoneNumber.indexOf("0")==0 && (phoneNumber.indexOf("2")==1|| phoneNumber.indexOf("5")==1 || phoneNumber.indexOf("6")==1 || phoneNumber.indexOf("7")==1))){
    someThingWentWrong=true;
    Toast.makeText(getActivity(), "Incorrect Phone Number", Toast.LENGTH_SHORT).show();
}
if(!isEmployee &&!isBusinessOwner){
    someThingWentWrong=true;
    Toast.makeText(getActivity(), "Please Check Employee/Business Owner", Toast.LENGTH_SHORT).show();
}


if(!someThingWentWrong){
    ((SignUpActivity)getActivity()).firstName = firstName;
    ((SignUpActivity)getActivity()).lastName = lastName;
    ((SignUpActivity)getActivity()).phoneNumber = phoneNumber;
    //if we came here from facebook and email is not available we should send email address to SignUpActivity
    ((SignUpActivity)getActivity()).isEmployee=isEmployee;
    ((SignUpActivity)getActivity()).isBusinessOwner=isBusinessOwner;

    ((SignUpActivity)getActivity()).setCurrentItemViewPager(2);
}

    }
}
