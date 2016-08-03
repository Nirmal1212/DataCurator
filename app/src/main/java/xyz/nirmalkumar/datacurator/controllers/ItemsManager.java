package xyz.nirmalkumar.datacurator.controllers;

import android.content.Context;
import android.os.Environment;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.nirmalkumar.datacurator.models.Product;

/**
 * Created by nirmal on 8/1/16.
 */
public class ItemsManager {


    private static final String ONLINE_ITEMS_LIST = "online_items";
    public static final String OTHERS_FOLDER = "_others";
    private static final String PRODUCT_LIST = "products_list";

    public static void pushItemToServer(Context mContext, String url, File file){

        int size = (int) file.length();
        final byte[] byteData = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(byteData, 0, byteData.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String url = "http://192.168.0.54:8000/images/test1/";

        BaseVolleyRequest baseVolleyRequest = new BaseVolleyRequest(1, url, null,null) {

            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String boundary = "apiclient-" + System.currentTimeMillis();
            String mimeType = "multipart/form-data; boundary=" + boundary;
            String twoHyphens = "--";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;

            @Override
            public String getBodyContentType() {
                return mimeType;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Utils.logd("Req. getting body");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                dos = new DataOutputStream(bos);
                try {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
//                                + "tagged_file.json" + "\"" + lineEnd);
                            + "tagged_file_"+System.currentTimeMillis()+".json" + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(byteData);
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    return bos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return byteData;
            }
        };

        Volley.newRequestQueue(mContext).add(baseVolleyRequest);
        Utils.logd("Req. called in volley");
    }


    public static List<File> getItemsList(Context mContext){
        List<File> items = new ArrayList<>();
        String filePath = TagManager.getItemsFolder(mContext);

        File tagFile = new File(Environment.getExternalStorageDirectory(),"/Data_curator/tagConfig1.json");
        Utils.logd("Getting image items from "+tagFile.getParentFile().getAbsolutePath() + " FilePath ="+filePath);
        File folder = tagFile.getParentFile();
        if(folder.exists())
        {
            Utils.logd("Parent Folder exists");
            List<File> files = listf(folder);
            for (int i=0;i<files.size();i++){
                if(files.get(i).getAbsolutePath().endsWith(".jpg") || files.get(i).getAbsolutePath().endsWith(".jpeg")) {
                    items.add(files.get(i));
                }
            }
        }else{
            Utils.logd("Parent Folder does not exist");
        }
        return items;
    }

    public static List<File> listf(File directory) {
        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listf(new File(file.getAbsolutePath())));
            }
        }
        //System.out.println(fList);
        return resultList;
    }

    public static void setOnlineItemsList(Context mContext,String json){
        Map<String, String> kvals=new HashMap<>();
        kvals.put(ONLINE_ITEMS_LIST,json);
        TagManager.saveKeyValueInPref(mContext,kvals);
    }

    public static String getProductsAsJSON(Context mContext, ArrayList<Product> items){
//        List<String> tags = new ArrayList();
//        Map<String,List<String>> map = new HashMap<>();
//        for(int i=0;i<items.size();i++){
//            Product p = items.get(i);
//            if(!tags.contains(p.getTagValue())){
//                tags.add(p.getTagValue());
//            }
//        }
//        for(int i=0;i<items.size();i++){
//            Product p = items.get(i);
//            if(!tags.contains(p.getTagValue())){
//                tags.add(p.getTagValue());
//            }
//        }
        JSONArray arr = new JSONArray();
        for(int i=0;i<items.size();i++){
            Product p = items.get(i);
            try {
                arr.put(p.getJSONObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arr.toString();
    }

    public static void saveProductsList(Context mContext, ArrayList<Product> items){
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        for(int i=0;i<items.size();i++){
            try {
                arr.put(items.get(i).getJSONObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put(PRODUCT_LIST,arr.toString());
        TagManager.saveKeyValueInPref(mContext,map);
    }

    public static ArrayList<Product> getSavedProductsList(Context mContext){
        String s = TagManager.getValueInPref(mContext,PRODUCT_LIST);
        ArrayList<Product> results = new ArrayList<>();
        if(s!=null){
            try {
                JSONArray arr = new JSONArray(s);
                for(int i=0;i<arr.length();i++){
                    results.add(Product.getInstance(arr.getJSONObject(i).toString()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public static JSONObject getOnlineItemsList(Context mContext) {
        String s = TagManager.getValueInPref(mContext,ONLINE_ITEMS_LIST);
        if(s==null)
            return null;
        try {
            JSONObject obj = new JSONObject(s);
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}