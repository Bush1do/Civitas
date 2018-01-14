package com.here.name.website.poom.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.poom.Models.User;
import com.here.name.website.poom.Profile.ProfileActivity;
import com.here.name.website.poom.R;
import com.here.name.website.poom.Utils.BottomNavigationHelper;
import com.here.name.website.poom.Utils.UserListAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Charles on 6/29/2017.
 */

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private Context mContext= SearchActivity.this;
    private static final int ACTIVITY_NUM=1;

    //Widgets
    private EditText mSearchParam;
    private ListView mListView;

    //Variables
    private List<User> mUserList;
    private UserListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchParam=(EditText) findViewById(R.id.search);
        mListView=(ListView) findViewById(R.id.listViewf);
        Log.d(TAG, "onCreate: Started.");

        closeKeyboard();
        setupBottomNavigation();
        initTextListener();
    }

    private void initTextListener(){
        Log.d(TAG, "initTextListener: Initializing");

        mUserList= new ArrayList<>();

        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text= mSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text);
            }
        });
    }

    private void searchForMatch(String keyword){
        Log.d(TAG, "searchForMatch: Searching for a match: "+keyword);
        mUserList.clear();
        //Update users list
        if(keyword.length()==0){

        } else{
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.field_username)).equalTo(keyword);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                Log.d(TAG, "onDataChange: Found user: " + singleSnapshot.getValue(User.class).toString());

                                mUserList.add(singleSnapshot.getValue(User.class));
                                //Update users list view
                                updateUsersList();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void updateUsersList(){
        Log.d(TAG, "updateUsersList: Updating users list");

        mAdapter=new UserListAdapter(SearchActivity.this,R.layout.layout_user_listitem,mUserList);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: Selected user: " + mUserList.get(position).toString());

                //To profile activity
                Intent intent= new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.search_activity));
                intent.putExtra(getString(R.string.intent_user), mUserList.get(position));
                startActivity(intent);
            }
        });
    }

    //Hide keyboard on activities
    private void  closeKeyboard(){
        if(getCurrentFocus()!=null){
            InputMethodManager imm= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    //Bottom Navigation Setup;
    //Copy and paste this on every activity to show the bottom navigation.
    private void setupBottomNavigation(){
        Log.d(TAG, "setupBottomNavigation: setting up BottomNavigation");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationHelper.setupBottomNavigation(bottomNavigationViewEx);
        BottomNavigationHelper.enableNavigation(mContext,this, bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
