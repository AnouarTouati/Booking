
package com.example.bookingapp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SignUpFrag3 extends Fragment {
    public List<String> stateList =new ArrayList<>();
    public List<String> communesForSelectedStateList =new ArrayList<>();


    public String salonName;
    public String selectedState;
    public String selectedCommune;
    public Boolean isMen=false;
    public Boolean isWomen=false;
    public Boolean useCoordinatesAKAaddMap =false;

    View view;


    Spinner stateSpinner;
    Spinner communesSpinner;
    CheckBox addMapToYourShopCheckBox;

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
                    useCoordinatesAKAaddMap =intent.getBooleanExtra(" UseCoordinatesAKAaddMap", true);
                 addMapToYourShopCheckBox.setChecked(useCoordinatesAKAaddMap);

                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,filter);

        loadStateList();
        stateSpinner=view.findViewById(R.id.stateSpinner);
        communesSpinner=view.findViewById(R.id.communesSpinner);

        ArrayAdapter<String> stateSpinnerArrayAdapter=new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item, stateList);
        stateSpinner.setAdapter(stateSpinnerArrayAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedState = stateList.get(i);
                loadCommunesForSelectedState(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

       communesSpinnerArrayAdapter=new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, communesForSelectedStateList);
        communesSpinner.setAdapter(communesSpinnerArrayAdapter);
        communesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCommune = communesForSelectedStateList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final CheckBox menCheckBox=view.findViewById(R.id.men);
        final CheckBox womenCheckBox=view.findViewById(R.id.women);

        menCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                womenCheckBox.setChecked(! menCheckBox.isChecked());
                isMen= menCheckBox.isChecked();
                isWomen=womenCheckBox.isChecked();


            }
        });

        womenCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menCheckBox .setChecked(!womenCheckBox.isChecked());
                isMen= menCheckBox.isChecked();
                isWomen=womenCheckBox.isChecked();
            }
        });
       addMapToYourShopCheckBox=view.findViewById(R.id.addMap);

     addMapToYourShopCheckBox.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               addMapToYourShopCheckBox.setChecked(false);
               if(useCoordinatesAKAaddMap){
                   useCoordinatesAKAaddMap =false;

               }else{
                   ((SignUpActivity)getActivity()).findLocationUsingGPS();
               }



           }


       });
 Button submitButton=view.findViewById(R.id.submit);
 submitButton.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         doneFillingFieldsGoNextFrag();
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
    void loadStateList(){
        try {

           JSONArray jsonArray=new JSONArray(loadJSONFromAsset("wilayas"));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
              stateList.add(jsonObject.getString("nom")) ;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void loadCommunesForSelectedState(int wilayaIndexInStateList){
        communesForSelectedStateList.clear();
        try {
            JSONArray jsonArray=new JSONArray(loadJSONFromAsset("communes"));
            Boolean FoundCommunesNoNeedToContinue=false;
            for (int i=0;i<jsonArray.length();i++){
            if(jsonArray.getJSONObject(i).getInt("wilaya_id")==wilayaIndexInStateList+1){
             communesForSelectedStateList.add(jsonArray.getJSONObject(i).getString("nom"));
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


    void doneFillingFieldsGoNextFrag(){

        Boolean somethingWentWrong=false;
        EditText salonNameEditText=view.findViewById(R.id.salonName);
        salonName =salonNameEditText.getText().toString();

        Toast.makeText(getActivity(), salonName, Toast.LENGTH_LONG).show();
        if(salonName.length()<=0){
            somethingWentWrong=true;
            Toast.makeText(getActivity(), "Please Make Sure to Enter Your Salon Name", Toast.LENGTH_LONG).show();
        }
        if(!isMen && !isWomen){
            somethingWentWrong=true;
            Toast.makeText(getActivity(), "Please Choose a Nature", Toast.LENGTH_LONG).show();
        }
        //NO NEED TO CHECK FOR ADDRESS BECAUSE THERE ARE DEFAULT VALUES APPLIED
        if(!somethingWentWrong){

            ((SignUpActivity)getActivity()).salonName = salonName;
            ((SignUpActivity)getActivity()).selectedState = selectedState;
            ((SignUpActivity)getActivity()).selectedCommune = selectedCommune;
            ((SignUpActivity)getActivity()).isMen=isMen;

            ((SignUpActivity)getActivity()).setCurrentItemViewPager(3);
        }


    }

}
