package xyz.nirmalkumar.datacurator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import xyz.nirmalkumar.datacurator.fragments.HomeFragment;

public class SimpleHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_home);
        RelativeLayout mContainer = (RelativeLayout) findViewById(R.id.container);
        Fragment mFrag = new HomeFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.container,mFrag,"Home").commit();
    }
}
