package com.here.name.website.poom.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.poom.Models.Comment;
import com.here.name.website.poom.Models.Like;
import com.here.name.website.poom.Models.Photo;
import com.here.name.website.poom.Profile.ProfileFragment;
import com.here.name.website.poom.R;
import com.here.name.website.poom.Utils.GridImageAdapter;

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
