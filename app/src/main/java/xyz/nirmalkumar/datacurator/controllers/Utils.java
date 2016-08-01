package xyz.nirmalkumar.datacurator.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by nirmal on 7/30/16.
 */
public class Utils {

    public static final String TAG = "nirmal";
    public static final String BASE_FOLDER_PATH = "Data_curator";
    private static final boolean OVERWRITE_FILES = true;

    public static void logd(String tag,String val){
        Log.d(tag,val);
    }

    public static void logd(String val){
        Log.d(TAG,val);
    }

    public static void loge(String val){
        Log.e(TAG,val);
    }


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static String getDeviceID(Context mContext) {
        String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(deviceId==null || deviceId.length()<5){
            String tmp = getUniquePsuedoID();
            deviceId = "null-"+tmp;
        }
        return deviceId;
    }

    public static String getUniquePsuedoID() {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their phone or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10)
                + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10)
                + (Build.DEVICE.length() % 10)
                + (Build.MANUFACTURER.length() % 10)
                + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their phone, there will be a
        // duplicate entry
        String serial = null;
        try {
            serial = Build.class.getField("SERIAL").get(null)
                    .toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
                    .toString();
        } catch (Exception e) {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to
        // create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
                .toString();
    }

    /**
     * Resize Image to required Specs without losing aspect ratio
     *
     * @param originalImage
     * @param width         - req Width
     * @param height        - req Height
     * @return resized bitmap
     */
    public static Bitmap resizeImageWithAspectRatio(Bitmap originalImage, int width, int height) {
        Bitmap background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        float originalWidth = originalImage.getWidth(), originalHeight = originalImage.getHeight();
        Canvas canvas = new Canvas(background);
        canvas.drawColor(Color.BLACK);
        float scale = width / originalWidth;
        float xTranslation = 0.0f, yTranslation = (height - originalHeight * scale) / 2.0f;
        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(originalImage, transformation, paint);
        logd( "Resized image from w=" + originalWidth + ", h=" + originalHeight +
                " to w=" + background.getWidth() + " ,h=" + background.getHeight() +
                " ,Scale=" + scale);
        return background;
    }

    /**
     * Resize Image to required Specs without losing aspect ratio
     *
     * @param originalImage
     * @param width         - req Width
     * @return resized bitmap
     */
    public static Bitmap resizeImageWithAspectRatio(Bitmap originalImage, int width) {
        int height;
        float originalWidth = originalImage.getWidth(), originalHeight = originalImage.getHeight();
        float scale = width / originalWidth;
        height = (int) (originalHeight * scale);
        float xTranslation = 0.0f, yTranslation = 0f;
        Bitmap background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(background);
        canvas.drawColor(Color.BLACK);
        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(originalImage, transformation, paint);
        logd( "Resized image from w=" + originalWidth + ", h=" + originalHeight +
                " to w=" + background.getWidth() + " ,h=" + background.getHeight() +
                " ,Scale=" + scale);
        return background;
    }

    public static String getPrettyTimeStamp() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yy_HH_mm_ss", Locale.ENGLISH);
        return sdf.format(now);
    }

    /**
     * Save image to internal storage and return the file name
     *
     * @param context  Application context
     * @param image    bitmap of the image to be saved
     * @param fileName with jpg extension
     * @return
     */
    public static String saveImageToInternalStorage(Context context, Bitmap image,
                                                    String fileName) {

        try {

            logd("Deleted file "+fileName+" "+ context.deleteFile(fileName));
            // Use the compress method on the Bitmap object to write image to
            // the OutputStream
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            // Writing the bitmap to the output stream
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            logd("saved To Internal Storage "+image.getWidth()+" , "+image.getHeight());
//			For Debug
//			saveBitmapToFile(image,fileName);
            return fileName;
        } catch (Exception e) {
            logd("saveToInternalStorage()", e.getMessage());
            return null;
        }
    }

    public static File loadImageFromInternalStorage(Context context, String filename) {
        try {
            File filePath = context.getFileStreamPath(filename);
            logd("loaded from Internal Storage" + filePath.getAbsolutePath());
            return filePath;
        } catch (Exception ex) {
            loge("getThumbnail() on internal storage"+ ex.getMessage());
        }
        return null;
    }

    public static String saveBitmapToFile(Bitmap bmp, String filename) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MSD");
        myDir.mkdirs();
        String fname = "img_" + getPrettyTimeStamp() + "_" + filename + ".jpeg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {

        }
        logd( "Saved new Image @ " + file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static File getNotDuplicateFile(String fname, File myDir,File f, String ext){
        if(f.exists()) {
            String name = fname.split(ext, 1) + "_new" + ext;
            File file = new File(myDir, fname);
            return getNotDuplicateFile(name,myDir,file,ext);
        }else
            return f;
    }

    public static String saveTextToFile(String txtData, String filename) {
        logd("On Save textfile");
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/"+BASE_FOLDER_PATH);
        String ext = ".txt";
        myDir.mkdirs();
        String fname = filename+"_" + getPrettyTimeStamp() + ext;
        File file = new File(myDir, fname);
        if (file.exists()){
            if(OVERWRITE_FILES)
                file.delete();
            else
                file = getNotDuplicateFile(fname,myDir,file,ext);
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(out);
            myOutWriter.append(txtData);
            myOutWriter.close();
            out.flush();
            out.close();

        } catch (Exception e) {

        }
        logd( "Saved file @ " + file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static String readTextFromFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        StringBuilder text;
        if (!file.exists())
            throw new FileNotFoundException();
        //Read text from file
        text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            loge("IO exception while reading data from file");
            e.printStackTrace();
        }
        logd( "Loaded file from " + file.getAbsolutePath());
        return text.toString();
    }

    public static boolean isConnectedToInternet(Context ctx) {
        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

}
