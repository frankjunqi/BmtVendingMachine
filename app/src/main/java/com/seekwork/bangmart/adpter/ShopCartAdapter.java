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

import java.util.List;

public class ShopCartAdapter extends BaseAdapter {

    private Context mContext;
    private List<MBangmartRoad> mBangmartRoads;
    private LayoutInflater inflater;
    private DeleteCartInterface deleteCartInterface;

    public ShopCartAdapter(Context mContext, DeleteCartInterface deleteCartInterface) {
        this.mContext = mContext;
        this.deleteCartInterface = deleteCartInterface;
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
            view = inflater.inflate(R.layout.shop_item_view, null);
            // Locate the TextViews in item_road_my_road.xml
            holder.iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
            holder.iv_delete_to_cart = (ImageView) view.findViewById(R.id.iv_delete_to_cart);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_add = (TextView) view.findViewById(R.id.tv_add);
            holder.tv_cut = (TextView) view.findViewById(R.id.tv_cut);
            holder.tv_choose_num = (TextView) view.findViewById(R.id.tv_choose_num);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // set product name
        holder.tv_name.setText(mBangmartRoads.get(i).getProductName());

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.iv_pic.getLayoutParams();

        layoutParams.width = ((DensityUtil.getWidth(mContext))) / 5;
        layoutParams.height = layoutParams.width;
        holder.iv_pic.setLayoutParams(layoutParams);
        Glide.with(mContext).load(mBangmartRoads.get(i).getPicName()).placeholder(R.drawable.default_iv_bg).into(holder.iv_pic);

        holder.tv_choose_num.setText(String.valueOf(mBangmartRoads.get(i).getChooseNum()));

        // add
        holder.tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBangmartRoads.get(i).getChooseNum() == mBangmartRoads.get(i).getQty()) {
                    // 提示最多只能库存数
                    holder.tv_add.setTextColor(mContext.getResources().getColor(R.color.un_title));
                    holder.tv_cut.setTextColor(mContext.getResources().getColor(R.color.title));
                    //showTipDialog("提示：最多只能取最大库存数。");
                } else {
                    holder.tv_add.setTextColor(mContext.getResources().getColor(R.color.title));
                    holder.tv_cut.setTextColor(mContext.getResources().getColor(R.color.title));
                    mBangmartRoads.get(i).setChooseNum(mBangmartRoads.get(i).getChooseNum() + 1);
                }
                holder.tv_choose_num.setText(String.valueOf(mBangmartRoads.get(i).getChooseNum()));
            }
        });

        // cut
        holder.tv_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBangmartRoads.get(i).getChooseNum() == 1) {
                    // 提示最少取1件
                    holder.tv_cut.setTextColor(mContext.getResources().getColor(R.color.un_title));
                    holder.tv_add.setTextColor(mContext.getResources().getColor(R.color.title));
                    //showTipDialog("提示：最少要取1件。");
                } else {
                    mBangmartRoads.get(i).setChooseNum(mBangmartRoads.get(i).getChooseNum() - 1);
                    holder.tv_cut.setTextColor(mContext.getResources().getColor(R.color.title));
                    holder.tv_add.setTextColor(mContext.getResources().getColor(R.color.title));
                }

                holder.tv_choose_num.setText(String.valueOf(mBangmartRoads.get(i).getChooseNum()));
            }
        });

        holder.iv_delete_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteCartInterface != null) {
                    deleteCartInterface.deleteFromCart(mBangmartRoads.get(i));
                }
            }
        });

        return view;
    }

    public interface DeleteCartInterface {
        void deleteFromCart(MBangmartRoad mBangmartRoad);
    }

    public class ViewHolder {
        public TextView tv_name;
        public ImageView iv_pic;
        public ImageView iv_delete_to_cart;
        public TextView tv_add, tv_cut, tv_choose_num;
    }
}
