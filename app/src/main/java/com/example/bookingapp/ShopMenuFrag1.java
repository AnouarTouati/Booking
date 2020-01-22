package com.example.bookingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ShopMenuFrag1 extends Fragment {
    EditText emailAddressEditText;
    EditText passwordEditText;
    Button signInButton;
    RecyclerView peoplePendingRecyclerView;
    static CustomRecyclerViewAdapterShop customRecyclerViewAdapterShop;

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.shopmenufrag1_layout, container,false);

        emailAddressEditText=view.findViewById(R.id.signInEmail);
        passwordEditText=view.findViewById(R.id.signInPassword);
        Button addPerson=view.findViewById(R.id.addPerson);
        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ShopActivity)getActivity()).serverAddPersonToPending();
            }
        });
        peoplePendingRecyclerView=view.findViewById(R.id.peoplePendingRecyclerView);
        ((ShopActivity)getActivity()).pendingList.clear();
        ((ShopActivity)getActivity()).pendingList.add("Marouan Touati");
        ((ShopActivity)getActivity()). pendingList.add("Anouar Touati");
        ((ShopActivity)getActivity()). pendingList.add("Touati Mar");
        ((ShopActivity)getActivity()). pendingList.add("Touati An");


        customRecyclerViewAdapterShop =new CustomRecyclerViewAdapterShop(getActivity(),  ((ShopActivity)getActivity()).pendingList);
        peoplePendingRecyclerView.setAdapter(customRecyclerViewAdapterShop);
        peoplePendingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    static void removePersonFromPending(String personNameToRemove){
        ShopActivity.serverRemovePersonFromPending(personNameToRemove);

    }

}
