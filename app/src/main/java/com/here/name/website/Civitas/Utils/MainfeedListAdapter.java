package com.here.name.website.Civitas.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.Civitas.Home.MainActivity;
import com.here.name.website.Civitas.Models.Comment;
import com.here.name.website.Civitas.Models.Like;
import com.here.name.website.Civitas.Models.Photo;
import com.here.name.website.Civitas.Models.User;
import com.here.name.website.Civitas.Models.UserAccountSettings;
import com.here.name.website.Civitas.Profile.ProfileActivity;
import com.here.name.website.Civitas.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Charles on 1/1/2018.
 */

public class MainfeedListAdapter extends ArrayAdapter<Photo> {

    public interface OnLoadMoreItemsListener{
        void onLoadMoreItems();
    }

    OnLoadMoreItemsListener mOnLoadMoreItemsListener;


    private static final String TAG = "MainfeedListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername="";

    public MainfeedListAdapter(@NonNull Context context, int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource=resource;
        this.mContext=context;
        mReference=FirebaseDatabase.getInstance().getReference();
    }

    static class ViewHolder{
        CircularImageView mProfileImage;
        String likesString;
        TextView username,timeDelta,caption,likes,comments;
        SquareImageView image;
        ImageView heartFill, heartOutline,commentBubble;
        UserAccountSettings settings=new UserAccountSettings();
        User user=new User();
        StringBuilder users;
        String mLikesString;
        boolean likedByCurrentUser;
        Heart heart;
        GestureDetector detector;
        Photo photo;

        private int mActivityNumber = 0;
        private String photoUsername = "";
        private String profilePhotoUrl = "";
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if(convertView==null){
            convertView=mInflater.inflate(mLayoutResource,parent,false);
            holder=new ViewHolder();

            holder.image = (SquareImageView) convertView.findViewById(R.id.post_image);
            holder.photo=getItem(position);
            holder.caption = (TextView) convertView.findViewById(R.id.imageCaption);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.timeDelta = (TextView) convertView.findViewById(R.id.image_time_posted);
            holder.heartFill = (ImageView) convertView.findViewById(R.id.image_heart_fill);
            holder.heartOutline = (ImageView) convertView.findViewById(R.id.image_heart_outline);
            holder.mProfileImage = (CircularImageView) convertView.findViewById(R.id.profile_photo);
            holder.likes = (TextView) convertView.findViewById(R.id.image_likes);
            holder.commentBubble= (ImageView) convertView.findViewById(R.id.speech_bubble);
            holder.comments= (TextView) convertView.findViewById(R.id.image_comments_link);
            holder.heart= new Heart(holder.heartOutline,holder.heartFill);
            holder.detector=new GestureDetector(mContext, new GestureListener(holder));
            holder.users=new StringBuilder();

            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }

        //Get current username before checking likes string
        getCurrentUsername();
        getLikesString(holder);

        //Set caption
        holder.caption.setText(getItem(position).getCaption());

