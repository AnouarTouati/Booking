package com.example.bookingapp.shop;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.bookingapp.R;

public class ShopMenuFrag1 extends Fragment {
    EditText emailAddressEditText;
    EditText passwordEditText;
    Button signInButton;
    RecyclerView peoplePendingRecyclerView;
    public CustomRecyclerViewAdapterShop customRecyclerViewAdapterShop;
    ShopActivity shopActivityReference;

   public ShopMenuFrag1(ShopActivity shopActivityReference){
       this.shopActivityReference=shopActivityReference;
   }
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
              shopActivityReference.serverAddPersonToPending();
            }
        });
        peoplePendingRecyclerView=view.findViewById(R.id.peoplePendingRecyclerView);

        customRecyclerViewAdapterShop =new CustomRecyclerViewAdapterShop(getActivity(),  shopActivityReference.pendingList,shopActivityReference);
        peoplePendingRecyclerView.setAdapter(customRecyclerViewAdapterShop);
        peoplePendingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        shopActivityReference.getPendingList(this);

        return view;
    }


}
