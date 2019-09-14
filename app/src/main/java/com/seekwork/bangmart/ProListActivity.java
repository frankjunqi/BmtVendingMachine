package com.seekwork.bangmart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seekwork.bangmart.fragment.ItemFragment;
import com.seekwork.bangmart.model.SubColumnBean;
import com.seekwork.bangmart.util.Variable;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;

import static com.seekwork.bangmart.util.SeekerSoftConstant.INTENT_INT_INDEX;


/**
 * 新增页面 列表 页卡显示
 */
public class ProListActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ScrollIndicatorView scrollIndicatorView;
    private IndicatorViewPager indicatorViewPager;
    private int unSelectTextColor, selectTextColor;
    private LayoutInflater inflate;
    private ArrayList<SubColumnBean> columnList;
    private MainFragmetAdpter mainFragmetAdpter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.pro_pager_layout);
        scrollIndicatorView = (ScrollIndicatorView) findViewById(R.id.vip_tpi);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        unSelectTextColor = Color.parseColor("#9B9B9B");
        selectTextColor = Color.parseColor("#fc4a80");
        // 设置滚动监听
        OnTransitionTextListener onTransitionTextListener = new OnTransitionTextListener();
        onTransitionTextListener.setColor(selectTextColor, unSelectTextColor);
        onTransitionTextListener.setSize(16, 16);
        scrollIndicatorView.setOnTransitionListener(onTransitionTextListener);
        viewPager.setOffscreenPageLimit(2);
        // 将viewPager和indicator使用
        indicatorViewPager = new IndicatorViewPager(scrollIndicatorView, viewPager);
        // 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
        // 而在activity里面用FragmentManager 是 getSupportFragmentManager()
        mainFragmetAdpter = new MainFragmetAdpter(getSupportFragmentManager());
        indicatorViewPager.setAdapter(mainFragmetAdpter);
    }

    private void initTabColumn() {
        if (columnList == null) {
            columnList = new ArrayList<>();
        }
        // 初始化tab
        SubColumnBean columnBeanService = new SubColumnBean();
        columnBeanService.setId("0");
        columnBeanService.setName("基础");
        columnList.add(columnBeanService);

        SubColumnBean columnBeanHot = new SubColumnBean();
        columnBeanHot.setId("1");
        columnBeanHot.setName("国家");
        columnList.add(columnBeanHot);

        SubColumnBean columnBeanShop = new SubColumnBean();
        columnBeanShop.setId("2");
        columnBeanShop.setName("工长");
        columnList.add(columnBeanShop);

        SubColumnBean columnBeanMy = new SubColumnBean();
        columnBeanMy.setId("3");
        columnBeanMy.setName("产品");
        columnList.add(columnBeanMy);

        mainFragmetAdpter.notifyDataSetChanged();
    }

    private class MainFragmetAdpter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public MainFragmetAdpter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return columnList == null ? 0 : columnList.size();
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            int padding = dipToPix(10);
            textView.setMinWidth((Variable.WIDTH - 100) / 5 - 20);
            textView.setText(columnList.get(position % columnList.size()).getName());
            textView.setPadding(padding, 0, padding, 0);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            return getSubColumnTabFragment(position);
        }

        @Override
        public int getItemPosition(Object object) {
            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_NONE;
        }

    }

    /**
     * 根据dip值转化成px值
     *
     * @param dip
     * @return
     */
    private int dipToPix(float dip) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
        return size;
    }

    private Fragment getSubColumnTabFragment(int position) {
        Bundle bundle = new Bundle();
        Fragment fragment = new ItemFragment();
        bundle.putInt(INTENT_INT_INDEX, position);
        fragment.setArguments(bundle);
        return fragment;
    }
}
