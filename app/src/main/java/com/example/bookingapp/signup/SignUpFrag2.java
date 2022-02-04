package com.example.bookingapp.signup;

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

import com.example.bookingapp.R;

public class SignUpFrag2 extends Fragment {
    private Boolean isEmployee = false;
    private Boolean isBusinessOwner = false;
    private Button submitFrag2;
    private CheckBox employeeCheckBox;
    private CheckBox businessOwnerCheckBox;
    private final SignUpActivity signUpActivity;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signupfrag2_layout, container, false);

        getViewsReferences();
        setUpViews();

        return view;
    }

    public SignUpFrag2(SignUpActivity signUpActivity) {
        this.signUpActivity = signUpActivity;
    }

    private void getViewsReferences() {
        submitFrag2 = view.findViewById(R.id.submitFrag2);
        employeeCheckBox = view.findViewById(R.id.employeeFrag2);
        businessOwnerCheckBox = view.findViewById(R.id.businessOwnerFrag2);
    }

    private void setUpViews() {
        submitFrag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneFillingFieldsGoNextFrag();
            }
        });


        employeeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                businessOwnerCheckBox.setChecked(!employeeCheckBox.isChecked());
                isEmployee = employeeCheckBox.isChecked();
                isBusinessOwner = businessOwnerCheckBox.isChecked();
            }
        });

        businessOwnerCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employeeCheckBox.setChecked(!businessOwnerCheckBox.isChecked());
                isEmployee = employeeCheckBox.isChecked();
                isBusinessOwner = businessOwnerCheckBox.isChecked();
            }
        });
    }

    void doneFillingFieldsGoNextFrag() {

        EditText firstNameEditText = view.findViewById(R.id.firstNameFrag2);
        String firstName = firstNameEditText.getText().toString();
        if (firstName.length() == 0) {
            Toast.makeText(getActivity(), "Please Enter Your First Name", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText lastNameEditText = view.findViewById(R.id.lastNameFrag2);
        String lastName = lastNameEditText.getText().toString();
        if (lastName.length() == 0) {
            Toast.makeText(getActivity(), "Please Enter Your Last Name", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText phoneNumberEditText = view.findViewById(R.id.phoneNumberFrag2);
        String phoneNumber = phoneNumberEditText.getText().toString();
        if (!(phoneNumber.indexOf("0") == 0 && (phoneNumber.indexOf("2") == 1 || phoneNumber.indexOf("5") == 1 || phoneNumber.indexOf("6") == 1 || phoneNumber.indexOf("7") == 1))) {
            Toast.makeText(getActivity(), "Incorrect Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEmployee && !isBusinessOwner) {
            Toast.makeText(getActivity(), "Please Check Employee/Business Owner", Toast.LENGTH_SHORT).show();
            return;
        }

        signUpActivity.firstName = firstName;
        signUpActivity.lastName = lastName;
        signUpActivity.phoneNumber = phoneNumber;

        signUpActivity.isEmployee = isEmployee;
        signUpActivity.isBusinessOwner = isBusinessOwner;

        signUpActivity.setCurrentItemViewPager(2);

    }
}
