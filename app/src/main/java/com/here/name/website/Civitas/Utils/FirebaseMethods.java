package com.here.name.website.Civitas.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.here.name.website.Civitas.Home.MainActivity;
import com.here.name.website.Civitas.Models.Photo;
import com.here.name.website.Civitas.Models.UserSettings;
import com.here.name.website.Civitas.Profile.AccountSettingsActivity;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Models.User;
import com.here.name.website.Civitas.Models.UserAccountSettings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.nostra13.universalimageloader.utils.L.e;

/**
 * Created by User on 6/26/2017.
 */
public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    //Variables
    private Context mContext;
    private double mPhotoUploadProgress=0;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference= FirebaseStorage.getInstance().getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void uploadNewPhoto(String photoType, final String caption, int count, final String imgUrl, Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: Attempting to upload new photo");

        FilesPaths filesPaths= new FilesPaths();
        //case1 New photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: Uploading new photo");

            String user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference= mStorageReference
                    .child(filesPaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo"+(count++) );

            //Convert image url to bitmap
            if(bm==null){
                bm=ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes= ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask= null;
            uploadTask=storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl= taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "Uploaded Successfully.", Toast.LENGTH_SHORT).show();

                    //add photo to photo node and user_photos node
                    addPhotoToDatabase(caption, firebaseUrl.toString());
                    //Nav to main feed so user can see photo
                    Intent intent=new Intent (mContext, MainActivity.class);
                    mContext.startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed.", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();

                    if(progress -25 >mPhotoUploadProgress){
                        Toast.makeText(mContext, "Photo upload progress "+String.format("%.0f", progress)+"%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress= progress;
                    }
                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }
        //case2 New Profile photo
        else if(photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: Uploading profile photo");

                String user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference storageReference= mStorageReference
                        .child(filesPaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

                //Convert image url to bitmap
            if(bm==null){
                bm=ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes= ImageManager.getBytesFromBitmap(bm, 100);

                UploadTask uploadTask= null;
                uploadTask=storageReference.putBytes(bytes);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri firebaseUrl= taskSnapshot.getDownloadUrl();
                        Toast.makeText(mContext, "Uploaded Successfully.", Toast.LENGTH_SHORT).show();

                        //Insert profile photo to user_account_settings node
                        setProfilePhoto(firebaseUrl.toString());

                        ((AccountSettingsActivity)mContext).setViewPager(
                                ((AccountSettingsActivity)mContext).pagerAdapter
                                        .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                        );
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Photo upload failed.");
                        Toast.makeText(mContext, "Photo upload failed.", Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();

                        if(progress -25 >mPhotoUploadProgress){
                            Toast.makeText(mContext, "Photo upload progress "+String.format("%.0f", progress)+"%", Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress= progress;
                        }
                        Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                    }
                });
        }
    }

    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: Setting new profile image: "+url);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    private String getTimeStamp(){
        SimpleDateFormat sdf=new SimpleDateFormat("MM-dd-yyyy'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String url){
        Log.d(TAG, "addPhotoToDatabase: Adding photo to database");

        String tags= StringManipulation.getTags(caption);
        String newPhotoKey=myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo=new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //Insert to database
        myRef.child(mContext.getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);
    }

    public int getImageCount(DataSnapshot dataSnapshot){
        int c=0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
                c++;
        }
        return c;
    }

    //Update user_account_settings in Firebase hierarchy
    public void updateUserAccountSettings(String displayName, String description,long phoneNumber){
        Log.d(TAG, "updateUserAccountSettings: Updating user account settings");

        if(displayName!=null){
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_displayName))
                    .setValue(displayName);
        }

        if(description!=null){
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }
        if(phoneNumber!=0){
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phoneNumber))
                    .setValue(phoneNumber);
        }

    }

    //Update username in Firebase hierarchy
    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: Updating username to "+username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    //Update email in Firebase hierarchy
    public void updateEmail(String email){
        Log.d(TAG, "updateEmail: Updating email to "+email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
    }

//    public boolean checkIfUsernameExists(String username, DataSnapshot datasnapshot){
//        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");
//
//        User user = new User();
//
//        for (DataSnapshot ds: datasnapshot.child(userID).getChildren()){
//            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);
//
//            user.setUsername(ds.getValue(User.class).getUsername());
//            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());
//
//            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
//                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(task.isSuccessful()){
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }



    public void addNewUser(String email, String username, String description, String profile_photo){

        User user = new User( userID,  1,  email,  StringManipulation.condenseUsername(username) );

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);


        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                StringManipulation.condenseUsername(username),
                userID
        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);

    }

    //Retrieves account settings for user
    //"user_account_settings" node in Firebase
    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserAccountSettings: Retrieving user account settings.");

        UserAccountSettings settings= new UserAccountSettings();
        User user= new User();
        for (DataSnapshot ds: dataSnapshot.getChildren()){
            //user_account_settings node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))){
                Log.d(TAG, "getUserAccountSettings: datasnapshot: "+ ds);

                try{

                settings.setDisplay_name(
                        ds.child(userID)
                        .getValue(UserAccountSettings.class)
                        .getDisplay_name()
                );
                settings.setUsername(
                ds.child(userID)
                        .getValue(UserAccountSettings.class)
                        .getUsername()
                );

                settings.setDescription(
                        ds.child(userID)
                                .getValue(UserAccountSettings.class)
                                .getDescription()
                );
                settings.setProfile_photo(
                        ds.child(userID)
                                .getValue(UserAccountSettings.class)
                                .getProfile_photo()
                );
                settings.setPosts(
                        ds.child(userID)
                                .getValue(UserAccountSettings.class)
                                .getPosts()
                );
                settings.setFollowing(
                        ds.child(userID)
                                .getValue(UserAccountSettings.class)
                                .getFollowing()
                );
                settings.setFollowers(
                        ds.child(userID)
                                .getValue(UserAccountSettings.class)
                                .getFollowers()
                );
                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings info: "+ settings.toString());
                } catch (NullPointerException e){
                    Log.e(TAG, "getUserAccountSettings: NullPointerException"+ e.getMessage());
                }
            }
            //Users Node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds);

                user.setUsername(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUsername()
                );
                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setPhone_number(
                        ds.child(userID)
                                .getValue(User.class)
                                .getPhone_number()
                );
                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()
                );
                Log.d(TAG, "getUserAccountSettings: retrieved user information: " + user.toString());
            }
        }
            return new UserSettings(user,settings);
    }
}