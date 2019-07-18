package com.example.bookingapp;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
    public Boolean isMen=true;
    public Bitmap SelectedImage;
    public String StorePhoneNumber;
    public String FacebookLink;
    public String InstagramLink;

    ViewPager viewPagerSignUP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        viewPagerSignUP=findViewById(R.id.viewPagerSignUP);
        CustomFragmentPagerAdapter customFragmentPagerAdapter=new CustomFragmentPagerAdapter(getSupportFragmentManager());
        customFragmentPagerAdapter.addFragment(new SignUpFrag5(), "SignUpFrag5");
        customFragmentPagerAdapter.addFragment(new SignUpFrag1(), "SignUpFrag1");
        customFragmentPagerAdapter.addFragment(new SignUpFrag2(), "SignUpFrag2");
        customFragmentPagerAdapter.addFragment(new SignUpFrag3(), "SignUpFrag3");
        customFragmentPagerAdapter.addFragment(new SignUpFrag4(), "SignUpFrag4");

        viewPagerSignUP.setAdapter(customFragmentPagerAdapter);

    }
    public void SetCurrentItemViewPager(int FragmentIndex){
          viewPagerSignUP.setCurrentItem(FragmentIndex);
    }

    @Override
    public void onBackPressed() {
        if(viewPagerSignUP.getCurrentItem()>0){
            SetCurrentItemViewPager(viewPagerSignUP.getCurrentItem()-1);
        }else{
            super.onBackPressed();
        }
    }

}
