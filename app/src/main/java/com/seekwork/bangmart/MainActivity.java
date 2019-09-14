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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.seekwork.bangmart.util.Variable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 首页
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_logo;
    private ImageView iv_show_pic;

    private LinearLayout ll_one, ll_two;
    private LinearLayout ll_naobao, ll_wujin, ll_wenju, ll_riyong, ll_yinliao, ll_shipin;

    private TextView tv_error;
    private TextView tv_mac_error;

    private MaterialDialog promissionDialog;
    private ProgressBar pb_loadingdata;
    private Button btn_try;

    private LinearLayout customView;

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
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // dialog tip
        customView = (LinearLayout) inflater.inflate(R.layout.activity_main, null);
        setContentView(customView);

        iv_logo = customView.findViewById(R.id.iv_logo);
        int logo_width = 605;
        int logo_height = 68;
        int imgLogoWidth = Variable.WIDTH / 7 * 4;
        int imgLogoHeight = imgLogoWidth * logo_height / logo_width;
        RelativeLayout.LayoutParams rl_logo = (RelativeLayout.LayoutParams) iv_logo.getLayoutParams();
        rl_logo.width = imgLogoWidth;
        rl_logo.height = imgLogoHeight;
        iv_logo.setLayoutParams(rl_logo);

        iv_show_pic = customView.findViewById(R.id.iv_show_pic);
        int width = 1080;
        int height = 629;
        int imgWidth = Variable.WIDTH;
        int imgHeigth = imgWidth * height / width;
        LinearLayout.LayoutParams rl = (LinearLayout.LayoutParams) iv_show_pic.getLayoutParams();
        rl.width = imgWidth;
        rl.height = imgHeigth;
        iv_show_pic.setLayoutParams(rl);


        ll_one = customView.findViewById(R.id.ll_one);
        ll_two = customView.findViewById(R.id.ll_two);
        int ll_width = Variable.WIDTH;
        int ll_height = Variable.WIDTH / 3;
        ll_one.getLayoutParams().height = ll_height;
        ll_two.getLayoutParams().height = ll_height;


        ll_naobao = customView.findViewById(R.id.ll_naobao);
        ll_naobao.setOnClickListener(this);
        ll_wujin = customView.findViewById(R.id.ll_wujin);
        ll_wujin.setOnClickListener(this);
        ll_wenju = customView.findViewById(R.id.ll_wenju);
        ll_wenju.setOnClickListener(this);
        ll_riyong = customView.findViewById(R.id.ll_riyong);
        ll_riyong.setOnClickListener(this);
        ll_yinliao = customView.findViewById(R.id.ll_yinliao);
        ll_yinliao.setOnClickListener(this);
        ll_shipin = customView.findViewById(R.id.ll_shipin);
        ll_shipin.setOnClickListener(this);


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

        promissionDialog = new MaterialDialog.Builder(this).customView(customView, false).build();
        promissionDialog.setCancelable(false);
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
        if (v.getId() == R.id.ll_naobao) {
            Intent intent = new Intent(this, ProList2Activity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.ll_wujin) {
            Intent intent = new Intent(this, ProListActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.ll_wujin) {

        } else if (v.getId() == R.id.ll_riyong) {

        } else if (v.getId() == R.id.ll_yinliao) {

        } else if (v.getId() == R.id.ll_shipin) {

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
                    List<MBangmartArea> mBangmartAreaList = response.body().getData().getmBangmartAreas();

                    SeekerSoftConstant.hashMap.put("A", new ArrayList<MBangmartRoad>());
                    SeekerSoftConstant.hashMap.put("B", new ArrayList<MBangmartRoad>());
                    SeekerSoftConstant.hashMap.put("C", new ArrayList<MBangmartRoad>());
                    SeekerSoftConstant.hashMap.put("D", new ArrayList<MBangmartRoad>());
                    SeekerSoftConstant.hashMap.put("E", new ArrayList<MBangmartRoad>());
                    SeekerSoftConstant.hashMap.put("F", new ArrayList<MBangmartRoad>());

                    int area = mBangmartAreaList.size();
                    for (int i = 0; i < area; i++) {
                        List<MBangmartRoad> rodeList = mBangmartAreaList.get(i).getAllBangmartRoads();
                        for (int k = 0; k < rodeList.size(); k++) {
                            if ("A".equals(rodeList.get(k).getDisplayType())) {
                                SeekerSoftConstant.hashMap.get("A").add(rodeList.get(k));
                            } else if ("B".equals(rodeList.get(k).getDisplayType())) {
                                SeekerSoftConstant.hashMap.get("B").add(rodeList.get(k));
                            } else if ("C".equals(rodeList.get(k).getDisplayType())) {
                                SeekerSoftConstant.hashMap.get("C").add(rodeList.get(k));
                            } else if ("D".equals(rodeList.get(k).getDisplayType())) {
                                SeekerSoftConstant.hashMap.get("D").add(rodeList.get(k));
                            } else if ("E".equals(rodeList.get(k).getDisplayType())) {
                                SeekerSoftConstant.hashMap.get("E").add(rodeList.get(k));
                            } else if ("F".equals(rodeList.get(k).getDisplayType())) {
                                SeekerSoftConstant.hashMap.get("F").add(rodeList.get(k));
                            }
                        }
                    }

                    // TODO 接口返回数据，与扫描货道的数据进行对比

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
