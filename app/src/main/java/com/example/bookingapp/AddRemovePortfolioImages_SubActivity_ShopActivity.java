package com.example.bookingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class AddRemovePortfolioImages_SubActivity_ShopActivity extends AppCompatActivity {

    static final String URL="http://192.168.43.139:8888/PortfolioImages-Business.php";
    final int IMG_REQ=10;
    ArrayList<Bitmap> PortfolioImages =new ArrayList<>();
    ArrayList<String> PortfolioImagesAsStrings=new ArrayList<>();
    ArrayList<String> ImagesLinkFromServer=new ArrayList<>();
    static ArrayList<String> PortfolioImagesLinks=new ArrayList<>();

    CustomRecyclerVAdapterPortfolioImages customRecyclerVAdapterPortfolioImages;
    RecyclerView PortfolioImagesRecyclerView;

    Button AddImages;
    static ArrayList<Bitmap> ImagesToBePushed=new ArrayList<>();
    static ArrayList<Long> CRC32ofImagesToBePushed=new ArrayList<>();//only used to make it esier to find the index of the image to save

    static Response.Listener<JSONObject> VolleyListener;
    static Response.ErrorListener VolleyErrorListener;
    static RequestQueue RequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_portfolio_images__sub__shop);
        customRecyclerVAdapterPortfolioImages=new CustomRecyclerVAdapterPortfolioImages(PortfolioImages, this);
        PortfolioImagesRecyclerView=findViewById(R.id.RecyclerView_PortfolioImages);
        PortfolioImagesRecyclerView.setAdapter(customRecyclerVAdapterPortfolioImages);
        PortfolioImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AddImages=findViewById(R.id.AddPortfolioImages_AddRemoveSubActivity);
        AddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetImages();
            }
        });


        VolleyListener=new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("VolleyReceived",response.toString());
                if(response.has("PortfolioImagesLinks")){

                        try {
                            ImagesLinkFromServer.clear();
                            for (int i=0;i<response.getJSONArray("PortfolioImagesLinks").length();i++){
                                ImagesLinkFromServer.add(response.getJSONArray("PortfolioImagesLinks").getString(i));
                            }
                            LoadLocalData("Shop");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                } else if(response.has("AddPortfolioImage")){
                    try {
                        if(response.getJSONObject("AddPortfolioImage").getString("successful").equals("true")){
                            int IndexOfTheSuccessfullyReceivedImage=CRC32ofImagesToBePushed.indexOf(response.getJSONObject("AddPortfolioImage").getLong("ImageCRC32"));
                            PortfolioImages.add(ImagesToBePushed.get(IndexOfTheSuccessfullyReceivedImage));
                            PortfolioImagesAsStrings.add(ConvertBitmapToString(ImagesToBePushed.get(IndexOfTheSuccessfullyReceivedImage)));
                            PortfolioImagesLinks.add(response.getJSONObject("AddPortfolioImage").getString("ImageLink"));
                            SaveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if(response.has("RemovePortfolioImage")){
                    try {
                        if(response.getJSONObject("RemovePortfolioImage").getString("successful").equals("true")){
                            int IndexOfImageRemoved=PortfolioImagesLinks.indexOf(response.getJSONObject("RemovePortfolioImage").getString("ImageLink"));
                            PortfolioImagesLinks.remove(response.getJSONObject("RemovePortfolioImage").getString("ImageLink"));
                            PortfolioImagesAsStrings.remove(IndexOfImageRemoved);
                            PortfolioImages.remove(IndexOfImageRemoved);
                            SaveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        VolleyErrorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError","Error "+error.toString());
            }
        };
        RequestQueue=Volley.newRequestQueue(this);
        GetPortfolioImagesLinksFromServer();
    }
void GetImages(){
    Intent GetImagesIntent=new Intent();
    GetImagesIntent.setType("image/*");
    GetImagesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    GetImagesIntent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(GetImagesIntent, IMG_REQ);

}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQ && resultCode==RESULT_OK && data!=null){

            if(data.getClipData() != null) {
                Log.v("VolleyReceived", "Up ToGet Images");
                int count = data.getClipData().getItemCount();
                for(int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    try {
                       PushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            }
        } else if(data.getData() != null) {
            Log.v("VolleyReceived", "Up ToGet Images");
            String imagePath = data.getData().getPath();

            try {
                PushImageToServer(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imagePath)));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    void UpdateTheRecyclerView(){
        customRecyclerVAdapterPortfolioImages=new CustomRecyclerVAdapterPortfolioImages(PortfolioImages, this);
        PortfolioImagesRecyclerView.swapAdapter(customRecyclerVAdapterPortfolioImages, true);
    }
    public static void RemoveImageFromServer(int Index){
        Map<String,Object> map=new HashMap<>();
        map.put("Request","RemovePortfolioImage");
        map.put("ImageLink",PortfolioImagesLinks.get(Index));
        JSONObject Data=new JSONObject(map);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,URL, Data, VolleyListener, VolleyErrorListener);
        RequestQueue.add(jsonObjectRequest);
    }

    public static void PushImageToServer(Bitmap ImageToBePushed){
        String ImageToBePushedAsString=ConvertBitmapToString(ImageToBePushed);

        Map<String,Object> map=new HashMap<>();
        map.put("Request","AddPortfolioImage");
        map.put("ImageAsString",ImageToBePushedAsString);

        CRC32 crc32=new CRC32();
        crc32.update(ImageToBePushedAsString.getBytes());

        map.put("ImageCRC32", crc32.getValue());

        CRC32ofImagesToBePushed.add(crc32.getValue());
        ImagesToBePushed.add(ImageToBePushed);


        JSONObject Data=new JSONObject(map);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,URL, Data, VolleyListener, VolleyErrorListener);

        RequestQueue.add(jsonObjectRequest);

    }



   static String ConvertBitmapToString(Bitmap image){

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP,85,byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
    public Bitmap ConvertStringToBitmap(String image) {

        byte[] bytes;
        bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }













    public String LoadJSONFile(String jsonFileName) {
        String[] mFileList = fileList();
        Boolean fileExists = false;

        for (int i = 0; i < mFileList.length; i++) {
            if (jsonFileName.equals(mFileList[i])) {
                fileExists = true;
                break;
            }
        }
        if (fileExists) {
            String jsonAsString = null;

            try {
                FileInputStream fis = openFileInput(jsonFileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String text;
                while ((text = br.readLine()) != null) {
                    sb.append(text);
                }
                jsonAsString = sb.toString();

                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonAsString;
        } else {

            FileOutputStream fos = null;
            JSONObject emptyJSONObject = new JSONObject();
            try {
                fos = openFileOutput("Data.txt", MODE_PRIVATE);
                fos.write(emptyJSONObject.toString().getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            return emptyJSONObject.toString();
        }


    }
    public void WriteNewShopDataToLocalMemory(JSONObject NewShopDataJSON) {
        try {


            String localMemoryJsonAsString = LoadJSONFile("Data.txt");
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
    public void LoadLocalData(String ShopName) {
        try {

            String jsonAsString = LoadJSONFile("Data.txt");

            if (jsonAsString != null ) {

                JSONObject jsonObject = new JSONObject(jsonAsString);

                if (jsonObject.has(ShopName)) {


                    JSONObject ShopDataJSONObject = jsonObject.getJSONObject(ShopName);


                    if(ShopDataJSONObject.has("PortfolioImagesLinks")){
                        ArrayList<String> ListOfImagesThatDidNotChange = new ArrayList<>();
                        ArrayList<Integer> IndexOfTheImageThatDidNotChange = new ArrayList<>();
                        ArrayList<String> ListOfImagesLinksThatDidNotChange = new ArrayList<>();

                        // ArrayList<String> PortfolioImagesLinks= ParseJSONtoArrayListOfStrings(ShopDataJSONObject.get("PortfolioImagesLinks").toString());
                        ArrayList<String> PortfolioImagesLinksLocal=new ArrayList<>();
                        for (int i=0;i<ShopDataJSONObject.getJSONArray("PortfolioImagesLinks").length();i++){
                            PortfolioImagesLinksLocal.add(i, ShopDataJSONObject.getJSONArray("PortfolioImagesLinks").getString(i));
                        }



                        ArrayList<String> PortfolioImagesAsStringsLocal=new ArrayList<>();
                        for ( int i=0;i<ShopDataJSONObject.getJSONArray("PortfolioImagesAsStrings").length();i++){
                            PortfolioImagesAsStringsLocal.add(i, ShopDataJSONObject.getJSONArray("PortfolioImagesAsStrings").getString(i));
                        }

                        for (int i = 0; i < ImagesLinkFromServer.size(); i++) {


                            if (i < PortfolioImagesLinksLocal.size()) {
                                Boolean LinkNotFound = true;
                                for (int j = 0; j <PortfolioImagesLinksLocal.size(); j++) {

                                    if (PortfolioImagesLinksLocal.get(j).equals(ImagesLinkFromServer.get(i))) {
                                        LinkNotFound = false;
                                        IndexOfTheImageThatDidNotChange.add(j);
                                        ListOfImagesThatDidNotChange.add(PortfolioImagesAsStringsLocal.get(j));
                                        ListOfImagesLinksThatDidNotChange.add(PortfolioImagesLinksLocal.get(j));
                                    }
                                }
                                if (LinkNotFound) {

                                    RequestImage(ImagesLinkFromServer.get(i));
                                }

                            } else {
                                RequestImage(ImagesLinkFromServer.get(i));
                            }

                        }
                        ShopDataJSONObject.remove("PortfolioImagesAsStrings");
                        ShopDataJSONObject.remove("PortfolioImagesLinks");
                        PortfolioImagesAsStrings.clear();
                        PortfolioImagesAsStrings=ListOfImagesThatDidNotChange;
                        PortfolioImagesLinks.clear();
                        PortfolioImagesLinks=ListOfImagesLinksThatDidNotChange;

                        PortfolioImages.clear();
                        for (int i=0;i<PortfolioImagesAsStringsLocal.size();i++){
                            PortfolioImages.add(ConvertStringToBitmap(PortfolioImagesAsStringsLocal.get(i)));
                        }


                    } else{

                        for(int i=0;i<ImagesLinkFromServer.size();i++){
                            RequestImage(ImagesLinkFromServer.get(i));
                        }
                    }

                 /// if some images no longer exists in server the code above will remove them so we need to save the trimmed set of images
                  SaveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();


                } else {
                   ///when we call request and receive an image it will call  SaveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView() to save it and hance create Data file

                    for(int i=0;i<ImagesLinkFromServer.size();i++){
                        RequestImage(ImagesLinkFromServer.get(i));
                    }

                }
            } else {
                //null is handled in the calling function LoadJSONFile()
            }


        } catch (JSONException e) {
            Log.v("LoadFromCache","Error parsing JSON in ShopDetailsActivity LoadLocalData function");
            e.printStackTrace();
        }
    }
    void RequestImage(final String ImageLink) {
        Log.v("VolleyReceived", "We Are Up Untill here ");
        ImageRequest imageRequest = new ImageRequest(ImageLink, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                PortfolioImages.add(response);
                PortfolioImagesAsStrings.add(ConvertBitmapToString(response));
                PortfolioImagesLinks.add(ImageLink);

                SaveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView();

            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyErrors", "onErrorResponse: IN SHOPDETAILS ACTIVITY IMAGES ImagesForPortfolio" + error.toString());
            }
        });
        RequestQueue.add(imageRequest);
    }
    void GetPortfolioImagesLinksFromServer(){
   Map<String,Object> map=new HashMap<>();
   map.put("Request", "PortfolioImagesLinks");
   map.put("Token","The Token");//or shop name
   JSONObject Data=new JSONObject(map);
   JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,URL, Data, VolleyListener, VolleyErrorListener);
   RequestQueue.add(jsonObjectRequest);
    }
    void SaveUpdatedShopDataToMemoryAndNotifyPortfolioRecyclerView(){
        Map<String,Object> map=new HashMap<>();
        map.put("PortfolioImagesAsStrings",PortfolioImagesAsStrings);
        map.put("PortfolioImagesLinks", PortfolioImagesLinks);

        JSONObject Data=new JSONObject(map);
        WriteNewShopDataToLocalMemory(Data);
        UpdateTheRecyclerView();

    }
}
