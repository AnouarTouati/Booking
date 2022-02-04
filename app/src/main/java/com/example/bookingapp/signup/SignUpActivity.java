package com.example.bookingapp.signup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.bookingapp.ActivityWithLocation;
import com.example.bookingapp.CustomFragmentPagerAdapter;
import com.example.bookingapp.MainActivity;
import com.example.bookingapp.R;
import com.example.bookingapp.shop.ShopActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends ActivityWithLocation {

    public String emailAddress;
    public String password;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public Boolean isEmployee=false;
    public Boolean isBusinessOwner=false;
    public String salonName;
    public String selectedState;
    public String selectedCommune;
    public Boolean hasLocation =false;
    public Double shopLatitude;
    public Double shopLongitude;
    public Boolean isMen=true;
    public Bitmap selectedImage;
    public String shopPhoneNumber;
    public List<Service_Frag5> services=new ArrayList<>();

    public ViewPager viewPagerSignUP;

    TextView signUpErrorText;
    TextView signUpSuccessfulText;
    CustomRecyclerAdapter customRecyclerAdapter;
    RecyclerView errorMessagesRecyclerView;
    ArrayList<String> errorsList =new ArrayList<>();
    Button retryButton;
    Button goToShopHomePageButton;
    public ProgressBar progressBar;
    Boolean signUpWasNotSuccessful=false;
    Boolean requestWasSentToServer =false;

    Context context;

    private  FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    private SignUpFrag3 signUpFrag3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context =this;

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        getViewsReferences();
        setUpViews();

    }
    private void getViewsReferences(){
        errorMessagesRecyclerView =findViewById(R.id.ErrorMessagesRecyclerView);
        progressBar=findViewById(R.id.progressBarSignUpActivity);
        signUpErrorText=findViewById(R.id.signUpErrorText);
        signUpSuccessfulText=findViewById(R.id.signUpSuccessfulText);
        retryButton=findViewById(R.id.retrySignUp);
        goToShopHomePageButton=findViewById(R.id.goToShopHomePage);
        viewPagerSignUP=findViewById(R.id.viewPagerSignUP);
    }
    private void setUpViews(){
        customRecyclerAdapter =new CustomRecyclerAdapter(errorsList);//initialized empty so we can just swap the adapter later

        errorMessagesRecyclerView.setAdapter(customRecyclerAdapter);//
        errorMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueSignUp();
            }
        });

        goToShopHomePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GoToShopActivityIntent=new Intent(context, ShopActivity.class);
                context.startActivity(GoToShopActivityIntent);
                setResult(RESULT_OK,new Intent());//just to kill sign in activity
                finish();//this to kill this sign up activity

            }
        });

        CustomFragmentPagerAdapter customFragmentPagerAdapter=new CustomFragmentPagerAdapter(getSupportFragmentManager());
        customFragmentPagerAdapter.addFragment(new SignUpFrag1(this), "SignUpFrag1");
        customFragmentPagerAdapter.addFragment(new SignUpFrag2(this), "SignUpFrag2");
        signUpFrag3=new SignUpFrag3(this);
        customFragmentPagerAdapter.addFragment(signUpFrag3, "SignUpFrag3");
        customFragmentPagerAdapter.addFragment(new SignUpFrag4(this), "SignUpFrag4");
        customFragmentPagerAdapter.addFragment(new SignUpFrag5(this), "SignUpFrag5");

        viewPagerSignUP.setAdapter(customFragmentPagerAdapter);
    }
    public void setCurrentItemViewPager(int FragmentIndex){
          viewPagerSignUP.setCurrentItem(FragmentIndex);
    }
    @Override
    protected void onLocationResult(Location location) {
        shopLatitude =location.getLatitude();
        shopLongitude =location.getLongitude();
        Toast.makeText(this, "Latitude"+ shopLatitude, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Longitude"+ shopLongitude, Toast.LENGTH_LONG).show();
        hasLocation =true;
        signUpFrag3.addMapToYourShopCheckBox.setChecked(true);
        signUpFrag3.hasLocation=true;
        signUpFrag3.getAddressFromLocation(location);
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

    public void continueSignUp(){
    viewPagerSignUP.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);

    signUpErrorText.setVisibility(View.GONE);
    retryButton.setVisibility(View.GONE);
    signUpSuccessfulText.setVisibility(View.GONE);
    goToShopHomePageButton.setVisibility(View.GONE);

    final Map<String,Object> map=new HashMap<>();

    if(selectedImage!=null){
        map.put("MainShopPhotoReferenceInStorage","Photos/"+firebaseUser.getUid()+"/MainShopPhoto"+".JPEG");
    }

        map.put("ShopUid", firebaseUser.getUid());
        map.put("EmailAddress", emailAddress);
        map.put("Password", password);
        map.put("FirstName", firstName);
        map.put("LastName", lastName);
        map.put("PhoneNumber", phoneNumber);
        map.put("IsBusinessOwner", isBusinessOwner);
        map.put("ShopName", salonName);
        map.put("SelectedState", selectedState);
        map.put("SelectedCommune", selectedCommune);
        map.put("UseCoordinatesAKAaddMap", hasLocation);
        map.put("ShopLatitude", shopLatitude);
        map.put("ShopLongitude", shopLongitude);
        map.put("IsMen", isMen);

        map.put("ShopPhoneNumber", shopPhoneNumber);
        List<String> servicesName=new ArrayList<>();
        List<String> servicesPrices=new  ArrayList<>();
        List<String> servicesDurations=new ArrayList<>();
        for(int i=0;i<services.size();i++){
            servicesName.add(services.get(i).serviceName);
            servicesPrices.add(services.get(i).servicePrice);
            servicesDurations.add(services.get(i).serviceDuration);
        }
        map.put("ServicesHairCutsNames", servicesName);
        map.put("ServicesHairCutsPrices", servicesPrices);
        map.put("ServicesHairCutsDuration", servicesDurations);


    firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            if(map.containsKey("MainShopPhotoReferenceInStorage")){
                pushPhotoToServer(map.get("MainShopPhotoReferenceInStorage").toString(),selectedImage);
            }else{
                successful();
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            notSuccessful("couldn't send shop data to cloud");
            Log.e("AppFilter", "Something went wrong continueSignUp()"  + e.getMessage());
            }
    });
}
    private void pushPhotoToServer(final String imageReferenceInStorage, final Bitmap imageToPush) {
        turnOnProgressBar();
        StorageReference imageReference = firebaseStorage.getReference();
        imageReference = imageReference.child(imageReferenceInStorage);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageToPush.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageInBytes = byteArrayOutputStream.toByteArray();
        UploadTask uploadImageTask = imageReference.putBytes(imageInBytes);
        uploadImageTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    turnOffProgressBar();
                    Toast.makeText(getApplicationContext(), "Done Signing  up", Toast.LENGTH_LONG).show();
                    successful();
                } else {

                    turnOffProgressBar();
                    Toast.makeText(getApplicationContext(), "Something went wrong we couldn't sign you up", Toast.LENGTH_LONG).show();
                    Log.e("AppFilter", "Something went wrong we couldn't Uploading Profile image" + "onComplete callback Push Image" + task.getException().getMessage());
                    somethingWentWrongPleaseTryAgainImageProblem(imageReferenceInStorage,imageToPush);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                turnOffProgressBar();
                Log.e("AppFilter", "Something went wrong we couldn't Uploading Profile image" + "onComplete callback Push Image" + e.getMessage());
            }
        });

    }
    private void somethingWentWrongPleaseTryAgainImageProblem(final String imageReferenceInStorage, final Bitmap imageToPush){

        AlertDialog.Builder alertDialogBuilder=  new AlertDialog.Builder(this).setTitle("Failed To Complete Sign UP")
                .setMessage("Something went wrong and we couldn't sign you up, let's give it another go shall we")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener(){
                    public void onClick( DialogInterface dialog,int id){
                        pushPhotoToServer(imageReferenceInStorage,imageToPush);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProfileAndGoBackToSignUpActivity();
                    }
                });
        alertDialogBuilder.create().show();
    }

    private void deleteProfileAndGoBackToSignUpActivity(){
        firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).delete();
        firebaseUser.delete();
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

      public void createAccount(){
        turnOnProgressBar();
    mAuth.createUserWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                turnOffProgressBar();
                firebaseUser=task.getResult().getUser();
                viewPagerSignUP.setCurrentItem(1);
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            notSuccessful("couldn't create an account");
            Log.e("AppFilter", "couldn't create an account "  + e.getMessage());
        }
    });
}

    void notSuccessful(String message){
        signUpWasNotSuccessful=true;
        progressBar.setVisibility(View.GONE);

        signUpErrorText.setVisibility(View.VISIBLE);
        signUpErrorText.setText(message);
        retryButton.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
            findViewById(R.id.MainConstraintLayout_Frag5).setVisibility(View.GONE);
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
            findViewById(R.id.MainConstraintLayout_Frag5).setVisibility(View.VISIBLE);
        }
    }


}
