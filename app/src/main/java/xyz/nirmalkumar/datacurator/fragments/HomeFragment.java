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
import xyz.nirmalkumar.datacurator.controllers.TagManager;
import xyz.nirmalkumar.datacurator.controllers.Utils;
import xyz.nirmalkumar.datacurator.models.Product;
import xyz.nirmalkumar.datacurator.models.Tag;
import xyz.nirmalkumar.datacurator.models.TagAttr;

public class HomeFragment extends Fragment {


    private ArrayList<Tag> tagList;
    private ImageAdapter mAdapter;
    private DisplayImageOptions imOptions;
    private ArrayList<Product> myItems;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageLoader(getActivity());
        L.enableLogging();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_bulk_mode, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Bind(R.id.container)
    LinearLayout mContainer;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadImageItems();
    }

    @Bind(R.id.tags)
    Spinner mSpinner;

    @OnItemSelected(R.id.tags)
    public void onSpinnerSelected(int pos) {
        Toast.makeText(getActivity(), "Selected " + tags.get(pos), Toast.LENGTH_SHORT).show();
        mAdapter.filterBy(tags.get(pos));
        mAdapter.notifyDataSetChanged();
    }

    @Bind(R.id.gridView)
    GridView mGridView;

    @OnItemClick(R.id.gridView)
    public void onItemsSelected(View v, int position) {
        if (mAdapter.selectedPositions.containsKey(position))
            mAdapter.selectedPositions.remove(position);
        else
            mAdapter.selectedPositions.put(position, true);

        mAdapter.updateItem(v, position);
//        mAdapter.notifyDataSetChanged();
    }

    void loadImageItems() {
        myItems = ItemsManager.getSavedProductsList(getActivity());
        tags = ItemsManager.getOnlineTagsList(getActivity());
        if(myItems.size()>0 && tags.size()>0) {
            mAdapter = new ImageAdapter(myItems, tags.get(0));
            mGridView.setAdapter(mAdapter);
            ArrayAdapter<String> mSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, tags);
            mSpinner.setAdapter(mSpinnerAdapter);
        }
            save_pull.setVisibility(View.VISIBLE);
            save_pull.setText("Save");
            save_exit.setVisibility(View.VISIBLE);
            save_exit.setText("Upload");

    }

    @OnCheckedChanged(R.id.select_all)
    public void onSelectAllChanged(boolean checked) {
        mAdapter.selectAll(checked);
    }

    @Bind(R.id.button_save_pull)
    Button save_pull;
    @Bind(R.id.button_save_exit)
    Button save_exit;

    @OnClick(R.id.button_save_pull)
    public void onSaveAndPull(View v) {


    }

    @OnClick(R.id.button_save_exit)
    public void onSaveAndExit(View v) {
        JSONObject data = mAdapter.getOutputData();
        String url = TagManager.getOnlineBaseURL(getActivity());

        try {
            String fileName = Utils.saveTextToFile(data.toString(3),"out.json");
            File file = new File(fileName);
            ItemsManager.pushItemToServer(getActivity(), url, file);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class ImageAdapter extends BaseAdapter {


        public ArrayList<Product> imageItems, allItems;
        public String filterBy;

        public ImageAdapter(ArrayList<Product> myItems, String option) {
            this.allItems = myItems;
            imageItems = new ArrayList<>();
            filterBy(option);
        }

        public void filterBy(String option) {
            filterBy = option;
            imageItems.clear();
            for (int i = 0; i < allItems.size(); i++) {
                Product p = allItems.get(i);
                if (p.getTagValue().equalsIgnoreCase(option))
                    imageItems.add(p);
            }
            notifyDataSetChanged();
        }

        public class ViewHolder {
            @Bind(R.id.image)
            ImageView img;

            @Bind(R.id.selection)
            ImageView selection;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

        public Map<Integer, Boolean> selectedPositions = new HashMap<>();

        public JSONObject getOutputData() {

            JSONObject data = new JSONObject();
            JSONObject res = new JSONObject();
            try {
                data.put(filterBy, res);
                for (int i = 0; i < imageItems.size(); i++) {
                    boolean b = selectedPositions.containsKey(i);
                    res.put(imageItems.get(i).getUrl(), b);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }

        public void selectAll(boolean selection) {
            if (selection) {
                for (int i = 0; i < imageItems.size(); i++)
                    selectedPositions.put(i, true);
            } else {
                selectedPositions.clear();
            }
            notifyDataSetChanged();
        }

//        public ImageAdapter(List<File> imgs, List<String> urls, boolean isOnline) {
//            images = imgs;
//            imageURLs = urls;
//            this.isOnline = isOnline;
//            selectedPositions = new HashMap<>();
//        }

        @Override
        public int getCount() {
            return imageItems.size();
        }

        @Override
        public Object getItem(int i) {
            Utils.logd("Getting item " + imageItems.get(i).getUrl());
            return imageItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void updateItem(View view, int pos) {
            ViewHolder holder = (ViewHolder) view.getTag();

            if (selectedPositions.containsKey(pos))
                holder.selection.setVisibility(View.VISIBLE);
            else
                holder.selection.setVisibility(View.INVISIBLE);

            holder.selection.bringToFront();
        }

        @Override
        public View getView(int pos, View view, ViewGroup viewGroup) {

            final ViewHolder holder;
            if (view != null) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.img_item, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
                holder.img.setAlpha(.1f);

            }
            String url = imageItems.get(pos).getUrl();
            Utils.logd("Calling image " + url);
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
            if (selectedPositions.containsKey(pos))
                holder.selection.setVisibility(View.VISIBLE);
            else
                holder.selection.setVisibility(View.INVISIBLE);

            holder.selection.bringToFront();

            return view;
        }
    }

    private void prepareTagsUI() {
        if (mContainer != null) {
            Iterator tagIterator = tagList.iterator();
            while (tagIterator.hasNext()) {
                Tag t = (Tag) tagIterator.next();

                Button child = new Button(getActivity());
                child.setText(t.getLabel() + "\t 5/10");
                Iterator it = t.getAttrList().iterator();

                List<Button> attrButtons = new ArrayList<>();
                attrButtons.add(child);
                while (it.hasNext()) {
                    TagAttr attr = (TagAttr) it.next();
                    Button b = new Button(getActivity());
                    b.setText("**" + attr.getLabel());
                    attrButtons.add(b);
                    Iterator valIterator = attr.getValues().iterator();
                    while (valIterator.hasNext()) {
                        String val = (String) valIterator.next();
                        Button bval = new Button(getActivity());
                        bval.setText("--" + bval);
                        attrButtons.add(bval);
                    }
                }

                Iterator buttonsIterator = attrButtons.iterator();
                while (buttonsIterator.hasNext()) {
                    Button b = (Button) buttonsIterator.next();
                    LinearLayout row = new LinearLayout(getActivity());
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.weight = 1;
                    row.addView(b, params);
                    mContainer.addView(row);
                }

            }
        } else
            Utils.loge("Container UI missing to populate the tags");
    }

    private void prepareTagConfig() throws JSONException, NullPointerException {
        myItems = ItemsManager.getSavedProductsList(getActivity());
    }

    ArrayList<String> tags = new ArrayList<>();
    Map<String, ArrayList<String>> tagItems = new HashMap<>();

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