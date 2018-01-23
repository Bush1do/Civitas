package com.here.name.website.Civitas.Share;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.here.name.website.Civitas.Home.MainActivity;
import com.here.name.website.Civitas.Login.LoginActivity;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.Permissions;
import com.here.name.website.Civitas.Utils.SectionsPagerAdapter;

/**
 * Created by Charles on 6/29/2017.
 */

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";
    private Context mContext= ShareActivity.this;

    //Constants
    public static final int ACTIVITY_NUM=2;
    private static final int VERIFY_PERMISSIONS_REQUEST=1;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: Started.");

        if(CheckPermissionsArray(Permissions.PERMISSIONS)){
            setupViewPager();
        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }

    }

    //Return current tab number
    //0= GalleryFragment
    //1= PhotoFragment
//    public int getCurrentTabNumber(){
//        return mViewPager.getCurrentItem();
//    }

    private void setupViewPager(){
        SectionsPagerAdapter adapter=new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        //adapter.addFragment(new PhotoFragment());

        mViewPager=(ViewPager) findViewById(R.id.viewpagerContainer);
        mViewPager.setAdapter(adapter);
        /*TabLayout tabLayout=(TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));*/

    }

    public int getTask(){
        return getIntent().getFlags();
    }

    //Verify all permissions passed in
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: Verifying permissions");
        ActivityCompat.requestPermissions(
                ShareActivity.this,
                        permissions,
                        VERIFY_PERMISSIONS_REQUEST
                        );
    }

    //Check an array of permissions
    public boolean CheckPermissionsArray(String[] permissions){
        Log.d(TAG, "CheckPermissionsArray: Checking permissions array");
        for(int i=0;i<permissions.length;i++){
            String check=permissions[i];
            if(!CheckPermissions(check)){
                return false;
            }
        }
        return true;
    }

    //Check if a single permission is verified
    public boolean CheckPermissions(String permission) {
        Log.d(TAG, "CheckPermissions: Checking permission");

        int permissionRequest= ActivityCompat.checkSelfPermission(ShareActivity.this,permission);

        if(permissionRequest!= PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "CheckPermissions: \n Permission denied for: "+permission);
            return false;
        }else{
            Log.d(TAG, "CheckPermissions: \n Permission granted for: "+permission);
            return true;
        }
    }

}
