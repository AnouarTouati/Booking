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

import com.example.bookingapp.R;
import com.example.bookingapp.SignUpActivity;

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
                DoneFillingFieldsGoNextFrag();
            }
        });

        return view;
    }
    void DoneFillingFieldsGoNextFrag(){

        ((SignUpActivity)getActivity()).Coiffure=checkBoxList.get(0).isChecked();
        ((SignUpActivity)getActivity()).MakeUp=checkBoxList.get(1).isChecked();
        ((SignUpActivity)getActivity()).Meches=checkBoxList.get(2).isChecked();
        ((SignUpActivity)getActivity()).Tinte=checkBoxList.get(3).isChecked();
        ((SignUpActivity)getActivity()).Pedcure=checkBoxList.get(4).isChecked();
        ((SignUpActivity)getActivity()).Manage=checkBoxList.get(5).isChecked();
        ((SignUpActivity)getActivity()).Manicure=checkBoxList.get(6).isChecked();
        ((SignUpActivity)getActivity()).Coupe=checkBoxList.get(7).isChecked();

        ((SignUpActivity)getActivity()).SetCurrentItemViewPager(5);
    }
}
