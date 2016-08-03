package xyz.nirmalkumar.datacurator.models;

import com.google.gson.annotations.Expose;

/**
 * Created by nirmal on 7/30/16.
 */
public enum ITEM_STATE {

    @Expose
    INVALID,
    @Expose
    NOT_VERIFIED,
    @Expose
    VERIFIED,
    @Expose
    PROVISIONALLY_VERIFIED,
    @Expose
    FOR_REVIEW
}