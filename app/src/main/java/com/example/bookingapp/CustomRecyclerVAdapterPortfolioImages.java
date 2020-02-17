package com.example.bookingapp;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class CustomRecyclerVAdapterPortfolioImages extends RecyclerView.Adapter<CustomRecyclerVAdapterPortfolioImages.ViewHolder> {

    ArrayList<Bitmap> portfolioImages =new ArrayList<>();
    Context mContext;
    public CustomRecyclerVAdapterPortfolioImages(ArrayList<Bitmap> portfolioImages, Context mContext) {
        this.portfolioImages = portfolioImages;
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
            viewHolder.imageView.setImageBitmap(portfolioImages.get(i));
            viewHolder.deletePortfolioImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddRemovePortfolioImages_SubActivity_ShopActivity.removeImageFromServer(i);
                }
            });
    }

    @Override
    public int getItemCount() {
        return portfolioImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button deletePortfolioImageButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.PortfolioImageImageView_Item);
            deletePortfolioImageButton =itemView.findViewById(R.id.DeletePortfolioImage);
        }
    }
}
