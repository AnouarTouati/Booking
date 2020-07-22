package com.example.bookingapp;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddRemovePortfolioImages_SubActivity_ShopActivity extends AppCompatActivity {

    final int IMG_REQ = 10;
    ArrayList<Bitmap> portfolioImages = new ArrayList<>();
    ArrayList<String> portfolioImagesAsStrings = new ArrayList<>();
    ArrayList<String> imagesReferencesFromServer = new ArrayList<>();
    static ArrayList<String> portfolioImagesReferences = new ArrayList<>();
    ArrayList<String> portfolioImagesReferencesToBeRequested = new ArrayList<>();
    Integer indexOfImageToReceiveNext = 0;
    CustomRecyclerVAdapterPortfolioImages customRecyclerVAdapterPortfolioImages;
    RecyclerView portfolioImagesRecyclerView;

    Button addImages;

   static FirebaseStorage firebaseStorage;
   static FirebaseUser firebaseUser;
   static FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_portfolio_images__sub__shop);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();


        customRecyclerVAdapterPortfolioImages = new CustomRecyclerVAdapterPortfolioImages(portfolioImages, this,this);
        portfolioImagesRecyclerView = findViewById(R.id.RecyclerView_PortfolioImages);
        portfolioImagesRecyclerView.setAdapter(customRecyclerVAdapterPortfolioImages);
        portfolioImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addImages = findViewById(R.id.AddPortfolioImages_AddRemoveSubActivity);
        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImages();
            }
        });

        getPortfolioImagesReferencesFromServer();
    }

    void getImages() {
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
                Log.v("VolleyReceived", "Up ToGet Images");
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    try {
                        pushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));//the .getPath here is passes into UUID constructor
                                                                                                                                // so we are sure there is different names for images on storage

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

    void updateTheRecyclerView() {
        customRecyclerVAdapterPortfolioImages = new CustomRecyclerVAdapterPortfolioImages(portfolioImages, this,this);
        portfolioImagesRecyclerView.swapAdapter(customRecyclerVAdapterPortfolioImages, true);
    }

    public  void removeImageFromServer(int Index) {

      final StorageReference imageReference=firebaseStorage.getReference(portfolioImagesReferences.get(Index));
      imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
              firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).update("ImagesPathsInFireStorage",FieldValue.arrayRemove(imageReference.getPath())).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                      getPortfolioImagesReferencesFromServer();
                  }
              });
          }
      });
    }

    public void pushImageToServer(final Bitmap ImageToBePushed) {

        StorageReference storageReference=firebaseStorage.getReference();
        final StorageReference imageReference =storageReference.child("Photos/"+firebaseUser.getUid()+"/"+UUID.randomUUID()+".JPEG");
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ImageToBePushed.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageByte =byteArrayOutputStream.toByteArray();

        UploadTask uploadTask= imageReference.putBytes(imageByte);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).update("PhotosPathsInFireStorage", FieldValue.arrayUnion(imageReference.getPath())).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    portfolioImages.add(ImageToBePushed);
                    portfolioImagesAsStrings.add(CommonMethods.convertBitmapToString(ImageToBePushed));
                    portfolioImagesReferences.add(imageReference.getPath());
                    saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();
                }
            });
                Log.v("MyFirebase","successfully uploaded image");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("MyFirebase","failed  to upload image");
            }
        });


    }

    public void writeNewShopDataToLocalMemory(JSONObject NewShopDataJSON) {
        try {

            String localMemoryJsonAsString = CommonMethods.loadJSONFile("Data.txt", getApplicationContext());
            if (localMemoryJsonAsString != null) {

                JSONObject localMemoryJsonObject = new JSONObject(localMemoryJsonAsString);
                if (localMemoryJsonObject.has("Shop")) {
                    localMemoryJsonObject.remove("Shop");
                }
                localMemoryJsonObject.put("Shop", NewShopDataJSON);

                FileOutputStream fos = openFileOutput("Data.txt", MODE_PRIVATE);
                fos.write(localMemoryJsonObject.toString().getBytes());

                fos.close();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadLocalData(String ShopName) {
        try {

            String jsonAsString = CommonMethods.loadJSONFile("Data.txt", getApplicationContext());

            if (jsonAsString != null) {

                JSONObject jsonObject = new JSONObject(jsonAsString);

                if (jsonObject.has(ShopName)) {


                    JSONObject ShopDataJSONObject = jsonObject.getJSONObject(ShopName);


                    if (ShopDataJSONObject.has("PortfolioImagesLinks")) {
                        ArrayList<String> ListOfImagesThatDidNotChange = new ArrayList<>();
                        ArrayList<Integer> IndexOfTheImageThatDidNotChange = new ArrayList<>();
                        ArrayList<String> ListOfImagesReferencesThatDidNotChange = new ArrayList<>();

                        ArrayList<String> PortfolioImagesReferencesLocal = new ArrayList<>();
                        for (int i = 0; i < ShopDataJSONObject.getJSONArray("PortfolioImagesLinks").length(); i++) {
                            PortfolioImagesReferencesLocal.add(i, ShopDataJSONObject.getJSONArray("PortfolioImagesLinks").getString(i));
                        }

                        //find doublicate links and remove them
                        for(int i =0;i<PortfolioImagesReferencesLocal.size();i++){
                            for (int j=i+1;j<PortfolioImagesReferencesLocal.size();j++){
                                if(PortfolioImagesReferencesLocal.get(i).equals(PortfolioImagesReferencesLocal.get(j))){
                                  PortfolioImagesReferencesLocal.remove(PortfolioImagesReferencesLocal.get(j));
                                  j--;
                                }
                            }

                        }

                        ArrayList<String> PortfolioImagesAsStringsLocal = new ArrayList<>();
                        for (int i = 0; i < ShopDataJSONObject.getJSONArray("PortfolioImagesAsStrings").length(); i++) {
                            PortfolioImagesAsStringsLocal.add(i, ShopDataJSONObject.getJSONArray("PortfolioImagesAsStrings").getString(i));
                        }

                        portfolioImagesReferencesToBeRequested.clear();
                        indexOfImageToReceiveNext = 0;

                        for (int i = 0; i < imagesReferencesFromServer.size(); i++) {

                            Boolean LinkNotFound = true;
                            for (int j = 0; j < PortfolioImagesReferencesLocal.size(); j++) {

                                if (PortfolioImagesReferencesLocal.get(j).equals(imagesReferencesFromServer.get(i))) {
                                    LinkNotFound = false;
                                    IndexOfTheImageThatDidNotChange.add(j);
                                    ListOfImagesThatDidNotChange.add(PortfolioImagesAsStringsLocal.get(j));
                                    ListOfImagesReferencesThatDidNotChange.add(PortfolioImagesReferencesLocal.get(j));
                                }
                            }
                            if (LinkNotFound) {

                                portfolioImagesReferencesToBeRequested.add(imagesReferencesFromServer.get(i));
                            }


                        }

                        portfolioImagesAsStrings.clear();
                        portfolioImagesReferences.clear();
                        portfolioImagesAsStrings = ListOfImagesThatDidNotChange;
                        portfolioImagesReferences = ListOfImagesReferencesThatDidNotChange;

                        portfolioImages.clear();
                        for (int i = 0; i < portfolioImagesAsStrings.size(); i++) {
                            portfolioImages.add(CommonMethods.convertStringToBitmap(portfolioImagesAsStrings.get(i)));
                        }



                    } else {
                        portfolioImagesReferencesToBeRequested.clear();
                        indexOfImageToReceiveNext = 0;
                        portfolioImagesReferencesToBeRequested.addAll(imagesReferencesFromServer);

                       /* for(int i=0;i<ImagesLinkFromServer.size();i++){
                            requestImage(ImagesLinkFromServer.get(i));
                        }*/
                    }

                    /// if some images no longer exists in server the code above will remove them so we need to save the trimmed set of images
                    saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();

                    if (portfolioImagesReferencesToBeRequested.size() > 0) {
                        requestImage(portfolioImagesReferencesToBeRequested.get(indexOfImageToReceiveNext));//IndexOfImageToReceiveNext should equal zero at this stage
                    }

                } else {
                    portfolioImagesReferencesToBeRequested.clear();
                    indexOfImageToReceiveNext = 0;
                    if(imagesReferencesFromServer!=null)
                    portfolioImagesReferencesToBeRequested.addAll(imagesReferencesFromServer);
                    if (portfolioImagesReferencesToBeRequested.size() > 0) {
                        requestImage(portfolioImagesReferencesToBeRequested.get(indexOfImageToReceiveNext));//IndexOfImageToReceiveNext should equal zero at this stage
                    }
                }
            } else {
                //null is handled in the calling function loadJSONFile()
            }


        } catch (JSONException e) {
            Log.v("LoadFromCache", "Error parsing JSON in ShopDetailsActivity loadLocalData function");
            e.printStackTrace();
        }
    }

    void requestImage(final String ImageReference) {
Log.v("MyFirebase","Requesting image from server");
    StorageReference imageReference =firebaseStorage.getReference(ImageReference);
    final long FOUR_MEGA_BYTES=4 * 1024*1024;
    imageReference.getBytes(FOUR_MEGA_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
            Bitmap response= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            portfolioImages.add(response);
            portfolioImagesAsStrings.add(CommonMethods.convertBitmapToString(response));
            portfolioImagesReferences.add(ImageReference);
            saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();
            indexOfImageToReceiveNext++;
            if (indexOfImageToReceiveNext < portfolioImagesReferencesToBeRequested.size()) {
                requestImage(portfolioImagesReferencesToBeRequested.get(indexOfImageToReceiveNext));
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {

        }
    });
    }

    void getPortfolioImagesReferencesFromServer() {
    //should get only the field PhotosPathsInFireStorage
    firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {

              final ArrayList<String> imagesPathsInFireStorage=(ArrayList<String>) documentSnapshot.get("PhotosPathsInFireStorage");
            imagesReferencesFromServer.clear();
            imagesReferencesFromServer=imagesPathsInFireStorage;
            Log.v("MyFirebase","got shop data with success "+imagesReferencesFromServer);
            loadLocalData("Shop");

        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.v("MyFirebase","FAILED TO get shop data");
        }
    });

    }

    void saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView() {
        Map<String, Object> map = new HashMap<>();
        map.put("PortfolioImagesAsStrings", portfolioImagesAsStrings);
        map.put("PortfolioImagesLinks", portfolioImagesReferences);

        JSONObject Data = new JSONObject(map);
        writeNewShopDataToLocalMemory(Data);
        updateTheRecyclerView();

    }

}
