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

import java.util.ArrayList;

public class StoreMenuFrag1 extends Fragment {
    EditText emailAddressEditText;
    EditText passwordEditText;
    Button signInButton;
    RecyclerView peoplePendingRecyclerView;
    static CustomRecyclerViewAdapterStore customRecyclerViewAdapterStore;

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.storemenufrag1_layout, container,false);

        emailAddressEditText=view.findViewById(R.id.signInEmail);
        passwordEditText=view.findViewById(R.id.signInPassword);
        signInButton=view.findViewById(R.id.signIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });
        Button addPerson=view.findViewById(R.id.addPerson);
        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SignInAndStore)getActivity()).AddPersonToPending();
            }
        });
        peoplePendingRecyclerView=view.findViewById(R.id.peoplePendingRecyclerView);
        ((SignInAndStore)getActivity()).pendingList.clear();
        ((SignInAndStore)getActivity()).pendingList.add("Marouan Touati");
        ((SignInAndStore)getActivity()). pendingList.add("Anouar Touati");
        ((SignInAndStore)getActivity()). pendingList.add("Touati Mar");
        ((SignInAndStore)getActivity()). pendingList.add("Touati An");


        customRecyclerViewAdapterStore=new CustomRecyclerViewAdapterStore(getActivity(),  ((SignInAndStore)getActivity()).pendingList);
        peoplePendingRecyclerView.setAdapter(customRecyclerViewAdapterStore);
        peoplePendingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
    void SignIn(){
        String Email;
        Email=emailAddressEditText.getText().toString();
        String Password;
        Password=passwordEditText.getText().toString();
        ((SignInAndStore)getActivity()).SignIn(Email, Password);
    }
    static void RemovePersonFromPending(String personNameToRemove){
        SignInAndStore.RemovePersonFromPending(personNameToRemove);
        customRecyclerViewAdapterStore.notifyDataSetChanged();
    }
    void AddToPendingListAndUpdateUI(String personName){
        customRecyclerViewAdapterStore.notifyDataSetChanged();
    }
}
