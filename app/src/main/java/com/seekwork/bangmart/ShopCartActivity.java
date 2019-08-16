package com.seekwork.bangmart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.seekwork.bangmart.adpter.ShopCartAdapter;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartRoad;
import com.seekwork.bangmart.util.SeekerSoftConstant;

import java.util.ArrayList;

/**
 * 购物车页面
 */
public class ShopCartActivity extends AppCompatActivity {

    private ListView lv_data;
    private ArrayList<MBangmartRoad> AddToBangmartRoadList;

    private ShopCartAdapter shopCartAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        lv_data = findViewById(R.id.lv_data);
        Bundle bundle = getIntent().getExtras();
        AddToBangmartRoadList = (ArrayList<MBangmartRoad>) bundle.getSerializable(SeekerSoftConstant.ADDCARTLIST);

        shopCartAdapter = new ShopCartAdapter(this, new ShopCartAdapter.DeleteCartInterface() {
            @Override
            public void deleteFromCart(MBangmartRoad mBangmartRoad) {
                AddToBangmartRoadList.remove(mBangmartRoad);
                shopCartAdapter.notifyDataSetChanged();
            }
        });

        lv_data.setAdapter(shopCartAdapter);
        shopCartAdapter.setDataList(AddToBangmartRoadList);

    }
}
