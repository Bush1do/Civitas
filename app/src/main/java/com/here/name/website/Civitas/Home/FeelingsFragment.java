package com.here.name.website.Civitas.Home;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.here.name.website.Civitas.Models.Comment;
import com.here.name.website.Civitas.Models.GridItem;
import com.here.name.website.Civitas.Models.Like;
import com.here.name.website.Civitas.Models.Photo;
import com.here.name.website.Civitas.Profile.ProfileFragment;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.FirebaseMethods;
import com.here.name.website.Civitas.Utils.GridEmotionAdapter;
import com.here.name.website.Civitas.Utils.GridImageAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Charles on 6/29/2017.
 */

public class FeelingsFragment extends Fragment {
    private static final String TAG = "FeelingsFragment";

    public interface OnGridImageSelectedListener{
        void  OnGridImageSelected(Photo photo, int activityNumber);
    }
    ProfileFragment.OnGridImageSelectedListener mOnGridImageSelectedListener;

    private static final int ACTIVITY_NUM=0;
    private static final int NUM_GRID_COLUMNS=3;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private StorageReference mEmotionDatabase;

    //Variables
    private GridView gridView;
    Context mContext;
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_feelings, container,false);
        gridView= (GridView) view.findViewById(R.id.emotionGridView);
        mEmotionDatabase= FirebaseStorage.getInstance().getReference().child("photos").child("Emotions");
        //Can delete
        mContext= getActivity();
        //imageView= (ImageView) view.findViewById(R.id.shrt);
        //setupGridView();

        mEmotionDatabase.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                // Pass it to Picasso to download, show in ImageView and caching
                Picasso.with(mContext).load(uri.toString()).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });        return view;
    }

    private void setupGridView(){
        Log.d(TAG, "setupGridView: Setting up image grid");

        final ArrayList<Photo> photos= new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query= reference
                .child(getString(R.string.dbname_emotions));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Photo photo=new Photo();
                    Map<String,Object> objectMap=(HashMap<String, Object>) singleSnapshot.getValue();

                    try {
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


//    //-------------------------Firebase------------------------
//    //Setting up Firebase Authentication
//    private void setupFirebaseAuth(){
//        Log.d(TAG, "setupFirebaseAuth: Setting up firebase auth.");
//        mAuth = FirebaseAuth.getInstance();
//        mFirebaseDatabase= FirebaseDatabase.getInstance();
//        myRef= mFirebaseDatabase.getReference();
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                //Retrieve user info from database
//            }
//        });
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }

}
