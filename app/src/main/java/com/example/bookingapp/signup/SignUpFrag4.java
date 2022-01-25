package com.example.bookingapp.signup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
   public Bitmap selectedImage;
   public String shopPhoneNumber;
   public String facebookLink;
   public String instagramLink;

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
                selectImage();
            }
        });
        Button submit=view.findViewById(R.id.submitFrag4);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneFillingFieldsGoNextFrag();
            }
        });
        return view;
    }
    void showImage(Bitmap ImageBitmap){
        ImageView imageView=view.findViewById(R.id.imageViewFrag4);
        imageView.setImageBitmap(ImageBitmap);
    }
    void selectImage(){
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
                    selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                    showImage(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
           else{
               Toast.makeText(getContext(), "Please Choose Only One Photo For Your Shop", Toast.LENGTH_LONG).show();
            }
        }

        }

    void doneFillingFieldsGoNextFrag(){
        Boolean somethingWentWrong=false;

        EditText shopPhoneNumberEditText=view.findViewById(R.id.ShopPhoneNumberFrag4);
        shopPhoneNumber =shopPhoneNumberEditText.getText().toString();
        if(shopPhoneNumber ==null){
            somethingWentWrong=true;
        }else if (shopPhoneNumber ==""){
            somethingWentWrong=true;
        }else if(!(shopPhoneNumber.indexOf("0")==0 && (shopPhoneNumber.indexOf("2")==1|| shopPhoneNumber.indexOf("5")==1 || shopPhoneNumber.indexOf("6")==1 || shopPhoneNumber.indexOf("7")==1))){
            somethingWentWrong=true;
            Toast.makeText(getActivity(), "Incorrect Shop Phone Number", Toast.LENGTH_SHORT).show();
        }

        if(selectedImage ==null){
            somethingWentWrong=true;
            Toast.makeText(getActivity(), "Please Choose an Image", Toast.LENGTH_LONG).show();
        }
        EditText facebookEditText=view.findViewById(R.id.facebookFrag4);
        facebookLink ="www.facebook.com/"+facebookEditText.getText().toString();
        EditText instagramEditText=view.findViewById(R.id.instagramFrag4);
        instagramLink ="www.instagram.com/"+instagramEditText.getText().toString();

        if(!somethingWentWrong){

            ((SignUpActivity)getActivity()).selectedImage = selectedImage;
            ((SignUpActivity)getActivity()).shopPhoneNumber = shopPhoneNumber;
            ((SignUpActivity)getActivity()).facebookLink = facebookLink;
            ((SignUpActivity)getActivity()).instagramLink = instagramLink;

            ((SignUpActivity)getActivity()).setCurrentItemViewPager(4);

        }
    }
}
