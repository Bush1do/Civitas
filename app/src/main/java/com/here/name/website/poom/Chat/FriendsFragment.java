package com.here.name.website.poom.Chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.here.name.website.poom.R;

/**
 * Created by Charles on 6/29/2017.
 */

public class FriendsFragment extends Fragment {
    private static final String TAG = "FeelingsFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_friends, container,false);

        return view;
    }
}
