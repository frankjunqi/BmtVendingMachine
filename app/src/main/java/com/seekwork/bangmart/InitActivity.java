package com.seekwork.bangmart;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bangmart.nt.channel.IChannelMonitor;
import com.bangmart.nt.command.Attention;
import com.bangmart.nt.command.Command;
import com.bangmart.nt.command.CommandDef;
import com.bangmart.nt.command.ICallbackListener;
import com.bangmart.nt.machine.HeartBeatListener;
import com.bangmart.nt.machine.MacMetaData;
import com.bangmart.nt.machine.Machine;
import com.bangmart.nt.machine.StateChangeListener;
import com.bangmart.nt.sys.ByteBuffer;
import com.bangmart.nt.sys.Tools;
import com.bangmart.nt.treatment.FaultState;
import com.seekwork.bangmart.util.BmtVendingMachineUtil;
import com.seekwork.bangmart.util.LogCat;
import com.seekwork.bangmart.util.SeekerSoftConstant;
import com.seekwork.bangmart.view.SingleCountDownView;

public class InitActivity extends AppCompatActivity {

    private Button btn_ok;
    private SingleCountDownView singleCountDownView;
    private ProgressBar pb_loadingdata;
    private TextView tv_mac_error;

    private Machine machine;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_layout);

        btn_ok = findViewById(R.id.btn_ok);
        singleCountDownView = findViewById(R.id.singleCountDownView);
        pb_loadingdata = findViewById(R.id.pb_loadingdata);
        tv_mac_error = findViewById(R.id.tv_mac_error);

        // 单个倒计时使用
        singleCountDownView = findViewById(R.id.singleCountDownView);
        singleCountDownView.setTextColor(Color.parseColor("#ff000000"));
        singleCountDownView.setTime(10).setTimeColorHex("#ff000000").setTimeSuffixText("s");
        // 单个倒计时结束事件监听
        singleCountDownView.setSingleCountDownEndListener(new SingleCountDownView.SingleCountDownEndListener() {
            @Override
            public void onSingleCountDownEnd() {
                btn_ok.setEnabled(true);
            }
        });
        singleCountDownView.startCountDown();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (machine != null && SeekerSoftConstant.storageMap != null) {
                    singleCountDownView.stopCountDown();
                    InitActivity.this.finish();
                    Intent intent = new Intent(InitActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    //singleCountDownView.setTime(180);
                    //singleCountDownView.startCountDown();
                    InitActivity.this.finish();
                    Intent intent = new Intent(InitActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        initBangMartMachine();
    }

    // 是否需要货架检测
    private boolean needToGetMacProperty = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (singleCountDownView != null) {
            singleCountDownView.stopCountDown();
            singleCountDownView = null;
        }
    }

    /**
     * 初始化machine
     */
    private void initBangMartMachine() {
        appendUILogAsync("机器正在初始化中...");

        machine = BmtVendingMachineUtil.getInstance().getMachine();
        machine.setChannelMonitor(new IChannelMonitor() {
            @Override
            public void onAvailable() {
                appendUILogAsync("打开串口成功！");
                initDevMachine();
            }

            @Override
            public void onUnavailable() {
                appendUILogAsync("串口出现异常，现在不可用。");
            }

            @Override
            public void onReceivedUnknownData(String errDesc, byte[] data) {
                StringBuilder sb = new StringBuilder();
                sb.append(errDesc).append(" : ").append(Tools.bytesToHexString(data));
                appendUILogAsync(sb.toString());
            }
        });
        machine.setHeartBeatListener(new HeartBeatListener() {
            @Override
            public void online(final byte[] data) {
                if (needToGetMacProperty) {
                    needToGetMacProperty = false;
                    final MacMetaData md = machine.reloadMetaData();
                    SeekerSoftConstant.storageMap = machine.getMetaData().loadStorageMap();
                    appendUILogAsync(md.toString());
                }
                appendUILogAsync("心跳正常，回传数据: " + Tools.bytesToHexString(data));
            }

            @Override
            public void offline(long startTime, final String errMsg) {
                final long duration = System.currentTimeMillis() - startTime;
                needToGetMacProperty = true;
                appendUILogAsync("没有心跳:" + duration / 1000 + "s." + errMsg);
            }
        });
        machine.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onFatal(final FaultState state) {
                appendUILogAsync("致命故障:" + Tools.intToHexStrForShow(state.getCode()) + " : " + state.getDesc());
            }

            @Override
            public void onError(final FaultState state) {
                appendUILogAsync("故障:" + Tools.intToHexStrForShow(state.getCode()) + " : " + state.getDesc());
            }

            @Override
            public void onWarn(final FaultState state) {
                appendUILogAsync("警告:" + Tools.intToHexStrForShow(state.getCode()) + " : " + state.getDesc());
            }

            @Override
            public void onRecover() {
                appendUILogAsync("正常:");
            }

            @Override
            public void onChange(final int state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String desc = "";
                        switch (state) {
                            case Machine.STATE_UNKONWN:
                                desc = "未知";
                                break;
                            case Machine.STATE_STANDBY:
                                desc = "待命";
                                break;
                            case Machine.STATE_ENERGY_SAVING:
                                desc = "节能";
                                break;
                            case Machine.STATE_BORED_TIME:
                                desc = "漫游";
                                break;
                            case Machine.STATE_BUSY:
                                desc = "忙";
                                break;
                            case Machine.STATE_FAULT:
                                desc = "故障";
                                break;
                            default:
                        }
                        appendUILogAsync(desc);
                    }
                });
            }
        });
    }

    /**
     * 初始化机器
     */
    private void initDevMachine() {
        machine.setName(SeekerSoftConstant.MACHINE);
        long replyTimeout = SeekerSoftConstant.replyTimeout;
        long responseTimeout = SeekerSoftConstant.responseTimeout;
        Command cmd = CommandFactory.createCommand4DeviceInit(SeekerSoftConstant.MACHINE, replyTimeout, responseTimeout);
        cmd.setOnCompleteListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                final MacMetaData md = machine.reloadMetaData();
                SeekerSoftConstant.storageMap = machine.getMetaData().loadStorageMap();
                LogCat.e("框架 = " + SeekerSoftConstant.storageMap.toString());
                appendUILogAsync(md.toString());
                return true;
            }
        });
        appendSelloutToTaskList(cmd, "初始化机器");
    }

    /**
     * 初始化扫描货道
     */
    private void initScanLocation() {
        long replyTimeout = SeekerSoftConstant.replyTimeout;
        long responseTimeout = SeekerSoftConstant.responseTimeout;

        Command cmd = CommandFactory.createCommand4LocationScan(replyTimeout, responseTimeout);
        cmd.setOnCompleteListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                SeekerSoftConstant.storageMap = machine.getMetaData().loadStorageMap();
                LogCat.e("框架 = " + SeekerSoftConstant.storageMap.toString());
                return true;
            }
        });

        appendSelloutToTaskList(cmd, "初始化扫描货道");
    }

    private void appendSelloutToTaskList(Command cmd, String desc) {
        cmd.setDesc(desc);
        cmd.setOnReplyListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                Attention attention = cmd.getLastAttentionData();
                //出货失败，需要补发一条命令。
                byte[] attData = attention.getData();
                switch (attData[0]) {
                    case CommandDef.ATT_SELLOUT_NOT_TAKEN_AWAY:
                        appendUILogAsync("上个用户没有把货取走，请先取走商品再继续出货");
                        break;
                    case CommandDef.ATT_SELLOUT_PICK_UP_COMPLETE:
                        appendUILogAsync("当前商品取货成功");
                        break;
                    case CommandDef.ATT_SELLOUT_PICK_UP_FAILED:
                        appendUILogAsync("第" + attData[1] + "个商品取货失败。");
                        //String nextLocation = TestSellOut.getNextLocation();
                        String nextLocation = null;
                        ByteBuffer bb = new ByteBuffer(5);

                        if (nextLocation == null) {
                            bb.append(CommandDef.ATT_SELLOUT_PICK_UP_NO_OPTION);
                            byte[] attCode = cmd.sendAttention(bb.copy());
                            appendUILogAsync("没有其他的货道可以出货了。取下一个或结束: " + Tools.bytesToHexString(attCode));
                        } else {
                            bb.append(CommandDef.ATT_SELLOUT_PICK_UP_RETRY).append(attData[1]).append(nextLocation);
                            byte[] attCode = cmd.sendAttention(bb.copy());
                            appendUILogAsync("重试: " + Tools.bytesToHexString(attCode));
                        }
                        break;
                    case CommandDef.ATT_SELLOUT_WAIT_TO_GET_AWAY:
                        appendUILogAsync("取货口已打开，等待用户取走。");
                        break;
                    default:
                }
                return true;
            }
        });
        machine.executeCommand(cmd);
    }

    public void appendUILogAsync(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appendUILog(msg);
            }
        });
    }

    private void appendUILog(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n > ").append(msg);
        tv_mac_error.append(sb.toString());
        int offset = tv_mac_error.getLineCount() * tv_mac_error.getLineHeight();
        if (offset > tv_mac_error.getHeight()) {
            tv_mac_error.scrollTo(0, offset - tv_mac_error.getHeight());
        }
    }

}
