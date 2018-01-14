package com.here.name.website.Civitas.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.here.name.website.Civitas.Models.Photo;
import com.here.name.website.Civitas.Profile.ProfileFragment;
import com.here.name.website.Civitas.R;

/**
 * Created by Charles on 6/29/2017.
 */

public class FeelingsFragment extends Fragment {
    private static final String TAG = "FeelingsFragment";

    public interface OnGridImageSelectedListener{
        void  OnGridImageSelected(Photo photo, int activityNumber);
    }
    ProfileFragment.OnGridImageSelectedListener mOnGridImageSelectedListener;

    private static final int ACTIVITY_NUM=4;
    private static final int NUM_GRID_COLUMNS=3;

    private GridView gridView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_feelings, container,false);
        gridView= (GridView) view.findViewById(R.id.emotionGridView);

        return view;
    }

}
