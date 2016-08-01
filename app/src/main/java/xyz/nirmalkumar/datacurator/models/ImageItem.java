package xyz.nirmalkumar.datacurator.models;


import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by nirmal on 7/30/16.
 */
public class ImageItem {

    String id;
    @Expose
    String path;

    @Expose
    String name;

    @Expose
    ArrayList<Tag> tags = new ArrayList<Tag>();

    @Expose
    ITEM_STATE state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public ITEM_STATE getState() {
        return state;
    }

    public void setState(ITEM_STATE state) {
        this.state = state;
    }
}