package com.example.bookingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class CustomRecyclerVAdapterPortfolioImages extends RecyclerView.Adapter<CustomRecyclerVAdapterPortfolioImages.ViewHolder> {

    ArrayList<Bitmap> PortfolioImages=new ArrayList<>();
    Context mContext;
    public CustomRecyclerVAdapterPortfolioImages(ArrayList<Bitmap> portfolioImages, Context mContext) {
        this.PortfolioImages = portfolioImages;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view= LayoutInflater.from(mContext).inflate(R.layout.recycler_view_portfolio_images_item, viewGroup,false);
    ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
            viewHolder.imageView.setImageBitmap(PortfolioImages.get(i));
            viewHolder.DeletePortfolioImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddRemovePortfolioImages_SubActivity_ShopActivity.RemoveImageFromServer(i);
                }
            });
    }

    @Override
    public int getItemCount() {
        return PortfolioImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button DeletePortfolioImageButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.PortfolioImageImageView_Item);
            DeletePortfolioImageButton=itemView.findViewById(R.id.DeletePortfolioImage);
        }
    }
}
