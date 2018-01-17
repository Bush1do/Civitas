package com.here.name.website.Civitas.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.Civitas.Profile.AccountSettingsActivity;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.FileSearch;
import com.here.name.website.Civitas.Utils.FilesPaths;
import com.here.name.website.Civitas.Utils.FirebaseMethods;
import com.here.name.website.Civitas.Utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Charles on 6/29/2017.
 */

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //Constants
    private static final int NUM_GRID_COLUMNS=3;
    private static final int GALLERY_REQUEST=1;

    //Widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;
    private EditText mCaption;

    //Variables
    private ArrayList<String> directories;
    private static final String mAppend="file://";
    private String mSelectedImage;
    public Uri imageUri=null;

    //Variables
    //private int imageCount=0;
    private String imgUrl;
    private Intent intent;
    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_gallery, container,false);
        galleryImage=(ImageView) view.findViewById(R.id.galleryImageView);
        gridView=(GridView) view.findViewById(R.id.gridView);
        directorySpinner=(Spinner) view.findViewById(R.id.spinnerDirectory);
        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        directories=new ArrayList<>();

        mCaption=(EditText) view.findViewById(R.id.caption);

        setupFirebaseAuth();

        Log.d(TAG, "onCreateView: Started");

        ImageView shareClose= (ImageView) view.findViewById(R.id.imageViewBackArrow);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Closing the gallery fragment");
                getActivity().finish();
            }
        });

        TextView share=(TextView) view.findViewById(R.id.textViewShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating to the final share screen");
                //Upload to Firebase
                Toast.makeText(getContext(), "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String caption= mCaption.getText().toString();
                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl=intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption,imgUrl,null);
                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap= (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption,null,bitmap);
                }

            }
        });

        init();
        return view;
    }

    private boolean isRootTask(){
        if(((ShareActivity)getActivity()).getTask()==0){
            return true;
        } else{
            return false;
        }
    }

    private void init(){

        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);

        /*FilesPaths filesPaths=new FilesPaths();

        //Check for other folders in "/storage/emulated/0/pictures"
        if(FileSearch.getDirectoryPaths(filesPaths.PICTURES) != null){
            directories=FileSearch.getDirectoryPaths(filesPaths.PICTURES);
        }

        ArrayList<String> directoryNames = new ArrayList<>();
        for(int i=0;i<directoryNames.size();i++){

            int index=directories.get(i).lastIndexOf("image/");

            String string=directories.get(i).substring(index);
            directoryNames.add(string);
        }

        directories.add(filesPaths.CAMERA);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,directories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: Selected "+directories.get(position));
                    //Grid for directory
                    SetupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST&& resultCode==RESULT_OK){
            imageUri=data.getData();

            galleryImage.setImageURI(imageUri);
        }
    }

    private void SetupGridView(String selectedDirectory){
        Log.d(TAG, "SetupGridView: Directory chosen: "+selectedDirectory);
        final ArrayList<String> imageURLS= FileSearch.getFilePath(selectedDirectory);

        //Set grid column width
        int gridWidth=getResources().getDisplayMetrics().widthPixels;
        int imageWidth=gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //Use grid adapter to adapt images to gridView file://
        GridImageAdapter adapter=new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,mAppend,imageURLS);
        gridView.setAdapter(adapter);

        //Set first image to display
        try {
            setImage(imageURLS.get(0),galleryImage,mAppend);
            mSelectedImage=imageURLS.get(0);
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "SetupGridView: ArrayIndexOutOfBoundsException: "+ e.getMessage() );
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "onItemClick: Selected an image: "+imageURLS.get(position));

                setImage(imageURLS.get(position),galleryImage,mAppend);
                mSelectedImage=imageURLS.get(position);

            }
        });
    }

    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "Image: Setting Image");

        ImageLoader imageLoader= ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    //-------------------------Firebase------------------------
    //Setting up Firebase Authentication
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: Setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef= mFirebaseDatabase.getReference();
        //Log.d(TAG, "onDataChange: Image count: "+imageCount);


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
                //imageCount= mFirebaseMethods.getImageCount(dataSnapshot);
                //Log.d(TAG, "onDataChange: Image count: "+imageCount);
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
