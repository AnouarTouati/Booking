package com.example.bookingapp;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class ShopDetailsActivity extends AppCompatActivity {

    CustomFragmentPagerAdapter customFragmentPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);

        SetUpViewPager(viewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setViewPager(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void SetUpViewPager(ViewPager viewPager) {
        CustomFragmentPagerAdapter customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        customFragmentPagerAdapter.addFragment(new BookFrag(), "BookFrag");
        customFragmentPagerAdapter.addFragment(new HairCutsFrag(), "HairCutsFrag");
        viewPager.setAdapter(customFragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    }

    public void setViewPager(int FragmentIndex) {
        viewPager.setCurrentItem(FragmentIndex);
    }
}
