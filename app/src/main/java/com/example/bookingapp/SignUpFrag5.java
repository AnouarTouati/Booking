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

import java.util.ArrayList;
import java.util.List;

public class SignUpFrag5 extends Fragment {

    View view;
    List<CheckBox> checkBoxList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.signupfrag5_layout, container,false);

        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox1));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox1));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox2));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox3));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox4));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox5));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox6));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox7));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox8));

        Button submit=view.findViewById(R.id.submitFrag5);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneFillingFieldsGoNextFrag();
            }
        });

        return view;
    }
    void doneFillingFieldsGoNextFrag(){

        ((SignUpActivity)getActivity()).coiffure =checkBoxList.get(0).isChecked();
        ((SignUpActivity)getActivity()).makeUp =checkBoxList.get(1).isChecked();
        ((SignUpActivity)getActivity()).meches =checkBoxList.get(2).isChecked();
        ((SignUpActivity)getActivity()).tinte =checkBoxList.get(3).isChecked();
        ((SignUpActivity)getActivity()).pedcure =checkBoxList.get(4).isChecked();
        ((SignUpActivity)getActivity()).manage =checkBoxList.get(5).isChecked();
        ((SignUpActivity)getActivity()).manicure =checkBoxList.get(6).isChecked();
        ((SignUpActivity)getActivity()).coupe =checkBoxList.get(7).isChecked();

        ((SignUpActivity)getActivity()).setCurrentItemViewPager(5);
    }
}
