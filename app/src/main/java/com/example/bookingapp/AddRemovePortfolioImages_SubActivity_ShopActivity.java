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

    static final String URL = "http://192.168.43.139:8888/PortfolioImages-Business.php";//dont change this without changing it in php code
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
    static ArrayList<Bitmap> imagesToBePushed = new ArrayList<>();
    static ArrayList<Long> cRC32ofImagesToBePushed = new ArrayList<>();//only used to make it easier to find the index of the image to save

    static Response.Listener<JSONObject> volleyListener;
    static Response.ErrorListener volleyErrorListener;
    static RequestQueue requestQueue;

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


        customRecyclerVAdapterPortfolioImages = new CustomRecyclerVAdapterPortfolioImages(portfolioImages, this);
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


        volleyListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("VolleyReceived", response.toString());
                if (response.has("PortfolioImagesLinks")) {

                    try {
                        imagesReferencesFromServer.clear();
                        for (int i = 0; i < response.getJSONArray("PortfolioImagesLinks").length(); i++) {
                            imagesReferencesFromServer.add(response.getJSONArray("PortfolioImagesLinks").getString(i));
                        }
                        loadLocalData("Shop");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (response.has("AddPortfolioImage")) {
                    try {
                        if (response.getJSONObject("AddPortfolioImage").getString("Successful").equals("True")) {
                            int IndexOfTheSuccessfullyReceivedImage = cRC32ofImagesToBePushed.indexOf(response.getJSONObject("AddPortfolioImage").getLong("ImageCRC32"));
                            portfolioImages.add(imagesToBePushed.get(IndexOfTheSuccessfullyReceivedImage));
                            portfolioImagesAsStrings.add(CommonMethods.convertBitmapToString(imagesToBePushed.get(IndexOfTheSuccessfullyReceivedImage)));
                            portfolioImagesReferences.add(response.getJSONObject("AddPortfolioImage").getString("ImageLink"));
                            saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.has("RemovePortfolioImage")) {
                    try {
                        if (response.getJSONObject("RemovePortfolioImage").getString("Successful").equals("True")) {
                            int IndexOfImageRemoved = portfolioImagesReferences.indexOf(response.getJSONObject("RemovePortfolioImage").getString("ImageLink"));
                            portfolioImagesReferences.remove(response.getJSONObject("RemovePortfolioImage").getString("ImageLink"));
                            portfolioImagesAsStrings.remove(IndexOfImageRemoved);
                            portfolioImages.remove(IndexOfImageRemoved);
                            saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        volleyErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", "Error " + error.toString());
            }
        };
        requestQueue = Volley.newRequestQueue(this);
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
        customRecyclerVAdapterPortfolioImages = new CustomRecyclerVAdapterPortfolioImages(portfolioImages, this);
        portfolioImagesRecyclerView.swapAdapter(customRecyclerVAdapterPortfolioImages, true);
    }

    public static void removeImageFromServer(int Index) {
        Map<String, Object> map = new HashMap<>();
        map.put("Request", "RemovePortfolioImage");
        map.put("ImageLink", portfolioImagesReferences.get(Index));
        JSONObject data = new JSONObject(map);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, data, volleyListener, volleyErrorListener);
        requestQueue.add(jsonObjectRequest);
    }

    public void pushImageToServer(Bitmap ImageToBePushed) {
/*
        String imageToBePushedAsString = CommonMethods.convertBitmapToString(ImageToBePushed);

        Map<String, Object> map = new HashMap<>();
        map.put("Request", "AddPortfolioImage");
        map.put("ImageAsString", imageToBePushedAsString);

        CRC32 crc32 = new CRC32();
        crc32.update(imageToBePushedAsString.getBytes());

        map.put("ImageCRC32", crc32.getValue());

        cRC32ofImagesToBePushed.add(crc32.getValue());
        imagesToBePushed.add(ImageToBePushed);


        JSONObject data = new JSONObject(map);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, data, volleyListener, volleyErrorListener);
        requestQueue.add(jsonObjectRequest);
*/

        StorageReference storageReference=firebaseStorage.getReference();
        final StorageReference imageReference =storageReference.child("images/"+firebaseUser.getUid()+"/"+UUID.randomUUID());
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ImageToBePushed.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageByte =byteArrayOutputStream.toByteArray();

        UploadTask uploadTask= imageReference.putBytes(imageByte);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).update("ImagesPathsInFireStorage", FieldValue.arrayUnion(imageReference.getPath())).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    getPortfolioImagesReferencesFromServer();
                }
            });
                Log.v("MyFirebaseVerbose","successfully uploaded image");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("MyFirebaseVerbose","failed  to upload image");
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

                        ShopDataJSONObject.remove("PortfolioImagesAsStrings");
                        ShopDataJSONObject.remove("PortfolioImagesLinks");
                        portfolioImagesAsStrings.clear();
                        portfolioImagesAsStrings = ListOfImagesThatDidNotChange;
                        portfolioImagesReferences.clear();
                        portfolioImagesReferences = ListOfImagesReferencesThatDidNotChange;

                        portfolioImages.clear();
                        for (int i = 0; i < PortfolioImagesAsStringsLocal.size(); i++) {
                            portfolioImages.add(CommonMethods.convertStringToBitmap(PortfolioImagesAsStringsLocal.get(i)));
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
Log.v("MyFirebaseVerbose","Requesting image from server");
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

    firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            Log.v("MyFirebaseVerbose","got shop data with success");
              final ArrayList<String> imagesPathsInFireStorage=(ArrayList<String>) documentSnapshot.get("ImagesPathsInFireStorage");
            imagesReferencesFromServer.clear();
            imagesReferencesFromServer=imagesPathsInFireStorage;
            loadLocalData("Shop");

        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.v("MyFirebaseVerbose","FAILED TO get shop data");
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
