package com.seekwork.bangmart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bangmart.nt.command.Attention;
import com.bangmart.nt.command.Command;
import com.bangmart.nt.command.CommandDef;
import com.bangmart.nt.command.CommandGroup;
import com.bangmart.nt.command.ICallbackListener;
import com.bangmart.nt.sys.ByteBuffer;
import com.bangmart.nt.sys.Tools;
import com.seekwork.bangmart.data.DBHelper;
import com.seekwork.bangmart.data.DataCache;
import com.seekwork.bangmart.data.DataStat;
import com.seekwork.bangmart.network.api.Host;
import com.seekwork.bangmart.network.api.SeekWorkService;
import com.seekwork.bangmart.network.api.SrvResult;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickRoadDetailResponse;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickSuccessRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarProcPick;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarProcPickRoad;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarSuccessGood;
import com.seekwork.bangmart.network.gsonfactory.GsonConverterFactory;
import com.seekwork.bangmart.util.LogCat;
import com.seekwork.bangmart.util.SeekerSoftConstant;
import com.seekwork.bangmart.view.SingleCountDownView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 出货结果页面处理
 */
public class ResultActivity extends AppCompatActivity {

    private SingleCountDownView singleCountDownViewPop;
    private TextView tv_tips_result;

    private String CardNo;
    private int orderId;
    private List<MBangmarProcPick> mBangmarProcPicks;
    private List<MBangmarProcPickRoad> mBangmarProcPickRoads;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tv_tips_result = findViewById(R.id.tv_tips_result);

        singleCountDownViewPop = findViewById(R.id.singleCountDownViewPop);
        singleCountDownViewPop.setTextColor(Color.parseColor("#ff000000"));
        singleCountDownViewPop.setTime(6);
        singleCountDownViewPop.setTimeColorHex("#ff000000");
        singleCountDownViewPop.setTimeSuffixText("s");
        singleCountDownViewPop.setSingleCountDownEndListener(new SingleCountDownView.SingleCountDownEndListener() {
            @Override
            public void onSingleCountDownEnd() {
                ResultActivity.this.finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        CardNo = bundle.getString(SeekerSoftConstant.CardNo);
        orderId = bundle.getInt(SeekerSoftConstant.OrderID);
        MBangmarPickRoadDetailResponse response = (MBangmarPickRoadDetailResponse) bundle.getSerializable(SeekerSoftConstant.OUTCART);
        mBangmarProcPicks = response.getmBangmarProcPicks();

        init();

        if (mBangmarProcPicks.size() > 0) {
            mBangmarProcPickRoads = mBangmarProcPicks.get(0).getmBangmarProcPickRoads();
        }

        sellOutMore();

    }

    private void sellOutOne() {
        byte[] paramCode = new byte[6];
        paramCode[0] = (byte) 0;
        paramCode[1] = (byte) 1;
        paramCode[2] = (byte) 0;
        paramCode[3] = (byte) 0;// area
        paramCode[4] = (byte) 0;// floor
        paramCode[5] = (byte) 0;// location

        Command cmd = Command.create(CommandDef.ID_SELLOUT, paramCode, Command.TYPE_ASYNC);
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
                    case CommandDef.ATT_SELLOUT_PICK_UP_FAILED:
                        appendUILogAsync("第" + attData[1] + "个商品取货失败。");
                        ByteBuffer bb = new ByteBuffer(5);
                        bb.append(CommandDef.ATT_SELLOUT_PICK_UP_NO_OPTION);
                        cmd.sendAttention(bb.copy());
                        break;
                    case CommandDef.ATT_SELLOUT_WAIT_TO_GET_AWAY:
                        appendUILogAsync("取货口已打开，等待用户取走。");
                        break;
                    default:
                }
                return true;
            }
        });
        SeekerSoftConstant.machine.executeCommand(cmd);
    }


    private void sellOutMore() {
        byte[] good;
        List<byte[]> params = new ArrayList<>();

        for (byte location = 0; mBangmarProcPickRoads != null && location < mBangmarProcPickRoads.size(); location++) {
            good = new byte[3];
            good[0] = (byte) mBangmarProcPickRoads.get(location).getArea();
            good[1] = (byte) mBangmarProcPickRoads.get(location).getFloor();
            good[2] = (byte) mBangmarProcPickRoads.get(location).getColumn();
            params.add(good);
        }

        Command cmd = CommandFactory.createCommand4SellOut(params, 0, 100000, 10000);
        appendSelloutToTaskList(cmd, "出货>" + params.size() + "件" + " 模式>" + 1);

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
                SeekerSoftConstant.machine.executeCommandGroup(cmdGrp);
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

    private CommandGroup cmdGrp;
    private DataStat dataStat;
    private DataCache dataCache;

    private void init() {

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
                sb.append("任务执行结束, 接收到数据->").append(Tools.bytesToHexString(cmd.getResult().getCode()));
                appendUILogAsync(sb.toString());
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
                appendUILogAsync("任务清单执行完毕！");
                return true;
            }
        });

        /*********** 初始化数据缓存对象       *************/
        dataCache = DataCache.getInstance();
        dataCache.loadFromDB();

        DBHelper.create(getApplicationContext());

        dataStat = DataStat.getInstance();

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
        tv_tips_result.append(sb.toString());
        int offset = tv_tips_result.getLineCount() * tv_tips_result.getLineHeight();
        if (offset > tv_tips_result.getHeight()) {
            tv_tips_result.scrollTo(0, offset - tv_tips_result.getHeight());
        }
    }

    /**
     * TODO 4. 成功出货
     */
    private void DoPickSuccess() {
        singleCountDownViewPop.startCountDown();

        final MBangmarPickSuccessRequest request = new MBangmarPickSuccessRequest();
        request.setCardNo(CardNo);
        request.setOrderID(orderId);
        request.setMachineCode(SeekerSoftConstant.MachineNo);
        List<MBangmarSuccessGood> mBangmarSuccessGoods = new ArrayList<>();
        for (int i = 0; mBangmarProcPicks != null && i < mBangmarProcPicks.size(); i++) {
            for (int k = 0; k < mBangmarProcPicks.get(i).getmBangmarProcPickRoads().size(); k++) {
                MBangmarSuccessGood successGood = new MBangmarSuccessGood();
                successGood.setRoadID(mBangmarProcPicks.get(i).getmBangmarProcPickRoads().get(k).getRoadID());
                successGood.setPickNum(mBangmarProcPicks.get(i).getmBangmarProcPickRoads().get(k).getOutNum());
                mBangmarSuccessGoods.add(successGood);
            }
        }
        request.setmBangmarSuccessGoods(mBangmarSuccessGoods);


        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> updateAction = service.DoPickSuccess(request);
        LogCat.e("url = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {

            }
        });
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
