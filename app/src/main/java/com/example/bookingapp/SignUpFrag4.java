package com.example.bookingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bookingapp.R;

import java.io.IOException;

public class SignUpFrag4 extends Fragment {
   public Bitmap SelectedImage;
   public String ShopPhoneNumber;
   public String FacebookLink;
   public String InstagramLink;

    View view;
    final int IMG_REQ=1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.signupfrag4_layout, container,false);

        ImageView imageView=view.findViewById(R.id.imageViewFrag4);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });
        Button submit=view.findViewById(R.id.submitFrag4);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoneFillingFieldsGoNextFrag();
            }
        });
        return view;
    }
    void ShowImage(Bitmap ImageBitmap){
        ImageView imageView=view.findViewById(R.id.imageViewFrag4);
        imageView.setImageBitmap(ImageBitmap);
    }
    void SelectImage(){
        Intent selectImageIntent=new Intent();
        selectImageIntent.setType("image/*");
        selectImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(selectImageIntent, IMG_REQ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQ && resultCode==getActivity().RESULT_OK && data!=null){
            if(data.getClipData()==null){
                Uri path=data.getData();
                try {
                    SelectedImage= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                    ShowImage(SelectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
           else{
               Toast.makeText(getContext(), "Please Choose Only One Photo For Your Shop", Toast.LENGTH_LONG).show();
            }
        }

        }

    void DoneFillingFieldsGoNextFrag(){
        Boolean SomethingWentWrong=false;

        EditText ShopPhoneNumberEditText=view.findViewById(R.id.ShopPhoneNumberFrag4);
        ShopPhoneNumber=ShopPhoneNumberEditText.getText().toString();
        if(ShopPhoneNumber==null){
            SomethingWentWrong=true;
        }else if (ShopPhoneNumber==""){
            SomethingWentWrong=true;
        }else if(!(ShopPhoneNumber.indexOf("0")==0 && (ShopPhoneNumber.indexOf("2")==1||ShopPhoneNumber.indexOf("5")==1 || ShopPhoneNumber.indexOf("6")==1 || ShopPhoneNumber.indexOf("7")==1))){
            SomethingWentWrong=true;
            Toast.makeText(getActivity(), "Incorrect Shop Phone Number", Toast.LENGTH_SHORT).show();
        }

        if(SelectedImage==null){
            SomethingWentWrong=true;
            Toast.makeText(getActivity(), "Please Choose an Image", Toast.LENGTH_LONG).show();
        }
        EditText facebookEditText=view.findViewById(R.id.facebookFrag4);
        FacebookLink="www.facebook.com/"+facebookEditText.getText().toString();
        EditText instagramEditText=view.findViewById(R.id.instagramFrag4);
        InstagramLink="www.instagram.com/"+instagramEditText.getText().toString();

        if(!SomethingWentWrong){

            ((com.example.bookingapp.SignUpActivity)getActivity()).SelectedImage=SelectedImage;
            ((com.example.bookingapp.SignUpActivity)getActivity()).ShopPhoneNumber=ShopPhoneNumber;
            ((com.example.bookingapp.SignUpActivity)getActivity()).FacebookLink=FacebookLink;
            ((com.example.bookingapp.SignUpActivity)getActivity()).InstagramLink=InstagramLink;
            ((com.example.bookingapp.SignUpActivity)getActivity()).TurnOnProgressBar();
             com.example.bookingapp.SignUpActivity.SendToServerToCheckAndRegister(4);
         //   ((SignUpActivity)getActivity()).SetCurrentItemViewPager(4);

        }
    }
}
