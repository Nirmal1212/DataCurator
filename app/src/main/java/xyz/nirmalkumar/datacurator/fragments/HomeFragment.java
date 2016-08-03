package xyz.nirmalkumar.datacurator.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import xyz.nirmalkumar.datacurator.R;
import xyz.nirmalkumar.datacurator.controllers.ItemsManager;
import xyz.nirmalkumar.datacurator.controllers.Utils;
import xyz.nirmalkumar.datacurator.models.Tag;
import xyz.nirmalkumar.datacurator.models.TagAttr;

public class HomeFragment extends Fragment {


    private ArrayList<Tag> tagList;
    private ImageAdapter mAdapter;
    private DisplayImageOptions imOptions;
    private JSONObject input,output;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Picasso.with(getActivity()).setLoggingEnabled(true);
        initImageLoader(getActivity());
        L.enableLogging();
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
//            prepareTagsUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadImageItems();
    }

    @Bind(R.id.tags)
    Spinner mSpinner;

    @OnItemSelected(R.id.tags) public void onSpinnerSelected(int pos){
        Toast.makeText(getActivity(),"Selected "+tags.get(pos),Toast.LENGTH_SHORT).show();
        mAdapter.imageURLs = tagItems.get(tags.get(pos));
        mAdapter.notifyDataSetChanged();
    }

    @Bind(R.id.gridView)
    GridView mGridView;

    @OnItemClick(R.id.gridView) public void onItemsSelected(View v,int position){
        if(mAdapter.selectedPositions.containsKey(position))
            mAdapter.selectedPositions.remove(position);
        else
            mAdapter.selectedPositions.put(position,true);

        mAdapter.updateItem(v,position);
//        mAdapter.notifyDataSetChanged();
    }

    List<File> imageData=new ArrayList<>();
    void loadImageItems(){
        imageData = ItemsManager.getItemsList(getActivity());
        boolean isOnline = true;
        if(isOnline) {
            mAdapter = new ImageAdapter(null,tagItems.get(tags.get(0)),true);
        }else{
            mAdapter = new ImageAdapter(imageData,null,false);
        }
        mGridView.setAdapter(mAdapter);
        ArrayAdapter<String> mSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, tags);
        mSpinner.setAdapter(mSpinnerAdapter);
        save_pull.setVisibility(View.VISIBLE);
        save_pull.setText("Save");
        save_exit.setVisibility(View.VISIBLE);
        save_exit.setText("Upload");
    }

    @OnCheckedChanged(R.id.select_all) public void onSelectAllChanged(boolean checked){
        mAdapter.selectAll(checked);
    }

    @Bind(R.id.button_save_pull) Button save_pull;
    @Bind(R.id.button_save_exit) Button save_exit;
    @OnClick(R.id.button_save_pull) public void onSaveAndPull(View v){


    }

    @OnClick(R.id.button_save_exit) public void onSaveAndExit(View v){
        mAdapter.getOutputData();
        String url = "";
        File file = new File("path");
        ItemsManager.pushItemToServer(getActivity(),url,file);
    }

    public class ImageAdapter extends BaseAdapter{



        public class ViewHolder{
            @Bind(R.id.image) ImageView img;

            @Bind(R.id.selection)ImageView selection;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

        List<File> images;
        List<String> imageURLs;
        final boolean isOnline;
        public Map<Integer,Boolean> selectedPositions;

        public void getOutputData(){
            if(isOnline){
                for (int i = 0; i < imageURLs.size(); i++) {
                    JSONObject res = new JSONObject();
                    try {
                        boolean b = selectedPositions.containsKey(i);
                        res.put(imageURLs.get(i), b);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                for (int i = 0; i < images.size(); i++) {
                    JSONObject res = new JSONObject();
                    try {
                        boolean b = selectedPositions.containsKey(i);
                        res.put(images.get(i).getPath(), b);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void selectAll(boolean selection){
            if(selection){
                for(int i=0; i<images.size(); i++)
                    selectedPositions.put(i,true);
            }else{
                selectedPositions.clear();
            }
            notifyDataSetChanged();
        }

        public ImageAdapter(List<File> imgs, List<String> urls, boolean isOnline) {
            images = imgs;
            imageURLs = urls;
            this.isOnline = isOnline;
            selectedPositions = new HashMap<>();
        }

        @Override
        public int getCount() {
            if(isOnline)
                return imageURLs.size();
            else
                return images.size();
        }

        @Override
        public Object getItem(int i) {
            if(isOnline){
                String url = imageURLs.get(i);
                Utils.logd("Getting item " + url);
                return url;
            }else {
                Utils.logd("Getting item " + images.get(i).getAbsolutePath());
                return images.get(i);
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void updateItem(View view,int pos){
            ViewHolder holder = (ViewHolder) view.getTag();

            if(selectedPositions.containsKey(pos))
                holder.selection.setVisibility(View.VISIBLE);
            else
                holder.selection.setVisibility(View.INVISIBLE);

            holder.selection.bringToFront();
        }

        @Override
        public View getView(int pos, View view, ViewGroup viewGroup) {

            final ViewHolder holder;
            if(view!=null){
                holder = (ViewHolder)view.getTag();
            }else{
                view = LayoutInflater.from(getActivity()).inflate(R.layout.img_item,viewGroup,false);
                holder = new ViewHolder(view);
                view.setTag(holder);
                holder.img.setAlpha(.1f);

            }
            if(isOnline){
                String url = (String) getItem(pos);
                Utils.logd("Calling image "+url);
                ImageLoader.getInstance().displayImage(url,
                        holder.img, imOptions, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                holder.img.setAlpha(.1f);
                                holder.img.setImageResource(R.drawable.placeholder);
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                holder.img.setAlpha(1f);
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {

                            }
                        });
            }else {
                Picasso.with(getActivity())
                        .load((File) getItem(pos))
                        .into(holder.img, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img.setAlpha(1f);
                            }

                            @Override
                            public void onError() {
                                holder.img.setAlpha(.1f);
                            }
                        });
            }
            if(selectedPositions.containsKey(pos))
                holder.selection.setVisibility(View.VISIBLE);
            else
                holder.selection.setVisibility(View.INVISIBLE);

            holder.selection.bringToFront();

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
//        JSONObject data = TagManager.getTagConfigData(getActivity());
//        tagList = new ArrayList<>();
//        JSONObject tags = data.getJSONObject("tags").getJSONObject("class");
//        Iterator it = tags.keys();
//        while(it.hasNext()){
//            String key = (String) it.next();
//            Tag t = new Tag(tags.getJSONObject(key));
//            tagList.add(t);
//        }

        input = ItemsManager.getOnlineItemsList(getActivity());
        output = new JSONObject();
        Iterator it = input.keys();
        while(it.hasNext()){
            String key = (String) it.next();
            tags.add(key);
            output.put(key,new JSONArray());
            ArrayList<String > items = new ArrayList<>();
            JSONArray arr = input.getJSONArray(key);
            for(int i=0;i<arr.length();i++)
                items.add(arr.getString(i));
            tagItems.put(key,items);
        }
        output.put(ItemsManager.OTHERS_FOLDER,new JSONArray());
    }

    ArrayList<String> tags=new ArrayList<>();
    Map<String,ArrayList<String>> tagItems = new HashMap<>();

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .memoryCacheExtraOptions(240, 400) // default = device screen dimensions
                .diskCacheExtraOptions(240, 400, null)
                .discCacheFileCount(30)
                .memoryCacheSizePercentage(12)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
        imOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .showImageOnLoading(R.drawable.placeholder)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }
}
