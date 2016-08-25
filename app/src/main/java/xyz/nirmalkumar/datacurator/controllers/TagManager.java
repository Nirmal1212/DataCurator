package xyz.nirmalkumar.datacurator.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by nirmal on 7/30/16.
 */
public class TagManager {

    public static final String IS_CONFIGURED = "is_configured";
    public static final String ROOT_FOLDER = "Data_curator";
    public static final String BASE_FOLDER = "BASE_FOLDER_PATH";
    public static final String TAGFILE = "TAG_FILE";
    public static final String ITEMS_FOLDER = "ITEMS_FOLDER";
    private static final String TAG_CONFIG_JSON = "TAG_CONFIG_JSON";
    private static final String TAG_ONLINEFILE = "TAG_ONLINE_FILE";

    public static void onLogin(){
        Utils.saveTextToFile("Test","test");
    }

    public static String getBaseFolder(Context mContext){
        return getValueInPref(mContext,BASE_FOLDER);
    }

    public static String getItemsFolder(Context mContext){
        return getValueInPref(mContext,ITEMS_FOLDER);
    }

    public static String getTagfilePath(Context mContext){
        return getValueInPref(mContext,TAGFILE);
    }

    public static void setItemsFolder(Context mContext,String path) {
        Map<String, String> map=new HashMap<>();
        map.put(ITEMS_FOLDER,path);
        saveKeyValueInPref(mContext,map);
    }

    public static void setTagFilePath(Context mContext,String path) {
        Map<String, String> map=new HashMap<>();
        map.put(TAGFILE,path);
        saveKeyValueInPref(mContext,map);
    }

    public static void setBaseFolder(Context mContext,String path) {
        Map<String, String> map=new HashMap<>();
        map.put(BASE_FOLDER,path);
        saveKeyValueInPref(mContext,map);
    }

    public static String loadTagConfig(Context mContext,String path){
        Utils.logd("Loading tag file"+path);
        String data = null;
        try {
            data = Utils.readTextFromFile(path);
            JSONObject obj = new JSONObject(data);
            Utils.logd("Read data = "+obj.toString(2));
            setTagConfig(mContext,obj.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject getTagConfigData(Context mContext){

        String s = getValueInPref(mContext,TAG_CONFIG_JSON);
        boolean isConfigured = !(null==s);
        Utils.logd("JSON configuration found="+isConfigured);
        if(isConfigured)
            try {
                return new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        else
            return null;
    }

    private static void setTagConfig(Context mContext, String tagJSONconfig) {
        Map<String, String> map=new HashMap<>();
        map.put(TAG_CONFIG_JSON,tagJSONconfig);
        saveKeyValueInPref(mContext,map);
    }

    public static boolean isConfigured(Context mContext){
        String s = getValueInPref(mContext,TAG_CONFIG_JSON);
        boolean isConfigured = !(null==s);
        Utils.logd("JSON configuration found="+isConfigured);
        return isConfigured;
    }

    public static String getValueInPref(Context mContext,String key){
        SharedPreferences preferences = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return preferences.getString(key,null);
    }


    public static void saveKeyValueInPref(Context mContext, Map<String,String> kVals) {
        SharedPreferences preferences = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();
        Iterator it = kVals.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            prefEditor.putString((String)pair.getKey(), (String)pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        prefEditor.commit();
    }


    public static void setOnlineTagFile(Context mContext, String url) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(TagManager.TAG_ONLINEFILE,url);
        String fullurl = url;
        String[] splits = fullurl.split("/");
        String baseURL = fullurl.substring(0,fullurl.indexOf(splits[splits.length -1]));
        Utils.logd("Full URL ="+fullurl+" baseURL = "+baseURL);
        saveKeyValueInPref(mContext,map);
    }

    public static String getOnlineTagFile(Context mContext){
        return getValueInPref(mContext,TAG_ONLINEFILE);
    }

    public static String getOnlineBaseURL(Context mContext){
        String fullurl = getValueInPref(mContext,TAG_ONLINEFILE);
        String[] splits = fullurl.split("/");
        String baseURL = fullurl.substring(0,fullurl.indexOf(splits[splits.length -1]));
        Utils.logd("Full URL ="+fullurl+" baseURL = "+baseURL);
        return baseURL;
    }
}
