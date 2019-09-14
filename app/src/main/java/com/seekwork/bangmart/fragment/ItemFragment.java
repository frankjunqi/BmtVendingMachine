package com.seekwork.bangmart.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.seekwork.bangmart.R;
import com.seekwork.bangmart.adpter.GridAdapter;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartArea;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartRoad;
import com.seekwork.bangmart.util.SeekerSoftConstant;
import com.shizhefei.fragment.LazyFragment;

import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends LazyFragment {

    private View customView;

    private GridView gv_data;
    private GridAdapter gridAdapter;

    private List<MBangmartArea> mBangmartAreaList;
    private ArrayList<MBangmartRoad> AddToBangmartRoadList = new ArrayList<>();

    // 点击的是哪个TAB项目
    private int postion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postion = getArguments().getInt(SeekerSoftConstant.INTENT_INT_INDEX);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        customView = inflater.inflate(R.layout.pro_list_layout, null);
        setContentView(customView);
        gv_data = customView.findViewById(R.id.gv_data);
        gridAdapter = new GridAdapter(getContext(), new GridAdapter.AddCartInterface() {
            @Override
            public void addToCart(MBangmartRoad mBangmartRoad) {
                //TODO clone 对象。判断库存，是否可以再添加商品
                MBangmartRoad opM = null;
                try {
                    opM = (MBangmartRoad) mBangmartRoad.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                boolean isExist = false;
                int position = -1;
                for (int i = 0; i < AddToBangmartRoadList.size(); i++) {
                    if (opM.getRoadID() == AddToBangmartRoadList.get(i).getRoadID()) {
                        isExist = true;
                        position = i;
                    }
                }

                if (!isExist) {
                    // 不存在，加入购物车列表
                    opM.setChooseNum(opM.getChooseNum() + 1);
                    AddToBangmartRoadList.add(opM);
                } else {
                    // 存在，判断库存，进行choosenum设置
                    if (AddToBangmartRoadList.get(position).getChooseNum() == AddToBangmartRoadList.get(position).getQty()) {
                        // TODO  不可以加了，库存满了
                    } else {
                        AddToBangmartRoadList.get(position).setChooseNum(AddToBangmartRoadList.get(position).getChooseNum() + 1);
                    }
                }
            }
        });
        gv_data.setAdapter(gridAdapter);
    }

}
