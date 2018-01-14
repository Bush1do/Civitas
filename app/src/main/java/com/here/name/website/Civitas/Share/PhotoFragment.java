package com.here.name.website.Civitas.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.here.name.website.Civitas.Profile.AccountSettingsActivity;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.Permissions;

/**
 * Created by Charles on 6/29/2017.
 */

public class PhotoFragment extends Fragment {
    private static final String TAG = "PhotoFragment";

    //Constant
    private static final int galleryFragNum=0;
    private static final int photoFragNum=1;
    private static final int CAMERA_REQUEST_CODE=4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_photo, container,false);
        Log.d(TAG, "onCreateView: Started");

        Button btnLaunchCamera= (Button) view.findViewById(R.id.btn_launch_camera);

        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Launching camera");

                //ShareActivity.getCurrentTabNumber();
                if(((ShareActivity)getActivity()).getCurrentTabNumber()==photoFragNum){
                    if(((ShareActivity)getActivity()).CheckPermissions(Permissions.CAMERA_PERMISSION[0])){
                        Log.d(TAG, "onClick: Starting Camera");
                        Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
                    } else{
                        Intent intent=new Intent(getActivity(),ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
        
        return view;
    }

    private boolean isRootTask(){
        if(((ShareActivity)getActivity()).getTask()==0){
            return true;
        } else{
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: Done taking photo");
            Log.d(TAG, "onActivityResult: Navigating back to final share screen");

            //Move to final share screen to publish photo
            Bitmap bitmap;
            bitmap= (Bitmap) data.getExtras().get("data");
            if(isRootTask()){
                try {
                    Log.d(TAG, "onActivityResult: Received new bitmap from camera: "+bitmap);
                    Intent intent= new Intent(getActivity(),NextActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap),bitmap);
                    startActivity(intent);
                } catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException: "+e.getMessage());
                }
            } else{
                try {
                    Log.d(TAG, "onActivityResult: Received new bitmap from camera: "+bitmap);
                    Intent intent= new Intent(getActivity(),AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap),bitmap);
                    intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                } catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException: "+e.getMessage());
                }
            }
        }
    }
}
