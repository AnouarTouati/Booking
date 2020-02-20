package com.example.bookingapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {




    public static String URL="http://192.168.43.139:8888/Business.php";
    static RequestQueue requestQueue;
    static Response.Listener<JSONObject> volleyListener;
    static Response.ErrorListener volleyErrorListener;
   private static FirebaseUser firebaseUser;
    static Context mContext;
    static ArrayList<String> pendingList=new ArrayList<>();
    CustomFragmentPagerAdapter customFragmentPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    TextView errorText;



    public String emailAddress;
    public String password;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public Boolean isEmployee=false;
    public Boolean isBusinessOwner=false;
    public static String shopName;
    public String selectedState;
    public String selectedCommune;
    public Boolean useCoordinatesAKAaddMap =false;
    public Double shopLatitude;
    public Double shopLongitude;
    public Boolean isMen=true;
    public Bitmap selectedImage;
    public String shopPhoneNumber;
    public String facebookLink;
    public String instagramLink;
    public Boolean coiffure =false;
    public Boolean makeUp =false;
    public Boolean meches =false;
    public Boolean tinte =false;
    public Boolean pedcure =false;
    public Boolean manage =false;
    public Boolean manicure =false;
    public Boolean coupe =false;
    public String saturday;
    public String sunday;
    public String monday;
    public String tuesday;
    public String wednesday;
    public String thursday;
    public String friday;

/////////////////////////////////////////////////
////FRAG_2 STORE INFO///////////////////////////
    static final int LOCATION_REQ=10;
    final int GPS_SETTING_REQ=5;
    static Boolean comingBackFromLocationSettings =false;
    static FusedLocationProviderClient fusedLocationProviderClient;
///////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        mContext=this;

        if(firebaseUser ==null){
            Intent goBACKtoSignInActivity=new Intent(this,SignInActivity.class);
            startActivity(goBACKtoSignInActivity);
        }

        volleyListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              Log.v("VolleyReceived","Volley Received Shop Activity "+response.toString());

                errorText.setText("");
                errorText.setVisibility(View.GONE);

              if(response.has("RemovePersonFromPending")){
                  try {
                      removePersonFromPending(response.getString("RemovePersonFromPending"));
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }

              if(response.has("AddPersonToPending")){
                  try {
                      addPersonToPending(response.getString("AddPersonToPending"));
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }
              if(response.has("Add_UpdateLocationMap")){
                  try {
                      if(response.getJSONObject("Add_UpdateLocationMap").getString("Successful").equals("True")){
                          addUpdateLocationMap(response.getJSONObject("Add_UpdateLocationMap").getDouble("Latitude"),response.getJSONObject("Add_UpdateLocationMap").getDouble("Longitude"));
                      }
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }
            }
        };
        volleyErrorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError","Volley Error  Shop Activity "+error.toString());
                errorText.setText("Connection To The Server Was Lost");
                errorText.setVisibility(View.VISIBLE);
            }
        };

        requestQueue= Volley.newRequestQueue(this);
        errorText =findViewById(R.id.ErrorTextView_ShopActivity);
        viewPager=findViewById(R.id.viewPagerShopMenu);
        customFragmentPagerAdapter=new CustomFragmentPagerAdapter(getSupportFragmentManager());
        customFragmentPagerAdapter.addFragment(new ShopMenuFrag1(), "ShopMenuFrag1");
        customFragmentPagerAdapter.addFragment(new ShopMenuFrag2(), "ShopMenuFrag2");
        viewPager.setAdapter(customFragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
               tabLayout.getTabAt(i).select();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout=findViewById(R.id.tabLayoutShopMenu);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fusedLocationProviderClient=new FusedLocationProviderClient(this);

    }

    public static void setFirebaseUser(FirebaseUser fireBaseUser){

        firebaseUser=fireBaseUser;
   }
    public static void findLocationUsingGPS(){

        if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},LOCATION_REQ);
        }else{


            LocationManager locationManager=(LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(mContext);
                alertBuilder.setMessage("In the Next Screen Allow this Application to Use Location Services");
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                        comingBackFromLocationSettings =true;
                    }
                });

                alertBuilder.show();

            }else{

                Toast.makeText(mContext, "GRANTED", Toast.LENGTH_LONG).show();
                LocationRequest locationRequest=new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setExpirationDuration(40000);
                locationRequest.setNumUpdates(1);

                LocationCallback locationCallback=new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        serverAddUpdateLocationMap(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());

                    }
                };
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());

            }}


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

            findLocationUsingGPS();
        }
    }
   void addUpdateLocationMap(double Latitude, double Longitude){
      ///i might show the map here or use gcoder to show the user where we put the point
       Toast.makeText(this, "Successfully Added the map to your shop at these coordinates Latitude: "+Latitude+"  Longitude: "+Longitude, Toast.LENGTH_LONG).show();
   }

    static void serverAddUpdateLocationMap(double Latitude, double Longitude){
        Map<String,Object> map=new HashMap<>();
        map.put("Request","Add_UpdateLocationMap");
        map.put("Latitude",Latitude);
        map.put("Longitude",Longitude);

        JSONObject data=new JSONObject(map);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,URL, data, volleyListener, volleyErrorListener);
        requestQueue.add(jsonObjectRequest);


     }

    @Override
    protected void onResume() {
        super.onResume();
        if(comingBackFromLocationSettings){
            LocationManager locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                findLocationUsingGPS();
            }
        }
    }

    public static void removePersonFromPending(String PersonNameToRemove){
        pendingList.remove(PersonNameToRemove);
       ShopMenuFrag1.customRecyclerViewAdapterShop.notifyDataSetChanged();

    }
    public static void serverRemovePersonFromPending(String PersonNameToRemove){
        Map<String,Object> map=new HashMap<>();
        map.put("Request", "RemovePersonFromPending");
        map.put("PersonName",PersonNameToRemove);
        map.put("ShopName", shopName);
        JSONObject data=new JSONObject(map);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(URL, data, volleyListener, volleyErrorListener);
        requestQueue.add(jsonObjectRequest);
    }
    public void addPersonToPending(String PersonName){
        pendingList.add(PersonName);
        ShopMenuFrag1.customRecyclerViewAdapterShop.notifyDataSetChanged();
    }
    public void serverAddPersonToPending(){
        final AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView=inflater.inflate(R.layout.addperson_alerdialog_layout, null);
        alertBuilder.setView(dialogView);

        alertBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText personToAdd= dialogView.findViewById(R.id.editText);
                String PersonName=personToAdd.getText().toString();
                Map<String,Object> map=new HashMap<>();
                map.put("Request","AddPersonToPending");
                map.put("PersonName", PersonName);
                map.put("ShopName", shopName);//this could be removed cause will have to send a token
                JSONObject Data=new JSONObject(map);
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(URL, Data, volleyListener, volleyErrorListener);
                requestQueue.add(jsonObjectRequest);
            }
        });

        AlertDialog alertDialog=alertBuilder.create();
        alertDialog.show();

    }
   
    void updateShopInfo(){

            Map<String,Object> map=new HashMap<>();
           
            map.put("EmailAddress", emailAddress);
            map.put("Password", password);
            map.put("FirstName", firstName);
            map.put("LastName", lastName);
            map.put("PhoneNumber", phoneNumber);
            map.put("IsBusinessOwner", isBusinessOwner);
            map.put("ShopName", shopName);
            map.put("SelectedState", selectedState);
            map.put("SelectedCommune", selectedCommune);
            map.put("UseCoordinatesAKAaddMap", useCoordinatesAKAaddMap);
            map.put("ShopLatitude", shopLatitude);
            map.put("ShopLongitude", shopLongitude);
            map.put("IsMen", isMen);
            map.put("SelectedImage", CommonMethods.convertBitmapToString(selectedImage));//photo
            map.put("ShopPhoneNumber", shopPhoneNumber);
            map.put("FacebookLink", facebookLink);
            map.put("InstagramLink", instagramLink);
            map.put("Coiffure", coiffure);
            map.put("MakeUp", makeUp);
            map.put("Meches", meches);
            map.put("Tinte", tinte);
            map.put("Pedcure", pedcure);
            map.put("Manage", manage);
            map.put("Manicure", manicure);
            map.put("coupe", coupe);
            map.put("Saturday", saturday);
            map.put("Sunday", sunday);
            map.put("Monday", monday);
            map.put("Tuesday", tuesday);
            map.put("Wednesday", wednesday);
            map.put("Thursday", thursday);
            map.put("Friday", friday);

        JSONObject updatedShopData=new JSONObject(map);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
