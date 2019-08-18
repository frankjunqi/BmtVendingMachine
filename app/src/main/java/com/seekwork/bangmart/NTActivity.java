package com.seekwork.bangmart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bangmart.nt.command.Attention;
import com.bangmart.nt.command.Command;
import com.bangmart.nt.command.CommandDef;
import com.bangmart.nt.command.CommandGroup;
import com.bangmart.nt.command.ICallbackListener;
import com.bangmart.nt.command.Response;
import com.bangmart.nt.machine.Area;
import com.bangmart.nt.machine.HeartBeatListener;
import com.bangmart.nt.machine.MacMetaData;
import com.bangmart.nt.machine.Machine;
import com.bangmart.nt.machine.StateChangeListener;
import com.bangmart.nt.machine.StorageMap;
import com.bangmart.nt.machine.UpgradeListener;
import com.bangmart.nt.serial.SerialListener;
import com.bangmart.nt.sys.ByteBuffer;
import com.bangmart.nt.sys.ContextInfo;
import com.bangmart.nt.sys.ILogcat;
import com.bangmart.nt.sys.Logger;
import com.bangmart.nt.sys.Tools;
import com.bangmart.nt.treatment.FaultDef;
import com.bangmart.nt.treatment.FaultState;
import com.seekwork.bangmart.data.DBHelper;
import com.seekwork.bangmart.data.DataCache;
import com.seekwork.bangmart.data.DataStat;
import com.seekwork.bangmart.util.SeekerSoftConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author gongtao
 */
public class NTActivity extends AppCompatActivity implements ILogcat {

    private Machine machine;
    private boolean needToGetMacProperty = true;

    private CommandGroup cmdGrp;

    private DataCache dataCache;
    private DataStat dataStat;
    private StorageMap storageMap;

    private TextView logView;
    private TextView txtHeartbeat;
    private TextView txtCmdSte;
    private TextView txtMacSte, txtMacSteDesc;
    private TextView txtSysInfo;

    //界面上显示的待处理的任务列表
    private ArrayAdapter<String> arrayAdapterTaskList;
    private ScrollView commandPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nt);

        txtHeartbeat = (TextView) findViewById(R.id.txtHeartbeat);
        txtCmdSte = (TextView) findViewById(R.id.txtCmdSte);
        txtMacSte = (TextView) findViewById(R.id.txtMacSte);
        txtMacSteDesc = (TextView) findViewById(R.id.txtMacSteDesc);
        txtSysInfo = (TextView) findViewById(R.id.txtSysInfo);

        /*********************************************************************************************
         *                                  以下初始化系统核心对象                                       *
         *********************************************************************************************/
        ContextInfo.init(getApplicationContext());

        /*********** 初始化设备              *************/
        machine = Machine.create(SeekerSoftConstant.MACHINE, getString(R.string.bmt_com2));

        machine.setSerialChannelListener(new SerialListener() {
            @Override
            public void onAvailable() {
                appendUILogAsync("打开串口成功！");
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
                    loadStorageMap();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtSysInfo.setText(md.toString());
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtHeartbeat.setText("心跳正常，回传数据: " + Tools.bytesToHexString(data));
                        txtHeartbeat.setBackgroundColor(Color.TRANSPARENT);
                    }
                });
            }

            @Override
            public void offline(long startTime, final String errMsg) {
                final long duration = System.currentTimeMillis() - startTime;

                needToGetMacProperty = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtHeartbeat.setText("没有心跳:" + duration / 1000 + "s." + errMsg);
                        txtHeartbeat.setBackgroundColor(Color.RED);
                    }
                });
            }
        });

        machine.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onFatal(final FaultState state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtCmdSte.setText("致命故障:" + Tools.intToHexStrForShow(state.getCode()) + " : " + state.getDesc());
                        txtCmdSte.setBackgroundColor(Color.RED);
                    }
                });
            }

            @Override
            public void onError(final FaultState state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtCmdSte.setText("故障:" + Tools.intToHexStrForShow(state.getCode()) + " : " + state.getDesc());
                        txtCmdSte.setBackgroundColor(Color.parseColor("#FFFF7F00"));
                    }
                });
            }

            @Override
            public void onWarn(final FaultState state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtCmdSte.setText("警告:" + Tools.intToHexStrForShow(state.getCode()) + " : " + state.getDesc());
                        txtCmdSte.setBackgroundColor(Color.YELLOW);
                    }
                });
            }

            @Override
            public void onRecover() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtCmdSte.setText("正常:");
                        txtCmdSte.setBackgroundColor(Color.TRANSPARENT);
                    }
                });
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
                        txtMacSteDesc.setText(desc);
                    }
                });
            }
        });

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                final Command cmd = Command.create(CommandDef.ID_GET_SYSTEM_STATE, Command.TYPE_SYNC);
                cmd.TRACE = false;

                machine.executeCommand(cmd);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!cmd.isError()) {
                            Response res = cmd.getResult();
                            byte[] data = res.getData();
                            byte mchSta = data[0];
                            int errCount = data[1];

                            boolean isPalletEmpty = FaultDef.isPalletEmpty(mchSta);

                            StringBuilder sb = new StringBuilder();
                            sb.append(((isPalletEmpty) ? "" : "[托盘有货]"))
                                    .append("[有").append(errCount).append("个错误:");
                            if (errCount > 0) {
                                int fixOffset = 2;
                                for (int i = 0; i < errCount; i++) {
                                    if (i > 0) {
                                        sb.append(" ");
                                    }
                                    byte[] err = new byte[]{data[i * 2 + fixOffset + 1], data[i * 2 + fixOffset]};
                                    sb.append("0x").append(Tools.bytesToHexString(err).replace(" ", ""));
                                }
                            }
                            sb.append("]");

                            txtMacSte.setText(sb.toString());

                            if (FaultDef.compare(mchSta, FaultDef.MACHINE_STATE_FAULT)) {
                                txtMacSte.setBackgroundColor(Color.RED);
                            } else if (FaultDef.compare(mchSta, FaultDef.MACHINE_STATE_BUSY)) {
                                txtMacSte.setBackgroundColor(Color.YELLOW);
                            } else if (!isPalletEmpty) {
                                txtMacSte.setBackgroundColor(Color.parseColor("#FFFF7F00"));
                            } else {
                                txtMacSte.setBackgroundColor(Color.TRANSPARENT);
                            }
                        } else {
                            txtMacSte.setText("未知");
                            txtMacSte.setBackgroundColor(Color.parseColor("#FFFF7F00"));
                        }
                    }
                });
            }
        }, 0, 3000);

