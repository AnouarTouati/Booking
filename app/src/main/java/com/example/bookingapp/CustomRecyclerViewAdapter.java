package com.example.bookingapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.DrawableWrapper;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomRecyclerViewAdapter extends Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    ArrayList<Image> ShopsMainImages;
    ArrayList<String> ShopsNames = new ArrayList<>();
    ArrayList<String> ShopsAddresses = new ArrayList<>();

    Context mContext;

    public CustomRecyclerViewAdapter(Context context, ArrayList<Image> ShopsMainImages, ArrayList<String> ShopsNames, ArrayList<String> ShopsAddresses) {

        this.ShopsNames = ShopsNames;
        this.ShopsAddresses = ShopsAddresses;
        //  this.ShopsMainImages=ShopsMainImages;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_result_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomRecyclerViewAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.SearchResultShopMainImage.setImageResource(R.drawable.animage);
        viewHolder.SearchResultShopName.setText(ShopsNames.get(i));
        viewHolder.SearchResultShopAddress.setText(ShopsAddresses.get(i));
        viewHolder.RecyclerViewResultItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start the ShopDetails Activity here without killing the main one
                //  Intent GoToShopDetailsActivityIntent=new Intent(mContext.this,ShopDetailsActivity.class);
                Intent GoToShopDetailsActivity = new Intent(mContext, ShopDetailsActivity.class);
                GoToShopDetailsActivity.putExtra("ShopName", ShopsNames.get(i));
                mContext.startActivity(GoToShopDetailsActivity);


            }
        });
    }

    @Override
    public int getItemCount() {
        return ShopsNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView SearchResultShopMainImage;
        TextView SearchResultShopName;
        TextView SearchResultShopAddress;
        ConstraintLayout RecyclerViewResultItemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            SearchResultShopMainImage = itemView.findViewById(R.id.searchResultShopMainImage);
            SearchResultShopName = itemView.findViewById(R.id.searchResultShopName);
            SearchResultShopAddress = itemView.findViewById(R.id.searchResultShopAddress);
            RecyclerViewResultItemLayout = itemView.findViewById(R.id.recyclerViewResultItemLayout);
        }
    }
}
