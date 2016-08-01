package xyz.nirmalkumar.datacurator.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nirmal on 7/31/16.
 */
public class TagAttr {
    String label;
    ArrayList<String> values;

    public TagAttr(JSONObject obj) throws JSONException {
        label = obj.getString("label");
        JSONObject val = obj.getJSONObject("values");
        Iterator it = val.keys();
        values = new ArrayList<>();
        while(it.hasNext()){
            String key = (String) it.next();
            values.add(val.getJSONObject(key).getString("label"));
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }
}