        //Get likes string
         List<Comment> comments= getItem(position).getComments();
        if(comments.size()>0) {
            holder.comments.setText("View all " + comments.size() + " comments");
        } else {
            holder.comments.setText("No comments");
        }

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Comment> comments= getItem(position).getComments();
                if(comments.size()>0) {
                    Log.d(TAG, "onClick: Loading comment thread for " + getItem(position).getPhoto_id());
                    ((MainActivity) mContext).onCommentThreadSelected(getItem(position),
                            mContext.getString(R.string.main_activity));

                    ((MainActivity) mContext).hideLayout();
                }
            }
        });

        holder.commentBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Log.d(TAG, "onClick: Loading comment thread for " + getItem(position).getPhoto_id());
                ((MainActivity) mContext).onCommentThreadSelected(getItem(position),
                        mContext.getString(R.string.main_activity));

                ((MainActivity) mContext).hideLayout();
            }
        });

        //Set comment
        String timeStampDifference=getTimeStampDifference(getItem(position));
        if(!timeStampDifference.equals("0")){
            holder.timeDelta.setText(timeStampDifference+ " "+mContext.getString(R.string.days_ago));
        } else {
            holder.timeDelta.setText("TODAY");
        }

        //Set profile image
        final ImageLoader imageLoader= ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImage_path(),holder.image);

        //Get profile image and username
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    //currentUsername= singleSnapshot.getValue(UserAccountSettings.class).getUsername();

                    Log.d(TAG, "onDataChange: Found user: "
                            +singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                    holder.username.setText(singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "onClick: Navigating to profile of: "
                                    +holder.user.getUsername());
                            Intent intent= new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity),
                                    mContext.getString(R.string.main_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user),holder.user);
                            mContext.startActivity(intent);
                        }
                    });

                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
                            holder.mProfileImage);
                    holder.mProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "onClick: Navigating to profile of: "
                                    +holder.user.getUsername());
                            Intent intent= new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity),
                                    mContext.getString(R.string.main_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user),holder.user);
                            mContext.startActivity(intent);
                        }
                    });

                    holder.settings= singleSnapshot.getValue(UserAccountSettings.class);
                    holder.comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "onClick: Loading comment thread for "+getItem(position).getPhoto_id());
                            ((MainActivity)mContext).onCommentThreadSelected(getItem(position),
                                    mContext.getString(R.string.main_activity));

                            ((MainActivity)mContext).hideLayout();

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Get user object
        Query userQuery = mReference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: Found user: "
                    +singleSnapshot.getValue(User.class).getUsername());

                    holder.user= singleSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(reachedEndofList(position)){
            loadMoreData();
        }

        return convertView;
    }

    private boolean reachedEndofList(int position){
        return position==getCount()-1;
    }

    private void loadMoreData(){
        try {
            mOnLoadMoreItemsListener=(OnLoadMoreItemsListener) getContext();
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: "+e.getMessage() );
        }

        try {
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: NullPointerException: "+e.getMessage() );
        }
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        ViewHolder mHolder;

        public GestureListener(ViewHolder holder) {
            mHolder=holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: Single tap detected");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(mContext.getString(R.string.dbname_photos))
                    .child(mHolder.photo.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        //case1 User liked photo
                        String keyID = singleSnapshot.getKey();
                        if (mHolder.likedByCurrentUser &&
                                singleSnapshot.getValue(Like.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                            mReference.child(mContext.getString(R.string.dbname_photos))
                                    .child(mHolder.photo.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mReference.child(mContext.getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mHolder.photo.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mHolder.heart.toggleLike();
                            getLikesString(mHolder);
                        }

                        //case1 Not liked
                        else if (!mHolder.likedByCurrentUser) {
                            //Add new like
                            addNewLike(mHolder);
                            break;
                        }
                    }
                    if (!dataSnapshot.exists()) {
                        //Add new like
                        addNewLike(mHolder);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void addNewLike(final ViewHolder holder) {
        Log.d(TAG, "addNewLike: Adding new like");

        String newLikeID = mReference.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mReference.child(mContext.getString(R.string.dbname_photos))
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mReference.child(mContext.getString(R.string.dbname_user_photos))
                .child(holder.photo.getUser_id())
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        holder.heart.toggleLike();
        getLikesString(holder);
    }

    private void getCurrentUsername(){
        Log.d(TAG, "getCurrentUsername: Retrieving user account settings");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    currentUsername= singleSnapshot.getValue(UserAccountSettings.class).getUsername();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLikesString(final ViewHolder holder) {
        Log.d(TAG, "getLikesString: Getting likes string");

        try {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_photos))
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.users = new StringBuilder();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(mContext.getString(R.string.dbname_users))
                            .orderByChild(mContext.getString(R.string.field_user_id))
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: Found like: " +
                                        singleSnapshot.getValue(User.class).getUsername());

                                holder.users.append(singleSnapshot.getValue(User.class).getUsername());
                                holder.users.append(",");
                            }

                            String[] splitUsers = holder.users .toString().split(",");
                            if (holder.users.toString().contains(currentUsername+",")) {//mitch, mitchell.tabian
                                holder.likedByCurrentUser = true;
                            } else {
                                holder.likedByCurrentUser = false;
                            }

                            int length = splitUsers.length;
                            if (length == 1) {
                                holder.likesString = "Liked by " + splitUsers[0];
                            } else if (length == 2) {
                                holder.likesString = "Liked by " + splitUsers[0]
                                        + " and " + splitUsers[1];
                            } else if (length == 3) {
                                holder.likesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + " and " + splitUsers[2];
                            } else if (length == 4) {
                                holder.likesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + splitUsers[3];
                            } else if (length > 4) {
                                holder.likesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + (splitUsers.length - 3) + " others";
                            }
                            Log.d(TAG, "onDataChange: likes string: " + holder.likesString);
                            setupLikesString(holder,holder.likesString);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if (!dataSnapshot.exists()) {
                    holder.likesString = "";
                    holder.likedByCurrentUser = false;
                    setupLikesString(holder,holder.likesString);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }catch (NullPointerException e){
        Log.e(TAG, "getLikesString: NullPointerException: "+e.getMessage() );
        holder.likesString="";
        holder.likedByCurrentUser=false;

        setupLikesString(holder,holder.likesString);
    }
    }

    private void setupLikesString(final ViewHolder holder, String likesString){
        Log.d(TAG, "setupLikesString: Likes string: "+holder.likesString);

        if(holder.likedByCurrentUser){
            Log.d(TAG, "setupLikesString: Photo is liked by current user");
            holder.heartOutline.setVisibility(View.GONE);
            holder.heartFill.setVisibility(View.VISIBLE);
            holder.heartFill.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return holder.detector.onTouchEvent(motionEvent);
                }
            });
        } else{
            Log.d(TAG, "setupLikesString: Photo is not liked by current user");
            holder.heartOutline.setVisibility(View.VISIBLE);
            holder.heartFill.setVisibility(View.GONE);
            holder.heartOutline.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return holder.detector.onTouchEvent(motionEvent);
                }
            });
        }
        holder.likes.setText(likesString);
    }

    //Returns string  saying how many days ago post was made
    private String getTimeStampDifference(Photo photo) {
        Log.d(TAG, "getTimeStampDifference: Getting timestamp difference.");
        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Date today = c.getTime();
        sdf.format(today);
        Date timeStamp;
        final String photoTimeStamp = photo.getDate_created();
        try {
            timeStamp = sdf.parse(photoTimeStamp);
            difference = String.valueOf(Math.round(((today.getTime() - timeStamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            Log.e(TAG, "getTimeStampDifference: ParseException: " + e.getMessage());
            difference = "0";
        }

        return difference;
    }
}