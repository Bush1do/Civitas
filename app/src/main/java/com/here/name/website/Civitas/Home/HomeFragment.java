package com.here.name.website.Civitas.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.Civitas.Models.Comment;
import com.here.name.website.Civitas.Models.Photo;
import com.here.name.website.Civitas.R;
import com.here.name.website.Civitas.Utils.MainfeedListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Charles on 6/29/2017.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    //Variables
    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mFollowing;
    private ListView mListView;
    private MainfeedListAdapter mAdapter;
    private int mResults;
    private ArrayList<Photo> mPaginatedPhotos;
    private Button sendButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container,false);
        mListView= (ListView) view.findViewById(R.id.listView);
        mFollowing=new ArrayList<>();
        mPhotos=new ArrayList<>();
        sendButton=(Button) view.findViewById(R.id.sendTextPostButton);

        getFollowing();

        return view;
    }
    private void getFollowing(){
        Log.d(TAG, "getFollowing: Searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: Found user: "
                    +singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                }
                mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //Get photos
                getPhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: Getting photos");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        for(int i= 0;i<mFollowing.size();i++){
            final int count=i;
            Query query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Photo photo=new Photo();
                        Map<String,Object> objectMap=(HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());

                        ArrayList<Comment> comments=new ArrayList<>();
                        for(DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Comment comment= new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);
                        mPhotos.add(photo);
                    }
                    if(count>=mFollowing.size()-1){
                        //Display photos
                        displayPhotos();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void displayPhotos(){
        mPaginatedPhotos=new ArrayList<>();
        if(mPhotos!=null){
            try {
                    Collections.sort(mPhotos, new Comparator<Photo>() {
                        @Override
                        public int compare(Photo o1, Photo o2) {
                            return o2.getDate_created().compareTo(o1.getDate_created());
                        }
                    });
                    int iterations=mPhotos.size();

                    if(iterations>10){
                        iterations=10;
                    }

                    mResults=10;
                    for(int i=0;i<iterations;i++){
                        mPaginatedPhotos.add(mPhotos.get(i));
                    }

                    mAdapter=new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem,mPaginatedPhotos);
                    mListView.setAdapter(mAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: "+e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: "+e.getMessage() );
            }
        }
    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: Displaying more photos");

        try {

            if(mPhotos.size()>mResults && mPhotos.size() > 0){
                int iterations;
                if(mPhotos.size()>mResults+10){
                    Log.d(TAG, "displayMorePhotos: Greater than 10 more photos");
                    iterations=10;
                }else {
                    Log.d(TAG, "displayMorePhotos: Less than 10 more photos");
                    iterations=mPhotos.size()-mResults;
                }
                //Add new photos to paginated results
                for(int i=mResults;i<mResults+iterations;i++){
                    Toast.makeText(getContext(), "Loading posts...", Toast.LENGTH_SHORT).show();
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                mResults+=iterations;
                mAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: "+e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: "+e.getMessage() );
        }
    }
}
