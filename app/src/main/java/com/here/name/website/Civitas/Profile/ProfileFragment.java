package com.here.name.website.Civitas.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.Civitas.Home.FeelingsFragment;
import com.here.name.website.Civitas.Models.Comment;
import com.here.name.website.Civitas.Models.Like;
import com.here.name.website.Civitas.Models.Photo;
import com.here.name.website.Civitas.Models.UserAccountSettings;
import com.here.name.website.Civitas.Models.UserSettings;
import com.here.name.website.Civitas.Utils.BottomNavigationHelper;
import com.here.name.website.Civitas.Utils.FirebaseMethods;
import com.here.name.website.Civitas.Utils.GridImageAdapter;
import com.here.name.website.Civitas.Utils.UniversalImageLoader;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.here.name.website.Civitas.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Charles on 7/6/2017.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    public interface OnGridImageSelectedListener{
        void  OnGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;

    private static final int ACTIVITY_NUM=3;
    private static final int NUM_GRID_COLUMNS=3;

    //Widgets
    private TextView mFeeling, mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mDescription;
    private ProgressBar mProgressbar;
    private CircularImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //Variables
    private int mFollowersCount=0;
    private int mFollowingCount=0;
    private int mPostsCount=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        mFeeling=(TextView) view.findViewById(R.id.feeling);
        mDisplayName= (TextView) view.findViewById(R.id.display_name);
        mUsername= (TextView) view.findViewById(R.id.username);
        mDescription= (TextView) view.findViewById(R.id.description);
        mProfilePhoto= (CircularImageView) view.findViewById(R.id.profile_photo);
        mPosts= (TextView) view.findViewById(R.id.tvPosts);
        mFollowers= (TextView) view.findViewById(R.id.tvFollowers);
        mFollowing= (TextView) view.findViewById(R.id.tvFollowing);
        mProgressbar= (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView= (GridView) view.findViewById(R.id.gridView);
        toolbar= (Toolbar) view.findViewById(R.id.profileToolbar);
        profileMenu= (ImageView) view.findViewById(R.id.profileMenu);
        bottomNavigationView= (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mContext= getActivity();
        mFirebaseMethods= new FirebaseMethods(getActivity());

        Log.d(TAG, "onCreateView: Started.");

        setupBottomNavigation();
        setupToolbar();
        setupFirebaseAuth();
        setupGridView();
        getFollowersCount();
        getFollowingCount();
        getPostsCount();

        TextView editProfile= (TextView) view.findViewById(R.id.textEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Navigating to "+ mContext.getString(R.string.edit_profile_fragment));
                Intent intent= new Intent(getActivity(), AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnGridImageSelectedListener= (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: "+ e.getMessage() );
        }

        super.onAttach(context);
    }

    private void setupGridView(){
        Log.d(TAG, "setupGridView: Setting up image grid");

        final ArrayList<Photo> photos= new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query= reference
                .child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Photo photo=new Photo();
                    Map<String,Object> objectMap=(HashMap<String, Object>) singleSnapshot.getValue();

                    try {
                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());

                        ArrayList<Comment> comments = new ArrayList<>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);

                        List<Like> likesList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                        }
                        photo.setLikes(likesList);
                        photos.add(photo);
                    }catch (NullPointerException e){
                        Log.e(TAG, "onDataChange: NullPointerException: "+e.getMessage());
                    }
                }

                //Setup image grid
                int gridWidth= getResources().getDisplayMetrics().widthPixels;
                int imageWidth= gridWidth/NUM_GRID_COLUMNS;
                gridView.setColumnWidth(imageWidth);

                ArrayList<String> imgUrls= new ArrayList<String>();
                for(int i=0;i<photos.size();i++){
                    imgUrls.add(photos.get(i).getImage_path());
                }
                GridImageAdapter adapter=new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,
                        "",imgUrls);
                gridView.setAdapter(adapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        mOnGridImageSelectedListener.OnGridImageSelected(photos.get(position),ACTIVITY_NUM);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Query cancelled");
            }
        });
    }

    private void getFollowersCount(){
        mFollowersCount= 0;

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_followers))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: Found follower: " + singleSnapshot.getValue());
                    mFollowersCount++;
                }
                mFollowers.setText(String.valueOf(mFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getFollowingCount(){
        mFollowingCount= 0;

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: Found following user: " + singleSnapshot.getValue());
                    mFollowingCount++;
                }
                mFollowing.setText(String.valueOf(mFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getPostsCount(){
        mPostsCount= 0;

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: Found post: " + singleSnapshot.getValue());
                    mPostsCount++;
                }
                mPosts.setText(String.valueOf(mPostsCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setProfileWidgets(UserSettings userSettings){
        //Log.d(TAG, "setProfileWidgets: setting widgets with data, retrieving from firebase database."+ userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data, retrieving from firebase database."+ userSettings.getSettings().getUsername());

        //User user= userSettings.getUser();
        UserAccountSettings settings= userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto,null,"");

        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
       // mFeeling.setText(settings.getFeeling());
        mDescription.setText(settings.getDescription());
        mFeeling.setText("I feel "+FeelingsFragment.getFeeling());
        mProgressbar.setVisibility(View.GONE);

    }

    //Toolbar
    private void setupToolbar(){
        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent=new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
    }

    //Bottom Navigation Setup;
    //Copy and paste this on every activity to show the bottom navigation.
    private void setupBottomNavigation(){
        Log.d(TAG, "setupBottomNavigation: setting up BottomNavigation");
        BottomNavigationHelper.setupBottomNavigation(bottomNavigationView);
        BottomNavigationHelper.enableNavigation(mContext,getActivity(), bottomNavigationView);
        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    //-------------------------Firebase------------------------
    //Setting up Firebase Authentication
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: Setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef= mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Retrieve user info from database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

                //Retrieve images for user


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Retrieve user info from database
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
