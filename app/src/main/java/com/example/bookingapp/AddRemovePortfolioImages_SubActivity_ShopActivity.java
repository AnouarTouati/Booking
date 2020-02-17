package com.example.bookingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class AddRemovePortfolioImages_SubActivity_ShopActivity extends AppCompatActivity {

    static final String URL = "http://192.168.43.139:8888/PortfolioImages-Business.php";//dont change this without changing it in php code
    final int IMG_REQ = 10;
    ArrayList<Bitmap> portfolioImages = new ArrayList<>();
    ArrayList<String> portfolioImagesAsStrings = new ArrayList<>();
    ArrayList<String> imagesLinkFromServer = new ArrayList<>();
    static ArrayList<String> portfolioImagesLinks = new ArrayList<>();
    ArrayList<String> portfolioImagesLinksToBeRequested = new ArrayList<>();
    Integer indexOfImageToReceiveNext = 0;
    CustomRecyclerVAdapterPortfolioImages customRecyclerVAdapterPortfolioImages;
    RecyclerView portfolioImagesRecyclerView;

    Button addImages;
    static ArrayList<Bitmap> imagesToBePushed = new ArrayList<>();
    static ArrayList<Long> cRC32ofImagesToBePushed = new ArrayList<>();//only used to make it easier to find the index of the image to save

    static Response.Listener<JSONObject> volleyListener;
    static Response.ErrorListener volleyErrorListener;
    static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_portfolio_images__sub__shop);
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
                        imagesLinkFromServer.clear();
                        for (int i = 0; i < response.getJSONArray("PortfolioImagesLinks").length(); i++) {
                            imagesLinkFromServer.add(response.getJSONArray("PortfolioImagesLinks").getString(i));
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
                            portfolioImagesLinks.add(response.getJSONObject("AddPortfolioImage").getString("ImageLink"));
                            saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.has("RemovePortfolioImage")) {
                    try {
                        if (response.getJSONObject("RemovePortfolioImage").getString("Successful").equals("True")) {
                            int IndexOfImageRemoved = portfolioImagesLinks.indexOf(response.getJSONObject("RemovePortfolioImage").getString("ImageLink"));
                            portfolioImagesLinks.remove(response.getJSONObject("RemovePortfolioImage").getString("ImageLink"));
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
        getPortfolioImagesLinksFromServer();
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
                        pushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            }
        } else if (data.getData() != null) {
            Log.v("VolleyReceived", "Up ToGet Images");
            String imagePath = data.getData().getPath();

            try {
                pushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imagePath)));

            } catch (IOException e) {
                e.printStackTrace();
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
        map.put("ImageLink", portfolioImagesLinks.get(Index));
        JSONObject data = new JSONObject(map);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, data, volleyListener, volleyErrorListener);
        requestQueue.add(jsonObjectRequest);
    }

    public static void pushImageToServer(Bitmap ImageToBePushed) {
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
                        ArrayList<String> ListOfImagesLinksThatDidNotChange = new ArrayList<>();

                        // ArrayList<String> PortfolioImagesLinks= ParseJSONtoArrayListOfStrings(ShopDataJSONObject.get("PortfolioImagesLinks").toString());
                        ArrayList<String> PortfolioImagesLinksLocal = new ArrayList<>();
                        for (int i = 0; i < ShopDataJSONObject.getJSONArray("PortfolioImagesLinks").length(); i++) {
                            PortfolioImagesLinksLocal.add(i, ShopDataJSONObject.getJSONArray("PortfolioImagesLinks").getString(i));
                        }


                        ArrayList<String> PortfolioImagesAsStringsLocal = new ArrayList<>();
                        for (int i = 0; i < ShopDataJSONObject.getJSONArray("PortfolioImagesAsStrings").length(); i++) {
                            PortfolioImagesAsStringsLocal.add(i, ShopDataJSONObject.getJSONArray("PortfolioImagesAsStrings").getString(i));
                        }
                        portfolioImagesLinksToBeRequested.clear();
                        indexOfImageToReceiveNext = 0;
                        for (int i = 0; i < imagesLinkFromServer.size(); i++) {

                            Boolean LinkNotFound = true;
                            for (int j = 0; j < PortfolioImagesLinksLocal.size(); j++) {

                                if (PortfolioImagesLinksLocal.get(j).equals(imagesLinkFromServer.get(i))) {
                                    LinkNotFound = false;
                                    IndexOfTheImageThatDidNotChange.add(j);
                                    ListOfImagesThatDidNotChange.add(PortfolioImagesAsStringsLocal.get(j));
                                    ListOfImagesLinksThatDidNotChange.add(PortfolioImagesLinksLocal.get(j));
                                }
                            }
                            if (LinkNotFound) {

                                portfolioImagesLinksToBeRequested.add(imagesLinkFromServer.get(i));
                            }


                        }
                        if (portfolioImagesLinksToBeRequested.size() > 0) {
                            requestImage(portfolioImagesLinksToBeRequested.get(indexOfImageToReceiveNext));//IndexOfImageToReceiveNext should equal zero at this stage
                        }
                        ShopDataJSONObject.remove("PortfolioImagesAsStrings");
                        ShopDataJSONObject.remove("PortfolioImagesLinks");
                        portfolioImagesAsStrings.clear();
                        portfolioImagesAsStrings = ListOfImagesThatDidNotChange;
                        portfolioImagesLinks.clear();
                        portfolioImagesLinks = ListOfImagesLinksThatDidNotChange;

                        portfolioImages.clear();
                        for (int i = 0; i < PortfolioImagesAsStringsLocal.size(); i++) {
                            portfolioImages.add(CommonMethods.convertStringToBitmap(PortfolioImagesAsStringsLocal.get(i)));
                        }


                    } else {
                        portfolioImagesLinksToBeRequested.clear();
                        indexOfImageToReceiveNext = 0;
                        portfolioImagesLinksToBeRequested.addAll(imagesLinkFromServer);
                        if (portfolioImagesLinksToBeRequested.size() > 0) {
                            requestImage(portfolioImagesLinksToBeRequested.get(indexOfImageToReceiveNext));
                        }
                       /* for(int i=0;i<ImagesLinkFromServer.size();i++){
                            requestImage(ImagesLinkFromServer.get(i));
                        }*/
                    }

                    /// if some images no longer exists in server the code above will remove them so we need to save the trimmed set of images
                    saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();


                } else {
                    ///when we call request and receive an image it will call  saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView() to save it and hance create Data file

                    for (int i = 0; i < imagesLinkFromServer.size(); i++) {
                        requestImage(imagesLinkFromServer.get(i));
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

    void requestImage(final String ImageLink) {
        Log.v("VolleyReceived", "We Are requesting images ");
        ImageRequest imageRequest = new ImageRequest(ImageLink, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                portfolioImages.add(response);
                portfolioImagesAsStrings.add(CommonMethods.convertBitmapToString(response));
                portfolioImagesLinks.add(ImageLink);
                saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();
                indexOfImageToReceiveNext++;
                if (indexOfImageToReceiveNext < portfolioImagesLinksToBeRequested.size()) {
                    requestImage(portfolioImagesLinksToBeRequested.get(indexOfImageToReceiveNext));
                }

            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyErrors", "onErrorResponse: IN SHOPDETAILS ACTIVITY IMAGES ImagesForPortfolio" + error.toString());
            }
        });
        requestQueue.add(imageRequest);
    }

    void getPortfolioImagesLinksFromServer() {
        Map<String, Object> map = new HashMap<>();
        map.put("Request", "PortfolioImagesLinks");
        map.put("Token", "The Token");//or shop name
        JSONObject Data = new JSONObject(map);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, Data, volleyListener, volleyErrorListener);
        requestQueue.add(jsonObjectRequest);
    }

    void saveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView() {
        Map<String, Object> map = new HashMap<>();
        map.put("PortfolioImagesAsStrings", portfolioImagesAsStrings);
        map.put("PortfolioImagesLinks", portfolioImagesLinks);

        JSONObject Data = new JSONObject(map);
        writeNewShopDataToLocalMemory(Data);
        updateTheRecyclerView();

    }
}
