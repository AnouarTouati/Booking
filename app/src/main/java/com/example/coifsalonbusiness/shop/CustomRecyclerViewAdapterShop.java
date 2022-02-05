package com.example.coifsalonbusiness.shop;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.coifsalonbusiness.R;

import java.util.ArrayList;

public class CustomRecyclerViewAdapterShop extends RecyclerView.Adapter<CustomRecyclerViewAdapterShop.ViewHolder> {

    ArrayList<ClientPending> peoplePending =new ArrayList<>();
    Context mContext;
    ShopActivity shopActivityReference;

    public CustomRecyclerViewAdapterShop(Context context, ArrayList<ClientPending> PeoplePending,ShopActivity shopActivityReference) {
        this.peoplePending =PeoplePending;
        this.mContext=context;
        this.shopActivityReference=shopActivityReference;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_people_pending_item, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final int t=i;
           viewHolder.personNameTextView.setText(peoplePending.get(i).getClientName());
           viewHolder.deletePersonButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
             shopActivityReference.serverRemovePersonFromPending(peoplePending.get(t));
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
