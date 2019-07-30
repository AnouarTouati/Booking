package com.example.bookingapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SignUpFrag3 extends Fragment {
    public List<String> StateList=new ArrayList<>();
    public List<String> CommunesForSelectedStateList=new ArrayList<>();


    public String SalonName;
    public String SelectedState;
    public String SelectedCommune;
    public Boolean isMen=false;
    public Boolean isWomen=false;
    public Boolean UseCoordinatesAKAaddMap=false;

    View view;


    Spinner stateSpinner;
    Spinner communesSpinner;
    CheckBox addMapToYourStoreCheckBox;

    ArrayAdapter<String> communesSpinnerArrayAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.signupfrag3_layout, container,false);

        IntentFilter filter=new IntentFilter();
        filter.addAction("ComingFromSignUpActivity");
        BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               String action=intent.getAction();
                if(action.equals("ComingFromSignUpActivity")){
                    UseCoordinatesAKAaddMap=intent.getBooleanExtra(" UseCoordinatesAKAaddMap", true);
                 addMapToYourStoreCheckBox.setChecked(UseCoordinatesAKAaddMap);
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,filter);

        LoadStateList();
        stateSpinner=view.findViewById(R.id.stateSpinner);
        communesSpinner=view.findViewById(R.id.communesSpinner);

        ArrayAdapter<String> stateSpinnerArrayAdapter=new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item,StateList);
        stateSpinner.setAdapter(stateSpinnerArrayAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedState=StateList.get(i);
                LoadCommunesForSelectedState(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

       communesSpinnerArrayAdapter=new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, CommunesForSelectedStateList);
        communesSpinner.setAdapter(communesSpinnerArrayAdapter);
        communesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedCommune=CommunesForSelectedStateList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final CheckBox MenCheckBox=view.findViewById(R.id.men);
        final CheckBox WomenCheckBox=view.findViewById(R.id.women);

        MenCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WomenCheckBox.setChecked(! MenCheckBox.isChecked());
                isMen= MenCheckBox.isChecked();
                isWomen=WomenCheckBox.isChecked();


            }
        });

        WomenCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenCheckBox .setChecked(!WomenCheckBox.isChecked());
                isMen= MenCheckBox.isChecked();
                isWomen=WomenCheckBox.isChecked();
            }
        });
       addMapToYourStoreCheckBox=view.findViewById(R.id.addMap);

     addMapToYourStoreCheckBox.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               addMapToYourStoreCheckBox.setChecked(false);
               if( UseCoordinatesAKAaddMap){
                   UseCoordinatesAKAaddMap=false;

               }else{
                   ((SignUpActivity)getActivity()).FindLocationUsingGPS();
               }



           }


       });
 Button submitButton=view.findViewById(R.id.submit);
 submitButton.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         DoneFillingFieldsGoNextFrag();
     }
 });

        return view;
    }


    public String loadJSONFromAsset(String jsonFileName) {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open(jsonFileName+".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    void LoadStateList(){
        try {

           JSONArray jsonArray=new JSONArray(loadJSONFromAsset("wilayas"));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
              StateList.add(jsonObject.getString("nom")) ;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void LoadCommunesForSelectedState(int wilayaIndexInStateList){
        CommunesForSelectedStateList.clear();
        try {
            JSONArray jsonArray=new JSONArray(loadJSONFromAsset("communes"));
            Boolean FoundCommunesNoNeedToContinue=false;
            for (int i=0;i<jsonArray.length();i++){
            if(jsonArray.getJSONObject(i).getInt("wilaya_id")==wilayaIndexInStateList+1){
             CommunesForSelectedStateList.add(jsonArray.getJSONObject(i).getString("nom"));
             FoundCommunesNoNeedToContinue=true;
            }else if(FoundCommunesNoNeedToContinue){
              break;
            }
                        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        communesSpinnerArrayAdapter.notifyDataSetChanged();
    }


    void DoneFillingFieldsGoNextFrag(){

        Boolean SomethingWentWrong=false;
        EditText salonNameEditText=view.findViewById(R.id.salonName);
        SalonName=salonNameEditText.getText().toString();

        Toast.makeText(getActivity(), SalonName, Toast.LENGTH_LONG).show();
        if(SalonName.length()<=0){
            SomethingWentWrong=true;
            Toast.makeText(getActivity(), "Please Make Sure to Enter Your Salon Name", Toast.LENGTH_LONG).show();
        }
        if(!isMen && !isWomen){
            SomethingWentWrong=true;
            Toast.makeText(getActivity(), "Please Choose a Nature", Toast.LENGTH_LONG).show();
        }
        //NO NEED TO CHECK FOR ADDRESS BECAUSE THERE ARE DEFAULT VALUES APPLIED
        if(!SomethingWentWrong){

            ((SignUpActivity)getActivity()).SalonName=SalonName;
            ((SignUpActivity)getActivity()).SelectedState=SelectedState;
            ((SignUpActivity)getActivity()).SelectedCommune=SelectedCommune;
            ((SignUpActivity)getActivity()).isMen=isMen;

            ((SignUpActivity)getActivity()).SetCurrentItemViewPager(3);
        }


    }

}