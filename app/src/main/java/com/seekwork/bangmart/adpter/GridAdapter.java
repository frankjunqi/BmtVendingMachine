package com.seekwork.bangmart.adpter;

import android.content.Context;
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
import com.seekwork.bangmart.util.Variable;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<MBangmartRoad> mBangmartRoads;
    private LayoutInflater inflater;
    private AddCartInterface addCartInterface;

    public GridAdapter(Context mContext, AddCartInterface addCartInterface) {
        this.mContext = mContext;
        this.addCartInterface = addCartInterface;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.gird_item_view, null);
            // Locate the TextViews in item_road_my_road.xml
            holder.iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
            holder.iv_add_to_cart = (ImageView) view.findViewById(R.id.iv_add_to_cart);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_sku = view.findViewById(R.id.tv_sku);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // set product name
        holder.tv_name.setText(mBangmartRoads.get(i).getProductName());

        holder.tv_sku.setText(mBangmartRoads.get(i).getSKU());

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.iv_pic.getLayoutParams();

        layoutParams.width = (Variable.WIDTH - 160) / 3;
        layoutParams.height = layoutParams.width / 3 * 4;
        holder.iv_pic.setLayoutParams(layoutParams);
        Glide.with(mContext).load(mBangmartRoads.get(i).getPicName()).placeholder(R.drawable.default_iv_bg).into(holder.iv_pic);
        holder.iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addCartInterface != null) {
                    addCartInterface.showDetail(mBangmartRoads.get(i));
                }
            }
        });

        holder.iv_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addCartInterface != null) {
                    addCartInterface.addToCart(mBangmartRoads.get(i));
                }
            }
        });
        return view;
    }

    public interface AddCartInterface {
        public void addToCart(MBangmartRoad mBangmartRoad);

        public void showDetail(MBangmartRoad mBangmartRoad);
    }

    public class ViewHolder {
        public TextView tv_name;
        public ImageView iv_pic;
        public ImageView iv_add_to_cart;
        public TextView tv_sku;
    }
}
