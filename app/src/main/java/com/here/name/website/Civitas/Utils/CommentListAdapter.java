package com.here.name.website.Civitas.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.here.name.website.Civitas.Models.Comment;
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
 * Created by Charles on 12/29/2017.
 */

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private static final String TAG = "CommentListAdapter";

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;

    public CommentListAdapter(@NonNull Context context, int resource,
                              @NonNull List<Comment> objects) {
        super(context, resource, objects);
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext= context;
        layoutResource=resource;
    }

    private static class ViewHolder{
        TextView comment,username,timeStamp,likes_this,reply;
        CircularImageView profileImage;
        ImageView commentLikeButton;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView==null){
            convertView=mInflater.inflate(layoutResource,parent,false);
            holder= new ViewHolder();

            holder.comment=(TextView) convertView.findViewById(R.id.comment);
            holder.username=(TextView) convertView.findViewById(R.id.comment_username);
            holder.timeStamp=(TextView) convertView.findViewById(R.id.comment_time_posted);
            holder.reply=(TextView) convertView.findViewById(R.id.comment_reply);
            //holder.likes_this=(TextView) convertView.findViewById(R.id.comment_likes);
            holder.commentLikeButton=(ImageView) convertView.findViewById(R.id.commentLikeButton);
            holder.profileImage=(CircularImageView) convertView.findViewById(R.id.comment_profile_image);

            convertView.setTag(holder);
        } else{
            holder=(ViewHolder) convertView.getTag();
        }

        //Set comment
        holder.comment.setText(getItem(position).getComment());

        //Set timestamp difference
        String timeStampDifference= getTimestampDifference(getItem(position));
        if(!timeStampDifference.equals("0")){
            holder.timeStamp.setText(timeStampDifference+" d");
        } else{
            holder.timeStamp.setText("Today");
        }

        //Set username and profile image
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    holder.username.setText(
                            singleSnapshot.getValue(UserAccountSettings.class).getUsername());

                    ImageLoader imageLoader=ImageLoader.getInstance();

                    imageLoader.displayImage(
                            singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
                    holder.profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Query cancelled");
            }
        });

        try {
            if(position==0){
                holder.commentLikeButton.setVisibility(View.GONE);
                holder.likes_this.setVisibility(View.GONE);
                holder.reply.setVisibility(View.GONE);
            }
        } catch (NullPointerException e){
            Log.e(TAG, "getView: NullPointerException: "+e.getMessage() );
        }

        return convertView;
    }

    //Returns string  saying how many days ago post was made
    private String getTimestampDifference(Comment comment){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = comment.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }
}
