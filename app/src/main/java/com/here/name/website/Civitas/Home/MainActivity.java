package com.here.name.website.Civitas.Home;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.here.name.website.Civitas.Chat.ChatFragment;
import com.here.name.website.Civitas.Chat.FriendsFragment;
import com.here.name.website.Civitas.Login.LoginActivity;
import com.here.name.website.Civitas.Models.Photo;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.BottomNavigationHelper;
import com.here.name.website.Civitas.Utils.MainfeedListAdapter;
import com.here.name.website.Civitas.Utils.SectionsPagerAdapter;
import com.here.name.website.Civitas.Utils.UniversalImageLoader;
import com.here.name.website.Civitas.Utils.ViewCommentsFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Charles on 6/29/2017.
 */

public class MainActivity extends AppCompatActivity implements MainfeedListAdapter.OnLoadMoreItemsListener {

    @Override
    public void onLoadMoreItems(){
        Log.d(TAG, "onLoadMoreItems: Displaying more photos");

        HomeFragment fragment=(HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:"+R.id.viewpagerContainer+":"+mViewPager.getCurrentItem());
        if(fragment !=null){
            fragment.displayMorePhotos();
        }
    }

    private static final String TAG="MainActivity";
    private Context mContext= MainActivity.this;
    private static final int ACTIVITY_NUM=0;
    private static final int HOME_FRAGMENT=1;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting.");
        mViewPager=(ViewPager) findViewById(R.id.viewpagerContainer);
        mFrameLayout=(FrameLayout) findViewById(R.id.container);
        mRelativeLayout=(RelativeLayout) findViewById(R.id.relLayoutParent);

        setupFirebaseAuth();
        initImageLoader();
        setupBottomNavigation();
        setupViewPager();

    }

    public void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: Selected a comment thread");

        ViewCommentsFragment fragment= new ViewCommentsFragment();
        Bundle args= new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        args.putString(getString(R.string.main_activity),getString(R.string.main_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: Hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    public void showLayout(){
        Log.d(TAG, "showLayout: Showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(mFrameLayout.getVisibility()==View.VISIBLE){
            showLayout();
        }
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader= new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    //Adds  3 top tabs
    private void setupViewPager(){
        final View background = findViewById(R.id.activityMain_Background);

        final SectionsPagerAdapter adapter= new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MessagesFragment()); //index 0
        adapter.addFragment(new HomeFragment()); //index 1
        adapter.addFragment(new FeelingsFragment()); //index 2
        adapter.addFragment(new FriendsFragment()); //index 3
        adapter.addFragment(new ChatFragment()); //index 4
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(1);

        final ArgbEvaluator argbEvaluator= new ArgbEvaluator();

        final int colorBlue = ContextCompat.getColor(this, R.color.blue);
        final int colorGreen = ContextCompat.getColor(this, R.color.green);
        final int colorYellow = ContextCompat.getColor(this, R.color.yellow);
        final int colorRed = ContextCompat.getColor(this, R.color.red);
        final int colorPurple = ContextCompat.getColor(this, R.color.purple);

        final Integer[] col={colorYellow,colorBlue,colorGreen,colorRed,colorPurple};

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffestPixels) {
                if (position < (adapter.getCount()-1) && position < (col.length-1)) {
                    background.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset,col[position],col[position+1]));
                } else {
                    background.setBackgroundColor(col[col.length-1]);
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        /*TabLayout tabLayout=(TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_action_name);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_messages);*/
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

    //-------------------------Firebase------------------------
    //Checks to see if user is logged in
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: Checking if user is logged in");
        if (user==null){
            Intent intent=new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    //Setting up Firebase Authentication
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: Setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //Check if user is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(mAuth.getCurrentUser());
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
