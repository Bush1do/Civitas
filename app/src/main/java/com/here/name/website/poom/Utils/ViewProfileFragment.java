package com.here.name.website.poom.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.poom.Models.Comment;
import com.here.name.website.poom.Models.Like;
import com.here.name.website.poom.Models.Photo;
import com.here.name.website.poom.Models.User;
import com.here.name.website.poom.Models.UserAccountSettings;
import com.here.name.website.poom.Models.UserSettings;
import com.here.name.website.poom.Profile.AccountSettingsActivity;
import com.here.name.website.poom.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Charles on 7/6/2017.
 */

public class ViewProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    public interface OnGridImageSelectedListener{
        void  OnGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;

    private static final int ACTIVITY_NUM=4;
    private static final int NUM_GRID_COLUMNS=3;


    //Widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription,
    mFollow,mUnfollow;
    private ProgressBar mProgressbar;
    private CircularImageView mProfilePhoto;
    private GridView gridView;
    private ImageView mBackArrow;
    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;
    private TextView editProfile;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private DatabaseReference mNotificationDatabase;

    //Variables
    private User mUser;
    private int mFollowersCount=0;
    private int mFollowingCount=0;
    private int mPostsCount=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_view_profile, container, false);
        mDisplayName= (TextView) view.findViewById(R.id.display_name);
        mUsername= (TextView) view.findViewById(R.id.username);
        mWebsite= (TextView) view.findViewById(R.id.website);
        mDescription= (TextView) view.findViewById(R.id.description);
        mProfilePhoto= (CircularImageView) view.findViewById(R.id.profile_photo);
        mPosts= (TextView) view.findViewById(R.id.tvPosts);
        mFollowers= (TextView) view.findViewById(R.id.tvFollowers);
        mFollowing= (TextView) view.findViewById(R.id.tvFollowing);
        mProgressbar= (ProgressBar) view.findViewById(R.id.viewProfileProgressBar);
        gridView= (GridView) view.findViewById(R.id.gridView);
        bottomNavigationView= (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mBackArrow= (ImageView) view.findViewById(R.id.backArrow);
        mContext= getActivity();
        mFollow= (TextView) view.findViewById(R.id.follow);
        mUnfollow= (TextView) view.findViewById(R.id.unfollow);
        editProfile= (TextView) view.findViewById(R.id.textEditProfile);
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("Notifications");

        Log.d(TAG, "onCreateView: Started.");

        try {
            mUser=getUserFromBundle();
            init();
        } catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException"+e.getMessage() );
            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            //go back to previous
            getActivity().getSupportFragmentManager().popBackStack();
        }

        setupBottomNavigation();
        setupFirebaseAuth();

        isFollowing();
        getFollowingCount();
        getFollowersCount();
        getPostsCount();

        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Now following: "+mUser.getUsername());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id())
                        .child(getString(R.string.field_user_id))
                        .setValue(mUser.getUser_id());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(mUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_user_id))
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                setFollowing();
                HashMap<String, String> notificationData=new HashMap<>();
                notificationData.put("from ",mUser.getUser_id());
                notificationData.put("type ","request");
                mNotificationDatabase.child(getString(R.string.field_user_id)).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });

        mUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Now unfollowing: "+mUser.getUsername());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id())
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(mUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();
                setUnfollowing();
            }
        });

        //setupGridView();

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

    private void init(){
        DatabaseReference reference1= FirebaseDatabase.getInstance().getReference();
        Query query1 = reference1.child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUser_id());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: Found user: " + singleSnapshot.getValue(UserAccountSettings.class).toString());

                    UserSettings settings=new UserSettings();
                    settings.setUser(mUser);
                    settings.setSettings(singleSnapshot.getValue(UserAccountSettings.class));
                    setProfileWidgets(settings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference();
        Query query2= reference2
                .child(getString(R.string.dbname_user_photos))
                .child(mUser.getUser_id());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Photo> photos=new ArrayList<Photo>();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Photo photo=new Photo();
                    Map<String,Object> objectMap=(HashMap<String, Object>) singleSnapshot.getValue();

                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());

                    ArrayList<Comment> comments=new ArrayList<>();
                    for(DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.field_comments)).getChildren()){
                        Comment comment= new Comment();
                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                        comments.add(comment);
                    }

                    photo.setComments(comments);

                    List<Like> likesList= new ArrayList<Like>();
                    for(DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.field_likes)).getChildren()){
                        Like like= new Like();
                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                        likesList.add(like);
                    }
                    photo.setLikes(likesList);
                    photos.add(photo);
                }
                setupImageGrid(photos);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Query cancelled");
            }
        });
    }

    private void isFollowing(){
        Log.d(TAG, "isFollowing: Checking if following this user");
        setUnfollowing();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: Found user: " + singleSnapshot.getValue());

                    setFollowing();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowersCount(){
        mFollowersCount= 0;

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_followers))
                .child(mUser.getUser_id());
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
                .child(mUser.getUser_id());
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
                .child(mUser.getUser_id());
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

    private void setFollowing(){
        Log.d(TAG, "setFollowing: Updating UI for following user");
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.VISIBLE);
        editProfile.setVisibility(View.GONE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: Updating UI for unfollowing user");
        mFollow.setVisibility(View.VISIBLE);
        mUnfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.GONE);
    }

    private void setCurrentUsersProfile(){
        Log.d(TAG, "setFollowing: Updating UI for showing this user their own profile");
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.VISIBLE);
    }

    private void setupImageGrid(final ArrayList<Photo> photos){
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

    private User getUserFromBundle(){
        Log.d(TAG, "getUserFromBundle: Arguments: "+getArguments());

        Bundle bundle= this.getArguments();
        if(bundle!=null){
            return bundle.getParcelable(getString(R.string.intent_user));
        } else{
            return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }

        super.onAttach(context);
    }

    private void setProfileWidgets(UserSettings userSettings){
        //Log.d(TAG, "setProfileWidgets: setting widgets with data, retrieving from firebase database."+ userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data, retrieving from firebase database."+ userSettings.getSettings().getUsername());

        //User user= userSettings.getUser();
        UserAccountSettings settings= userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto,null,"");

        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mProgressbar.setVisibility(View.GONE);

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
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
