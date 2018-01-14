package com.here.name.website.poom.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.here.name.website.poom.Requests.RequestsActivity;
import com.here.name.website.poom.Home.MainActivity;
import com.here.name.website.poom.Profile.ProfileActivity;
import com.here.name.website.poom.R;
import com.here.name.website.poom.Search.SearchActivity;
import com.here.name.website.poom.Share.ShareActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by Charles on 6/29/2017.
 */

public class BottomNavigationHelper {
    private static final String TAG = "BottomNavigationHelper";

    public static void setupBottomNavigation(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigation: Setting up BottomNavigationViewEx");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }
    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.icon_home:
                        Intent intent1= new Intent(context, MainActivity.class);//ACTIVITY_NUM=0
                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                    case R.id.icon_search:
                        Intent intent2= new Intent(context, SearchActivity.class);//ACTIVITY_NUM=1
                        context.startActivity(intent2);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                    case R.id.icon_circle:
                        Intent intent3= new Intent(context, ShareActivity.class);//ACTIVITY_NUM=2
                        context.startActivity(intent3);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                    case R.id.icon_alert:
                        Intent intent4= new Intent(context, RequestsActivity.class);//ACTIVITY_NUM=3
                        context.startActivity(intent4);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                    case R.id.icon_android:
                        Intent intent5= new Intent(context, ProfileActivity.class);//ACTIVITY_NUM=4
                        context.startActivity(intent5);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                }
                return false;
            }
        });
    }
}
