package com.example.bookingapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    public static String EmailAddress;
    public static String Password;
    public String FirstName;
    public String LastName;
    public static String PhoneNumber;
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
    public static String ShopPhoneNumber;
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

    static final String SignUpUrl="http://192.168.43.139:81/ThirdPage.php";

    public  static ViewPager viewPagerSignUP;
    final int LOCATION_REQ=10;
    final int GPS_SETTING_REQ=5;
    Boolean ComingBackFromLocationSettings=false;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView signUpErrorText;
    TextView signUpSuccessfulText;
    CustomRecyclerViewAdapterSignUpErrors customRecyclerViewAdapterSignUpErrors;
    RecyclerView ErrorMessagesRecyclerView;
    ArrayList<String> ErrorsList=new ArrayList<>();
    Button retryButton;
    Button goToShopHomePageButton;
    public static ProgressBar progressBar;
    Boolean signUpWasNotSuccessful=false;
    static Boolean RequestWasSentToServer=false;
    static Response.Listener<JSONObject> VolleyListener;
    static Response.ErrorListener VolleyErrorListener;
    static RequestQueue requestQueue;
    static Context mContext;

    String TokenReceived;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContext=this;

        VolleyListener =new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("VolleyReceived","VolleyReceived in SignUpActivity "+response.toString());

                if(response.has("SignUp")){
                    try {
                        if(response.getJSONObject("SignUp").getString("Successful").equals("true")){
                            TokenReceived=response.getJSONObject("SignUp").getString("Token");
                            Successful();
                        }else{
                            NotSuccessful();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(response.has("CheckAndRegister")){
                    try {
                        ServerResponseCheckAndRegister(response.getJSONObject("CheckAndRegister"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        VolleyErrorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError","VolleyError in SignUpActivity "+error.toString());
                error.printStackTrace();

            }
        };
        requestQueue= Volley.newRequestQueue(this);
        customRecyclerViewAdapterSignUpErrors=new CustomRecyclerViewAdapterSignUpErrors(ErrorsList);//initialized empty so we can just swap the adapter later
        ErrorMessagesRecyclerView =findViewById(R.id.ErrorMessagesRecyclerView);
        ErrorMessagesRecyclerView.setAdapter(customRecyclerViewAdapterSignUpErrors);//
        ErrorMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
         Intent GoToShopActivityIntent=new Intent(mContext,ShopActivity.class);
         GoToShopActivityIntent.putExtra("Token", TokenReceived);
         mContext.startActivity(GoToShopActivityIntent);
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
        if(RequestWasSentToServer){
           //cancel the requests here
            TurnOffProgressBar();
        }else{
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

    }
public void SignUp(){
    viewPagerSignUP.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);

    signUpErrorText.setVisibility(View.GONE);
    retryButton.setVisibility(View.GONE);
    signUpSuccessfulText.setVisibility(View.GONE);
    goToShopHomePageButton.setVisibility(View.GONE);

    Map<String,Object> map=new HashMap<>();
    
        map.put("Request","SignUp");
        map.put("EmailAddress", EmailAddress);
        map.put("Password",Password );
        map.put("FirstName",FirstName );
        map.put("LastName",LastName );
        map.put("PhoneNumber", PhoneNumber);
        map.put("isBusinessOwner", isBusinessOwner);
        map.put("ShopName",SalonName );
        map.put("SelectedState", SelectedState);
        map.put("SelectedCommune",SelectedCommune );
        map.put("UseCoordinatesAKAaddMap",UseCoordinatesAKAaddMap );
        map.put("ShopLatitude", ShopLatitude);
        map.put("ShopLongitude", ShopLongitude);
        map.put("isMen", isMen);
        map.put("SelectedImage", ConvertBitmapToString(SelectedImage));//photo
        map.put("ShopPhoneNumber", ShopPhoneNumber);
        map.put("FacebookLink", FacebookLink);
        map.put("InstagramLink",InstagramLink );
        map.put("Coiffure",Coiffure );
        map.put("MakeUp", MakeUp);
        map.put("Meches", Meches);
        map.put("Tinte",Tinte );
        map.put("Pedcure", Pedcure);
        map.put("Manage", Manage);
        map.put("Manicure", Manicure);
        map.put("Coupe",Coupe );
        map.put("Saturday", Saturday);
        map.put("Sunday",Sunday );
        map.put("Monday", Monday);
        map.put("Tuesday", Tuesday);
        map.put("Wednesday", Wednesday);
        map.put("Thursday", Thursday);
        map.put("Friday", Friday);

   
    JSONObject Data=new JSONObject(map);

    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, SignUpUrl, Data,VolleyListener,VolleyErrorListener);

    requestQueue.add(jsonObjectRequest);


}


    public static void SendToServerToCheckAndRegister(int NextIndexInPagerAdapter){

        Map<String,Object> map=new HashMap<>();
        map.put("Request","CheckAndRegister");

        if(NextIndexInPagerAdapter==1 && viewPagerSignUP.getCurrentItem()==0){
        map.put("EmailAddress",EmailAddress);
        map.put("Password",Password);
        }
            else if(NextIndexInPagerAdapter==2 && viewPagerSignUP.getCurrentItem()==1){
                map.put("PhoneNumber",PhoneNumber);
            }

            else if(NextIndexInPagerAdapter==4 && viewPagerSignUP.getCurrentItem()==3){
                map.put("ShopPhoneNumber", ShopPhoneNumber);
        }
        JSONObject Dara=new JSONObject(map);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(SignUpUrl, Dara, VolleyListener, VolleyErrorListener);
        requestQueue.add(jsonObjectRequest);
        RequestWasSentToServer=true;
    }

    void ServerResponseCheckAndRegister(JSONObject response){
        TurnOffProgressBar();
       ErrorsList.clear();
      if(response.has("EmailAddress")&& response.has("Password")){
          try {
              if(response.getString("EmailAddress").equals("ok") && response.getString("Password").equals("ok") && viewPagerSignUP.getCurrentItem()==0){
                SetCurrentItemViewPager(1);
              }else {
                  if(!response.getString("EmailAddress").equals("ok")){
                      ErrorsList.add("-Please Choose Another Email Address");
                  }
                  if(!response.getString("Password").equals("ok")){
                      ErrorsList.add("-The Password You Entered Doesn't Meet The Requirements");
                  }
              }
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }
      if(response.has("PhoneNumber")){
          try {
              if(response.getString("PhoneNumber").equals("ok") && viewPagerSignUP.getCurrentItem()==1){
                  SetCurrentItemViewPager(2);
              }else{
                  ErrorsList.add("-The Phone Number You Entered Is Already Used");
              }
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }

      if(response.has("ShopPhoneNumber")){
          try {
              if(response.getString("ShopPhoneNumber").equals("ok") && viewPagerSignUP.getCurrentItem()==3){
                SetCurrentItemViewPager(4);
              }
              else{
                  ErrorsList.add("-The Shop Phone Number Is Already Used");
              }
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }
      if(ErrorsList.size()>0){//greater than zero meaning we have an error

          ErrorMessagesRecyclerView.swapAdapter(new CustomRecyclerViewAdapterSignUpErrors(ErrorsList), true);
          ErrorMessagesRecyclerView.setVisibility(View.VISIBLE);//will make it invisible in turn on progress bar
      }
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
    public void TurnOnProgressBar(){
        ErrorMessagesRecyclerView.setVisibility(View.GONE);//SET VISIBLE IS IN SERVERRESPONSECHECKANDREGISTER
        progressBar.setVisibility(View.VISIBLE);
        int CallingFragmentIndex=viewPagerSignUP.getCurrentItem();

        if(CallingFragmentIndex==0){
           findViewById(R.id.MainScrollView_Frag1).setVisibility(View.GONE);
        }
         else if(CallingFragmentIndex==1){
            findViewById(R.id.MainScrollView_Frag2).setVisibility(View.GONE);
        }
        else if(CallingFragmentIndex==2){
            findViewById(R.id.MainScrollView_Frag3).setVisibility(View.GONE);
        }
        else if(CallingFragmentIndex==3){
            findViewById(R.id.MainScrollView_Frag4).setVisibility(View.GONE);
        }
        else if(CallingFragmentIndex==4){
            findViewById(R.id.MainScrollView_Frag5).setVisibility(View.GONE);
        }
        else if(CallingFragmentIndex==5){
            findViewById(R.id.MainScrollView_Frag6).setVisibility(View.GONE);
        }

    }
    public void TurnOffProgressBar(){
        RequestWasSentToServer=false;
        progressBar.setVisibility(View.GONE);
        int CallingFragmentIndex=viewPagerSignUP.getCurrentItem();

        if(CallingFragmentIndex==0){
            findViewById(R.id.MainScrollView_Frag1).setVisibility(View.VISIBLE);
        }
        else if(CallingFragmentIndex==1){
            findViewById(R.id.MainScrollView_Frag2).setVisibility(View.VISIBLE);
        }
        else if(CallingFragmentIndex==2){
            findViewById(R.id.MainScrollView_Frag3).setVisibility(View.VISIBLE);
        }
        else if(CallingFragmentIndex==3){
            findViewById(R.id.MainScrollView_Frag4).setVisibility(View.VISIBLE);
        }
        else if(CallingFragmentIndex==4){
            findViewById(R.id.MainScrollView_Frag5).setVisibility(View.VISIBLE);
        }
        else if(CallingFragmentIndex==5){
            findViewById(R.id.MainScrollView_Frag6).setVisibility(View.VISIBLE);
        }
    }
}
