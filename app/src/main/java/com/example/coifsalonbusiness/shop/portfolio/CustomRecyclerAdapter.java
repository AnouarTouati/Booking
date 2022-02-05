package com.example.coifsalonbusiness.shop.portfolio;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.coifsalonbusiness.R;

import java.util.ArrayList;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

    ArrayList<Image> portfolioImages =new ArrayList<>();
    Context mContext;
    Portfolio parentActivity;
    public CustomRecyclerAdapter( ArrayList<Image> portfolioImages, Context mContext, Portfolio parentActivity) {
        this.portfolioImages = portfolioImages;
        this.mContext=mContext;
        this.parentActivity=parentActivity;
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
            viewHolder.imageView.setImageBitmap(portfolioImages.get(i).bitmap);
            viewHolder.deletePortfolioImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parentActivity.removeImageFromServer(portfolioImages.get(i).reference);
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
