package com.here.name.website.Civitas.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.here.name.website.Civitas.Models.GridItem;
import com.here.name.website.Civitas.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Charles on 6/30/2017.
 */

public class GridEmotionAdapter extends ArrayAdapter<GridItem>{
    private Context mContext;
    //private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();

    public GridEmotionAdapter(Context context, int layoutResource, String append, ArrayList<GridItem> mGridData) {
        super(context, layoutResource, mGridData);
       // mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.mGridData = mGridData;
    }

    private  static  class ViewHolder{
        TextView titleTextView;
        ImageView imageView;
    }
    public void setGridData(ArrayList<GridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Viewholder build pattern is similar to recyclerView
        ViewHolder holder;
        if (convertView==null){

            //If error, use mInflater

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView=inflater.inflate(layoutResource, parent, false);
            holder= new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.gridTitleView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.gridImageView);
            convertView.setTag(holder);
        } else{
            holder= (ViewHolder) convertView.getTag();
        }
        GridItem item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getTitle()));

        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return convertView;
    }
}
