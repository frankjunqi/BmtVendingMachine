package com.seekwork.bangmart;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.seekwork.bangmart.adpter.GridAdapter;
import com.seekwork.bangmart.network.api.Host;
import com.seekwork.bangmart.network.api.SeekWorkService;
import com.seekwork.bangmart.network.api.SrvResult;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartArea;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartDetail;
import com.seekwork.bangmart.network.entity.seekwork.MBangmartRoad;
import com.seekwork.bangmart.network.entity.seekwork.MMachineInfo;
import com.seekwork.bangmart.network.gsonfactory.GsonConverterFactory;
import com.seekwork.bangmart.serialport.CardReadSerialPort;
import com.seekwork.bangmart.util.LogCat;
import com.seekwork.bangmart.util.SeekerSoftConstant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 首页
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private TextView tv_error;

    private String mac_error_str;
    private TextView tv_mac_error;

    private MaterialDialog promissionDialog;
    private ProgressBar pb_loadingdata;
    private Button btn_try;

    private LinearLayout customView;
    private LinearLayout ll_btns;
    private Button btn_cart;
    private TextView tv_title;
    private TextView tv_cart_desc;

    private GridView gv_data;
    private GridAdapter gridAdapter;

    private List<MBangmartArea> mBangmartAreaList;
    private ArrayList<MBangmartRoad> AddToBangmartRoadList = new ArrayList<>();

    private CountDownTimer timer = new CountDownTimer(10000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            if (btn_try != null) {
                btn_try.setText((millisUntilFinished / 1000) + "秒后重试");
            }
        }

        @Override
        public void onFinish() {
            btn_try.setEnabled(true);
            btn_try.setText("重试");
            loadRegisterMachine();
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // dialog tip
        customView = (LinearLayout) inflater.inflate(R.layout.activity_main, null);
        setContentView(customView);

        tv_title = customView.findViewById(R.id.tv_title);
        tv_title.setOnClickListener(this);
        tv_cart_desc = customView.findViewById(R.id.tv_cart_desc);
        btn_cart = customView.findViewById(R.id.btn_cart);
        gv_data = customView.findViewById(R.id.gv_data);
        ll_btns = customView.findViewById(R.id.ll_btns);

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

                tv_cart_desc.setText("选择了" + AddToBangmartRoadList.size() + "个商品.");
            }
        });
        gv_data.setAdapter(gridAdapter);


        View customView = inflater.inflate(R.layout.pop_auth_layout, null);
        pb_loadingdata = customView.findViewById(R.id.pb_loadingdata);
        tv_error = customView.findViewById(R.id.tv_error);
        tv_mac_error = customView.findViewById(R.id.tv_mac_error);
        tv_mac_error.setMovementMethod(ScrollingMovementMethod.getInstance());

        TextView tv_machine = customView.findViewById(R.id.tv_machine);
        tv_machine.setText("设备号：" + SeekerSoftConstant.DEVICEID);
        btn_try = customView.findViewById(R.id.btn_try);
        btn_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRegisterMachine();
            }
        });

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ShopCartActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(SeekerSoftConstant.ADDCARTLIST, AddToBangmartRoadList);
                intent.putExtras(bundle);
                startActivity(intent);
                AddToBangmartRoadList.clear();
            }
        });

        promissionDialog = new MaterialDialog.Builder(this).customView(customView, false).build();
        promissionDialog.setCancelable(false);
        // initBangMartMachine();
        registerMachine(true);
    }

    private void loadRegisterMachine() {
        // 加载进度
        tv_error.setText("");
        pb_loadingdata.setVisibility(View.VISIBLE);
        btn_try.setVisibility(View.GONE);
        // 请求接口
        registerMachine(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_title) {
        }
    }

    /**
     * 获取货柜数据
     */
    private void bangMartQueryRoad() {
        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<MBangmartDetail>> updateAction = service.queryRoad(SeekerSoftConstant.MachineNo);
        LogCat.e("url = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<SrvResult<MBangmartDetail>>() {
            @Override
            public void onResponse(Call<SrvResult<MBangmartDetail>> call, Response<SrvResult<MBangmartDetail>> response) {
                if (response != null && response.body() != null && response.body().getData() != null) {
                    mBangmartAreaList = response.body().getData().getmBangmartAreas();

                    //  默认处理第一个货柜的数据
                    gridAdapter.setDataList(mBangmartAreaList.get(0).getAllBangmartRoads());

                    // 处理柜子数量
                    int count = mBangmartAreaList.size();
                    ll_btns.setWeightSum(count);

                    for (int i = 0; i < count; i++) {
                        final int k = i;
                        Button btn = new Button(mContext);
                        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                        btn.setText(String.valueOf(mBangmartAreaList.get(i).getArea()));
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gridAdapter.setDataList(mBangmartAreaList.get(k).getAllBangmartRoads());
                            }
                        });
                        ll_btns.addView(btn);
                    }

                    // TODO 接口返回数据，与扫描货道的数据进行对比
                    SeekerSoftConstant.storageMap.getArea(0);
                    SeekerSoftConstant.storageMap.getArea(1);
                    SeekerSoftConstant.storageMap.getArea(2);
                    mBangmartAreaList.get(0).getmBangmartFloors();

                    // TODO 如果数据出现不一样，需要提交接口，进行数据初始化失败的错误；

                }
            }

            @Override
            public void onFailure(Call<SrvResult<MBangmartDetail>> call, Throwable throwable) {

            }
        });
    }

    private void registerMachine(boolean isOk) {
        // 读卡器初始化串口设备
        CardReadSerialPort cardReadSerialPort = CardReadSerialPort.SingleInit();
        String error = "";
        if (cardReadSerialPort == null) {
            error = "读卡器串口打开失败。\n";
        }

        error = "";

        if (!TextUtils.isEmpty(error) || !isOk) {
            if (!promissionDialog.isShowing()) {
                promissionDialog.show();
            }
            tv_error.setText("错误：\n" + error);
            pb_loadingdata.setVisibility(View.GONE);
            btn_try.setVisibility(View.VISIBLE);

            // 开启定时器 10秒再发送一次请求直到成功 才关闭这个模态框 关闭这个重试定时器
            btn_try.setEnabled(false);
            timer.start();
            return;
        }

        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<MMachineInfo>> updateAction = service.getMachineInfo(SeekerSoftConstant.DEVICEID);
        LogCat.e("url = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<SrvResult<MMachineInfo>>() {
            @Override
            public void onResponse(Call<SrvResult<MMachineInfo>> call, Response<SrvResult<MMachineInfo>> response) {
                if (response != null && response.body() != null && response.body().getStatus() == 1 && response.body().getData() != null && response.body().getData().isAuthorize()) {
                    LogCat.e("Status: " + response.body().getStatus());
                    SeekerSoftConstant.MachineNo = response.body().getData().getMachineNo();

                    tv_title.setText(SeekerSoftConstant.MachineNo);

                    // 成功授权显示逻辑
                    promissionDialog.dismiss();
                    // 成功授权取消加载进度
                    pb_loadingdata.setVisibility(View.GONE);
                    btn_try.setVisibility(View.VISIBLE);

                    bangMartQueryRoad();

                } else {
                    if (!promissionDialog.isShowing()) {
                        promissionDialog.show();
                    }
                    tv_error.setText("错误：" + response != null ? response.body() != null ? response.body().getMsg() != null ? response.body().getMsg() : "response.body().getMsg() == null" : "response.body() == null" : "response == null");
                    pb_loadingdata.setVisibility(View.GONE);
                    btn_try.setVisibility(View.VISIBLE);

                    // 开启定时器 10秒再发送一次请求直到成功 才关闭这个模态框 关闭这个重试定时器
                    btn_try.setEnabled(false);
                    timer.start();

                }
            }

            @Override
            public void onFailure(Call<SrvResult<MMachineInfo>> call, Throwable throwable) {
                if (!promissionDialog.isShowing()) {
                    promissionDialog.show();
                }

                if (throwable != null) {
                    tv_error.setText("错误：" + throwable.getMessage());
                } else {
                    tv_error.setText("未知错误，请联系管理员。");
                }

                pb_loadingdata.setVisibility(View.GONE);
                btn_try.setVisibility(View.VISIBLE);

                // 开启定时器 10秒再发送一次请求直到成功 才关闭这个模态框 关闭这个重试定时器
                btn_try.setEnabled(false);
                timer.start();
            }
        });
    }

    private void loginValidate(final String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> updateAction = service.loginValidate(SeekerSoftConstant.MachineNo, cardNo);
        LogCat.e("url = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                if (response != null && response.body() != null && response.body().getStatus() == 1 && response.body().getData()) {
                    // 打开管理员页面
                    Intent intent = new Intent(MainActivity.this, ManageActivity.class);
                    intent.putExtra("cardNo", cardNo);
                    startActivity(intent);
                } else {
                    // 不是管理员卡，不需要做任何操作
                    LogCat.e("提示信息：" + response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CardReadSerialPort.SingleInit() != null) {
            CardReadSerialPort.SingleInit().setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
                @Override
                public void onDataReceiveString(String IDNUM) {
                    loginValidate(IDNUM);
                }
            });
        }
        if (tv_cart_desc != null) {
            tv_cart_desc.setText("选择了" + AddToBangmartRoadList.size() + "个商品.");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (CardReadSerialPort.SingleInit() != null) {
            CardReadSerialPort.SingleInit().setOnDataReceiveListener(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CardReadSerialPort.SingleInit() != null) {
            CardReadSerialPort.SingleInit().setOnDataReceiveListener(null);
        }
        System.exit(0);//直接结束程序
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int exitFlag = intent.getIntExtra(SeekerSoftConstant.EXITAPP, 0);
        if (exitFlag == 1) {
            // 退出程序
            if (CardReadSerialPort.SingleInit() != null) {
                CardReadSerialPort.SingleInit().closeSerialPort();
            }
            this.finish();
        }
    }

}
