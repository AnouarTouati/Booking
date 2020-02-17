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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    public static String emailAddress;
    public static String password;
    public String firstName;
    public String lastName;
    public static String phoneNumber;
    public Boolean isEmployee=false;
    public Boolean isBusinessOwner=false;
    public String salonName;
    public String selectedState;
    public String selectedCommune;
    public Boolean useCoordinatesAKAaddMap =false;
    public Double shopLatitude;
    public Double shopLongitude;
    public Boolean isMen=true;
    public Bitmap selectedImage;
    public static String shopPhoneNumber;
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

    static final String SIGN_UP_URL ="http://192.168.43.139:8888/Business.php";

    public  static ViewPager viewPagerSignUP;
    final int LOCATION_REQ=10;
    final int GPS_SETTING_REQ=5;
    Boolean comingBackFromLocationSettings =false;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView signUpErrorText;
    TextView signUpSuccessfulText;
    CustomRecyclerViewAdapterSignUpErrors customRecyclerViewAdapterSignUpErrors;
    RecyclerView errorMessagesRecyclerView;
    ArrayList<String> errorsList =new ArrayList<>();
    Button retryButton;
    Button goToShopHomePageButton;
    public static ProgressBar progressBar;
    Boolean signUpWasNotSuccessful=false;
    static Boolean requestWasSentToServer =false;
    static Response.Listener<JSONObject> volleyListener;
    static Response.ErrorListener volleyErrorListener;
    static RequestQueue requestQueue;
    static Context mContext;

    String tokenReceived;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContext=this;

        mAuth=FirebaseAuth.getInstance();

        volleyListener =new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("VolleyReceived","VolleyReceived in SignUpActivity "+response.toString());

                if(response.has("SignUp")){
                    try {
                        if(response.getJSONObject("SignUp").getString("Successful").equals("True")){
                            tokenReceived =response.getJSONObject("SignUp").getString("Token");
                            successful();
                        }else{
                            notSuccessful();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(response.has("CheckAndRegister")){
                    try {
                        serverResponseCheckAndRegister(response.getJSONObject("CheckAndRegister"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        volleyErrorListener =new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(error.networkResponse==null){
                    Log.v("VolleyError","VolleyError in SignUpActivity  Null error.networkResponse");
                    signUpErrorText.setText("Couldn't Reach The Server Please Check Your Internet Connection");
                    signUpErrorText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    retryButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            turnOnProgressBar();
                            sendToServerToCheckAndRegister(viewPagerSignUP.getCurrentItem()+1);
                        }
                    });
                    retryButton.setVisibility(View.VISIBLE);
                }
               else{
                    Log.v("VolleyError","VolleyError in SignUpActivity "+error.toString());

                    signUpErrorText.setText("Connection Timed Out");
                    signUpErrorText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    retryButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            turnOnProgressBar();
                            sendToServerToCheckAndRegister(viewPagerSignUP.getCurrentItem()+1);
                        }
                    });
                    retryButton.setVisibility(View.VISIBLE);
               }


            }
        };
        requestQueue= Volley.newRequestQueue(this);
        customRecyclerViewAdapterSignUpErrors=new CustomRecyclerViewAdapterSignUpErrors(errorsList);//initialized empty so we can just swap the adapter later
        errorMessagesRecyclerView =findViewById(R.id.ErrorMessagesRecyclerView);
        errorMessagesRecyclerView.setAdapter(customRecyclerViewAdapterSignUpErrors);//
        errorMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar=findViewById(R.id.progressBarSignUp);
        signUpErrorText=findViewById(R.id.signUpErrorText);
        signUpSuccessfulText=findViewById(R.id.signUpSuccessfulText);
        retryButton=findViewById(R.id.retrySignUp);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        goToShopHomePageButton=findViewById(R.id.goToShopHomePage);
        goToShopHomePageButton.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         Intent GoToShopActivityIntent=new Intent(mContext,ShopActivity.class);
         GoToShopActivityIntent.putExtra("Token", tokenReceived);
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
    public void setCurrentItemViewPager(int FragmentIndex){
          viewPagerSignUP.setCurrentItem(FragmentIndex);
    }

   public  void findLocationUsingGPS(){

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
                        comingBackFromLocationSettings =true;
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

                        saveTheCoordinatesAndFindAddress(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());

                    }
                };
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());

            }}


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(comingBackFromLocationSettings){
        LocationManager locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
           findLocationUsingGPS();
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
        comingBackFromLocationSettings =false;
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

            findLocationUsingGPS();
        }
    }



    void saveTheCoordinatesAndFindAddress(Double Latitude, Double Longitude){
        shopLatitude =Latitude;
        shopLongitude =Longitude;
        Toast.makeText(this, "Latitude"+ shopLatitude, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Longitude"+ shopLongitude, Toast.LENGTH_LONG).show();
        useCoordinatesAKAaddMap =true;
        Intent intent=new Intent();
        intent.setAction("ComingFromSignUpActivity");
        intent.putExtra("FoundCoordinatesSuccessfully", useCoordinatesAKAaddMap);
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
        if(requestWasSentToServer){
           //cancel the requests here
            turnOffProgressBar();
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
                setCurrentItemViewPager(viewPagerSignUP.getCurrentItem()-1);
            }else{
                super.onBackPressed();
            }
        }

    }
