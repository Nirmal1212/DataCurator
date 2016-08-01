package xyz.nirmalkumar.datacurator.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import xyz.nirmalkumar.datacurator.R;
import xyz.nirmalkumar.datacurator.controllers.ItemsManager;
import xyz.nirmalkumar.datacurator.controllers.TagManager;
import xyz.nirmalkumar.datacurator.controllers.Utils;
import xyz.nirmalkumar.datacurator.models.Tag;
import xyz.nirmalkumar.datacurator.models.TagAttr;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeBulkMode.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeBulkMode#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeBulkMode extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ArrayList<Tag> tagList;

    public HomeBulkMode() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeBulkMode.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeBulkMode newInstance(String param1, String param2) {
        HomeBulkMode fragment = new HomeBulkMode();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Picasso.with(getActivity()).setLoggingEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_bulk_mode, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Bind(R.id.container) LinearLayout mContainer;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            prepareTagConfig();
            prepareTagsUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadImageItems();
    }

    @Bind(R.id.gridView)
    GridView mGridView;

    @OnItemClick(R.id.gridView) public void onItemsSelected(View v){
        ImageAdapter.ViewHolder holder = (ImageAdapter.ViewHolder) v.getTag();
        holder.isSelected = !holder.isSelected;
        if(holder.isSelected)
            holder.selection.setVisibility(View.VISIBLE);
        else
            holder.selection.setVisibility(View.INVISIBLE);
        v.setTag(holder);
    }

    void loadImageItems(){
        List<File> imageData = ItemsManager.getItemsList(getActivity());
        mGridView.setAdapter(new ImageAdapter(imageData));

    }

    public class ImageAdapter extends BaseAdapter{

        public class ViewHolder{
            @Bind(R.id.image) ImageView img;

            @Bind(R.id.selection)ImageView selection;

            boolean isSelected = false;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

        List<File> images;
        public ImageAdapter(List<File> imgs) {
            images = imgs;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int i) {
            Utils.logd("Getting item "+ images.get(i).getPath());
            return images.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int pos, View view, ViewGroup viewGroup) {

            ViewHolder holder;
            if(view!=null){
                holder = (ViewHolder)view.getTag();
            }else{
                view = LayoutInflater.from(getActivity()).inflate(R.layout.img_item,viewGroup,false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
            Picasso.with(getActivity())
                    .load((File)getItem(pos))
                    .placeholder(R.drawable.placeholder)
                    .into(holder.img);

            return view;
        }
    }

    private void prepareTagsUI() {
        if (mContainer!=null) {
            Iterator tagIterator = tagList.iterator();
            while(tagIterator.hasNext()){
                Tag t = (Tag) tagIterator.next();

                Button child = new Button(getActivity());
                child.setText(t.getLabel() + "\t 5/10");
                Iterator it = t.getAttrList().iterator();

                List<Button> attrButtons = new ArrayList<>();
                attrButtons.add(child);
                while(it.hasNext()){
                    TagAttr attr = (TagAttr) it.next();
                    Button b = new Button(getActivity());
                    b.setText("**"+attr.getLabel());
                    attrButtons.add(b);
                    Iterator valIterator = attr.getValues().iterator();
                    while(valIterator.hasNext()){
                        String val = (String) valIterator.next();
                        Button bval = new Button(getActivity());
                        bval.setText("--"+bval);
                        attrButtons.add(bval);
                    }
                }

                Iterator buttonsIterator = attrButtons.iterator();
                while(buttonsIterator.hasNext()){
                    Button b = (Button) buttonsIterator.next();
                    LinearLayout row = new LinearLayout(getActivity());
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.weight = 1;
                    row.addView(b,params);
                    mContainer.addView(row);
                }

            }
        }else
            Utils.loge("Container UI missing to populate the tags");
    }

    private void prepareTagConfig() throws JSONException,NullPointerException{
        JSONObject data = TagManager.getTagConfigData(getActivity());
        tagList = new ArrayList<>();
        JSONObject tags = data.getJSONObject("tags").getJSONObject("class");
        Iterator it = tags.keys();
        while(it.hasNext()){
            String key = (String) it.next();
            Tag t = new Tag(tags.getJSONObject(key));
            tagList.add(t);
        }

    }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
