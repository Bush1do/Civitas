package com.here.name.website.Civitas.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.FirebaseMethods;
import com.here.name.website.Civitas.Utils.UniversalImageLoader;

/**
 * Created by Charles on 12/22/2017.
 */

public class NextActivity extends AppCompatActivity{

    /*private static final String TAG = "NextActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //Widgets
    private EditText mCaption;

    //Variables
    private static final String mAppend="file://";
    private int imageCount=0;
    private String imgUrl;
    private Intent intent;
    private Bitmap bitmap;
    private Uri imgUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFirebaseMethods=new FirebaseMethods(NextActivity.this);
        mCaption=(EditText) findViewById(R.id.caption);

        setupFirebaseAuth();

        ImageView backArrow= (ImageView) findViewById(R.id.imageViewBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Closing the activity");
                finish();
            }
        });

        TextView share=(TextView) findViewById(R.id.textViewShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating to the final share screen");
                //Upload to Firebase
                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String caption= mCaption.getText().toString();

                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl=intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption, imageCount,imgUrl,null);
                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap= (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption, imageCount,null,bitmap);

                }

            }
        });

        setImage();
    }

    //Gets url from the incoming intent and displays the chosen image
    private void setImage(){
        intent=getIntent();
        ImageView image= (ImageView) findViewById(R.id.imageShare);

        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl=intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: Got new image url: "+imgUrl);
            UniversalImageLoader.setImage(imgUrl,image,null,mAppend);
        }
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap= (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: Got new bitmap");
            image.setImageBitmap(bitmap);
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
    }*/
}
