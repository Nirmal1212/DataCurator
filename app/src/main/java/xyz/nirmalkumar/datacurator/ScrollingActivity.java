package xyz.nirmalkumar.datacurator;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import xyz.nirmalkumar.datacurator.controllers.BaseVolleyRequest;
import xyz.nirmalkumar.datacurator.controllers.Utils;

public class ScrollingActivity extends AppCompatActivity {

    final Context mContext = this;
    String mimeType;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String boundary = "apiclient-" + System.currentTimeMillis();
    String twoHyphens = "--";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        File file = new File(Environment.getExternalStorageDirectory(),"/Data_curator/tagConfig1.json");
        int size = (int) file.length();
        final byte[] byteData = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(byteData, 0, byteData.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = "http://192.168.0.54:8000/images/test1/";

        mimeType = "multipart/form-data; boundary=" + boundary;

        BaseVolleyRequest baseVolleyRequest = new BaseVolleyRequest(1, url, null,null) {
            @Override
            public String getBodyContentType() {
                return mimeType;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Utils.logd("Req. getting body");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                dos = new DataOutputStream(bos);
                try {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
//                                + "tagged_file.json" + "\"" + lineEnd);
                            + "tagged_file_"+System.currentTimeMillis()+".json" + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(byteData);
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    return bos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return byteData;
            }
        };

        Volley.newRequestQueue(mContext).add(baseVolleyRequest);
        Utils.logd("Req. called in volley");
    }
}
