package com.example.bookingapp.shop.portfolio;

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

import com.example.bookingapp.R;
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
import java.util.UUID;

public class Portfolio extends AppCompatActivity {

    final int IMG_REQ = 10;
    ArrayList<Bitmap> portfolioImages = new ArrayList<>();
    ArrayList<String> imagesReferences = new ArrayList<>();

    CustomRecyclerAdapter customRecyclerAdapter;
    RecyclerView portfolioImagesRecyclerView;

    Button addImagesButton;

    static FirebaseStorage firebaseStorage;
    static FirebaseUser firebaseUser;
    static FirebaseFirestore firebaseFirestore;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_portfolio_images__sub__shop);


        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBarAddRemoveImageActivity);

        customRecyclerAdapter = new CustomRecyclerAdapter(portfolioImages, this, this);
        portfolioImagesRecyclerView = findViewById(R.id.RecyclerView_PortfolioImages);
        portfolioImagesRecyclerView.setAdapter(customRecyclerAdapter);
        portfolioImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addImagesButton = findViewById(R.id.AddPortfolioImages_AddRemoveSubActivity);
        addImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImagesFromPhoneToSendToServer();
            }
        });

        getPortfolioImagesReferencesFromServer();
    }
    void getPortfolioImagesReferencesFromServer() {
        turnOnProgressBar();
        firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                final ArrayList<String> imagesPathsInFireStorage = (ArrayList<String>) documentSnapshot.get("PhotosPathsInFireStorage");
                imagesReferences.clear();
                imagesReferences = imagesPathsInFireStorage;
                turnOFFProgressBar();
                Log.v("MyFirebase", "got shop data with success " + imagesReferences);
                getImagesFromServer();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("MyFirebase", "FAILED TO get shop data");
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
            portfolioImages.clear();
            for(String reference : imagesReferences){
                requestImage(reference);
            }
        }

    }


    void requestImage(final String ImageReference) {
        Log.v("MyFirebase", "Requesting image from server");
        StorageReference imageReference = firebaseStorage.getReference(ImageReference);
        final long FOUR_MEGA_BYTES = 4 * 1024 * 1024;
        imageReference.getBytes(FOUR_MEGA_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap response = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                portfolioImages.add(response);
                updateTheRecyclerView();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    void updateTheRecyclerView() {
        customRecyclerAdapter = new CustomRecyclerAdapter(portfolioImages, this, this);
        portfolioImagesRecyclerView.swapAdapter(customRecyclerAdapter, true);
        turnOFFProgressBar();
    }


    void selectImagesFromPhoneToSendToServer() {
        Intent getImagesIntent = new Intent();
        getImagesIntent.setType("image/*");
        getImagesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        getImagesIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(getImagesIntent, IMG_REQ);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    try {
                        pushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));//the .getPath here is passes into UUID constructor

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (data.getData() != null) {

                try {
                    pushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public void pushImageToServer(final Bitmap ImageToBePushed) {
        turnOnProgressBar();
        StorageReference storageReference = firebaseStorage.getReference();
        final StorageReference imageReference = storageReference.child("Photos/" + firebaseUser.getUid() + "/" + UUID.randomUUID() + ".JPEG");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageToBePushed.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = imageReference.putBytes(imageByte);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).update("PhotosPathsInFireStorage", FieldValue.arrayUnion(imageReference.getPath())).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        portfolioImages.add(ImageToBePushed);
                        imagesReferences.add(imageReference.getPath());
                        updateTheRecyclerView();
                    }
                });
                Log.v("MyFirebase", "successfully uploaded image");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("MyFirebase", "failed  to upload image");
                turnOFFProgressBar();
            }
        });

    }

    public void removeImageFromServer(int Index) {
        turnOnProgressBar();
        final StorageReference imageReference = firebaseStorage.getReference(imagesReferences.get(Index));
        imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).update("PhotosPathsInFireStorage", FieldValue.arrayRemove(imageReference.getPath())).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getPortfolioImagesReferencesFromServer();
                    }
                });
            }
        });
    }

    void turnOnProgressBar() {
        addImagesButton.setVisibility(View.GONE);
        portfolioImagesRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }



}
