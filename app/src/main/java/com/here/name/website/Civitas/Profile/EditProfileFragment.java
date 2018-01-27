package com.here.name.website.Civitas.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.Civitas.Dialogs.ConfirmPasswordDialog;
import com.here.name.website.Civitas.Models.User;
import com.here.name.website.Civitas.Models.UserAccountSettings;
import com.here.name.website.Civitas.Models.UserSettings;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Share.ShareActivity;
import com.here.name.website.Civitas.Utils.FirebaseMethods;
import com.here.name.website.Civitas.Utils.UniversalImageLoader;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by Charles on 6/30/2017.
 */

public class EditProfileFragment extends DialogFragment implements
        ConfirmPasswordDialog.OnConfirmPasswordListener{
    
    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: Got Password");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        /////////////////Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "User re-authenticated.");

                            /////////////////Check if email is not already in the database
                            mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if(task.isSuccessful()){
                                        try {
                                            if(task.getResult().getProviders().size()==1){
                                                Log.d(TAG, "onComplete: That email is already in use");
                                                Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();

                                            } else{
                                                Log.d(TAG, "onComplete: That email is available");

                                                /////////////////The email is available
                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User email address updated.");
                                                                    Toast.makeText(getActivity(), "Email updated", Toast.LENGTH_SHORT).show();
                                                                    mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                }
                                                            }
                                                        });
                                            }
                                        }catch (NullPointerException e){
                                            Log.e(TAG, "NullPointerException: "+e.getMessage() );
                                        }
                                    }
                                }
                            });
                        } else{
                            Log.d(TAG, "Re-authentication failed.");

                        }
                    }
                });
    }

    private static final String TAG = "EditProfileFragment";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //EditProfile Fragment Widgets
    private EditText mDisplayName, mUsername, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircularImageView mProfilePhoto;

    //Variables
    private UserSettings mUserSettings;
    private static final int RESULT_OK=3;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_editprofile, container,false);
        mProfilePhoto=(CircularImageView) view.findViewById(R.id.profile_photo);
        mDisplayName= (EditText) view.findViewById(R.id.display_name);
        mUsername=(EditText)view.findViewById(R.id.username);
        mDescription=(EditText)view.findViewById(R.id.description);
        mEmail=(EditText)view.findViewById(R.id.email);
        mPhoneNumber=(EditText)view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto= (TextView) view.findViewById(R.id.changeProfilePhoto);
        mFirebaseMethods= new FirebaseMethods(getActivity());

        //setProfileImage();
        setupFirebaseAuth();

        //Setup back arrow button to profile
        ImageView backArrow=(ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Navigating back to 'ProfileActivity'");
                getActivity().finish();
            }
        });

        ImageView checkMark= (ImageView) view.findViewById(R.id.saveChanges);
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attemping to save changes");
                saveProfileSettings();
                getActivity().finish();
            }
        });

        return view;
    }

    //Retrieves data contained in widgets and submits it to the database
    //Before submitting it checks to make sure the username is unique
    private void saveProfileSettings(){
        final String displayName= mDisplayName.getText().toString();
        final String username= mUsername.getText().toString();
        final String description= mDescription.getText().toString();
        final String email= mEmail.getText().toString();
        final long phoneNumber= Long.parseLong(mPhoneNumber.getText().toString());

        //case1: If user changed their username
        if (!mUserSettings.getUser().getUsername().equals(username)) {
            checkIfUsernameExists(username);
        }
        //case2: If the user changed their email
         if(!mUserSettings.getUser().getEmail().equals(email)) {
             //Step 1: Re-auth
             //     -Confirm password and email
             ConfirmPasswordDialog dialog= new ConfirmPasswordDialog();
             dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
             dialog.setTargetFragment(EditProfileFragment.this,1);
             //Step 2: Check if email already registered

             //Step 3: Change email
        }

        //Change other user settings
        if(!mUserSettings.getSettings().getDisplay_name().equals(displayName)){
            //update displayName
            mFirebaseMethods.updateUserAccountSettings(displayName,null,0);
        }
        if(!mUserSettings.getSettings().getDescription().equals(description)){
            //update description
            mFirebaseMethods.updateUserAccountSettings(null,description,0);
        }
        if(!mUserSettings.getSettings().getProfile_photo().equals(phoneNumber)){
            //update phone number
            mFirebaseMethods.updateUserAccountSettings(null,null,phoneNumber);
        }

    }

    /*
    * Check if @param username already exists in database
    * */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if "+username+" Already Exists");

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        Query query= reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    //add username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "Changed username", Toast.LENGTH_SHORT).show();
                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: "+singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "That username already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data, retrieving from firebase database."+ userSettings.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data, retrieving from firebase database."+ userSettings.getUser().getEmail());
        Log.d(TAG, "setProfileWidgets: setting widgets with data, retrieving from firebase database."+ userSettings.getUser().getPhone_number());

        mUserSettings=userSettings;
        //User user= userSettings.getUser();
        UserAccountSettings settings= userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto,null,"");

        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));
        
        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Changing profile photo");
                Intent intent= new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
                /*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start((Activity) getContext());*/
            }
        });

    }


    //For profile photo crop
    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                CropImage.activity(resultUri)
                        .setAspectRatio(1,1)
                        .start((Activity) getContext());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }*/

    //-------------------------Firebase------------------------
    //Setting up Firebase Authentication
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: Setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef= mFirebaseDatabase.getReference();
        userID= mAuth.getCurrentUser().getUid();

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
