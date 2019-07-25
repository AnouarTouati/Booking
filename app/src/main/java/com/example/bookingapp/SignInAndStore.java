package com.example.bookingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class SignInAndStore extends AppCompatActivity {

    final String SignInURL="";



    RequestQueue requestQueue;
    Response.Listener<JSONObject> volleyListener;
    Response.ErrorListener volleyErrorListener;
    static Context mContext;
    static ArrayList<String> pendingList=new ArrayList<>();
    CustomFragmentPagerAdapter customFragmentPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;




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
    public Double StoreLatitude;
    public Double StoreLongitude;
    public Boolean isMen=true;
    public Bitmap SelectedImage;
    public String StorePhoneNumber;
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_and_store);
        mContext=this;

        volleyListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response.has("SignInResponse")){
                    Successful();
                }else{
                    NotSuccessful();
                }
            }
        };
        volleyErrorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NotSuccessful();
            }
        };

        requestQueue= Volley.newRequestQueue(this);

        viewPager=findViewById(R.id.viewPagerStoreMenu);
        customFragmentPagerAdapter=new CustomFragmentPagerAdapter(getSupportFragmentManager());
        customFragmentPagerAdapter.addFragment(new StoreMenuFrag1(), "StoreMenuFrag1");
        customFragmentPagerAdapter.addFragment(new SignUpFrag3(), "dfedf");
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
        tabLayout=findViewById(R.id.tabLayoutStoreMenu);

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


    }
    void SignIn(String Email,String Password){

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("EmailAddress", Email);
            jsonObject.put("Password",Password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request<JSONObject> request=new JsonObjectRequest(Request.Method.POST, SignInURL, jsonObject, volleyListener, volleyErrorListener);
        requestQueue.add(request);
    }
    void  Successful(){

    }
    void  NotSuccessful(){
        Toast.makeText(this, "Failed to sign in", Toast.LENGTH_LONG).show();
    }
    public static void RemovePersonFromPending(String personNameToRemove){
        //we notify the server here
        pendingList.remove(personNameToRemove);

       Toast.makeText(mContext, "remove "+personNameToRemove, Toast.LENGTH_LONG).show();
    }
    public void AddPersonToPending(){
        final AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView=inflater.inflate(R.layout.addperson_alerdialog_layout, null);
        alertBuilder.setView(dialogView);

        alertBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText personToAdd= dialogView.findViewById(R.id.editText);
                String personName=personToAdd.getText().toString();
                AddToPendingListAndUpdateUI(personName);
            }
        });

        AlertDialog alertDialog=alertBuilder.create();
        alertDialog.show();

    }
    void AddToPendingListAndUpdateUI(String personName){
       //we notify the server here
        pendingList.add(personName);

    }
    void UpdateStoreInfo(){
        JSONObject signUpJsonObject=new JSONObject();
        try {

            signUpJsonObject.put("EmailAddress", EmailAddress);
            signUpJsonObject.put("Password",Password );
            signUpJsonObject.put("FirstName",FirstName );
            signUpJsonObject.put("LastName",LastName );
            signUpJsonObject.put("PhoneNumber", PhoneNumber);
            signUpJsonObject.put("isBusinessOwner", isBusinessOwner);
            signUpJsonObject.put("SalonName",SalonName );
            signUpJsonObject.put("SelectedState", SelectedState);
            signUpJsonObject.put("SelectedCommune",SelectedCommune );
            signUpJsonObject.put("UseCoordinatesAKAaddMap",UseCoordinatesAKAaddMap );
            signUpJsonObject.put("StoreLatitude", StoreLatitude);
            signUpJsonObject.put("StoreLongitude", StoreLongitude);
            signUpJsonObject.put("isMen", isMen);
            signUpJsonObject.put("SelectedImage", ConvertBitmapToString(SelectedImage));//photo
            signUpJsonObject.put("StorePhoneNumber", StorePhoneNumber);
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

    }
    String ConvertBitmapToString(Bitmap image){

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP,85,byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
}
