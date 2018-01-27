package com.here.name.website.Civitas.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.here.name.website.Civitas.Models.Photo;
import com.here.name.website.Civitas.Models.User;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.ViewCommentsFragment;
import com.here.name.website.Civitas.Utils.ViewPostFragment;
import com.here.name.website.Civitas.Utils.ViewProfileFragment;

/**
 * Created by Charles on 6/29/2017.
 */

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener,
        ViewProfileFragment.OnGridImageSelectedListener{
    private static final String TAG = "ProfileActivity";

    @Override
    public void OnCommentThreadSelectedListener(Photo photo) {
        Log.d(TAG, "OnCommentThreadSelectedListener: Selected a comment thread");

        ViewCommentsFragment fragment= new ViewCommentsFragment();
        Bundle args=new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        fragment.setArguments(args);

        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    @Override
    public void OnGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "OnGridImageSelected: Selected an image from gridview: "+photo.toString());

        ViewPostFragment fragment= new ViewPostFragment();
        Bundle args= new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        args.putInt(getString(R.string.activity_number),activityNumber);

        fragment.setArguments(args);

        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }

    private Context mContext= ProfileActivity.this;
    private static final int ACTIVITY_NUM=3;
    private static final int NUM_GRID_COLUMNS=3;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: Started.");

        init();
    }

    private void init(){
        Log.d(TAG, "init: inflating: " + getString(R.string.profile_fragment));

        Intent intent= getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "init: Searching for user object attached as intent extra");
            if(intent.hasExtra(getString(R.string.intent_user))){
                User user=intent.getParcelableExtra(getString(R.string.intent_user));
                if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Log.d(TAG, "init: Inflating View Profile");
                    ViewProfileFragment fragment= new ViewProfileFragment();
                    Bundle args= new Bundle();
                    args.putParcelable(getString(R.string.intent_user),
                            intent.getParcelableExtra(getString(R.string.intent_user)));
                    fragment.setArguments(args);

                    FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(getString(R.string.view_post_fragment));
                    transaction.commit();
                } else{
                    Log.d(TAG, "init: Inflating Profile");
                    ProfileFragment fragment= new ProfileFragment();
                    FragmentTransaction transaction= ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(getString(R.string.profile_fragment));
                    transaction.commit();
                }

            } else{
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        } else{
            Log.d(TAG, "init: Inflating Profile");
            ProfileFragment fragment= new ProfileFragment();
            FragmentTransaction transaction= ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }

    }


}
