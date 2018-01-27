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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.here.name.website.Civitas.Home.MainActivity;
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

public class GridEmotionAdapter extends BaseAdapter{
    private Context mContext;
    LayoutInflater inflaterf;
    private final int[] emotions;
    private final String[] titles;

    public GridEmotionAdapter(Context context, String[] values,int[] emotions) {
        this.mContext = context;
        this.titles = values;
        this.emotions = emotions;
        inflaterf=(LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView=convertView;

        if(convertView==null){
//            inflaterf=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView=inflaterf.inflate(R.layout.layout_grid_emotionview,null);
        }

        ImageView imageView= (ImageView) gridView.findViewById(R.id.gridEmotionView);
        TextView textView=(TextView) gridView.findViewById(R.id.gridEmotionTitleView);

        imageView.setImageResource(emotions[position]);
        textView.setText(titles[position]);

        return gridView;
    }
}
