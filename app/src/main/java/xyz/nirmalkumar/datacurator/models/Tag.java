package xyz.nirmalkumar.datacurator.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import xyz.nirmalkumar.datacurator.controllers.Utils;

/**
 * Created by nirmal on 7/30/16.
 */
public class Tag {

    @Expose
    String label;
    @Expose
    @SerializedName("class")
    ArrayList<Tag> subTags;

    @Expose
    ArrayList<TagAttr> attrList;

    @Expose
    TAG_STATE state;

    JSONObject jObj;

    public Tag(JSONObject obj) throws JSONException {
        jObj = obj;
        label = obj.getString("label");
        Utils.logd("In constructor for "+label);
        JSONObject subclasses = obj.optJSONObject("class");
        subTags = new ArrayList<>();
        if(subclasses!=null){
//            Utils.logd("In sub classes : "+subclasses.toString(2));
            Iterator it = subclasses.keys();
            while(it.hasNext()){
                String key = (String) it.next();
//                Utils.logd("Read key = "+key+" from "+subclasses.toString(2));
                Tag sTag = new Tag(subclasses.getJSONObject(key));
                subTags.add(sTag);
            }
        }
        JSONObject jAttr = obj.optJSONObject("attr");
        attrList = new ArrayList<>();
        if(jAttr!=null){
            Iterator it = jAttr.keys();
            while(it.hasNext()){
                String key = (String) it.next();
                TagAttr attr = new TagAttr(jAttr.getJSONObject(key));
                attrList.add(attr);
            }
        }
    }

    @Override
    public String toString() {
        if(jObj!=null)
            try {
                return jObj.toString(2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return "JSON null";
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Tag> getSubTags() {
        return subTags;
    }

    public void setSubTags(ArrayList<Tag> subTags) {
        this.subTags = subTags;
    }

    public ArrayList<TagAttr> getAttrList() {
        return attrList;
    }

    public void setAttrList(ArrayList<TagAttr> attrList) {
        this.attrList = attrList;
    }

    public TAG_STATE getState() {
        return state;
    }

    public void setState(TAG_STATE state) {
        this.state = state;
    }
}
