package com.here.name.website.Civitas.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.here.name.website.Civitas.Home.MainActivity;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Share.GotoActivity;

/**
 * Created by Charles on 6/29/2017.
 */

public class FriendsFragment extends Fragment {
    private static final String TAG = "FeelingsFragment";

    private Button mbutt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_friends, container,false);
        mbutt=(Button) view.findViewById(R.id.butbutt);

        mbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((MainActivity)getActivity()).getCurrentTabNumber()==3){

                    Intent intent= new Intent(getContext(), GotoActivity.class);
                startActivity(intent);
            }
            }
        });
        return view;
    }
}
