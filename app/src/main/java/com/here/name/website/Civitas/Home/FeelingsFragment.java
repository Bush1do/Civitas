package com.here.name.website.Civitas.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.here.name.website.Civitas.R;

import java.io.FileOutputStream;


/**
 * Created by Charles on 6/29/2017.
 */

public class FeelingsFragment extends Fragment {
    private static final String TAG = "FeelingsFragment";

    private static final int ACTIVITY_NUM=0;
    private static final int NUM_GRID_COLUMNS=3;

    //Variables
    Context mContext;
    View view;
    private ImageView mBlue, mRed,mYellow,mOrange,mPurp,mGreen;
    private static String feel="___";
    String s="w";
    String FILENAME="feeling_file";




    //Feelings
    int[]emotions={R.drawable.ic_blue_smile,
            R.drawable.ic_red_frown,
            R.drawable.ic_yellow_meh,
            R.drawable.ic_surprised,
            R.drawable.ic_sleepy};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_feelings, container, false);

        mContext=getActivity();
        mBlue=(ImageView) view.findViewById(R.id.bluesmile);
        mBlue.setClickable(true);
        mBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feel="happy!";
                Toast.makeText(mContext, "You feel happy!", Toast.LENGTH_SHORT).show();
            }
        });
        mRed=(ImageView) view.findViewById(R.id.redfrown);
        mRed.setClickable(true);
        mRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feel="angry";
                Toast.makeText(mContext, "You feel angry.", Toast.LENGTH_SHORT).show();
            }
        });
        mYellow=(ImageView) view.findViewById(R.id.yellowmeh);
        mYellow.setClickable(true);
        mYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feel="meh";
                Toast.makeText(mContext, "You feel meh.", Toast.LENGTH_SHORT).show();
            }
        });
        mOrange=(ImageView) view.findViewById(R.id.orangesurp);
        mOrange.setClickable(true);
        mOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feel="surprised";
                Toast.makeText(mContext, "You feel surprised!", Toast.LENGTH_SHORT).show();
            }
        });
        mPurp=(ImageView) view.findViewById(R.id.sleeppurp);
        mPurp.setClickable(true);
        mPurp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feel="sleepy";
                Toast.makeText(mContext, "You feel sleepy.", Toast.LENGTH_SHORT).show();
            }
        });
        mGreen=(ImageView) view.findViewById(R.id.greennerv);
        mGreen.setClickable(true);
        mGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feel="nervous";
                Toast.makeText(mContext, "You feel nervous.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public static String getFeeling() {
        return feel;
    }

}