//        machine.setCommandListener(0x00, new IDataReceiveListener() {
//            @Override
//            public void onDataReceived(byte[] buffer) {
//
//            }
//        });

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        (findViewById(R.id.btnRun)).setBackground(getResources().getDrawable(R.drawable.pause));
                    }
                });
                setUILogAsync("开始执行当前的命令清单。第一条命令是：" + cmd.getName());
                disableUIAsync();

                return true;
            }
        });
        //监听一个命令执行的开始
        cmdGrp.setOnSendListener(new ICallbackListener() {
            int i = 0;

            @Override
            public boolean callback(final Command cmd) {
                dataStat.setBusy();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView lv = ((ListView) findViewById(R.id.taskList));
                        if (cmdGrp.getCursor() == 0) {
                            i = 0;
                            lv.smoothScrollToPosition(0);//9
                        }

                        if (i < 5 || (cmdGrp.getCommandCount() - cmdGrp.getCursor() - 1) < 5) {
                            if (i > 0) {
                                lv.getChildAt(i - 1).setBackgroundColor(Color.TRANSPARENT);
                            }
                            lv.getChildAt(i++).setBackgroundColor(Color.parseColor("#FFC3CBFD"));

                        } else {
                            lv.getChildAt(4).setBackgroundColor(Color.TRANSPARENT);
                            lv.smoothScrollToPosition(cmdGrp.getCursor() + 5);
                            lv.getChildAt(5).setBackgroundColor(Color.parseColor("#FFC3CBFD"));
                        }

                        appendUILog("执行命令：" + cmd.getName() + "   Code: " + Tools.bytesToHexString(cmd.getCode()));
                    }
                });

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
                sb.append("任务执行结束, 接收到数据->").append(Tools.bytesToHexString(cmd.getResult().getCode()));

                appendUILogAsync(sb.toString());
                showResult(sb.toString());

                return true;
            }
        });

        //监听一个命令列表的执行结束
        cmdGrp.setOnEndListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {

                if (cmdGrp.hasNext()) {
                    dataStat.pause();
                } else {
                    dataStat.stop();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        (findViewById(R.id.btnRun)).setBackground(getResources().getDrawable(R.drawable.run));
                        ListView lv = ((ListView) findViewById(R.id.taskList));
                        lv.getChildAt(lv.getChildCount() - 1).setBackgroundColor(Color.TRANSPARENT);
                        lv.smoothScrollToPosition(0);

                        enableUI();
                        appendUILog("任务清单执行完毕！");
                    }
                });

                return true;
            }
        });

        //监听警告的事件
        cmdGrp.setOnWarnListener(new ICallbackListener() {

            @Override
            public boolean callback(final Command cmd) {
                appendUILogAsync("收到警告！" + cmd.getErrorDescription());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) (findViewById(R.id.txtResult))).setText(cmd.getErrorDescription());
                    }
                });
                flash(1);
                return true;
            }
        });

        //监听错误的事件
        cmdGrp.setOnErrorListener(new ICallbackListener() {
            @Override
            public boolean callback(final Command cmd) {
                if (cmd.isTaskTimeout()) {
                    //没想好
                } else if (cmd.isReplyTimeout()) {
                    dataStat.updateReplyTime(cmd.getReplyTime());
                } else if (cmd.isError()) {
                    //没想好
                }
                dataStat.increaseFailTaskCount();
                dataStat.pause();

                StringBuilder sb = new StringBuilder();
                sb.append("发生错误：")
                        .append("Error Code: 0x").append(Tools.intToHexStrForShow(cmd.getErrorCode()).replace(" ", ""))
                        .append("Error Desc: ").append(cmd.getErrorDescription())
                        .append("Last Data: ").append(Tools.bytesToHexString(cmd.getLastResponseData()));
                appendUILogAsync(sb.toString());

                showResult(cmd.getErrorDescription());

                if (cmdGrp.getBlockOnError() == CommandGroup.TYPE_BREAK_ON_ERROR) {
                    flash(0);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            (findViewById(R.id.btnRun)).setBackground(getResources().getDrawable(R.drawable.resume));
                            enableUI();
                        }
                    });
                }

                return true;
            }
        });

        /*********** 初始化数据缓存对象       *************/
        dataCache = DataCache.getInstance();
        dataCache.loadFromDB();

        DBHelper.create(getApplicationContext());

        /*********** 初始化数据收集统计对象    *************/
        dataStat = DataStat.getInstance();
        dataStat.setOnchange(new DataStat.OnChangeLinstense() {
            @Override
            public void onChange(DataStat ds) {
                setTextViewById(R.id.txtReplyTime, String.valueOf(ds.getReplyTime()));
                setTextViewById(R.id.txtAvgeReplyTime, String.valueOf(ds.getAverageReplyTime()));
                setTextViewById(R.id.txtShortReplyTime, String.valueOf(ds.getShortReplyTime()));
                setTextViewById(R.id.txtLongReplyTime, String.valueOf(ds.getLongReplyTime()));
                setTextViewById(R.id.txtIdleTime, String.valueOf(ds.getIdleTime()));
                setTextViewById(R.id.txtBusyTime, String.valueOf(ds.getBusyTime()));
                setTextViewById(R.id.txtRightTime, String.valueOf(ds.getRecordTime()));
                setTextViewById(R.id.txtRecord, String.valueOf(ds.getRecordTime()));
                if (cmdGrp.getExecType() == CommandGroup.TYPE_ROUND_UNLIMITED) {
                    setTextViewById(R.id.txtCmdCnt, "无限循环");
                } else {
                    setTextViewById(R.id.txtCmdCnt, String.valueOf(ds.getTaskCnt()));
                }

                setTextViewById(R.id.txtSuccessCmdCnt, String.valueOf(ds.getSuccessTaskCnt()));
                setTextViewById(R.id.txtFailCmdCnt, String.valueOf(ds.getFailTaskCnt()));
            }

            @Override
            public void onContinuedSuccess(long execTime) {
                setTextViewById(R.id.txtTaskTime, String.valueOf(execTime));
            }
        });

        /*********************************************************************************************
         *                                  以下初始化界面操作对象                                       *
         *********************************************************************************************/
        //设定在UI上的Log区域对像
        logView = (TextView) findViewById(R.id.logView);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
        setUILog("Current version : " + R.string.version);

        commandPanel = (ScrollView) findViewById(R.id.commandListContainer);

        //初始化UI上的任务列表对象
        arrayAdapterTaskList = new ArrayAdapter<>(this, R.layout.task_list_item, cmdGrp.getNameList());
        ((ListView) findViewById(R.id.taskList)).setAdapter(arrayAdapterTaskList);
        ((ListView) findViewById(R.id.taskList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cmdGrp.remove(position);
                arrayAdapterTaskList.notifyDataSetChanged();

                dataStat.updateTaskCount(cmdGrp.getCommandCount());
            }
        });

        findViewById(R.id.btnCmdPanel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visible = (commandPanel.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                commandPanel.setVisibility(visible);

                if (visible == View.VISIBLE) {
                    (findViewById(R.id.btnCmdPanel)).setBackground(getResources().getDrawable(R.drawable.command_panel_close));
                } else {
                    (findViewById(R.id.btnCmdPanel)).setBackground(getResources().getDrawable(R.drawable.command_panel_open));
                }
            }
        });

        findViewById(R.id.btnRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (cmdGrp.getState()) {
                    case CommandGroup.STATE_IDLE:
                        if (cmdGrp.getCommandCount() == 0) {
                            appendUILog("没有可执行的命令！");
                            return;
                        }

                        (findViewById(R.id.btnRun)).setBackground(getResources().getDrawable(R.drawable.pause));

                        if (((CheckBox) findViewById(R.id.cbRound)).isChecked()) {
                            cmdGrp.setExecType(CommandGroup.TYPE_ROUND_UNLIMITED);
                            dataStat.updateTaskCount(-1);
                        } else {
                            int roundCnt = Integer.parseInt(((EditText) findViewById(R.id.edtRoundCount)).getText().toString());
                            if (roundCnt == 1) {
                                cmdGrp.setExecType(CommandGroup.TYPE_ONLY_ONE);
                            } else {
                                cmdGrp.setExecType(CommandGroup.TYPE_ROUND_LIMITED);
                            }
                            cmdGrp.setExecCount(roundCnt);
                            dataStat.updateTaskCount(cmdGrp.getCommandCount() * roundCnt);
                        }
                        cmdGrp.setBlockOnError(((CheckBox) findViewById(R.id.cbBlockOnError)).isChecked() ? CommandGroup.TYPE_BREAK_ON_ERROR : CommandGroup.TYPE_CONTINUE_ON_ERROR);
                        cmdGrp.setCommandGroupInterval(Integer.parseInt(((EditText) findViewById(R.id.edtGroupTimeout)).getText().toString()));
                        cmdGrp.setCommandInterval(Integer.parseInt(((EditText) findViewById(R.id.edtCmdTimeout)).getText().toString()));
                        cmdGrp.moveFirst();
                        machine.executeCommandGroup(cmdGrp);
                        break;
                    case CommandGroup.STATE_BUSY:
                        (findViewById(R.id.btnRun)).setBackground(getResources().getDrawable(R.drawable.resume));

                        dataStat.pause();
                        cmdGrp.pause();
                        enableUI();

                        break;
                    case CommandGroup.STATE_PAUSE:
                        (findViewById(R.id.btnRun)).setBackground(getResources().getDrawable(R.drawable.pause));
                        disableUI();
                        noFlash();
                        dataStat.resume();
                        cmdGrp.resume();

                        break;
                    default:
                }
            }
        });

        findViewById(R.id.btnSaveData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataCache.save();
            }
        });

        findViewById(R.id.btnFinish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (findViewById(R.id.btnRun)).setBackground(getResources().getDrawable(R.drawable.run));
                cmdGrp.stop();
                dataStat.stop();
                enableUI();
                noFlash();
            }
        });

        findViewById(R.id.btnCmdSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        findViewById(R.id.btnCmdLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        findViewById(R.id.txtSysInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MacMetaData md = machine.reloadMetaData();
                txtSysInfo.setText(md.toString());
            }
        });

        initMasterCommandPanel();

        initOtherTestCommandPanel();

        initOptCommandPanel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Logger.info(getClass().getSimpleName(), "Main Activity onResume.");
        machine.initialize();
    }

