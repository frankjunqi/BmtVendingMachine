package com.seekwork.bangmart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.seekwork.bangmart.adpter.ShopCartAdapter;
import com.seekwork.bangmart.network.api.Host;
import com.seekwork.bangmart.network.api.SeekWorkService;
import com.seekwork.bangmart.network.api.SrvResult;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarNeedGood;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickRoadDetailRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickRoadDetailResponse;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartAuthPickUpRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartAuthPickUpResponse;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartProductDetail;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartRoad;
import com.seekwork.bangmart.network.gsonfactory.GsonConverterFactory;
import com.seekwork.bangmart.serialport.CardReadSerialPort;
import com.seekwork.bangmart.util.LogCat;
import com.seekwork.bangmart.util.SeekerSoftConstant;
import com.seekwork.bangmart.view.SingleCountDownView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.seekwork.bangmart.util.SeekerSoftConstant.INIT_PRO_LIST_TIME;

/**
 * 购物车页面
 */
public class ShopCartActivity extends AppCompatActivity {

    private SingleCountDownView singleCountDownView;
    private LinearLayout ll_back;
    private ListView lv_data;
    private ArrayList<MBangmartRoad> AddToBangmartRoadList;
    private ShopCartAdapter shopCartAdapter;
    private TextView btn_sure;

    private MaterialDialog promissionDialog;
    private SingleCountDownView singleCountDownViewPop;
    private RelativeLayout rl_tip;

    private TextView tv_com_tip;

    private ImageView iv_tip_result;
    private TextView tv_tips_result;
    private ImageView iv_back_pop;

    private LinearLayout ll_take;
    private ProgressBar pb_loadingdata;

