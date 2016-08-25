package xyz.nirmalkumar.datacurator.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import xyz.nirmalkumar.datacurator.controllers.Utils;

/**
 * Created by nirmal on 8/3/16.
 */
public class Product {

    @Expose
    boolean isLocal;

    @Expose
    String url,localPath,tagValue;

    @Expose
    ITEM_STATE state;

    public static Product getInstance(String json) {
        Gson gson = new GsonBuilder().create();
        Product it =gson.fromJson(json,Product.class);
        Utils.logd(" Product: "+json);
        Utils.logd(" Product ur: "+it.url);
        return it;
    }

    public String getJSON(){
        String s = "";
        Gson gson = new GsonBuilder().create();
        s = gson.toJson(this);
        return s;
    }

    public JSONObject getJSONObject() throws JSONException {
        return new JSONObject(getJSON());
    }

    public Product(boolean isLocal, String url, String localPath, String value) {
        this.isLocal = isLocal;
        this.url = url;
        this.localPath = localPath;
        setTagValue(value);
        setState(ITEM_STATE.PROVISIONALLY_VERIFIED);
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public ITEM_STATE getState() {
        return state;
    }

    public void setState(ITEM_STATE state) {
        this.state = state;
    }


}
