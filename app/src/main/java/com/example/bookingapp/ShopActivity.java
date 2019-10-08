package com.example.bookingapp;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {




    public static String URL="http://192.168.43.139:81/ThirdPage.php";
    static RequestQueue requestQueue;
    static Response.Listener<JSONObject> volleyListener;
    static Response.ErrorListener volleyErrorListener;
    String TokenToUseComingFromSigninActivity;
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
    public static String ShopName;
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        mContext=this;

        TokenToUseComingFromSigninActivity=getIntent().getStringExtra("Token");
        if(TokenToUseComingFromSigninActivity==""){
            Intent goBACKtoSignInActivity=new Intent(this,SignInActivity.class);
            startActivity(goBACKtoSignInActivity);
        }

        volleyListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              Log.v("VolleyReceived","Volley Received Shop Activity "+response.toString());

              if(response.has("RemovePersonFromPending")){
                  try {
                      RemovePersonFromPending(response.getString("RemovePersonFromPending"));
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }

              if(response.has("AddPersonToPending")){
                  try {
                      AddPersonToPending(response.getString("AddPersonToPending"));
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
            }
        };

        requestQueue= Volley.newRequestQueue(this);

        viewPager=findViewById(R.id.viewPagerShopMenu);
        customFragmentPagerAdapter=new CustomFragmentPagerAdapter(getSupportFragmentManager());
        customFragmentPagerAdapter.addFragment(new ShopMenuFrag1(), "ShopMenuFrag1");
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

    }





    public static void RemovePersonFromPending(String PersonNameToRemove){
        pendingList.remove(PersonNameToRemove);
       ShopMenuFrag1.customRecyclerViewAdapterShop.notifyDataSetChanged();
       Toast.makeText(mContext, "remove "+PersonNameToRemove, Toast.LENGTH_LONG).show();
    }
    public static void Server_RemovePersonFromPending(String PersonNameToRemove){
        Map<String,Object> map=new HashMap<>();
        map.put("Request", "RemovePersonFromPending");
        map.put("PersonName",PersonNameToRemove);
        map.put("ShopName",ShopName);
        JSONObject Data=new JSONObject(map);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(URL, Data, volleyListener, volleyErrorListener);
        requestQueue.add(jsonObjectRequest);
    }
    public void AddPersonToPending(String PersonName){
        pendingList.add(PersonName);
        ShopMenuFrag1.customRecyclerViewAdapterShop.notifyDataSetChanged();
    }
    public void Server_AddPersonToPending(){
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
                map.put("ShopName", ShopName);//this could be removed cause will have to send a token
                JSONObject Data=new JSONObject(map);
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(URL, Data, volleyListener, volleyErrorListener);
                requestQueue.add(jsonObjectRequest);
            }
        });

        AlertDialog alertDialog=alertBuilder.create();
        alertDialog.show();

    }
   
    void UpdateShopInfo(){

            Map<String,Object> map=new HashMap<>();
           
            map.put("EmailAddress", EmailAddress);
            map.put("Password",Password );
            map.put("FirstName",FirstName );
            map.put("LastName",LastName );
            map.put("PhoneNumber", PhoneNumber);
            map.put("isBusinessOwner", isBusinessOwner);
            map.put("ShopName", ShopName);
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

        JSONObject UpdatedShopData=new JSONObject(map);

    }
    String ConvertBitmapToString(Bitmap image){

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP,85,byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
}
