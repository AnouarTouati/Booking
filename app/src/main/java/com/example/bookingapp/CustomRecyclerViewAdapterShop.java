package com.example.bookingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomRecyclerViewAdapterShop extends RecyclerView.Adapter<CustomRecyclerViewAdapterShop.ViewHolder> {

    ArrayList<String> peoplePending =new ArrayList<>();
    Context mContext;

    public CustomRecyclerViewAdapterShop(Context context, ArrayList<String> PeoplePending) {
        this.peoplePending =PeoplePending;
        this.mContext=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_people_pending_item, viewGroup,false);
       ViewHolder viewHolder=new ViewHolder(view);
       return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final int t=i;
           viewHolder.personNameTextView.setText(peoplePending.get(i));
           viewHolder.deletePersonButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
             ShopMenuFrag1.removePersonFromPending(peoplePending.get(t));
               }
           });
    }

    @Override
    public int getItemCount() {
        return peoplePending.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView personNameTextView;
        Button deletePersonButton;
        ConstraintLayout recyclerViewPeoplePendingItemLayout;
        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            personNameTextView=itemView.findViewById(R.id.personNameText);
            deletePersonButton=itemView.findViewById(R.id.deletePerson);
            recyclerViewPeoplePendingItemLayout=itemView.findViewById(R.layout.recycler_view_people_pending_item);
        }
    }
}
