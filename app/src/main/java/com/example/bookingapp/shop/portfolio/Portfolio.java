package com.example.bookingapp.shop.portfolio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bookingapp.CommonMethods;
import com.example.bookingapp.R;
import com.example.bookingapp.shop.ShopActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Portfolio extends AppCompatActivity {

    ArrayList<String> imagesReferences = new ArrayList<>();
    ArrayList<Image> images=new ArrayList<>();
    CustomRecyclerAdapter customRecyclerAdapter;
    RecyclerView portfolioImagesRecyclerView;

    Button addImagesButton;

    FirebaseStorage firebaseStorage;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    ProgressBar progressBar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_portfolio_images__sub__shop);

        context=getApplicationContext();

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getViewsReferences();
        setUpViews();

        getPortfolioImagesReferencesFromServer();
    }
    private void getViewsReferences(){
        progressBar = findViewById(R.id.progressBarAddRemoveImageActivity);
        portfolioImagesRecyclerView = findViewById(R.id.RecyclerView_PortfolioImages);
        addImagesButton = findViewById(R.id.AddPortfolioImages_AddRemoveSubActivity);
    }
    private void setUpViews(){
        customRecyclerAdapter = new CustomRecyclerAdapter(images, this, this);

        portfolioImagesRecyclerView.setAdapter(customRecyclerAdapter);
        portfolioImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addImagesButton.setOnClickListener(view -> selectImagesFromPhoneToSendToServer());
    }
    void getPortfolioImagesReferencesFromServer() {
        turnOnProgressBar();
        firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try{
                    final ArrayList<String> imagesPathsInFireStorage = (ArrayList<String>) documentSnapshot.get("PhotosPathsInFireStorage");
                    imagesReferences.clear();
                    imagesReferences = imagesPathsInFireStorage;
                    turnOFFProgressBar();
                    getImagesFromServer();
                }catch (Exception e){
                    Log.e("AppFilter", getString(R.string.Failed_to_get_image_from_server)+e);
                    Toast.makeText(context, R.string.Failed_to_get_image_from_server,Toast.LENGTH_LONG).show();
                    turnOFFProgressBar();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("AppFilter", getString(R.string.Failed_to_get_image_from_server)+e);
                Toast.makeText(context, R.string.Failed_to_get_image_from_server,Toast.LENGTH_LONG).show();
                turnOFFProgressBar();
            }
        });

    }
    void turnOFFProgressBar() {
        addImagesButton.setVisibility(View.VISIBLE);
        portfolioImagesRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public void getImagesFromServer() {

        if(imagesReferences!=null){
            images.clear();
            for(String reference : imagesReferences){
                requestImage(reference);
            }
        }
    }

    void requestImage(final String imageReference) {
        StorageReference storageReference = firebaseStorage.getReference(imageReference);
        final long FOUR_MEGA_BYTES = 4 * 1024 * 1024;
        storageReference.getBytes(FOUR_MEGA_BYTES).addOnSuccessListener(bytes -> {
            Bitmap response = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            images.add(new Image(response,imageReference));
            updateTheRecyclerView();

        }).addOnFailureListener(e -> {
            Log.e("AppFilter", getString(R.string.Failed_to_get_image_from_server)+e);
            Toast.makeText(context, R.string.Failed_to_get_image_from_server,Toast.LENGTH_LONG).show();
        });
    }
    void updateTheRecyclerView() {
        customRecyclerAdapter.notifyDataSetChanged();
        turnOFFProgressBar();
    }

    void selectImagesFromPhoneToSendToServer() {
        Intent getImagesIntent = new Intent();
        getImagesIntent.setType("image/*");
        getImagesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        getImagesIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(getImagesIntent, CommonMethods.IMG_REQ);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CommonMethods.IMG_REQ && resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    try {
                        pushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));//the .getPath here is passes into UUID constructor

                    } catch (IOException e) {
                        Log.e("AppFilter", getString(R.string.Failed_to_load_image_from_phone_gallery)+e);
                        Toast.makeText(context, R.string.Failed_to_load_image_from_phone_gallery,Toast.LENGTH_LONG).show();
                    }
                }
            } else if (data.getData() != null) {

                try {
                    pushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));

                } catch (IOException e) {
                    Log.e("AppFilter", getString(R.string.Failed_to_load_image_from_phone_gallery)+e);
                    Toast.makeText(context, R.string.Failed_to_load_image_from_phone_gallery,Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    public void pushImageToServer(final Bitmap imageToBePushed) {
        turnOnProgressBar();
        StorageReference storageReference = firebaseStorage.getReference();
        final StorageReference imageReference = storageReference.child("Photos/" + firebaseUser.getUid() + "/" + UUID.randomUUID() + ".JPEG");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageToBePushed.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = imageReference.putBytes(imageByte);
        uploadTask.addOnSuccessListener(taskSnapshot -> firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).update("PhotosPathsInFireStorage", FieldValue.arrayUnion(imageReference.getPath())).addOnSuccessListener(aVoid -> {
            images.add(new Image(imageToBePushed,imageReference.getPath()));
            if(imagesReferences==null){
                imagesReferences=new ArrayList<>();
            }
            imagesReferences.add(imageReference.getPath());
            updateTheRecyclerView();
        })).addOnFailureListener(e -> {
            Log.e("AppFilter", getString(R.string.Failed_to_upload_image)+e);
            Toast.makeText(context, getString(R.string.Failed_to_upload_image),Toast.LENGTH_LONG).show();
            turnOFFProgressBar();
        });

    }

    public void removeImageFromServer(String reference) {
        turnOnProgressBar();
        final StorageReference imageReference = firebaseStorage.getReference(reference);
        imageReference.delete().addOnSuccessListener(aVoid -> firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).update("PhotosPathsInFireStorage", FieldValue.arrayRemove(imageReference.getPath())).addOnSuccessListener(aVoid1 -> getPortfolioImagesReferencesFromServer()));
    }

    void turnOnProgressBar() {
        addImagesButton.setVisibility(View.GONE);
        portfolioImagesRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }


}
