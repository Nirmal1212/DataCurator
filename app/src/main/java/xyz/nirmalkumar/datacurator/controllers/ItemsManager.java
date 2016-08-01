package xyz.nirmalkumar.datacurator.controllers;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nirmal on 8/1/16.
 */
public class ItemsManager {


    public static List<File> getItemsList(Context mContext){
        List<File> items = new ArrayList<>();
        String filePath = TagManager.getItemsFolder(mContext);

        File tagFile = new File(Environment.getExternalStorageDirectory(),"/Data_curator/tagConfig1.json");
        Utils.logd("Getting image items from "+tagFile.getParentFile().getAbsolutePath() + " FilePath ="+filePath);
        File folder = tagFile.getParentFile();
        if(folder.exists())
        {
            Utils.logd("Parent Folder exists");
            List<File> files = listf(folder);
            return files;
        }else{
            Utils.logd("Parent Folder does not exist");
        }
        return items;
    }

    public static List<File> listf(File directory) {
        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listf(new File(file.getAbsolutePath())));
            }
        }
        //System.out.println(fList);
        return resultList;
    }

}