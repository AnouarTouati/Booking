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

import com.example.bookingapp.R;
import com.example.bookingapp.signup.Service_Frag5;

public class SignUpFrag5 extends Fragment {

    View view;
    EditText serviceNameEditText,serviceDurationEditText,servicePriceEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.signupfrag5_layout, container,false);

        serviceNameEditText=view.findViewById(R.id.serviceName_Frag5);
        serviceDurationEditText=view.findViewById(R.id.serviceDuration);
        servicePriceEditText=view.findViewById(R.id.servicePrice);

         Button addButton=view.findViewById(R.id.addButton_Frag5);
         addButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
             if(!serviceNameEditText.getText().toString().equals("") &&
                     !serviceDurationEditText.getText().toString().equals("") &&
                     !servicePriceEditText.getText().toString().equals(""))
             {
                 Service_Frag5 service=new Service_Frag5(serviceNameEditText.getText().toString(),
                         servicePriceEditText.getText().toString(),
                         serviceDurationEditText.getText().toString());
                 ((SignUpActivity)getActivity()).services.add(service);
                 serviceNameEditText.getText().clear();
                 serviceDurationEditText.getText().clear();
                 servicePriceEditText.getText().clear();
             }
             }
         });
        Button finishButton=view.findViewById(R.id.finishButton_Frag5);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneFillingFieldsGoNextFrag();
            }
        });

        return view;
    }
    void doneFillingFieldsGoNextFrag(){
        ((SignUpActivity)getActivity()).setCurrentItemViewPager(5);
    }
}
