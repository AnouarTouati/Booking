package com.example.bookingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

public class ShopMenuFrag2 extends Fragment {

    View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.shopmenufrag2_layout, container,false);
        Button AddPortfolioImagesButton=view.findViewById(R.id.AddPortfolioImagesButton_ShopMenuFrag2);
        AddPortfolioImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent GoToPortfolioImages_SubActivity=new Intent(getContext(),AddRemovePortfolioImages_SubActivity_ShopActivity.class);
             startActivity(GoToPortfolioImages_SubActivity);
            }
        });
        return view;
    }




}
