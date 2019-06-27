package com.seekwork.bangmart;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.seekwork.bangmart.network.api.Host;
import com.seekwork.bangmart.network.api.SeekWorkService;
import com.seekwork.bangmart.network.api.SrvResult;
import com.seekwork.bangmart.network.entity.seekwork.MMachineInfo;
import com.seekwork.bangmart.network.gsonfactory.GsonConverterFactory;
import com.seekwork.bangmart.serialport.CardReadSerialPort;
import com.seekwork.bangmart.util.LogCat;
import com.seekwork.bangmart.util.SeekerSoftConstant;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 首页
public class MainActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private RelativeLayout customView;

    private TextView tv_error;

    private MaterialDialog promissionDialog;

    private ProgressBar pb_loadingdata;
    private Button btn_try;

    public final static String[] PERMS_WRITE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS};

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
        customView = (RelativeLayout) inflater.inflate(R.layout.activity_main, null);
        setContentView(customView);

        View customView = inflater.inflate(R.layout.pop_auth_layout, null);

        pb_loadingdata = customView.findViewById(R.id.pb_loadingdata);
        tv_error = customView.findViewById(R.id.tv_error);

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

        // 授权弹框
        registerMachine();

        EasyPermissions.requestPermissions(this, "请求权限", 12, PERMS_WRITE);

    }

    private void loadRegisterMachine() {
        // 加载进度
        tv_error.setText("");
        pb_loadingdata.setVisibility(View.VISIBLE);
        btn_try.setVisibility(View.GONE);
        // 请求接口
        registerMachine();
    }


    @Override
    public void onClick(View v) {

    }

    private void registerMachine() {

        // 初始化串口设备
        CardReadSerialPort cardReadSerialPort = CardReadSerialPort.SingleInit();
        String error = "";
        if (cardReadSerialPort == null) {
            error = "读卡器串口打开失败。\n";
        }

        error = "";
        if (!TextUtils.isEmpty(error)) {
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

                } else {
                    if (!promissionDialog.isShowing()) {
                        promissionDialog.show();
                    }
                    tv_error.setText("错误：" + response.body().getMsg());
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
        CardReadSerialPort.SingleInit().setOnDataReceiveListener(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);//直接结束程序
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int exitFlag = intent.getIntExtra(SeekerSoftConstant.EXITAPP, 0);
        if (exitFlag == 1) {
            // 退出程序
            CardReadSerialPort.SingleInit().closeSerialPort();
            this.finish();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
