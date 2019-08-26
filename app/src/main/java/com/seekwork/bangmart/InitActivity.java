package com.seekwork.bangmart;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bangmart.nt.command.Attention;
import com.bangmart.nt.command.Command;
import com.bangmart.nt.command.CommandDef;
import com.bangmart.nt.command.CommandGroup;
import com.bangmart.nt.command.ICallbackListener;
import com.bangmart.nt.machine.HeartBeatListener;
import com.bangmart.nt.machine.MacMetaData;
import com.bangmart.nt.machine.Machine;
import com.bangmart.nt.machine.StateChangeListener;
import com.bangmart.nt.serial.SerialListener;
import com.bangmart.nt.sys.ByteBuffer;
import com.bangmart.nt.sys.ContextInfo;
import com.bangmart.nt.sys.Tools;
import com.bangmart.nt.treatment.FaultState;
import com.google.gson.Gson;
import com.seekwork.bangmart.data.DBHelper;
import com.seekwork.bangmart.data.DataCache;
import com.seekwork.bangmart.data.DataStat;
import com.seekwork.bangmart.util.LogCat;
import com.seekwork.bangmart.util.SeekerSoftConstant;
import com.seekwork.bangmart.view.SingleCountDownView;

public class InitActivity extends AppCompatActivity {

    private Button btn_ok;
    private SingleCountDownView singleCountDownView;
    private ProgressBar pb_loadingdata;

