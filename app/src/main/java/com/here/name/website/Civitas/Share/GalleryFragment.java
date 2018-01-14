package com.here.name.website.Civitas.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.here.name.website.Civitas.Profile.AccountSettingsActivity;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.FileSearch;
import com.here.name.website.Civitas.Utils.FilesPaths;
import com.here.name.website.Civitas.Utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Charles on 6/29/2017.
 */

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";

    //Constants
    private static final int NUM_GRID_COLUMNS=3;

    //Widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    //Variables
    private ArrayList<String> directories;
    private static final String mAppend="file://";
    private String mSelectedImage;

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

        Log.d(TAG, "onCreateView: Started");

        ImageView shareClose= (ImageView) view.findViewById(R.id.imageViewCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Closing the gallery fragment");
                getActivity().finish();
            }
        });

        TextView nextScreen=(TextView) view.findViewById(R.id.textViewNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating to the final share screen");

                if(isRootTask()){
                    Intent intent= new Intent(getActivity(),NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image),mSelectedImage);
                    startActivity(intent);
                } else{
                    Intent intent= new Intent(getActivity(),AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_image),mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
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
        FilesPaths filesPaths=new FilesPaths();

        //Check for other folders in "/storage/emulated/0/pictures"
        if(FileSearch.getDirectoryPaths(filesPaths.PICTURES) != null){
            directories=FileSearch.getDirectoryPaths(filesPaths.PICTURES);
        }

        ArrayList<String> directoryNames = new ArrayList<>();
        for(int i=0;i<directoryNames.size();i++){

            int index=directories.get(i).lastIndexOf("/");

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
        });
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


}