    private CardReadSerialPort.OnDataReceiveListener onDataReceiveListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        lv_data = findViewById(R.id.lv_data);
        btn_sure = findViewById(R.id.btn_sure);
        ll_back = findViewById(R.id.ll_back);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopCartActivity.this.finish();
            }
        });

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

        // 单个倒计时使用
        singleCountDownView = findViewById(R.id.singleCountDownView);
        singleCountDownView.setTextColor(Color.parseColor("#CC181717"));
        singleCountDownView.setTime(INIT_PRO_LIST_TIME).setTimeColorHex("#CC181717").setTimeSuffixText("S");

        // 单个倒计时结束事件监听
        singleCountDownView.setSingleCountDownEndListener(new SingleCountDownView.SingleCountDownEndListener() {
            @Override
            public void onSingleCountDownEnd() {
                ShopCartActivity.this.finish();
            }
        });

        singleCountDownView.startCountDown();

        initTakeDialog();

        onDataReceiveListener = new CardReadSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(final String IDNUM) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 读卡之后不接收读卡信息
                        if (CardReadSerialPort.SingleInit() != null) {
                            CardReadSerialPort.SingleInit().setOnDataReceiveListener(null);
                        }

                        // 初始化界面
                        rl_tip.setVisibility(View.GONE);
                        ll_take.setVisibility(View.GONE);
                        pb_loadingdata.setVisibility(View.VISIBLE);

                        // 有货道 ＋ 有库存 请求接口 判断是否有权限出货

                    }
                });

            }
        };

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AddToBangmartRoadList != null && AddToBangmartRoadList.size() > 0) {
                    ll_take.setVisibility(View.VISIBLE);
                    pb_loadingdata.setVisibility(View.INVISIBLE);
                    rl_tip.setVisibility(View.INVISIBLE);

                    // 开去串口读卡器
                    if (CardReadSerialPort.SingleInit() != null) {
                        CardReadSerialPort.SingleInit().setOnDataReceiveListener(onDataReceiveListener);
                    }

                    // 授权弹框
                    promissionDialog.show();

                    PickQuery("2032899024");
                }
            }
        });

    }


    /**
     * TODO 1.读取卡号
     */
    private void initTakeDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.activity_read_card, null);
        rl_tip = customView.findViewById(R.id.rl_tip);
        tv_com_tip = customView.findViewById(R.id.tv_com_tip);
        iv_tip_result = customView.findViewById(R.id.iv_tip_result);
        tv_tips_result = customView.findViewById(R.id.tv_tips_result);
        iv_back_pop = customView.findViewById(R.id.iv_back);

        iv_back_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 停止pop倒计时
                if (singleCountDownViewPop != null) {
                    singleCountDownViewPop.stopCountDown();
                }
                // 开启主界面倒计时
                if (singleCountDownView != null) {
                    singleCountDownView.startCountDown();
                }
            }
        });

        ll_take = customView.findViewById(R.id.ll_take);
        pb_loadingdata = customView.findViewById(R.id.pb_loadingdata);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;

        // 刷卡后续操作
        promissionDialog = new MaterialDialog.Builder(this).customView(customView, false).build();
        setWindowUIHide(promissionDialog.getWindow());

        WindowManager.LayoutParams wlp = promissionDialog.getWindow().getAttributes();
        wlp.width = widthPixels - 88;
        wlp.height = heightPixels / 4 * 3;
        wlp.gravity = Gravity.CENTER;//设置dialog 在布局中的位置
        promissionDialog.getWindow().setAttributes(wlp);
        promissionDialog.setCancelable(false);

        promissionDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // 开启pop倒计时
                if (singleCountDownViewPop != null) {
                    singleCountDownViewPop.setTime(60);
                    singleCountDownViewPop.startCountDown();
                }
                // 暂停主界面倒计时
                if (singleCountDownView != null) {
                    singleCountDownView.pauseCountDown();
                }
            }
        });

        promissionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // 停止pop倒计时
                if (singleCountDownViewPop != null) {
                    singleCountDownViewPop.stopCountDown();
                }
                // 开启主界面倒计时
                if (singleCountDownView != null) {
                    singleCountDownView.startCountDown();
                }
            }
        });

        // pop take 单个倒计时使用
        singleCountDownViewPop = customView.findViewById(R.id.singleCountDownViewPop);
        singleCountDownViewPop.setTextColor(Color.parseColor("#ffffffff"));
        singleCountDownViewPop.setTime(60);
        singleCountDownViewPop.setTimeColorHex("#ffffffff");
        singleCountDownViewPop.setTimeSuffixText("s");

        // pop take 单个倒计时结束事件监听
        singleCountDownViewPop.setSingleCountDownEndListener(new SingleCountDownView.SingleCountDownEndListener() {
            @Override
            public void onSingleCountDownEnd() {
                // TODO pop take 倒计时结束，关闭页面元素
                if (promissionDialog != null && promissionDialog.isShowing()) {
                    promissionDialog.dismiss();
                }
            }
        });
    }

    public void setWindowUIHide(Window window) {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        window.getDecorView().setSystemUiVisibility(uiFlags);
    }

    /**
     * TODO 2.购物车授权验证
     */
    private void PickQuery(final String CardNo) {
        if (TextUtils.isEmpty(CardNo)) {
            return;
        }
        tv_com_tip.setText("正在校验权限中...");
        MBangmartAuthPickUpRequest mBangmartAuthPickUpRequest = new MBangmartAuthPickUpRequest();
        mBangmartAuthPickUpRequest.setCardNo(CardNo);
        mBangmartAuthPickUpRequest.setMachineCode(SeekerSoftConstant.MachineNo);
        List<MBangmartProductDetail> detailList = new ArrayList<>();
        for (int i = 0; i < AddToBangmartRoadList.size(); i++) {
            MBangmartProductDetail detail = new MBangmartProductDetail();
            detail.setProductID(AddToBangmartRoadList.get(i).getProductID());
            detail.setPickNum(AddToBangmartRoadList.get(i).getChooseNum());
            detailList.add(detail);
        }
        mBangmartAuthPickUpRequest.setDetail(detailList);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<MBangmartAuthPickUpResponse>> updateAction = service.PickQuery(mBangmartAuthPickUpRequest);
        LogCat.e("url = " + updateAction.request().url().toString());
        Gson gson = new Gson();
        String json = gson.toJson(mBangmartAuthPickUpRequest);
        LogCat.e("url = " + json);
        updateAction.enqueue(new Callback<SrvResult<MBangmartAuthPickUpResponse>>() {
            @Override
            public void onResponse(Call<SrvResult<MBangmartAuthPickUpResponse>> call, Response<SrvResult<MBangmartAuthPickUpResponse>> response) {
                if (response != null && response.body() != null && response.body().getData() != null) {
                    boolean isAuth = response.body().getData().isAuth();
                    if (isAuth) {
                        // TODO 校验成功 拿到 orderid
                        ll_take.setVisibility(View.VISIBLE);
                        pb_loadingdata.setVisibility(View.VISIBLE);
                        GetPickUpRoads(response.body().getData().getOrderID(), CardNo);
                    } else {
                        // 不成功
                        ll_take.setVisibility(View.INVISIBLE);
                        pb_loadingdata.setVisibility(View.INVISIBLE);
                        rl_tip.setVisibility(View.VISIBLE);
                        tv_tips_result.setText(response.body().getData().getExceptionMsgStr());
                    }
                } else {
                    // 显示提示信息
                    ll_take.setVisibility(View.INVISIBLE);
                    pb_loadingdata.setVisibility(View.INVISIBLE);
                    rl_tip.setVisibility(View.VISIBLE);
                    tv_tips_result.setText(response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<SrvResult<MBangmartAuthPickUpResponse>> call, Throwable throwable) {
                ll_take.setVisibility(View.INVISIBLE);
                pb_loadingdata.setVisibility(View.INVISIBLE);
                rl_tip.setVisibility(View.VISIBLE);
                tv_tips_result.setText("网络异常，请联系管理员.");
            }
        });
    }


    /**
     * TODO 3.获取购物货道
     */
    private void GetPickUpRoads(final int orderId, final String CardNo) {
        tv_com_tip.setText("正在获取货道中...");
        final MBangmarPickRoadDetailRequest request = new MBangmarPickRoadDetailRequest();
        request.setMachineCode(SeekerSoftConstant.MachineNo);
        request.setOrderID(orderId);
        List<MBangmarNeedGood> mBangmarNeedGoods = new ArrayList<>();
        for (int i = 0; AddToBangmartRoadList != null && i < AddToBangmartRoadList.size(); i++) {
            MBangmarNeedGood good = new MBangmarNeedGood();
            good.setPickNum(AddToBangmartRoadList.get(i).getChooseNum());
            good.setProductID(AddToBangmartRoadList.get(i).getProductID());
            mBangmarNeedGoods.add(good);
        }
        request.setmBangmarNeedGoods(mBangmarNeedGoods);


        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<MBangmarPickRoadDetailResponse>> updateAction = service.GetPickUpRoads(request);
        LogCat.e("url = " + updateAction.request().url().toString());
        Gson gson = new Gson();
        String json = gson.toJson(request);
        LogCat.e("url = " + json);

        updateAction.enqueue(new Callback<SrvResult<MBangmarPickRoadDetailResponse>>() {
            @Override
            public void onResponse(Call<SrvResult<MBangmarPickRoadDetailResponse>> call, Response<SrvResult<MBangmarPickRoadDetailResponse>> response) {
                if (response != null && response.body() != null && response.body().getData() != null) {
                    if (promissionDialog != null && promissionDialog.isShowing()) {
                        promissionDialog.dismiss();
                    }

                    // TODO 出货
                    Intent intent = new Intent(ShopCartActivity.this, ResultActivity.class);
                    MBangmarPickRoadDetailResponse outResponse = response.body().getData();

                    Gson gson = new Gson();
                    String json = gson.toJson(outResponse);
                    LogCat.e("url = " + json);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SeekerSoftConstant.OUTCART, outResponse);
                    bundle.putSerializable(SeekerSoftConstant.CardNo, CardNo);
                    bundle.putInt(SeekerSoftConstant.OrderID, orderId);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    ShopCartActivity.this.finish();
                } else {
                    // 异常
                    ll_take.setVisibility(View.INVISIBLE);
                    pb_loadingdata.setVisibility(View.INVISIBLE);
                    rl_tip.setVisibility(View.VISIBLE);
                    tv_tips_result.setText(response.body().getMsg());

                }
            }

            @Override
            public void onFailure(Call<SrvResult<MBangmarPickRoadDetailResponse>> call, Throwable throwable) {
                // 异常
                ll_take.setVisibility(View.INVISIBLE);
                pb_loadingdata.setVisibility(View.INVISIBLE);
                rl_tip.setVisibility(View.VISIBLE);
                tv_tips_result.setText("网络异常，请联系管理员.");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (singleCountDownView != null) {
            singleCountDownView.stopCountDown();
            singleCountDownView = null;
        }

        if (singleCountDownViewPop != null) {
            singleCountDownViewPop.stopCountDown();
            singleCountDownViewPop = null;
        }

        if (promissionDialog != null && promissionDialog.isShowing()) {
            promissionDialog.dismiss();
            promissionDialog = null;
        }
    }
}