    private TextView tv_mac_error;

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
                if (SeekerSoftConstant.machine != null && SeekerSoftConstant.storageMap != null) {
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
    private CommandGroup cmdGrp;
    private DataStat dataStat;
    private DataCache dataCache;


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

        ContextInfo.init(getApplicationContext());
        SeekerSoftConstant.machine = Machine.create(SeekerSoftConstant.MACHINE, getString(R.string.bmt_com2));
        SeekerSoftConstant.machine.setSerialChannelListener(new SerialListener() {
            @Override
            public void onAvailable() {
                appendUILogAsync("打开串口成功！");
                // TODO 初始化基础数据
                initCmdGroup();
                initDevMachine();
                //initScanLocation();
                makeCmdOrder();
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
        SeekerSoftConstant.machine.setHeartBeatListener(new HeartBeatListener() {
            @Override
            public void online(final byte[] data) {
                if (needToGetMacProperty) {
                    needToGetMacProperty = false;
                    final MacMetaData md = SeekerSoftConstant.machine.reloadMetaData();
                    SeekerSoftConstant.storageMap = SeekerSoftConstant.machine.getMetaData().loadStorageMap();
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
        SeekerSoftConstant.machine.setStateChangeListener(new StateChangeListener() {
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
     * 初始化cmd group
     */
    private void initCmdGroup() {
        /*********** 初始化数据缓存对象       *************/
        dataCache = DataCache.getInstance();
        dataCache.loadFromDB();

        DBHelper.create(getApplicationContext());

        dataStat = DataStat.getInstance();

        /*********** 初始化命令管理对象       *************/
        //初始化命令管理对象。命令的对列，执行生命周期都是由这个对象负责
        cmdGrp = CommandGroup.getInstance();

        //监听一个命令列表执行的开始
        cmdGrp.setOnStartListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                if (dataStat.is(DataStat.STATE_UNKNOW)) {
                    dataStat.startStat();
                } else {
                    dataStat.resume();
                }
                appendUILogAsync("开始执行当前的命令清单。第一条命令是：" + cmd.getName());
                return true;
            }
        });
        //监听一个命令执行的开始
        cmdGrp.setOnSendListener(new ICallbackListener() {
            @Override
            public boolean callback(final Command cmd) {
                dataStat.setBusy();
                appendUILogAsync("执行命令：" + cmd.getName() + "   Code: " + Tools.bytesToHexString(cmd.getCode()));
                return true;
            }
        });
        //监听下位机命令响应
        cmdGrp.setOnReplyListener(new ICallbackListener() {
            @Override
            public boolean callback(final Command cmd) {
                dataStat.updateReplyTime(cmd.getReplyTime());
                StringBuilder sb = new StringBuilder();
                sb.append("收到消息->").append(Tools.bytesToHexString(cmd.getLastAttentionData().getCode()));
                appendUILogAsync(sb.toString());
                return true;
            }
        });
        //监听下位机命令回复数据（可能一条命令有多个数据包）
        cmdGrp.setOnResponseListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                StringBuilder sb = new StringBuilder();
                sb.append("收到数据->").append(Tools.bytesToHexString(cmd.getLastResponseData()));
                appendUILogAsync(sb.toString());
                return true;
            }
        });
        //监听一个命令执行的结束
        cmdGrp.setOnCompleteListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                final StringBuilder sb = new StringBuilder();
                dataStat.increaseSuccessTaskCount();
                dataStat.setIdle();

                if (cmd != null && cmd.getResult() != null) {
                    sb.append("任务执行结束, 接收到数据->").append(Tools.bytesToHexString(cmd.getResult().getCode()));
                }

                appendUILogAsync(sb.toString());
                return true;
            }
        });

        //监听一个命令列表的执行结束
        cmdGrp.setOnEndListener(new ICallbackListener() {
            @Override
            public boolean callback(final Command cmd) {
                if (cmdGrp.hasNext()) {
                    dataStat.pause();
                } else {
                    dataStat.stop();
                }
                // TODO 命令执行完成
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < cmdGrp.getCommandCount(); i++) {
                            cmdGrp.remove(i);
                        }
                        singleCountDownView.stopCountDown();
                        btn_ok.setEnabled(true);
                    }
                });
                appendUILogAsync("任务清单执行完毕！");
                return true;
            }
        });


    }

    /**
     * 初始化机器
     */
    private void initDevMachine() {
        SeekerSoftConstant.machine.setName(SeekerSoftConstant.MACHINE);
        long replyTimeout = SeekerSoftConstant.replyTimeout;
        long responseTimeout = SeekerSoftConstant.responseTimeout;
        Command cmd = CommandFactory.createCommand4DeviceInit(SeekerSoftConstant.MACHINE, replyTimeout, responseTimeout);
        cmd.setOnCompleteListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                final MacMetaData md = SeekerSoftConstant.machine.reloadMetaData();
                loadStorageMap();
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
                loadStorageMap();
                return true;
            }
        });
        appendSelloutToTaskList(cmd, "初始化扫描货道");
    }

    /**
     * 检查初始化，扫描货架命令执行
     */
    private void makeCmdOrder() {
        switch (cmdGrp.getState()) {
            case CommandGroup.STATE_IDLE:
                if (cmdGrp.getCommandCount() == 0) {
                    appendUILogAsync("没有可执行的命令！");
                    return;
                }
                int roundCnt = Integer.parseInt("1");
                if (roundCnt == 1) {
                    cmdGrp.setExecType(CommandGroup.TYPE_ONLY_ONE);
                } else {
                    cmdGrp.setExecType(CommandGroup.TYPE_ROUND_LIMITED);
                }
                cmdGrp.setExecCount(roundCnt);
                dataStat.updateTaskCount(cmdGrp.getCommandCount() * roundCnt);

                cmdGrp.setBlockOnError(false ? CommandGroup.TYPE_BREAK_ON_ERROR : CommandGroup.TYPE_CONTINUE_ON_ERROR);
                cmdGrp.setCommandGroupInterval(10000);
                cmdGrp.setCommandInterval(10000);
                cmdGrp.moveFirst();
                if (SeekerSoftConstant.machine != null) {
                    SeekerSoftConstant.machine.executeCommandGroup(cmdGrp);
                }
                break;
            case CommandGroup.STATE_BUSY:
                dataStat.pause();
                cmdGrp.pause();
                break;
            case CommandGroup.STATE_PAUSE:
                dataStat.resume();
                cmdGrp.resume();
                break;
            default:
        }
    }

    private void appendSelloutToTaskList(Command cmd, String desc) {
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
        int cmdIdx = cmdGrp.add(cmd);
        cmdGrp.setCommandDesc(cmdIdx, desc);
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

    private void loadStorageMap() {
        SeekerSoftConstant.storageMap = SeekerSoftConstant.machine.getMetaData().loadStorageMap();

        LogCat.e("url = " + SeekerSoftConstant.storageMap.toString());

    }

}