public void signUp(){
    viewPagerSignUP.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);

    signUpErrorText.setVisibility(View.GONE);
    retryButton.setVisibility(View.GONE);
    signUpSuccessfulText.setVisibility(View.GONE);
    goToShopHomePageButton.setVisibility(View.GONE);

    Map<String,Object> map=new HashMap<>();
    
        map.put("Request","SignUp");
        map.put("EmailAddress", emailAddress);
        map.put("Password", password);
        map.put("FirstName", firstName);
        map.put("LastName", lastName);
        map.put("PhoneNumber", phoneNumber);
        map.put("IsBusinessOwner", isBusinessOwner);
        map.put("ShopName", salonName);
        map.put("SelectedState", selectedState);
        map.put("SelectedCommune", selectedCommune);
        map.put("UseCoordinatesAKAaddMap", useCoordinatesAKAaddMap);
        map.put("ShopLatitude", shopLatitude);
        map.put("ShopLongitude", shopLongitude);
        map.put("IsMen", isMen);
        map.put("SelectedImage", convertBitmapToString(selectedImage));//photo
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
        map.put("Coupe", coupe);
        map.put("Saturday", saturday);
        map.put("Sunday", sunday);
        map.put("Monday", monday);
        map.put("Tuesday", tuesday);
        map.put("Wednesday", wednesday);
        map.put("Thursday", thursday);
        map.put("Friday", friday);

   
    JSONObject data=new JSONObject(map);

    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, SIGN_UP_URL, data, volleyListener, volleyErrorListener);

 /*   FirebaseFirestore db=FirebaseFirestore.getInstance();
db.collection("Shops").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
    @Override
    public void onSuccess(DocumentReference documentReference) {
          Toast.makeText(getApplicationContext(),""+documentReference.getId(),Toast.LENGTH_LONG).show();

    }
});
*/
  //  requestQueue.add(jsonObjectRequest);
    mAuth.createUserWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                FirebaseUser firebaseUser=mAuth.getCurrentUser();
                 successful();
            }
            else{
           notSuccessful();
            }
        }
    });

}


    public static void sendToServerToCheckAndRegister(int nextIndexInPagerAdapter){

        Map<String,Object> map=new HashMap<>();
        map.put("Request","CheckAndRegister");

        if(nextIndexInPagerAdapter==1 && viewPagerSignUP.getCurrentItem()==0){
        map.put("EmailAddress", emailAddress);
        map.put("Password", password);
        }
            else if(nextIndexInPagerAdapter==2 && viewPagerSignUP.getCurrentItem()==1){
                map.put("PhoneNumber", phoneNumber);
            }

            else if(nextIndexInPagerAdapter==4 && viewPagerSignUP.getCurrentItem()==3){
                map.put("ShopPhoneNumber", shopPhoneNumber);
        }
        JSONObject data =new JSONObject(map);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(SIGN_UP_URL, data, volleyListener, volleyErrorListener);
        requestQueue.add(jsonObjectRequest);
        requestWasSentToServer =true;
    }

    void serverResponseCheckAndRegister(JSONObject response){
        turnOffProgressBar();
       errorsList.clear();
      if(response.has("EmailAddress")&& response.has("Password")){
          try {
              if(response.getString("EmailAddress").equals("Ok") && response.getString("Password").equals("Ok") && viewPagerSignUP.getCurrentItem()==0){
                setCurrentItemViewPager(1);
              }else {
                  if(!response.getString("EmailAddress").equals("Ok")){
                      errorsList.add("-Please Choose Another Email Address");
                  }
                  if(!response.getString("Password").equals("Ok")){
                      errorsList.add("-The Password You Entered Doesn't Meet The Requirements");
                  }
              }
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }
      if(response.has("PhoneNumber")){
          try {
              if(response.getString("PhoneNumber").equals("Ok") && viewPagerSignUP.getCurrentItem()==1){
                  setCurrentItemViewPager(2);
              }else{
                  errorsList.add("-The Phone Number You Entered Is Already Used");
              }
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }

      if(response.has("ShopPhoneNumber")){
          try {
              if(response.getString("ShopPhoneNumber").equals("Ok") && viewPagerSignUP.getCurrentItem()==3){
                setCurrentItemViewPager(4);
              }
              else{
                  errorsList.add("-The Shop Phone Number Is Already Used");
              }
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }
      if(errorsList.size()>0){//greater than zero meaning we have an error

          errorMessagesRecyclerView.swapAdapter(new CustomRecyclerViewAdapterSignUpErrors(errorsList), true);
          errorMessagesRecyclerView.setVisibility(View.VISIBLE);//will make it invisible in turn on progress bar
      }
    }
    String convertBitmapToString(Bitmap image){

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP,85,byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
    void notSuccessful(){
        signUpWasNotSuccessful=true;
        progressBar.setVisibility(View.GONE);

        signUpErrorText.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Not SuccessfulSignIn", Toast.LENGTH_LONG).show();
    }
    void successful(){
        signUpWasNotSuccessful=false;
        progressBar.setVisibility(View.GONE);
        signUpSuccessfulText.setVisibility(View.VISIBLE);
        goToShopHomePageButton.setVisibility(View.VISIBLE);

    }
    public void turnOnProgressBar(){

        //COMMON BETWEEN TURN ON/OFF PROGRESS BAR
        signUpErrorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        ////////////////////////////////////////

        errorMessagesRecyclerView.setVisibility(View.GONE);//SET VISIBLE IS IN SERVERRESPONSECHECKANDREGISTER
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
    public void turnOffProgressBar(){

        //COMMON BETWEEN TURN ON/OFF PROGRESS BAR
        signUpErrorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        ////////////////////////////////////////

        requestWasSentToServer =false;
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
