package com.here.name.website.Civitas.Requests;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.BottomNavigationHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by Charles on 6/29/2017.
 */

public class RequestsActivity extends AppCompatActivity {
    private static final String TAG = "RequestsActivity";
    private Context mContext= RequestsActivity.this;
    private static final int ACTIVITY_NUM=3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        Log.d(TAG, "onCreate: Started.");

        setupBottomNavigation();
    }

    //Bottom Navigation Setup;
    //Copy and paste this on every activity to show the bottom navigation.
    private void setupBottomNavigation(){
        Log.d(TAG, "setupBottomNavigation: setting up BottomNavigation");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationHelper.setupBottomNavigation(bottomNavigationViewEx);
        BottomNavigationHelper.enableNavigation(mContext,this, bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