//*********************************************************************************************
    //*                                       只为模拟测试而生                                       *
    //*********************************************************************************************/
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_nt);
//
//        txtHeartbeat = (TextView) findViewById(R.id.txtHeartbeat);
//        txtCmdSte = (TextView) findViewById(R.id.txtCmdSte);
//        txtMacSte = (TextView) findViewById(R.id.txtMacSte);
//        txtSysInfo = (TextView) findViewById(R.id.txtSysInfo);
//
//        Logger.init(getApplicationContext());
//
//        //设定在UI上的Log区域对像
//        logView = (TextView) findViewById(R.id.logView);
//        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
//
//        String dataStr = "00 00 00 18 00 00 00 00 37 08 AE 04 00 00 01 F5 08 AE 04 00 00 02 95 09 AE 04 00 00 03 33 0A AE 04 00 01 00 37 08 92 03 00 01 01 11 09 92 03 00 01 02 FE 09 92 03 00 01 03 BB 0A 92 03 00 02 00 37 08 B8 02 00 02 01 0F 09 B8 02 00 02 02 E0 09 B8 02 00 02 03 B1 0A B8 02 00 03 00 37 08 DD 01 00 03 01 CB 08 DD 01 00 03 02 13 0A DD 01 00 03 03 9F 0A DD 01 00 04 00 37 08 05 01 00 04 01 C9 08 05 01 00 04 02 69 09 05 01 00 05 00 37 08 29 00 00 05 01 EA 08 29 00 00 05 02 9C 09 29 00 00 05 03 38 0A 29 00 00 05 04 9A 0A 29 00";
//        byte[] data = Tools.hexStringToBytes(dataStr);
//
//        storageMap = new StorageMap();
//        storageMap.setData(data);
//        storageMap.setAreaParam(0, 2103, 674 + 300, 1301 + 210);
//        Logger.info("StorageMap", storageMap.toString());
//        refreshMap();
//    }

    private void initMasterCommandPanel() {
        findViewById(R.id.btnDevInit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String devID = ((EditText) findViewById(R.id.edtMacName)).getText().toString();
                machine.setName(devID);
                long replyTimeout = Long.parseLong(((EditText) findViewById(R.id.edtDIReplyTimeout)).getText().toString());
                long responseTimeout = Long.parseLong(((EditText) findViewById(R.id.edtDIResponseTimeout)).getText().toString());
                Command cmd = CommandFactory.createCommand4DeviceInit(devID, replyTimeout, responseTimeout);
                cmd.setOnCompleteListener(new ICallbackListener() {
                    @Override
                    public boolean callback(Command cmd) {
                        final MacMetaData md = machine.reloadMetaData();
                        loadStorageMap();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtSysInfo.setText(md.toString());
                            }
                        });
                        return true;
                    }
                });
                cmdGrp.add(cmd);

                arrayAdapterTaskList.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btnLocationScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long replyTimeout = Long.parseLong(((EditText) findViewById(R.id.edtLSReplyTimeout)).getText().toString());
                long responseTimeout = Long.parseLong(((EditText) findViewById(R.id.edtLSResponseTimeout)).getText().toString());

                Command cmd = CommandFactory.createCommand4LocationScan(replyTimeout, responseTimeout);
                cmd.setOnCompleteListener(new ICallbackListener() {
                    @Override
                    public boolean callback(Command cmd) {
                        loadStorageMap();
                        return true;
                    }
                });
                cmdGrp.add(cmd);

                arrayAdapterTaskList.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btnPrePickup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte area, floor, location;

                if (((CheckBox) findViewById(R.id.cbPPAuto)).isChecked()) {
                    if (storageMap == null) {
                        appendUILog("没有货架结构数据，不能自动生成预出货命令，请手工指定。");
                        return;
                    }

                    area = (byte) Math.floor(Math.random() * storageMap.getAreaCount());
                    floor = (byte) Math.floor(Math.random() * storageMap.getArea(area).getFloorCount());
                    location = (byte) Math.floor(Math.random() * storageMap.getArea(area).getFloor(floor).getCount());
                } else {
                    area = Byte.parseByte(((EditText) findViewById(R.id.edtPPArea)).getText().toString());
                    floor = Byte.parseByte(((EditText) findViewById(R.id.edtPPFloor)).getText().toString());
                    location = Byte.parseByte(((EditText) findViewById(R.id.edtPPLocation)).getText().toString());
                }

                long replyTimeout = Long.parseLong(((EditText) findViewById(R.id.edtPPReplyTimeout)).getText().toString());
                long responseTimeout = Long.parseLong(((EditText) findViewById(R.id.edtPPResponseTimeout)).getText().toString());
                Command cmd = CommandFactory.createCommand4prePickup(area, floor, location, replyTimeout, responseTimeout);

                int cmdIdx = cmdGrp.add(cmd);
                cmdGrp.setCommandDesc(cmdIdx, "预出货准备>" + area + "-" + floor + "-" + location);

                arrayAdapterTaskList.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btnSellOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((CheckBox) findViewById(R.id.cbAllLocation)).isChecked()) {
                    // 自动生成全部货道测试的任务列表
                    int areaCnt = storageMap.getAreaCount();
                    if (areaCnt == 0) {
                        appendUILog("没有货道布局信息，无法执行");
                        return;
                    }

                    for (byte area = 0; area < areaCnt; area++) {
                        int floorCnt = storageMap.getArea(area).getFloorCount();
                        if (floorCnt == 0) {
                            appendUILog("没有货道布局信息，无法执行");
                            return;
                        }

                        for (byte floor = 0; floor < floorCnt; floor++) {
                            int locationCnt = storageMap.getArea(area).getFloor(floor).getCount();

                            for (byte location = 0; location < locationCnt; location++) {
                                byte[] paramCode = new byte[6];
                                paramCode[0] = (byte) 0;    //不连续出货
                                paramCode[1] = (byte) 1;    //只出一件货
                                paramCode[2] = (byte) 0;    //这是第 0 件货
                                paramCode[3] = area;
                                paramCode[4] = floor;
                                paramCode[5] = location;

                                Command cmd = Command.create(CommandDef.ID_SELLOUT, paramCode, Command.TYPE_ASYNC);
                                appendSelloutToTaskList(cmd, "出货>" + area + "-" + floor + "-" + location);
                            }
                        }
                    }
                } else {
                    // 测试单独货道任务
                    byte[] good;
                    List<byte[]> params = new ArrayList<>();

                    int count, mode;

                    if (((CheckBox) findViewById(R.id.cbSOAuto)).isChecked()) {
                        mode = 1;
                    } else {
                        mode = 0;
                    }

                    count = Integer.parseInt(((EditText) findViewById(R.id.edtSOGoodCount)).getText().toString());
                    count = (count > 3) ? 3 : count;
                    if (count > 0) {
                        good = new byte[3];
                        good[0] = Byte.parseByte(((EditText) findViewById(R.id.edtSOArea0)).getText().toString());
                        good[1] = Byte.parseByte(((EditText) findViewById(R.id.edtSOFloor0)).getText().toString());
                        good[2] = Byte.parseByte(((EditText) findViewById(R.id.edtSOLocation0)).getText().toString());
                        params.add(good);
                    }
                    if (count > 1) {
                        good = new byte[3];
                        good[0] = Byte.parseByte(((EditText) findViewById(R.id.edtSOArea1)).getText().toString());
                        good[1] = Byte.parseByte(((EditText) findViewById(R.id.edtSOFloor1)).getText().toString());
                        good[2] = Byte.parseByte(((EditText) findViewById(R.id.edtSOLocation1)).getText().toString());
                        params.add(good);
                    }
                    if (count > 2) {
                        good = new byte[3];
                        good[0] = Byte.parseByte(((EditText) findViewById(R.id.edtSOArea2)).getText().toString());
                        good[1] = Byte.parseByte(((EditText) findViewById(R.id.edtSOFloor2)).getText().toString());
                        good[2] = Byte.parseByte(((EditText) findViewById(R.id.edtSOLocation2)).getText().toString());
                        params.add(good);
                    }

                    long replyTimeout = Long.parseLong(((EditText) findViewById(R.id.edtSOReplyTimeout)).getText().toString());
                    long responseTimeout = Long.parseLong(((EditText) findViewById(R.id.edtSOResponseTimeout)).getText().toString());

                    Command cmd = CommandFactory.createCommand4SellOut(params, mode, replyTimeout, responseTimeout);
                    appendSelloutToTaskList(cmd, "出货>" + params.size() + "件" + " 模式>" + mode);
                }
            }
        });

        findViewById(R.id.btnAny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strCode = ((EditText) findViewById(R.id.edtCmdCode)).getText().toString();
                strCode = strCode.replaceAll(" ", "");
                if (strCode.length() <= CommandDef.FIX_LENGTH) {
                    toastLog("输入的命令的格式错误。");
                    return;
                }

                byte[] codes = Tools.hexStringToBytes(strCode);

                String strRTO = ((EditText) findViewById(R.id.edtAnyReplyTimeout)).getText().toString();
                long replyTimeout = ("".equals(strRTO)) ? 0 : Long.parseLong(strRTO);
                long responseTimeout = Long.parseLong(((EditText) findViewById(R.id.edtAnyResponseTimeout)).getText().toString());

                Command cmd;
                try {
                    cmd = new Command(codes, Command.TYPE_ASYNC, replyTimeout, responseTimeout);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    appendUILogAsync(e.getLocalizedMessage());
                    return;
                }
                int cmdIdx = cmdGrp.add(cmd);
                cmdGrp.setCommandDesc(cmdIdx, "临时手输-》" + Tools.byte2Hex(codes[1]) + " - " + Tools.byte2Hex(codes[2]));

                arrayAdapterTaskList.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btnAnyExec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strCode = ((EditText) findViewById(R.id.edtCmdCode)).getText().toString();
                strCode = strCode.replaceAll(" ", "");

                byte[] codes = Tools.hexStringToBytes(strCode);

                String strRTO = ((EditText) findViewById(R.id.edtAnyReplyTimeout)).getText().toString();
                long replyTimeout = ("".equals(strRTO)) ? 0 : Long.parseLong(strRTO);
                long responseTimeout = Long.parseLong(((EditText) findViewById(R.id.edtAnyResponseTimeout)).getText().toString());

                Command cmd;
                try {
                    cmd = new Command(codes, Command.TYPE_ASYNC, replyTimeout, responseTimeout);
                } catch (IllegalArgumentException e) {
                    Logger.error(getClass().getSimpleName(), "Error occur!", e);
                    appendUILogAsync(e.getLocalizedMessage());
                    return;
                }

                cmd.setOnReplyListener(new ICallbackListener() {
                    @Override
                    public boolean callback(Command cmd) {

                        toastLog("收到命令响应数据！");
                        StringBuilder sb = new StringBuilder();
                        sb.append("收到命令响应消息->").append(Tools.bytesToHexString(cmd.getLastAttentionData().getCode()));
                        appendUILogAsync(sb.toString());
                        return true;
                    }
                });
                cmd.setOnResponseListener(new ICallbackListener() {
                    @Override
                    public boolean callback(Command cmd) {
                        toastLog("收到任务回复数据包！");
                        StringBuilder sb = new StringBuilder();
                        sb.append("收到任务回复数据包->").append(Tools.bytesToHexString(cmd.getLastResponseData()));
                        appendUILogAsync(sb.toString());
                        return true;
                    }
                });
                cmd.setOnCompleteListener(new ICallbackListener() {
                    @Override
                    public boolean callback(Command cmd) {

                        toastLog("命令执行完成！");
                        StringBuilder sb = new StringBuilder();
                        sb.append("命令执行完成->").append(Tools.bytesToHexString(cmd.getResult().getCode()));
                        appendUILogAsync(sb.toString());
                        return true;
                    }
                });
                cmd.setOnWarnListener(new ICallbackListener() {
                    @Override
                    public boolean callback(Command cmd) {
                        toastLog("命令执行警告！" + cmd.getErrorDescription());
                        StringBuilder sb = new StringBuilder();
                        sb.append("命令执行警告->").append(Tools.bytesToHexString(cmd.getLastResponseData()));
                        appendUILogAsync(sb.toString());
                        return true;
                    }
                });
                cmd.setOnErrorListener(new ICallbackListener() {
                    @Override
                    public boolean callback(Command cmd) {
                        toastLog("命令执行失败！" + cmd.getErrorDescription());
                        StringBuilder sb = new StringBuilder();
                        sb.append("命令执行失败->")
                                .append("Error Code: 0x").append(Tools.intToHexStrForShow(cmd.getErrorCode()).replace(" ", ""))
                                .append("Error Desc: ").append(cmd.getErrorDescription())
                                .append("Last Data: ").append(Tools.bytesToHexString(cmd.getLastResponseData()));
                        appendUILogAsync(sb.toString());
                        return true;
                    }
                });
                machine.executeCommand(cmd);
            }
        });
    }

    private void initOtherTestCommandPanel() {
        final ICallbackListener completeListener = new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                final StringBuilder sb = new StringBuilder();
                sb.append("任务执行结束, 接收到数据->").append(Tools.bytesToHexString(cmd.getResult().getCode()));

                appendUILogAsync(sb.toString());
                showResult(sb.toString());

                return true;
            }
        };

        final ICallbackListener errorListener = new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                if (cmd.isTaskTimeout()) {
                    //没想好
                } else if (cmd.isReplyTimeout()) {
                    dataStat.updateReplyTime(cmd.getReplyTime());
                } else if (cmd.isError()) {
                    //没想好
                }

                StringBuilder sb = new StringBuilder();
                sb.append("发生错误：")
                        .append("\r\nError Code: 0x").append(Tools.intToHexStrForShow(cmd.getErrorCode()).replace(" ", ""))
                        .append("\r\nError Desc: ").append(cmd.getErrorDescription())
                        .append("\r\nLast Data: ").append(Tools.bytesToHexString(cmd.getLastResponseData()));
                appendUILogAsync(sb.toString());

                showResult(cmd.getErrorDescription());

                return true;
            }
        };

        //打开内门
        findViewById(R.id.btnOpenInnerDoor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Command cmd = Command.create(CommandDef.ID_UNIT_TEST, Tools.hexStringToBytes("300004000100"), Command.TYPE_ASYNC);

                if (!((CheckBox) findViewById(R.id.cbExecImmd)).isChecked()) {
                    int cmdIdx = cmdGrp.add(cmd);
                    cmdGrp.setCommandDesc(cmdIdx, "打开内门");

                    arrayAdapterTaskList.notifyDataSetChanged();
                } else {
                    cmd.setOnCompleteListener(completeListener);
                    cmd.setOnErrorListener(errorListener);
                    appendUILogAsync("打开内门> 发出指令 : " + Tools.bytesToHexString(cmd.getCode()));
                    machine.executeCommand(cmd);
                }
            }
        });

        //关闭内门
        findViewById(R.id.btnCloseInnerDoor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Command cmd = Command.create(CommandDef.ID_UNIT_TEST, Tools.hexStringToBytes("300004000101"), Command.TYPE_ASYNC);
                if (!((CheckBox) findViewById(R.id.cbExecImmd)).isChecked()) {
                    int cmdIdx = cmdGrp.add(cmd);
                    cmdGrp.setCommandDesc(cmdIdx, "关闭内门");

                    arrayAdapterTaskList.notifyDataSetChanged();
                } else {
                    cmd.setOnCompleteListener(completeListener);
                    cmd.setOnErrorListener(errorListener);
                    appendUILogAsync("关闭内门> 发出指令 : " + Tools.bytesToHexString(cmd.getCode()));
                    machine.executeCommand(cmd);
                }
            }
        });

        //打开外门
        findViewById(R.id.btnOpenOuterDoor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Command cmd = Command.create(CommandDef.ID_UNIT_TEST, Tools.hexStringToBytes("300003000100"), Command.TYPE_ASYNC);
                if (!((CheckBox) findViewById(R.id.cbExecImmd)).isChecked()) {
                    int cmdIdx = cmdGrp.add(cmd);
                    cmdGrp.setCommandDesc(cmdIdx, "打开外门");

                    arrayAdapterTaskList.notifyDataSetChanged();
                } else {
                    cmd.setOnCompleteListener(completeListener);
                    cmd.setOnErrorListener(errorListener);
                    appendUILogAsync("打开外门> 发出指令 : " + Tools.bytesToHexString(cmd.getCode()));
                    machine.executeCommand(cmd);
                }
            }
        });

        //关闭外门
        findViewById(R.id.btnCloseOuterDoor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Command cmd = Command.create(CommandDef.ID_UNIT_TEST, Tools.hexStringToBytes("300003000101"), Command.TYPE_ASYNC);
                if (!((CheckBox) findViewById(R.id.cbExecImmd)).isChecked()) {
                    int cmdIdx = cmdGrp.add(cmd);
                    cmdGrp.setCommandDesc(cmdIdx, "关闭外门");

                    arrayAdapterTaskList.notifyDataSetChanged();
                } else {
                    cmd.setOnCompleteListener(completeListener);
                    cmd.setOnErrorListener(errorListener);
                    appendUILogAsync("关闭外门> 发出指令 : " + Tools.bytesToHexString(cmd.getCode()));
                    machine.executeCommand(cmd);
                }
            }
        });

        //随机移动
        findViewById(R.id.btnRamdonMove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int areaCnt = storageMap.getAreaCount();
                if (areaCnt == 0) {
                    appendUILog("没有货道布局信息，无法执行");
                    return;
                }

                byte area = (byte) (Math.random() * areaCnt);

                int floorCnt = storageMap.getArea(area).getFloorCount();
                if (floorCnt == 0) {
                    appendUILog("没有货道布局信息，无法执行");
                    return;
                }

                byte floor = (byte) (Math.random() * floorCnt);

                int locationCnt = storageMap.getArea(area).getFloor(floor).getCount();
                byte location = (byte) (Math.random() * locationCnt);

                Command cmd = CommandFactory.createCommand4prePickup(area, floor, location, CommandDef.getCmdReplyTimeout(CommandDef.ID_PRE_PICKUP), CommandDef.getCmdTaskTimeout(CommandDef.ID_PRE_PICKUP));

                if (!((CheckBox) findViewById(R.id.cbExecImmd)).isChecked()) {
                    int cmdIdx = cmdGrp.add(cmd);
                    cmdGrp.setCommandDesc(cmdIdx, "预出货准备>" + area + "-" + floor + "-" + location);

                    arrayAdapterTaskList.notifyDataSetChanged();
                } else {
                    cmd.setOnCompleteListener(completeListener);
                    cmd.setOnErrorListener(errorListener);
                    appendUILogAsync("预出货准备>" + area + "-" + floor + "-" + location + " : " + Tools.bytesToHexString(cmd.getCode()));
                    machine.executeCommand(cmd);
                }
            }
        });

        //随机出货
        findViewById(R.id.btnRamdonOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int goodsCnt = (int) (Math.random() * 10) + 1;
                for (int i = 0; i < goodsCnt; i++) {

                }
            }
        });

        //测试货道供电
        findViewById(R.id.btnPowerSupply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Command cmd = Command.create(CommandDef.ID_UNIT_TEST, Tools.hexStringToBytes("500002000100"), Command.TYPE_ASYNC);
                if (!((CheckBox) findViewById(R.id.cbExecImmd)).isChecked()) {
                    int cmdIdx = cmdGrp.add(cmd);
                    cmdGrp.setCommandDesc(cmdIdx, "测试货道供电");

                    arrayAdapterTaskList.notifyDataSetChanged();
                } else {
                    cmd.setOnCompleteListener(completeListener);
                    cmd.setOnErrorListener(errorListener);
                    appendUILogAsync("测试货道供电> 发出指令 : " + Tools.bytesToHexString(cmd.getCode()));
                    machine.executeCommand(cmd);
                }
            }
        });

        //测试货道滑块
        findViewById(R.id.btnTestSlider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = Integer.valueOf(((EditText) findViewById(R.id.edtSlidePos)).getText().toString());
                Command cmd = Command.create(CommandDef.ID_UNIT_TEST, Tools.hexStringToBytes("500005000814000000" + Tools.intToHexStr4Code(pos) + "0000"), Command.TYPE_ASYNC);
                if (!((CheckBox) findViewById(R.id.cbExecImmd)).isChecked()) {
                    int cmdIdx = cmdGrp.add(cmd);
                    cmdGrp.setCommandDesc(cmdIdx, "测试货道滑块");

                    arrayAdapterTaskList.notifyDataSetChanged();
                } else {
                    cmd.setOnCompleteListener(completeListener);
                    cmd.setOnErrorListener(errorListener);
                    appendUILogAsync("测试货道滑块> 发出指令 : " + Tools.bytesToHexString(cmd.getCode()));
                    machine.executeCommand(cmd);
                }
            }
        });
    }

    private void initOptCommandPanel() {
        findViewById(R.id.btnTestR2Z).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView) findViewById(R.id.txtDeviation)).setText("");
                Command cmd = Command.create(CommandDef.ID_GO_TO_ORIGIN, Command.TYPE_ASYNC);
                cmd.setOnCompleteListener(new ICallbackListener() {
                    @Override
                    public boolean callback(Command cmd) {
                        if (cmd.isReplyTimeout() || cmd.isTaskTimeout()) {
                            return true;
                        }

                        byte[] data = cmd.getResult().getData();

                        final int deviationX = Tools.byteToInt16((byte) (data[4] & 0xff), data[3]);
                        final int deviationY = Tools.byteToInt16((byte) (data[6] & 0xff), data[5]);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder sb = new StringBuilder();
                                sb.append("偏差值 -》 x:").append(deviationX).append(", y:").append(deviationY);
                                ((TextView) findViewById(R.id.txtDeviation)).setText(sb.toString());
                            }
                        });
                        return true;
                    }
                });
                cmd.setOnErrorListener(new ICallbackListener() {
                    @Override
                    public boolean callback(Command cmd) {
                        StringBuffer sb = new StringBuffer();
                        sb.append("命令执行失败->")
                                .append("Error Code: 0x").append(Tools.intToHexStrForShow(cmd.getErrorCode()).replace(" ", ""))
                                .append("Error Desc: ").append(cmd.getErrorDescription())
                                .append("Last Data: ").append(Tools.bytesToHexString(cmd.getLastResponseData()));
                        toastLog(sb.toString());
                        return true;
                    }
                });
                machine.executeCommand(cmd);
            }
        });

        findViewById(R.id.btnOpenDoor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Command cmd = Command.create(CommandDef.ID_OPEN_THE_DOOR, Command.TYPE_SYNC);
                machine.executeCommand(cmd);

                StringBuilder sb = new StringBuilder();
                sb.append("开门:");
                if (!cmd.isError()) {
                    sb.append("正常，门已打开!");
                } else {
                    sb.append("异常：").append(cmd.getErrorDescription());
                }
                toastLog(sb.toString());
            }
        });

        findViewById(R.id.btnTestCnnt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Command cmd = Command.create(CommandDef.ID_TEST_SERIAL, Command.TYPE_SYNC);
                machine.executeCommand(cmd);

                StringBuilder sb = new StringBuilder();
                sb.append("串口通讯:");
                if (!cmd.isError()) {
                    sb.append("正常!");
                } else {
                    sb.append("异常：").append(cmd.getErrorDescription());
                }
                toastLog(sb.toString());
            }
        });

        ((CheckBox) findViewById(R.id.cbDebug)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checked = ((CheckBox) findViewById(R.id.cbDebug)).isChecked();
                if (checked) {
                    Logger.DEBUG = true;
                    Logger.setLogcat(getLogcat());
                } else {
                    Logger.DEBUG = false;
                    Logger.setLogcat(null);
                }
            }
        });

        findViewById(R.id.btnRebootIPS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Command cmd = Command.create(CommandDef.ID_REBOOT_IPS, Command.TYPE_SYNC);
                machine.executeCommand(cmd);

                if (cmd.isError()) {
                    toastLog(cmd.getErrorDescription() + " : " + Tools.bytesToHexString(cmd.getResult().getCode()));
                } else {
                    toastLog("命令调用成功。执行成不成功，请自己看！");
                }
            }
        });

        findViewById(R.id.btnRebootAndroid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Command cmd = Command.create(CommandDef.ID_REBOOT_ANDROID, Command.TYPE_SYNC);
                machine.executeCommand(cmd);

                if (cmd.isError()) {
                    toastLog(cmd.getErrorDescription() + " : " + Tools.bytesToHexString(cmd.getResult().getCode()));
                } else {
                    toastLog("正常情况下，你看不到这个提示的。");
                }
            }
        });

        findViewById(R.id.btnUpgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path;
                File dir = getApplicationContext().getExternalFilesDir(null);
                if (null != dir) {
                    path = dir.getPath();
                } else {
                    toastLog("没找到系统路径！");
                    return;
                }

                path += ("/" + ((EditText) findViewById(R.id.edtFileName)).getText().toString());
                File file = new File(path);
                if (!file.exists()) {
                    toastLog("指定的文件不存在：" + file.getAbsolutePath());
                    return;
                }

                try {
                    machine.upgrade(file, new UpgradeListener() {
                        @Override
                        public void onBegin(int size, int pageCount) {
                            DataCache.getInstance().put("upgrade_file_size", size);
                            DataCache.getInstance().put("upgrade_file_page_count", pageCount);
                            showResult("开始上传固件文件, 文件大小为 " + size + " 字节， 共 【" + pageCount + "】 个数据包！");
                            appendUILogAsync("开始上传固件文件");
                        }

                        @Override
                        public void onProcess(int pageNo) {
                            int size = (Integer) DataCache.getInstance().get("upgrade_file_size");
                            int pageCount = (Integer) DataCache.getInstance().get("upgrade_file_page_count");
                            showResult("成功上传第 " + pageNo + " 个数据包， 共 【" + pageCount + "】 个数据包, " + size + " 字节");
                            appendUILogAsync("成功上传第 " + pageNo + " 个数据包，完成度 " + ((float) pageNo / (float) pageCount) * 100 + "%");
                        }

                        @Override
                        public void onDownloadComplete() {
                            showResult("固件文件已正常上传到IPS");
                            appendUILogAsync("固件文件已正常上传到IPS");
                        }

                        @Override
                        public void onInstallComplete() {
                            showResult("固件已刷新成功");
                            appendUILogAsync("固件已刷新成功");
                        }

                        @Override
                        public void onError(int errCode, String errDesc) {
                            showResult("0x" + Tools.intToHexStrForShow(errCode) + ": " + errDesc);
                            appendUILogAsync("0x" + Tools.intToHexStrForShow(errCode) + ": " + errDesc);
                        }
                    });
                } catch (Exception e) {
                    Logger.error(getClass().getSimpleName(), "指定的文件不存在：" + file.getAbsolutePath(), e);
                    toastLog("指定的文件不存在：" + file.getAbsolutePath());
                    return;
                }
            }
        });
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
                        showResult("上个用户没有把货取走，请先取走商品再继续出货");
                        appendUILogAsync("上个用户没有把货取走，请先取走商品再继续出货");
                        break;
                    case CommandDef.ATT_SELLOUT_PICK_UP_COMPLETE:
                        showResult("当前商品取货成功");
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

                            showResult("没有其他的货道可以出货了。取下一个或结束。");
                            appendUILogAsync("没有其他的货道可以出货了。取下一个或结束: " + Tools.bytesToHexString(attCode));
                        } else {
                            bb.append(CommandDef.ATT_SELLOUT_PICK_UP_RETRY).append(attData[1]).append(nextLocation);
                            byte[] attCode = cmd.sendAttention(bb.copy());

                            showResult("重试: " + nextLocation);
                            appendUILogAsync("重试: " + Tools.bytesToHexString(attCode));
                        }
                        break;
                    case CommandDef.ATT_SELLOUT_WAIT_TO_GET_AWAY:
                        showResult("取货口已打开，等待用户取走。");
                        appendUILogAsync("取货口已打开，等待用户取走。");
                        break;
                    default:
                }
                return true;
            }
        });

        int cmdIdx = cmdGrp.add(cmd);
        cmdGrp.setCommandDesc(cmdIdx, desc);

        arrayAdapterTaskList.notifyDataSetChanged();
    }

    private void loadStorageMap() {
        storageMap = machine.getMetaData().loadStorageMap();
        refreshMap();
    }

    private void refreshMap() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout) findViewById(R.id.diagramLayout)).removeAllViews();

                Logger.info("", storageMap.toString());

                if (null == storageMap) return;

                int count = storageMap.getAreaCount();

                for (int i = 0; i < count; i++) {
                    Area area = storageMap.getArea(i);

                    LocationView lv = new LocationView(getApplicationContext());
                    lv.setLayoutParams(new ViewGroup.LayoutParams(lv.WIDTH, lv.HEIGHT));
                    lv.init(area);
                    lv.refresh();
                    lv.setOnSelectedListener(new LocationView.OnSelectedListener() {
                        @Override
                        public void onSelected(int area, int floor, int location, boolean isSellOut) {
                            if (isSellOut) {
                                byte[] paramCode = new byte[6];
                                paramCode[0] = (byte) 0;
                                paramCode[1] = (byte) 1;
                                paramCode[2] = (byte) 0;
                                paramCode[3] = (byte) area;
                                paramCode[4] = (byte) floor;
                                paramCode[5] = (byte) location;

                                Command cmd = Command.create(CommandDef.ID_SELLOUT, paramCode, Command.TYPE_ASYNC);
                                cmd.setOnReplyListener(new ICallbackListener() {
                                    @Override
                                    public boolean callback(Command cmd) {

                                        Attention attention = cmd.getLastAttentionData();

                                        //出货失败，需要补发一条命令。
                                        byte[] attData = attention.getData();
                                        switch (attData[0]) {
                                            case CommandDef.ATT_SELLOUT_NOT_TAKEN_AWAY:
                                                showResult("上个用户没有把货取走，请先取走商品再继续出货");
                                                appendUILogAsync("上个用户没有把货取走，请先取走商品再继续出货");
                                                break;
                                            case CommandDef.ATT_SELLOUT_PICK_UP_FAILED:
                                                appendUILogAsync("第" + attData[1] + "个商品取货失败。");
                                                ByteBuffer bb = new ByteBuffer(5);
                                                bb.append(CommandDef.ATT_SELLOUT_PICK_UP_NO_OPTION);
                                                cmd.sendAttention(bb.copy());
                                                break;
                                            case CommandDef.ATT_SELLOUT_WAIT_TO_GET_AWAY:
                                                showResult("取货口已打开，等待用户取走。");
                                                appendUILogAsync("取货口已打开，等待用户取走。");
                                                break;
                                            default:
                                        }
                                        return true;
                                    }
                                });
                                machine.executeCommand(cmd);
                            } else {
                                byte[] params = new byte[3];
                                params[0] = (byte) area;
                                params[1] = (byte) floor;
                                params[2] = (byte) location;
                                Command cmd = Command.create(CommandDef.ID_PRE_PICKUP, params, Command.TYPE_ASYNC);
                                machine.executeCommand(cmd);
                            }
                        }
                    });
                    ((LinearLayout) findViewById(R.id.diagramLayout)).addView(lv);
                }

            }
        });
    }

    /**
     * private void refreshMap() {
     * if (!dataCache.containsKey("STORAGE_LOCATION")) {
     * appendUILog("没有货架结构数据，不能正确绘制货架结构图。");
     * return;
     * }
     * <p>
     * //@todo 在设备初始化的命令里，需要设定货柜的有效宽度和高度。目前这块的代码还没有写
     * int widthInFact = 1000;
     * int heightInFact = 1000;
     * <p>
     * StorageLocation storageLocation = (StorageLocation) dataCache.get("STORAGE_LOCATION");
     * <p>
     * int areaCount = storageLocation.getAreaCount();
     * <p>
     * ((LinearLayout) findViewById(R.id.diagramLayout)).removeAllViews();
     * <p>
     * for (int i = 0; i < areaCount; i++) {
     * LocationView lv = new LocationView(this.getApplicationContext());
     * lv.setLayoutParams(new ViewGroup.LayoutParams(lv.WIDTH, lv.HEIGHT));
     * lv.init(storageLocation, i, widthInFact, heightInFact);
     * lv.refresh();
     * <p>
     * lv.setOnSelectedListener(new LocationView.OnSelectedListener() {
     *
     * @Override public void onSelected(int area, int floor, int location) {
     * byte[] params = new byte[3];
     * params[0] = (byte) area;
     * params[1] = (byte) floor;
     * params[2] = (byte) location;
     * <p>
     * Command cmd = Command.create(ID_PRE_PICKUP, params, Command.TYPE_ASYNC);
     * machine.executeCommand(cmd);
     * <p>
     * if (cmd.isError()) {
     * Toast.makeText(getApplicationContext(), cmd.getErrorDescription(), Toast.LENGTH_SHORT).show();
     * }
     * }
     * });
     * <p>
     * ((LinearLayout) findViewById(R.id.diagramLayout)).addView(lv);
     * }
     * }
     */

    private void setTextViewById(final int id, final String val) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(id)).setText(val);
            }
        });
    }

    private void setUILogAsync(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUILog(str);
            }
        });
    }

    public void appendUILogAsync(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appendUILog(msg);
            }
        });
    }

    private void setUILog(String str) {
        logView.setText(str);
        logView.scrollTo(0, 0);
    }

    private void appendUILog(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n > ").append(msg);

        logView.append(sb.toString());

        int offset = logView.getLineCount() * logView.getLineHeight();
        if (offset > logView.getHeight()) {
            logView.scrollTo(0, offset - logView.getHeight());
        }
    }

    private void disableUIAsync() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disableUI();
            }
        });
    }

    private void disableUI() {
        (findViewById(R.id.btnCmdPanel)).setEnabled(false);
        (findViewById(R.id.commandListContainer)).setVisibility(View.GONE);
        (findViewById(R.id.taskList)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    private void enableUIAsync() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableUI();
            }
        });
    }

    private void enableUI() {
        (findViewById(R.id.btnCmdPanel)).setEnabled(true);
        (findViewById(R.id.taskList)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

    }

    private Timer flashTimer;

    private void flash(int type) {
        if (flashTimer != null) {
            flashTimer.cancel();
        }

        flashTimer = new Timer();
        TimerTask tt = new TimerTask() {
            int[] colors = new int[]{Color.RED, Color.parseColor("#FFF6F3D6")};
            int idx = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.resultPanel).setBackgroundColor(colors[idx]);
                        idx = (idx == 0) ? 1 : 0;
                    }
                });
            }
        };

        if (type == 0) {
            flashTimer.schedule(tt, 0, 200);
        } else {
            //为了省事，就直接硬编码了。反正以后不会改了。
            flashTimer.schedule(tt, 200);
        }
    }

    private void noFlash() {
        if (flashTimer != null) {
            flashTimer.cancel();
            flashTimer = null;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.resultPanel).setBackgroundColor(Color.parseColor("#FFF6F3D6"));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        Logger.info(getClass().getSimpleName(), "Main Activity onStop.");
        machine.destroy();
    }

    public void toastLog(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast t = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                t.show();

                appendUILog(msg);
            }
        });
    }

    public void showResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) (findViewById(R.id.txtResult))).setText(msg);
            }
        });
    }

    private ILogcat getLogcat() {
        return this;
    }

    @Override
    public void log(String msg) {
        appendUILogAsync(msg);
    }
}
