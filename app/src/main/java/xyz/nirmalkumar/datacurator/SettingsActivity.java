package xyz.nirmalkumar.datacurator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.nirmalkumar.datacurator.controllers.TagManager;
import xyz.nirmalkumar.datacurator.controllers.Utils;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_TAG_FILE = 1;
    private static final int REQUEST_BASE_FOLDER = 2;
    private static final int REQUEST_ITEMS_FOLDER = 3;
    @Bind(R.id.base_folder)
public TextView mBaseFolder;
    @Bind(R.id.tag_config)
public TextView mTagFile;
    @Bind(R.id.items_folder)
public TextView mItemsFolder;

    @OnClick(R.id.base_folder) public void onBaseFolderClick(){
        triggerFilePicker(REQUEST_BASE_FOLDER);
    }

    @OnClick(R.id.tag_config) public void onTagConfigFileClick(){
        Utils.logd("Triggering tag file picker");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_TAG_FILE);
        }
    }

    @OnClick(R.id.items_folder) public void onItemsFolderClick(){
        triggerFilePicker(REQUEST_ITEMS_FOLDER);
    }

    void triggerFilePicker(int req){
        Utils.logd("Triggering file picker");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if(req==REQUEST_BASE_FOLDER || req==REQUEST_ITEMS_FOLDER)
            intent.setType(DocumentsContract.Document.MIME_TYPE_DIR);//For API 19+
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/"+ TagManager.ROOT_FOLDER+"/");
        intent.setDataAndType(uri, "*/*");
        startActivityForResult(Intent.createChooser(intent, "Open folder"),req);
    }

    Context mContext;

    @OnClick(R.id.home) public void goHome(){
        startActivity(new Intent(SettingsActivity.this,Home.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = SettingsActivity.this;
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        Utils.verifyStoragePermissions(this);
        String baseFolder = TagManager.getBaseFolder(this);
        String tagFile = TagManager.getTagfilePath(this);
        String imgFolder = TagManager.getItemsFolder(this);

        if(baseFolder!=null)
            mBaseFolder.setText(baseFolder);
        if(tagFile!=null)
            mTagFile.setText(baseFolder);
        if(imgFolder!=null)
            mItemsFolder.setText(baseFolder);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(SettingsActivity.this, "Req", Toast.LENGTH_SHORT).show();
        if (resultCode == RESULT_OK) {
            Uri fileURI = data.getData();
            if (requestCode == REQUEST_BASE_FOLDER) {
//                Toast.makeText(SettingsActivity.this,"Response ="+fullPhotoUri.getEncodedPath(),Toast.LENGTH_LONG).show();
                mBaseFolder.setText(fileURI.getPath());
                TagManager.setBaseFolder(mContext,fileURI.getPath());
            }else if (requestCode == REQUEST_TAG_FILE) {
                mTagFile.setText(fileURI.getPath());
                TagManager.setTagFilePath(mContext,fileURI.getPath());
                TagManager.loadTagConfig(mContext,fileURI.getPath());
            }else if (requestCode == REQUEST_ITEMS_FOLDER) {
                mItemsFolder.setText(fileURI.getPath());
                TagManager.setItemsFolder(mContext,fileURI.getPath());
            }
        }
    }


}
