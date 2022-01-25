package com.example.bookingapp.shop;

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

import com.example.bookingapp.CommonMethods;
import com.example.bookingapp.CustomFragmentPagerAdapter;
import com.example.bookingapp.R;
import com.example.bookingapp.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopActivity extends AppCompatActivity {


    static Context mContext;
    static ArrayList<ClientPending> pendingList=new ArrayList<>();
    CustomFragmentPagerAdapter customFragmentPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    TextView errorText;
    ProgressBar progressBar;

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

    private static FirebaseUser firebaseUser;
    private static FirebaseFirestore firebaseFirestore;
    private static FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        mContext=this;

        progressBar=findViewById(R.id.progressBarShopActivity);

        if(firebaseUser ==null){
            Intent goBACKtoSignInActivity=new Intent(this, SignInActivity.class);
            startActivity(goBACKtoSignInActivity);
        }
         firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        errorText =findViewById(R.id.ErrorTextView_ShopActivity);
        viewPager=findViewById(R.id.viewPagerShopMenu);
        customFragmentPagerAdapter=new CustomFragmentPagerAdapter(getSupportFragmentManager());
        customFragmentPagerAdapter.addFragment(new ShopMenuFrag1(this), "ShopMenuFrag1");
        customFragmentPagerAdapter.addFragment(new ShopMenuFrag2(this), "ShopMenuFrag2");
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
    public void findLocationUsingGPS(){

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
                turnOnProgressBar();
            }}

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

   void addUpdateLocationMapSuccessful(Context mContext,double Latitude, double Longitude){
      ///i might show the map here or use gcoder to show the user where we put the point
       Toast.makeText(mContext, "Successfully Added the map to your shop at these coordinates Latitude: "+Latitude+"  Longitude: "+Longitude, Toast.LENGTH_LONG).show();
       turnOffProgressBar();
   }
   void serverAddUpdateLocationMap(final double Latitude, final double Longitude){
       turnOnProgressBar();
        Map<String,Object> map=new HashMap<>();
        map.put("ShopLatitude",Latitude);
        map.put("ShopLongitude",Longitude);

       firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   addUpdateLocationMapSuccessful(mContext,Latitude,Longitude);
               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
            notSuccessful("Failed to update location info");
           }
       });
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

    public  void removePersonFromPending(ClientPending personToRemove){
        pendingList.remove(personToRemove);
        ((ShopMenuFrag1) customFragmentPagerAdapter.getItem(0)).customRecyclerViewAdapterShop.notifyDataSetChanged();
       turnOffProgressBar();

    }
    public  void serverRemovePersonFromPending(final ClientPending personToRemove){
        turnOnProgressBar();
        String personToRemoveUidOnFirestore;
        if(!personToRemove.getClientFirebaseUid().equals("null")){//means added from client phone(client app)
            personToRemoveUidOnFirestore=personToRemove.getClientFirebaseUid();
        }else{//means added from shop owner phone (business app)
             personToRemoveUidOnFirestore=personToRemove.getClientFakeFirebaseUid();
        }
       firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).collection("ClientsPending").document(personToRemoveUidOnFirestore).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   removePersonFromPending(personToRemove);
               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Log.v("MyFirebase","Could not delete person from pending");
               notSuccessful("Could not delete person from pending");
           }
       });
    }
    public void addPersonToPending(ClientPending personToAdd){
        pendingList.add(personToAdd);
        ((ShopMenuFrag1) customFragmentPagerAdapter.getItem(0)).customRecyclerViewAdapterShop.notifyDataSetChanged();
       turnOffProgressBar();
    }
    public void serverAddPersonToPending(){
        final AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView=inflater.inflate(R.layout.addperson_alerdialog_layout, null);
        alertBuilder.setView(dialogView);

        alertBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                turnOnProgressBar();
                EditText personToAdd= dialogView.findViewById(R.id.editText);
                final String PersonName=personToAdd.getText().toString();
                final String clientFakeFirebaseUid=UUID.randomUUID().toString();
                Map<String,Object> map=new HashMap<>();
               //we may want to add want kind of service this client
                map.put("PersonName", PersonName);
                map.put("ClientFakeFirebaseUid",clientFakeFirebaseUid);
                map.put("ClientFireBaseUid","null");
                map.put("Services","null");

                firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).collection("ClientsPending").document(clientFakeFirebaseUid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            addPersonToPending(new ClientPending(PersonName,"null","N/A",clientFakeFirebaseUid));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    notSuccessful("Couldn't add "+PersonName+" to the list of pending people");
                    }
                });
            }
        });

        AlertDialog alertDialog=alertBuilder.create();
        alertDialog.show();

    }
   public void getPendingList(final ShopMenuFrag1 shopMenuFrag1){
        turnOnProgressBar();
        firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).collection("ClientsPending").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> clientsPendingList=task.getResult().getDocuments();
                    pendingList.clear();
                    for(int i=0;i<clientsPendingList.size();i++){
                        pendingList.add(new ClientPending(clientsPendingList.get(i).get("PersonName").toString(),clientsPendingList.get(i).get("ClientFireBaseUid").toString(),clientsPendingList.get(i).get("Services").toString(),clientsPendingList.get(i).get("ClientFakeFirebaseUid").toString()));
                }
                    shopMenuFrag1.customRecyclerViewAdapterShop.notifyDataSetChanged();
                    turnOffProgressBar();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notSuccessful("Couldn't get clients pending");
            }
        });
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
          //  map.put("SelectedImage", CommonMethods.convertBitmapToString(selectedImage));//photo
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
    private void notSuccessful(String message){
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
        turnOffProgressBar();
    }
    void turnOnProgressBar(){
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }
    void turnOffProgressBar(){
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
