package com.seekwork.bangmart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.seekwork.bangmart.adpter.GridAdapter;
import com.seekwork.bangmart.model.SubColumnBean;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartRoad;
import com.seekwork.bangmart.util.SeekerSoftConstant;
import com.seekwork.bangmart.util.Variable;
import com.seekwork.bangmart.view.SingleCountDownView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.DrawableBar;
import com.shizhefei.view.indicator.slidebar.ScrollBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import static com.seekwork.bangmart.util.SeekerSoftConstant.INIT_PRO_LIST_TIME;


/**
 * 新增页面 列表 页卡显示
 */
public class ProList2Activity extends AppCompatActivity {
    private GridView gv_data;
    private GridAdapter gridAdapter;
    private ArrayList<MBangmartRoad> AddToBangmartRoadList = new ArrayList<>();
    // 点击的是哪个TAB项目
    private List<MBangmartRoad> mBangmartRoads;


    private ImageView iv_logo;
    private LinearLayout ll_back;
    private SingleCountDownView singleCountDownViewPop;

    private TextView tv_shopping;

    private ScrollIndicatorView scrollIndicatorView;
    private int unSelectTextColor, selectTextColor;
    private LayoutInflater inflater;
    private ArrayList<SubColumnBean> columnList;

    private Context mContext;

    private MaterialDialog detailDialog;
    private ImageView iv_pic;
    private TextView tv_pro_name, tv_sku, tv_desc;
    private TextView tv_sure, tv_add, tv_choose_num, tv_cut;


    private int currentChoosePositon = 0;

