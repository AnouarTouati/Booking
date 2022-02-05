package com.example.coifsalonbusiness.signup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.coifsalonbusiness.CommonMethods;
import com.example.coifsalonbusiness.R;

import java.io.IOException;

public class SignUpFrag4 extends Fragment {
   private Bitmap selectedImage;

   private ImageView imageView;
   private Button submit;
   private EditText shopPhoneNumberEditText;
   private final SignUpActivity signUpActivity;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.signupfrag4_layout, container,false);

        getViewsReferences();
        setUpViews();

        return view;
    }
    public SignUpFrag4(SignUpActivity signUpActivity){
        this.signUpActivity=signUpActivity;
    }
    private void getViewsReferences(){
        imageView=view.findViewById(R.id.imageViewFrag4);
        submit=view.findViewById(R.id.submitFrag4);
        shopPhoneNumberEditText=view.findViewById(R.id.ShopPhoneNumberFrag4);
    }
    private void setUpViews(){
        imageView.setOnClickListener(view -> selectImage());
        submit.setOnClickListener(view -> doneFillingFieldsGoNextFrag());
    }

    void showImage(Bitmap ImageBitmap){
        imageView.setImageBitmap(ImageBitmap);
    }
    void selectImage(){
        Intent selectImageIntent=new Intent();
        selectImageIntent.setType("image/*");
        selectImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(selectImageIntent, CommonMethods.IMG_REQ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CommonMethods.IMG_REQ && resultCode== FragmentActivity.RESULT_OK && data!=null){
            if(data.getClipData()==null){
                Uri path=data.getData();
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(signUpActivity.getContentResolver(), path);
                    showImage(selectedImage);
                } catch (IOException e) {
                    Log.e("AppFilter", "Error loading image from phone gallery "+e);
                    Toast.makeText(signUpActivity, "Error loading image from phone gallery", Toast.LENGTH_LONG).show();
                }
            }
           else{
               Toast.makeText(getContext(), "Please Choose Only One Photo For Your Shop", Toast.LENGTH_LONG).show();
            }
        }
    }

    void doneFillingFieldsGoNextFrag(){
        String shopPhoneNumber = shopPhoneNumberEditText.getText().toString();
        if (shopPhoneNumber.equals("")){
            Toast.makeText(getActivity(), "Shop Phone Number Can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }else if(!(shopPhoneNumber.indexOf("0")==0 && (shopPhoneNumber.indexOf("2")==1|| shopPhoneNumber.indexOf("5")==1 || shopPhoneNumber.indexOf("6")==1 || shopPhoneNumber.indexOf("7")==1))){
            Toast.makeText(getActivity(), "Incorrect Shop Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedImage ==null){
            Toast.makeText(getActivity(), "Please Choose an a custom image for your shop", Toast.LENGTH_LONG).show();
            return;
        }

        signUpActivity.selectedImage = selectedImage;
        signUpActivity.shopPhoneNumber = shopPhoneNumber;
        signUpActivity.setCurrentItemViewPager(4);
    }
}
