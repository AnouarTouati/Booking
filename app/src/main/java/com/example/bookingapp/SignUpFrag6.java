package com.example.bookingapp;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SignUpFrag6 extends Fragment {
    List<Spinner> spinnerList=new ArrayList<>();
    List<String> eveningList=new ArrayList<>();
    List<String> morningList=new ArrayList<>();
    List<List<String>> clonesListOfMorningList=new ArrayList<>();
    List<ArrayAdapter<String>> morningSpinnerAdapterLists=new ArrayList<>();
    List<ArrayAdapter<String>> eveningSpinnerAdapterLists=new ArrayList<>();
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.signupfrag6_layout, container,false);

        Button submit=view.findViewById(R.id.submitFrag6);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoneFillingFieldsGoNextFrag();
            }
        });


        spinnerList.add((Spinner) view.findViewById(R.id.spinner1));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner2));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner3));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner4));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner5));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner6));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner7));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner8));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner9));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner10));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner11));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner12));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner13));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner14));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner15));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner16));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner17));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner18));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner19));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner20));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner21));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner22));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner23));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner24));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner25));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner26));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner27));
        spinnerList.add((Spinner) view.findViewById(R.id.spinner28));

        morningList.add("No");
        for(int i=8;i<13;i++){
            morningList.add(i+"h");
        }

        eveningList.add("No");
        for(int i=12;i<=24;i++){
            eveningList.add(i+"h");
        }
      int spinnerIndexInMorningAdapterList=0;
        for(int t=0;t<spinnerList.size();t=t+4){

            final ArrayAdapter<String> spinnerAdapter1=new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, morningList);
            morningSpinnerAdapterLists.add(spinnerAdapter1);
            spinnerList.get(t).setAdapter(morningSpinnerAdapterLists.get(spinnerIndexInMorningAdapterList));
            spinnerIndexInMorningAdapterList++;



            ArrayAdapter<String> spinnerAdapter2=new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, morningList);
            morningSpinnerAdapterLists.add(spinnerAdapter2);
            spinnerList.get(t+1).setAdapter(morningSpinnerAdapterLists.get(spinnerIndexInMorningAdapterList));
            spinnerIndexInMorningAdapterList++;

        }
                for(int i=2;i<spinnerList.size();i=i+4){
                    ArrayAdapter<String> spinnerAdapteer=new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, eveningList);
                    spinnerList.get(i).setAdapter(spinnerAdapteer);
                    spinnerList.get(i+1).setAdapter(spinnerAdapteer);
                }
        return view;
    }

    private void DoneFillingFieldsGoNextFrag() {
        ((SignUpActivity)getActivity()).Saturday=spinnerList.get(0).getSelectedItem().toString()+spinnerList.get(1).getSelectedItem().toString()+spinnerList.get(2).getSelectedItem().toString()+spinnerList.get(3).getSelectedItem().toString();
        ((SignUpActivity)getActivity()).Sunday=spinnerList.get(4).getSelectedItem().toString()+spinnerList.get(5).getSelectedItem().toString()+spinnerList.get(6).getSelectedItem().toString()+spinnerList.get(7).getSelectedItem().toString();
        ((SignUpActivity)getActivity()).Monday=spinnerList.get(8).getSelectedItem().toString()+spinnerList.get(9).getSelectedItem().toString()+spinnerList.get(10).getSelectedItem().toString()+spinnerList.get(11).getSelectedItem().toString();
        ((SignUpActivity)getActivity()).Tuesday=spinnerList.get(12).getSelectedItem().toString()+spinnerList.get(13).getSelectedItem().toString()+spinnerList.get(14).getSelectedItem().toString()+spinnerList.get(15).getSelectedItem().toString();
        ((SignUpActivity)getActivity()).Wednesday=spinnerList.get(16).getSelectedItem().toString()+spinnerList.get(17).getSelectedItem().toString()+spinnerList.get(18).getSelectedItem().toString()+spinnerList.get(19).getSelectedItem().toString();
        ((SignUpActivity)getActivity()).Thursday=spinnerList.get(20).getSelectedItem().toString()+spinnerList.get(21).getSelectedItem().toString()+spinnerList.get(22).getSelectedItem().toString()+spinnerList.get(23).getSelectedItem().toString();
        ((SignUpActivity)getActivity()).Friday=spinnerList.get(24).getSelectedItem().toString()+spinnerList.get(25).getSelectedItem().toString()+spinnerList.get(26).getSelectedItem().toString()+spinnerList.get(27).getSelectedItem().toString();

        Toast.makeText(getActivity(),((SignUpActivity)getActivity()).Friday , Toast.LENGTH_LONG).show();
       // ((SignUpActivity)getActivity()).SetCurrentItemViewPager(6);
       ((SignUpActivity)getActivity()).SignUp();
    }
}
