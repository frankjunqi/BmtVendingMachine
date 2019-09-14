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
import com.bangmart.nt.vendingmachine.model.LocationCoordinate;
import com.bangmart.nt.vendingmachine.model.RspGetDeviceParameter;
import com.bangmart.nt.vendingmachine.model.RspGetLocationCoordinateData;
import com.seekwork.bangmart.model.SerialLocationBean;
import com.seekwork.bangmart.util.BmtVendingMachineUtil;
import com.seekwork.bangmart.util.SeekerSoftConstant;
import com.seekwork.bangmart.view.SingleCountDownView;

import java.util.ArrayList;
import java.util.List;

import static com.bangmart.nt.command.CommandDef.ID_GET_LOCATION_DATA;

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
        singleCountDownView.setTime(2).setTimeColorHex("#ff000000").setTimeSuffixText("s");
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
                if (machine != null) {
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

                // 串口打开成功后，开始初始化机器
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
                appendUILogAsync(md.toString());

                // 初始化成功后，开始查询机器参数
                showMacData();

                return true;
            }
        });
        appendSelloutToTaskList(cmd, "初始化机器");
    }

    /**
     * 查询设备参数
     */
    private void showMacData() {
        /********************************start:查询设备参数（0x23）(同步)********************************/
        /**
         *1.构造命令(同步)
         */
        Command cmd = Command.create(CommandDef.ID_GET_MAC_PARAMETER, Command.TYPE_SYNC);
        /**
         * 2.执行命令
         */
        machine.executeCommand(cmd);
        /**
         * 3.解析命令响应数据
         */
        if (cmd.isError()) {
            appendUILogAsync("查询设备参数（0x23）(同步) 命令错误。 " + cmd.getErrorDescription());
        }
        if (null != cmd.getResult()) {
            byte[] rspData = cmd.getResult().getData();
            if (null != rspData) {
                RspGetDeviceParameter rspGetDeviceParameter = RspGetDeviceParameter.valueOf(rspData);
                // 根据设备参数信息,进行其它业务处理
                SeekerSoftConstant.TOP_LIMIT = rspGetDeviceParameter.getTopLimit2();
                SeekerSoftConstant.RIGHT_LIMIT = rspGetDeviceParameter.getRightLimit2();
                SeekerSoftConstant.AREA_COUNT = rspGetDeviceParameter.getAreaCount2();
                appendUILogAsync("查询设备参数（0x23）(同步) 命令执行 成功。");

                getLocationWidth();

            } else {
                appendUILogAsync("查询设备参数（0x23）(同步) 命令执行 返回数据失败。");
            }
        } else {
            appendUILogAsync("查询设备参数（0x23）(同步) 命令执行 失败。 ");
        }
        /********************************end:查询设备参数（0x23）(同步)********************************/
    }


    /**
     * 查询货道坐标列表
     */
    private void getLocationWidth() {
        /********************************start:查询货道坐标列表（0x24）(异步)********************************/
        /**
         *1.构造命令(异步)
         */
        Command cmd = Command.create(ID_GET_LOCATION_DATA, null, Command.TYPE_ASYNC);
        /**
         * 2.监听相关回调
         */
        cmd.setOnCompleteListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                if (cmd.isReplyTimeout() || cmd.isTaskTimeout()) {
                    appendUILogAsync("查询货道坐标列表（0x24）(异步) 命令执行 超时。 ");
                    return true;
                }
                if (null != cmd.getResult()) {
                    byte[] data = cmd.getResult().getData();
                    if (null != data) {
                        RspGetLocationCoordinateData rspGetCargoRoadCoordinateData = RspGetLocationCoordinateData.valueOf(data);
                        if (null != rspGetCargoRoadCoordinateData) {
                            appendUILogAsync("查询货道坐标列表（0x24）(异步) 命令执行 成功。");

                            Log.e("url", rspGetCargoRoadCoordinateData.toString());

                            //TODO 1.解析货道坐标
                            SeekerSoftConstant.list = handleLocationList(rspGetCargoRoadCoordinateData);

                            // 2. 货柜宽度=右极限+托盘外部宽度(300mm), 货柜高度=顶部极限+托盘外部高度(210mm)
                            SeekerSoftConstant.BOX_WIDTH = SeekerSoftConstant.RIGHT_LIMIT + SeekerSoftConstant.MACHINE_WIDTH;
                            SeekerSoftConstant.BOX_HEIGHT = SeekerSoftConstant.TOP_LIMIT + SeekerSoftConstant.MACHINT_IN_FACT_HEIGHT;

                            //TODO 3.根据 1和 2中数据解析货道宽度和高度
                            if (SeekerSoftConstant.list != null) {
                                int areaSize = SeekerSoftConstant.list.size();
                                for (int i = 0; i < areaSize; i++) {
                                    // 第一个区域计算宽度
                                    int floorSize = SeekerSoftConstant.list.get(i).size();
                                    for (int j = 0; j < floorSize; j++) {
                                        int columnSize = SeekerSoftConstant.list.get(i).get(j).size();
                                        for (int k = 0; k < columnSize; k++) {
                                            SerialLocationBean bean = SeekerSoftConstant.list.get(i).get(j).get(k);
                                            if (k == columnSize - 1) {
                                                // 最后一个货道宽度的计算方式
                                                if (i == areaSize - 1) {
                                                    // 最后一个柜子
                                                    bean.setWidth(SeekerSoftConstant.RIGHT_LIMIT + SeekerSoftConstant.MACHINE_WIDTH - bean.getPosXValue());
                                                } else {
                                                    // 下一个柜子的第0行 第0列
                                                    int xNextPos = SeekerSoftConstant.list.get(i + 1).get(0).get(0).getPosXValue();
                                                    bean.setWidth(xNextPos - SeekerSoftConstant.MACHINE_BETWEEN - bean.getPosXValue());
                                                }
                                            } else {
                                                // 正常货道计算方式
                                                SerialLocationBean beanNext = SeekerSoftConstant.list.get(i).get(j).get(k + 1);
                                                bean.setWidth(beanNext.getPosXValue() - bean.getPosXValue());
                                            }
                                        }
                                    }
                                }
                            } else {
                                appendUILogAsync("解析货道坐标返回数据为空。");
                            }
                        }
                    } else {
                        appendUILogAsync("查询货道坐标列表（0x24）(异步) 命令执行 返回数据失败。");
                    }
                } else {
                    appendUILogAsync("查询货道坐标列表（0x24）(异步) 命令执行 失败。 ");
                }
                return true;
            }
        });
        /**
         * 3.执行命令
         */
        machine.executeCommand(cmd);
        /********************************end:查询货道坐标列表（0x24）(异步)********************************/
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


    private List<List<List<SerialLocationBean>>> handleLocationList(RspGetLocationCoordinateData rspData) {
        List<List<List<SerialLocationBean>>> allList = new ArrayList<>();

        int var2 = rspData.getLocationCountValue();
        if (var2 != 0) {
            var2 = 0;
            int var3 = 0;
            int var4 = 0;
            int var5 = rspData.getLocationCoordinates().size();

            // 创建第一个货柜
            List<List<SerialLocationBean>> arealist = new ArrayList<>();
            allList.add(arealist);
            // 创建第一个货柜 第一行货道
            List<SerialLocationBean> floorList = new ArrayList<>();
            arealist.add(floorList);

            for (int var6 = 0; var6 < var5; ++var6) {
                LocationCoordinate var7 = (LocationCoordinate) rspData.getLocationCoordinates().get(var6);
                byte var8;
                if (var2 == var7.getAreaValue()) {
                    if (var3 == var7.getFloorValue()) {
                        ++var4;
                    } else {
                        List<SerialLocationBean> floorInList = new ArrayList<>();
                        arealist.add(floorList);
                        allList.get(var2).add(floorInList);
                        var8 = 0;
                        var4 = var8 + 1;
                    }
                } else {
                    var8 = 0;
                    var4 = var8 + 1;
                    List<List<SerialLocationBean>> areaInlist = new ArrayList<>();
                    allList.add(areaInlist);
                }
                SerialLocationBean serialLocationBean = new SerialLocationBean();
                serialLocationBean.setArea(var7.getArea());
                serialLocationBean.setFloor(var7.getFloor());
                serialLocationBean.setLocation(var7.getLocation());
                serialLocationBean.setPosX(var7.getPosX());
                serialLocationBean.setPosY(var7.getPosY());
                allList.get(var2).get(var3).add(serialLocationBean);

                var2 = var7.getAreaValue();
                var3 = var7.getFloorValue();
                if (var6 == var5 - 1) {
                    var4 = 0;
                }
            }
        }
        return allList;
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