    private void showDetailPop() {
        View customView = inflater.inflate(R.layout.pop_detail_layout, null);

        ImageView iv_close = customView.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailDialog != null && detailDialog.isShowing()) {
                    detailDialog.dismiss();
                }
            }
        });

        iv_pic = customView.findViewById(R.id.iv_pic);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_pic.getLayoutParams();
        layoutParams.width = (Variable.WIDTH - 160) / 4;
        layoutParams.height = layoutParams.width / 3 * 4;
        iv_pic.setLayoutParams(layoutParams);

        tv_pro_name = customView.findViewById(R.id.tv_pro_name);
        tv_sku = customView.findViewById(R.id.tv_sku);
        tv_desc = customView.findViewById(R.id.tv_desc);
        tv_sure = customView.findViewById(R.id.tv_sure);
        tv_add = customView.findViewById(R.id.tv_add);
        tv_choose_num = customView.findViewById(R.id.tv_choose_num);
        tv_cut = customView.findViewById(R.id.tv_cut);

        detailDialog = new MaterialDialog.Builder(this).customView(customView, false).build();
        WindowManager.LayoutParams wl = detailDialog.getWindow().getAttributes();
        wl.width = Variable.WIDTH - 160;
        wl.height = Variable.WIDTH - 160;
        wl.gravity = Gravity.CENTER;//设置dialog 在布局中的位置
        detailDialog.getWindow().setAttributes(wl);
        detailDialog.setCancelable(false);


        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailDialog != null && detailDialog.isShowing()) {
                    detailDialog.dismiss();
                }
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.pro_pager_new_layout);
        currentChoosePositon = getIntent().getExtras().getInt(SeekerSoftConstant.CHOOSE_POSITION);

        showDetailPop();

        ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (singleCountDownViewPop != null) {
                    singleCountDownViewPop.stopCountDown();
                    singleCountDownViewPop = null;
                }

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean("success", isSuccess);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                ProList2Activity.this.finish();
            }
        });

        tv_shopping = findViewById(R.id.tv_shopping);
        tv_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 彈出框
                if (AddToBangmartRoadList != null && AddToBangmartRoadList.size() > 0) {
                    Intent intent = new Intent(mContext, ShopCartActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SeekerSoftConstant.ADDCARTLIST, AddToBangmartRoadList);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 100);
                }
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
        singleCountDownViewPop.setTextColor(Color.parseColor("#CC181717"));
        singleCountDownViewPop.setTime(INIT_PRO_LIST_TIME);
        singleCountDownViewPop.setTimeColorHex("#CC181717");
        singleCountDownViewPop.setTimeSuffixText("S");
        singleCountDownViewPop.setSingleCountDownEndListener(new SingleCountDownView.SingleCountDownEndListener() {
            @Override
            public void onSingleCountDownEnd() {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean("success", isSuccess);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                ProList2Activity.this.finish();
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

        unSelectTextColor = Color.parseColor("#155398");
        selectTextColor = Color.parseColor("#155398");
        // 设置滚动监听
        OnTransitionTextListener onTransitionTextListener = new OnTransitionTextListener();
        onTransitionTextListener.setColor(selectTextColor, unSelectTextColor);
        onTransitionTextListener.setSize(17, 17);
        scrollIndicatorView.setOnTransitionListener(onTransitionTextListener);
        initTabColumn();
        scrollIndicatorView.setAdapter(new Indicator.IndicatorAdapter() {
            @Override
            public int getCount() {
                return columnList == null ? 0 : columnList.size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.tab_top, null);
                }
                TextView textView = (TextView) convertView;
                int padding = dipToPix(10);
                textView.setMinWidth((Variable.WIDTH - 100) / 5 - 20);
                textView.setMinHeight(dipToPix(46));
                textView.setText(columnList.get(position % columnList.size()).getName());
                textView.setPadding(padding, 0, padding, 0);
                Drawable drawableExpand = getResources().getDrawable(columnList.get(position).getResId());
                drawableExpand.setBounds(0, 0, drawableExpand.getMinimumWidth(), drawableExpand.getMinimumHeight());
                textView.setCompoundDrawables(drawableExpand, null, null, null);
                return convertView;
            }
        });
        scrollIndicatorView.setCurrentItem(currentChoosePositon);
        scrollIndicatorView.setOnIndicatorItemClickListener(new Indicator.OnIndicatorItemClickListener() {
            @Override
            public boolean onItemClick(View clickItemView, int postion) {
                chageAdapter(postion);
                return false;
            }
        });

        gv_data = findViewById(R.id.gv_data);
        gridAdapter = new GridAdapter(this, new GridAdapter.AddCartInterface() {
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
                tv_shopping.setText("去购物车(" + AddToBangmartRoadList.size() + ")");
            }

            @Override
            public void showDetail(final MBangmartRoad mBangmartRoad) {
                try {
                    final MBangmartRoad opM = (MBangmartRoad) mBangmartRoad.clone();
                    boolean isExist = false;
                    int position = -1;
                    for (int i = 0; i < AddToBangmartRoadList.size(); i++) {
                        if (opM.getRoadID() == AddToBangmartRoadList.get(i).getRoadID()) {
                            isExist = true;
                            position = i;
                        }
                    }

                    MBangmartRoad inRoad = null;
                    if (!isExist) {
                        // 不存在，加入购物车列表
                        inRoad = opM;
                        inRoad.setChooseNum(inRoad.getChooseNum() + 1);
                        AddToBangmartRoadList.add(inRoad);
                    } else {
                        // 存在，判断库存，进行choosenum设置
                        inRoad = AddToBangmartRoadList.get(position);
                    }
                    tv_shopping.setText("去购物车(" + AddToBangmartRoadList.size() + ")");

                    Glide.with(mContext).load(inRoad.getPicName()).placeholder(R.drawable.default_iv_bg).into(iv_pic);
                    tv_pro_name.setText(inRoad.getProductName());
                    tv_sku.setText("SKU: " + inRoad.getSKU());
                    tv_desc.setText(inRoad.getDescribe());
                    tv_choose_num.setText(String.valueOf(inRoad.getChooseNum()));

                    // add
                    final MBangmartRoad tmp = inRoad;
                    tv_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (tmp.getChooseNum() == tmp.getQty()) {
                                // 提示最多只能库存数
                                tv_add.setTextColor(mContext.getResources().getColor(R.color.un_title));
                                tv_cut.setTextColor(mContext.getResources().getColor(R.color.title));
                                //showTipDialog("提示：最多只能取最大库存数。");
                            } else {
                                tv_add.setTextColor(mContext.getResources().getColor(R.color.title));
                                tv_cut.setTextColor(mContext.getResources().getColor(R.color.title));
                                tmp.setChooseNum(tmp.getChooseNum() + 1);
                            }
                            tv_choose_num.setText(String.valueOf(tmp.getChooseNum()));
                        }
                    });

                    // cut
                    tv_cut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (tmp.getChooseNum() <= 0) {
                                // 提示最少取1件
                                tv_cut.setTextColor(mContext.getResources().getColor(R.color.un_title));
                                tv_add.setTextColor(mContext.getResources().getColor(R.color.title));
                                //showTipDialog("提示：最少要取1件。");
                            } else {
                                tmp.setChooseNum(tmp.getChooseNum() - 1);
                                tv_cut.setTextColor(mContext.getResources().getColor(R.color.title));
                                tv_add.setTextColor(mContext.getResources().getColor(R.color.title));
                            }

                            tv_choose_num.setText(String.valueOf(tmp.getChooseNum()));
                        }
                    });

                    detailDialog.show();
                } catch (Exception e) {

                }
            }
        });

        chageAdapter(currentChoosePositon);
        gv_data.setAdapter(gridAdapter);
    }

    private void chageAdapter(int postion) {
        if (postion == 0) {
            mBangmartRoads = SeekerSoftConstant.hashMap.get("A");
        } else if (postion == 1) {
            mBangmartRoads = SeekerSoftConstant.hashMap.get("B");
        } else if (postion == 2) {
            mBangmartRoads = SeekerSoftConstant.hashMap.get("C");
        } else if (postion == 3) {
            mBangmartRoads = SeekerSoftConstant.hashMap.get("D");
        } else if (postion == 4) {
            mBangmartRoads = SeekerSoftConstant.hashMap.get("E");
        } else if (postion == 5) {
            mBangmartRoads = SeekerSoftConstant.hashMap.get("F");
        }
        gridAdapter.setDataList(mBangmartRoads);
    }


    private boolean isSuccess = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            AddToBangmartRoadList.clear();
            if (data.getExtras() != null) {
                isSuccess = data.getExtras().getBoolean("success");
            }
            if (tv_shopping != null && AddToBangmartRoadList != null) {
                tv_shopping.setText("去购物车(" + AddToBangmartRoadList.size() + ")");
            }
        }
        if (requestCode == 100 && resultCode == 10 && data != null) {
            AddToBangmartRoadList.clear();
            AddToBangmartRoadList = (ArrayList<MBangmartRoad>) data.getExtras().getSerializable(SeekerSoftConstant.ADDCARTLIST);
            if (tv_shopping != null && AddToBangmartRoadList != null) {
                tv_shopping.setText("去购物车(" + AddToBangmartRoadList.size() + ")");
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tv_shopping != null && AddToBangmartRoadList != null) {
            tv_shopping.setText("去购物车(" + AddToBangmartRoadList.size() + ")");
        }
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

}
