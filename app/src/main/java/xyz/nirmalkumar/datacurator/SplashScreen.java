package xyz.nirmalkumar.datacurator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.nirmalkumar.datacurator.controllers.ItemsManager;
import xyz.nirmalkumar.datacurator.controllers.TagManager;
import xyz.nirmalkumar.datacurator.controllers.Utils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {


    @Bind(R.id.tag_url)
    EditText mTagURL;

    @OnClick(R.id.proceed) public void onProceed(){
        String url = mTagURL.getText().toString().trim();
        TagManager.setOnlineTagFile(SplashScreen.this,url);
        RequestQueue queue = Volley.newRequestQueue(this);
//        String url ="http://192.168.0.54/input.json";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Utils.logd("response = "+response);
                        Toast.makeText(SplashScreen.this,"Successfully pulled the config file!",Toast.LENGTH_LONG).show();
                        ItemsManager.setOnlineItemsList(SplashScreen.this,response);
                        if(mDialog!=null)
                            mDialog.dismiss();
                        startActivity(new Intent(SplashScreen.this,SimpleHomeActivity.class));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(mDialog!=null)
                    mDialog.dismiss();
                Utils.logd("Error received "+error.toString());
                Toast.makeText(SplashScreen.this,"Error connecting to the server!",Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        mDialog = ProgressDialog.show(SplashScreen.this,"Data Curator","Loading...");
    }

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
        TagManager.onLogin();
        ButterKnife.bind(this);
        String url = TagManager.getOnlineTagFile(this);

        if(url != null){
            mTagURL.setText(url);
        }
    }

}