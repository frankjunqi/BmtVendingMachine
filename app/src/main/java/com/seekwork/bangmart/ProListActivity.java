package com.seekwork.bangmart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seekwork.bangmart.fragment.ItemFragment;
import com.seekwork.bangmart.model.SubColumnBean;
import com.seekwork.bangmart.util.Variable;
import com.seekwork.bangmart.view.SingleCountDownView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.DrawableBar;
import com.shizhefei.view.indicator.slidebar.ScrollBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;

import static com.seekwork.bangmart.util.SeekerSoftConstant.INTENT_INT_INDEX;


/**
 * 新增页面 列表 页卡显示
 */
public class ProListActivity extends AppCompatActivity {


    private ImageView iv_logo, iv_back;
    private SingleCountDownView singleCountDownViewPop;

    private TextView tv_shopping;

    private ViewPager viewPager;
    private ScrollIndicatorView scrollIndicatorView;
    private IndicatorViewPager indicatorViewPager;
    private int unSelectTextColor, selectTextColor;
    private LayoutInflater inflater;
    private ArrayList<SubColumnBean> columnList;
    private MainFragmetAdpter mainFragmetAdpter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.pro_pager_layout);

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (singleCountDownViewPop != null) {
                    singleCountDownViewPop.stopCountDown();
                    singleCountDownViewPop = null;
                }
                ProListActivity.this.finish();
            }
        });

        tv_shopping = findViewById(R.id.tv_shopping);
        tv_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 彈出框
            }
        });


        iv_logo = findViewById(R.id.iv_logo);
        int logo_width = 605;
        int logo_height = 68;
        int imgLogoWidth = Variable.WIDTH / 7 * 4;
        int imgLogoHeight = imgLogoWidth * logo_height / logo_width;
        RelativeLayout.LayoutParams rl_logo = (RelativeLayout.LayoutParams) iv_logo.getLayoutParams();
        rl_logo.width = imgLogoWidth;
        rl_logo.height = imgLogoHeight;
        iv_logo.setLayoutParams(rl_logo);


        singleCountDownViewPop = findViewById(R.id.singleCountDownView);
        singleCountDownViewPop.setTextColor(Color.parseColor("#3A3939"));
        singleCountDownViewPop.setTime(90);
        singleCountDownViewPop.setTimeColorHex("#3A3939");
        singleCountDownViewPop.setTimeSuffixText("S");
        singleCountDownViewPop.setSingleCountDownEndListener(new SingleCountDownView.SingleCountDownEndListener() {
            @Override
            public void onSingleCountDownEnd() {
                ProListActivity.this.finish();
            }
        });
        singleCountDownViewPop.startCountDown();

        scrollIndicatorView = (ScrollIndicatorView) findViewById(R.id.vip_tpi);
        // 设置 srcoll page indicator back ground
        scrollIndicatorView.setBackgroundColor(getResources().getColor(R.color.action_bg));
        scrollIndicatorView.setScrollBar(new DrawableBar(this, R.drawable.round_border_white_selector, ScrollBar.Gravity.CENTENT_BACKGROUND) {
            @Override
            public int getHeight(int tabHeight) {
                return tabHeight - dipToPix(12);
            }

            @Override
            public int getWidth(int tabWidth) {
                return tabWidth - dipToPix(4);
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        unSelectTextColor = Color.parseColor("#155398");
        selectTextColor = Color.parseColor("#155398");
        // 设置滚动监听
        OnTransitionTextListener onTransitionTextListener = new OnTransitionTextListener();
        onTransitionTextListener.setColor(selectTextColor, unSelectTextColor);
        onTransitionTextListener.setSize(17, 17);
        scrollIndicatorView.setOnTransitionListener(onTransitionTextListener);
        viewPager.setOffscreenPageLimit(6);
        // 将viewPager和indicator使用
        indicatorViewPager = new IndicatorViewPager(scrollIndicatorView, viewPager);
        // 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
        // 而在activity里面用FragmentManager 是 getSupportFragmentManager()
        mainFragmetAdpter = new MainFragmetAdpter(getSupportFragmentManager());
        indicatorViewPager.setAdapter(mainFragmetAdpter);


        initTabColumn();
    }

    private void initTabColumn() {
        if (columnList == null) {
            columnList = new ArrayList<>();
        }
        // 初始化tab
        SubColumnBean naobao = new SubColumnBean();
        naobao.setId("0");
        naobao.setName("劳保");
        naobao.setResId(R.mipmap.naobao_s);
        columnList.add(naobao);

        SubColumnBean wujin = new SubColumnBean();
        wujin.setId("1");
        wujin.setName("五金");
        wujin.setResId(R.mipmap.wujin_s);
        columnList.add(wujin);

        SubColumnBean wenju = new SubColumnBean();
        wenju.setId("2");
        wenju.setName("文具");
        wenju.setResId(R.mipmap.wenju_s);
        columnList.add(wenju);

        SubColumnBean riyong = new SubColumnBean();
        riyong.setId("3");
        riyong.setName("日用");
        riyong.setResId(R.mipmap.riyong_s);
        columnList.add(riyong);

        SubColumnBean yinliao = new SubColumnBean();
        yinliao.setId("4");
        yinliao.setName("饮料");
        yinliao.setResId(R.mipmap.yinliao_s);
        columnList.add(yinliao);

        SubColumnBean shipin = new SubColumnBean();
        shipin.setId("5");
        shipin.setName("食品");
        shipin.setResId(R.mipmap.shipin_s);
        columnList.add(shipin);

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
                convertView = inflater.inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            int padding = dipToPix(10);
            textView.setMinWidth((Variable.WIDTH - 100) / 5 - 20);
            textView.setText(columnList.get(position % columnList.size()).getName());
            textView.setPadding(padding, 0, padding, 0);
            Drawable drawableExpand = getResources().getDrawable(columnList.get(position).getResId());
            drawableExpand.setBounds(0, 0, drawableExpand.getMinimumWidth(), drawableExpand.getMinimumHeight());
            textView.setCompoundDrawables(drawableExpand, null, null, null);
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

        class ViewHoder {
            public TextView title;
            public ImageView iv_icon;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (singleCountDownViewPop != null) {
            singleCountDownViewPop.stopCountDown();
            singleCountDownViewPop = null;
        }
    }

    private Fragment getSubColumnTabFragment(int position) {
        Bundle bundle = new Bundle();
        Fragment fragment = new ItemFragment();
        bundle.putInt(INTENT_INT_INDEX, position);
        fragment.setArguments(bundle);
        return fragment;
    }
}
