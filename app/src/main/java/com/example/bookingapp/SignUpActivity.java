package com.example.bookingapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    public String EmailAddress;
    public String Password;
    public String FirstName;
    public String LastName;
    public String PhoneNumber;
    public Boolean isEmployee=false;
    public Boolean isBusinessOwner=false;
    public String SalonName;
    public String SelectedState;
    public String SelectedCommune;
    public Boolean UseCoordinatesAKAaddMap=false;
    public Double ShopLatitude;
    public Double ShopLongitude;
    public Boolean isMen=true;
    public Bitmap SelectedImage;
    public String ShopPhoneNumber;
    public String FacebookLink;
    public String InstagramLink;
    public Boolean Coiffure=false;
    public Boolean MakeUp=false;
    public Boolean Meches=false;
    public Boolean Tinte=false;
    public Boolean Pedcure=false;
    public Boolean Manage=false;
    public Boolean Manicure=false;
    public Boolean Coupe=false;
    public String Saturday;
    public String Sunday;
    public String Monday;
    public String Tuesday;
    public String Wednesday;
    public String Thursday;
    public String Friday;

    final String SignUpUrl="dztraintrack.tk";

    ViewPager viewPagerSignUP;
    final int LOCATION_REQ=10;
    final int GPS_SETTING_REQ=5;
    Boolean ComingBackFromLocationSettings=false;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView signUpErrorText;
    TextView signUpSuccessfulText;
    Button retryButton;
    Button goToShopHomePageButton;
    ProgressBar progressBar;
    Boolean signUpWasNotSuccessful=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar=findViewById(R.id.progressBarSignUp);
        signUpErrorText=findViewById(R.id.signUpErrorText);
        signUpSuccessfulText=findViewById(R.id.signUpSuccessfulText);
        retryButton=findViewById(R.id.retrySignUp);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });
 goToShopHomePageButton=findViewById(R.id.goToShopHomePage);
 goToShopHomePageButton.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         //something here
     }
 });
        fusedLocationProviderClient=new FusedLocationProviderClient(this);

        viewPagerSignUP=findViewById(R.id.viewPagerSignUP);
        CustomFragmentPagerAdapter customFragmentPagerAdapter=new CustomFragmentPagerAdapter(getSupportFragmentManager());


        customFragmentPagerAdapter.addFragment(new SignUpFrag1(), "SignUpFrag1");
        customFragmentPagerAdapter.addFragment(new SignUpFrag2(), "SignUpFrag2");
        customFragmentPagerAdapter.addFragment(new SignUpFrag3(), "SignUpFrag3");
        customFragmentPagerAdapter.addFragment(new SignUpFrag4(), "SignUpFrag4");
        customFragmentPagerAdapter.addFragment(new SignUpFrag5(), "SignUpFrag5");
        customFragmentPagerAdapter.addFragment(new SignUpFrag6(), "SignUpFrag6");

        viewPagerSignUP.setAdapter(customFragmentPagerAdapter);

    }
    public void SetCurrentItemViewPager(int FragmentIndex){
          viewPagerSignUP.setCurrentItem(FragmentIndex);
    }

   public  void FindLocationUsingGPS(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},LOCATION_REQ);
        }else{


            LocationManager locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
                alertBuilder.setMessage("In the Next Screen Allow this Application to Use Location Services");
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        ComingBackFromLocationSettings=true;
                    }
                });

                alertBuilder.show();

            }else{

                Toast.makeText(this, "GRANTED", Toast.LENGTH_LONG).show();
                LocationRequest locationRequest=new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setExpirationDuration(40000);
                locationRequest.setNumUpdates(1);

                LocationCallback locationCallback=new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        SaveTheCoordinatesAndFindAddress(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());

                    }
                };
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());

            }}


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ComingBackFromLocationSettings){
        LocationManager locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
           FindLocationUsingGPS();
        }else{
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setMessage("Can't Know Your Position without Location Services");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialog.show();
        }
        ComingBackFromLocationSettings=false;
    }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean granted = true;
        if (requestCode == LOCATION_REQ) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    granted = false;
                    break;
                }
            }
        }
        if (granted) {

            FindLocationUsingGPS();
        }
    }



    void SaveTheCoordinatesAndFindAddress(Double Latitude, Double Longitude){
        ShopLatitude=Latitude;
        ShopLongitude=Longitude;
        Toast.makeText(this, "Latitude"+ShopLatitude, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Longitude"+ShopLongitude, Toast.LENGTH_LONG).show();
        UseCoordinatesAKAaddMap=true;
        Intent intent=new Intent();
        intent.setAction("ComingFromSignUpActivity");
        intent.putExtra("FoundCoordinatesSuccessfully", UseCoordinatesAKAaddMap);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

      /*  Geocoder geocoder=new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            Toast.makeText(this, "Trying To gET Address", Toast.LENGTH_LONG).show();
          addresses =  geocoder.getFromLocation(ShopLatitude, ShopLongitude, 1);
          SelectedState=addresses.get(0).getAdminArea();
          SelectedCommune=addresses.get(0).getLocality();
            Toast.makeText(this, SelectedCommune, Toast.LENGTH_LONG).show();
            Toast.makeText(this, SelectedState, Toast.LENGTH_LONG).show();
            Toast.makeText(this, addresses.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
            Toast.makeText(this, addresses.get(0).getCountryName(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, addresses.get(0).getPostalCode(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, addresses.get(0).getFeatureName(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    @Override
    public void onBackPressed() {
        if(signUpWasNotSuccessful){
            viewPagerSignUP.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            signUpErrorText.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
            signUpSuccessfulText.setVisibility(View.GONE);
            goToShopHomePageButton.setVisibility(View.GONE);
            signUpWasNotSuccessful=false;
        }
        if(viewPagerSignUP.getCurrentItem()>0){
            SetCurrentItemViewPager(viewPagerSignUP.getCurrentItem()-1);
        }else{
            super.onBackPressed();
        }
    }
public void SignUp(){
    viewPagerSignUP.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);

    signUpErrorText.setVisibility(View.GONE);
    retryButton.setVisibility(View.GONE);
    signUpSuccessfulText.setVisibility(View.GONE);
    goToShopHomePageButton.setVisibility(View.GONE);

   Response.Listener<JSONObject> volleyJSONObjectListener=new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            if(response.has("SignUpResult")){
                try {
                    if(response.getBoolean("SignUpResult")){
                        Successful();
                    }else{
                     NotSuccessful();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
   Response.ErrorListener volleyErrorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
           NotSuccessful();
        }
    };
    JSONObject signUpJsonObject=new JSONObject();
    try {

        signUpJsonObject.put("EmailAddress", EmailAddress);
         signUpJsonObject.put("Password",Password );
        signUpJsonObject.put("FirstName",FirstName );
        signUpJsonObject.put("LastName",LastName );
        signUpJsonObject.put("PhoneNumber", PhoneNumber);
        signUpJsonObject.put("isBusinessOwner", isBusinessOwner);
        signUpJsonObject.put("ShopName",SalonName );
       signUpJsonObject.put("SelectedState", SelectedState);
        signUpJsonObject.put("SelectedCommune",SelectedCommune );
        signUpJsonObject.put("UseCoordinatesAKAaddMap",UseCoordinatesAKAaddMap );
        signUpJsonObject.put("ShopLatitude", ShopLatitude);
        signUpJsonObject.put("ShopLongitude", ShopLongitude);
        signUpJsonObject.put("isMen", isMen);
        signUpJsonObject.put("SelectedImage", ConvertBitmapToString(SelectedImage));//photo
        signUpJsonObject.put("ShopPhoneNumber", ShopPhoneNumber);
        signUpJsonObject.put("FacebookLink", FacebookLink);
        signUpJsonObject.put("InstagramLink",InstagramLink );
        signUpJsonObject.put("Coiffure",Coiffure );
        signUpJsonObject.put("MakeUp", MakeUp);
        signUpJsonObject.put("Meches", Meches);
        signUpJsonObject.put("Tinte",Tinte );
        signUpJsonObject.put("Pedcure", Pedcure);
        signUpJsonObject.put("Manage", Manage);
        signUpJsonObject.put("Manicure", Manicure);
        signUpJsonObject.put("Coupe",Coupe );
        signUpJsonObject.put("Saturday", Saturday);
        signUpJsonObject.put("Sunday",Sunday );
        signUpJsonObject.put("Monday", Monday);
        signUpJsonObject.put("Tuesday", Tuesday);
        signUpJsonObject.put("Wednesday", Wednesday);
        signUpJsonObject.put("Thursday", Thursday);
        signUpJsonObject.put("Friday", Friday);

    } catch (JSONException e) {
        e.printStackTrace();

    }


    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, SignUpUrl, signUpJsonObject,volleyJSONObjectListener,volleyErrorListener);
    RequestQueue requestQueue= Volley.newRequestQueue(this);
    requestQueue.add(jsonObjectRequest);




}
    String ConvertBitmapToString(Bitmap image){

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP,85,byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
    void NotSuccessful(){
        signUpWasNotSuccessful=true;
        progressBar.setVisibility(View.GONE);

        signUpErrorText.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Not SuccessfulSignIn", Toast.LENGTH_LONG).show();
    }
    void Successful(){
        signUpWasNotSuccessful=false;
        progressBar.setVisibility(View.GONE);
        signUpSuccessfulText.setVisibility(View.VISIBLE);
        goToShopHomePageButton.setVisibility(View.VISIBLE);

    }
}
