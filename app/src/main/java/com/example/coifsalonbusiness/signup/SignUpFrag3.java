
package com.example.coifsalonbusiness.signup;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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

import com.example.coifsalonbusiness.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SignUpFrag3 extends Fragment {

    private List<String> states=new ArrayList<>();
    private List<List<String>> communes=new ArrayList<>();
    private List<String> communesForSelectedState=new ArrayList<>();
    private String selectedState;
    private String selectedCommune;
    private Boolean isMen=false;
    private Boolean isWomen=false;
    public Boolean hasLocation =false;
    private CheckBox menCheckBox;
    private CheckBox womenCheckBox;
    private Button submitButton;
    private boolean geoLocation=false;
    private int communeFromGeoLocationIndex=-1;
    View view;


    Spinner stateSpinner;
    Spinner communesSpinner;
    public CheckBox addMapToYourShopCheckBox;

    ArrayAdapter<String> communesSpinnerArrayAdapter;
    private final SignUpActivity signUpActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.signupfrag3_layout, container,false);

        loadStateList();
        getViewsReferences();
        setUpViews();

        return view;
    }

    public SignUpFrag3(SignUpActivity signUpActivity){
        this.signUpActivity=signUpActivity;
    }
    private void getViewsReferences(){
        stateSpinner=view.findViewById(R.id.stateSpinner);
        communesSpinner=view.findViewById(R.id.communesSpinner);
        menCheckBox=view.findViewById(R.id.men);
        womenCheckBox=view.findViewById(R.id.women);
        addMapToYourShopCheckBox=view.findViewById(R.id.addMap);
        submitButton=view.findViewById(R.id.submit);
    }
    private void setUpViews(){
        ArrayAdapter<String> stateSpinnerArrayAdapter=new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item, states);
        stateSpinner.setAdapter(stateSpinnerArrayAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    setState(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        communesSpinnerArrayAdapter=new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, communesForSelectedState);
        communesSpinner.setAdapter(communesSpinnerArrayAdapter);
        communesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCommune = communesForSelectedState.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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


        addMapToYourShopCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMapToYourShopCheckBox.setChecked(false);
                if(hasLocation){
                    hasLocation =false;

                }else{
                    signUpActivity.turnOnProgressBar();
                    signUpActivity.findLocationUsingGPS();
                }

            }

        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneFillingFieldsGoNextFrag();
            }
        });
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
        } catch (Exception e) {
            Log.e("AppFilter", "Error parsing JSON "+e);
        }
        return json;
    }

    void loadStateList(){
        try {

           JSONArray jsonArray=new JSONArray(loadJSONFromAsset("wilayas"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                states.add(jsonObject.getString("nom"));
                communes.add(new ArrayList<>());
            }
            loadCommunesForSelectedState();
        } catch (JSONException e) {
            Log.e("AppFilter", "Error parsing JSON "+e);
        }
    }

    void loadCommunesForSelectedState(){
        try {
            JSONArray jsonArray=new JSONArray(loadJSONFromAsset("communes"));
            for (int i=0;i<jsonArray.length();i++){
                int wilayaId=jsonArray.getJSONObject(i).getInt("wilaya_id")-1;
                String communeName=jsonArray.getJSONObject(i).getString("nom");
                communes.get(wilayaId).add(communeName);
            }
            communesForSelectedState=communes.get(0);
        } catch (JSONException e) {
            Log.e("AppFilter", "Error parsing JSON "+e);
        }
    }

    public void getAddressFromLocation(Location location){
        Geocoder geocoder=new Geocoder(signUpActivity, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses =  geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            for(int i=0;i<communes.size();i++){
                for(int j=0;j<communes.get(i).size();j++){
                    if(addresses.get(0).getAddressLine(0).contains(communes.get(i).get(j))){
                        stateSpinner.setSelection(i);
                        communeFromGeoLocationIndex=j;
                        geoLocation=true;
                    }
                }
            }
            Toast.makeText(signUpActivity, addresses.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("AppFilter", "Error getting geolocation address "+e);
            Toast.makeText(signUpActivity, "Could not get location address", Toast.LENGTH_LONG).show();
        }
        signUpActivity.turnOffProgressBar();
    }
private void setState(int stateIndex){
    selectedState = states.get(stateIndex);
    communesForSelectedState = communes.get(stateIndex);
    communesSpinnerArrayAdapter=new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, communesForSelectedState);
    communesSpinner.setAdapter(communesSpinnerArrayAdapter);
    if(geoLocation){
        communesSpinner.setSelection(communeFromGeoLocationIndex);
        geoLocation=false;
        communeFromGeoLocationIndex=-1;
    }
}
    void doneFillingFieldsGoNextFrag(){
        EditText salonNameEditText=view.findViewById(R.id.salonName);
        String salonName = salonNameEditText.getText().toString();

        Toast.makeText(getActivity(), salonName, Toast.LENGTH_LONG).show();
        if(salonName.length()<=0){
            Toast.makeText(getActivity(), "Please Make Sure to Enter Your Salon Name", Toast.LENGTH_LONG).show();
            return;
        }
        if(!isMen && !isWomen){
            Toast.makeText(getActivity(), "Please Choose a Nature", Toast.LENGTH_LONG).show();
            return;
        }
            signUpActivity.salonName = salonName;
            signUpActivity.selectedState = selectedState;
            signUpActivity.selectedCommune = selectedCommune;
            signUpActivity.isMen=isMen;

            signUpActivity.setCurrentItemViewPager(3);
    }

}
