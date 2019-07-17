package com.seekwork.bangmart.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seekwork.bangmart.R;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartRoad;
import com.seekwork.bangmart.util.DensityUtil;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<MBangmartRoad> mBangmartRoads;
    private LayoutInflater inflater;

    public GridAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public void setDataList(List<MBangmartRoad> mBangmartRoads) {
        this.mBangmartRoads = mBangmartRoads;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mBangmartRoads == null) {
            return 0;
        }
        return mBangmartRoads.size();
    }

    @Override
    public Object getItem(int i) {
        return mBangmartRoads.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.gird_item_view, null);
            // Locate the TextViews in item_road_my_road.xml
            holder.iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // set product name
        holder.tv_name.setText(mBangmartRoads.get(i).getProductName());

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.iv_pic.getLayoutParams();
        layoutParams.width = (DensityUtil.getWidth(mContext)) / 3;
        layoutParams.height = layoutParams.width;
        holder.iv_pic.setLayoutParams(layoutParams);
        Glide.with(mContext).load(mBangmartRoads.get(i).getPicName()).placeholder(R.drawable.default_iv_bg).into(holder.iv_pic);
        return view;
    }


    public class ViewHolder {
        public TextView tv_name;
        public ImageView iv_pic;
    }
}
