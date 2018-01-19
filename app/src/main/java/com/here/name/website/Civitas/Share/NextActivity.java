package com.here.name.website.Civitas.Share;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.Civitas.Home.MainActivity;
import com.here.name.website.Civitas.Profile.AccountSettingsActivity;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.FirebaseMethods;
import com.here.name.website.Civitas.Utils.UniversalImageLoader;

/**
 * Created by Charles on 12/22/2017.
 */

public class NextActivity extends AppCompatActivity{

    private static final String TAG = "NextActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //Widgets
    private EditText mCaption;
    private ProgressBar mProg;
    private ImageView mImage;

    //Constants
    private static final int GALLERY_REQUEST=1;
    public static final int ACTIVITY_NUM=2;
    private static final int VERIFY_PERMISSIONS_REQUEST=1;

    //Variables
    private static final String mAppend="file://";
    private int imageCount=0;
    private String imgUrl;
    private Intent intent;
    private Bitmap bitmap;
    private Uri imgUri;
    private Context mCont=NextActivity.this;
    private String mSelectedImage;

    //MERGING GALLERYFRAG AND NEXTACTIVITY



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mProg= (ProgressBar) findViewById(R.id.progressBarNext);
        mProg.setVisibility(View.VISIBLE);
        mFirebaseMethods=new FirebaseMethods(NextActivity.this);
        mCaption=(EditText) findViewById(R.id.caption);
        mImage= (ImageView) findViewById(R.id.galleryImageView);

        setupFirebaseAuth();

        ImageView backArrow= (ImageView) findViewById(R.id.imageViewBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Closing the activity");
                finish();
            }
        });

        TextView share=(TextView) findViewById(R.id.textViewNextShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating to the final share screen");
                //Upload to Firebase
                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String caption= mCaption.getText().toString();


                //OTHER CODE
//                if(PhotoFragment.isRootTask()){
//                    try {
//
//                        Log.d(TAG, "onActivityResult: Received new bitmap from camera: "+bitmap);
//                        Intent intent= new Intent(mCont, MainActivity.class);
//                        intent.putExtra(getString(R.string.selected_bitmap),bitmap);
//                        startActivity(intent);
//                    } catch (NullPointerException e){
//                        Log.d(TAG, "onActivityResult: NullPointerException: "+e.getMessage());
//                    }
//                } else{
//                    try {
//
//                        Log.d(TAG, "onActivityResult: Received new bitmap from camera: "+bitmap);
//                        Intent intent= new Intent(mCont,AccountSettingsActivity.class);
//                        intent.putExtra(getString(R.string.selected_bitmap),bitmap);
//                        intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.edit_profile_fragment));
//                        startActivity(intent);
//                        //getActivity().finish();
//                    } catch (NullPointerException e){
//                        Log.d(TAG, "onActivityResult: NullPointerException: "+e.getMessage());
//                    }
//                }
//
//
//                if(intent.hasExtra(getString(R.string.selected_image))){
//                    imgUrl=intent.getStringExtra(getString(R.string.selected_image));
//                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption, imageCount,imgUrl,null);
//                }
//                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
//                    bitmap= (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
//                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption, imageCount,null,bitmap);
//                }


                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl,null);
                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null,bitmap);
                }

                if(PhotoFragment.isRootTask()){
                    Intent intent = new Intent(NextActivity.this, MainActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(NextActivity.this, AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    NextActivity.this.finish();
                }
            }

        });
        init();
        setImage();
    }

    private void init() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    //Gets url from the incoming intent and displays the chosen image
    private void setImage(){
        intent=getIntent();

        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl=intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: Got new image url: "+imgUrl);
            UniversalImageLoader.setImage(imgUrl,mImage,null,mAppend);
        }
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap= (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: Got new bitmap");
            mImage.setImageBitmap(bitmap);
        }
        mProg.setVisibility(View.GONE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST&& resultCode==RESULT_OK) {
            Uri imageUri = data.getData();
            mImage.setImageURI(imageUri);

        }
    }

    public int getTask(){
        return getIntent().getFlags();
    }

    //Verify all permissions passed in
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: Verifying permissions");
        ActivityCompat.requestPermissions(
                NextActivity.this,
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

        int permissionRequest= ActivityCompat.checkSelfPermission(NextActivity.this,permission);

        if(permissionRequest!= PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "CheckPermissions: \n Permission denied for: "+permission);
            return false;
        }else{
            Log.d(TAG, "CheckPermissions: \n Permission granted for: "+permission);
            return true;
        }
    }



    //-------------------------Firebase------------------------
    //Setting up Firebase Authentication
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: Setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef= mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: Image count: "+imageCount);


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
                imageCount= mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: Image count: "+imageCount);
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
