package com.seekwork.bangmart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.bangmart.nt.command.Attention;
import com.bangmart.nt.command.Command;
import com.bangmart.nt.command.CommandDef;
import com.bangmart.nt.command.ICallbackListener;
import com.bangmart.nt.machine.Machine;
import com.bangmart.nt.smartwarehouse.pickupcar.ReqOutGoods;
import com.bangmart.nt.smartwarehouse.pickupcar.RspOutGoods;
import com.bangmart.nt.sys.ByteBuffer;
import com.bangmart.nt.sys.Tools;
import com.bangmart.nt.vendingmachine.constant.BmtMsgConstant;
import com.google.gson.Gson;
import com.seekwork.bangmart.network.api.Host;
import com.seekwork.bangmart.network.api.SeekWorkService;
import com.seekwork.bangmart.network.api.SrvResult;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickRoadDetailResponse;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickSuccessRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarProcPick;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarProcPickRoad;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarSuccessGood;
import com.seekwork.bangmart.network.entity.seekwork.TakeOutProductBean;
import com.seekwork.bangmart.network.gsonfactory.GsonConverterFactory;
import com.seekwork.bangmart.util.BmtVendingMachineUtil;
import com.seekwork.bangmart.util.LogCat;
import com.seekwork.bangmart.util.SeekerSoftConstant;
import com.seekwork.bangmart.view.SingleCountDownView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bangmart.nt.command.CommandDef.ATT_SELLOUT_NOT_TAKEN_AWAY;
import static com.bangmart.nt.command.CommandDef.ATT_SELLOUT_PICK_UP_COMPLETE;
import static com.bangmart.nt.command.CommandDef.ATT_SELLOUT_PICK_UP_FAILED;
import static com.bangmart.nt.command.CommandDef.ATT_SELLOUT_PICK_UP_NO_OPTION;
import static com.bangmart.nt.command.CommandDef.ATT_SELLOUT_PICK_UP_RETRY;
import static com.bangmart.nt.command.CommandDef.ATT_SELLOUT_WAIT_TO_GET_AWAY;

/**
 * 出货结果页面处理
 */
public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";
    private SingleCountDownView singleCountDownViewPop;
    private TextView tv_tips_result;

    private String CardNo;
    private int orderId;
    private List<MBangmarProcPick> mBangmarProcPicks;

    private Machine machine;

    // 出货分组
    private ArrayList<ArrayList<TakeOutProductBean>> groupList = new ArrayList<>();
    private int currentSellOut = 0;

    private List<MBangmarSuccessGood> mBangmarSuccessGoods = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tv_tips_result = findViewById(R.id.tv_tips_result);

        singleCountDownViewPop = findViewById(R.id.singleCountDownViewPop);
        singleCountDownViewPop.setTextColor(Color.parseColor("#155398"));
        singleCountDownViewPop.setTime(15);
        singleCountDownViewPop.setTimeColorHex("#155398");
        singleCountDownViewPop.setTimeSuffixText("S");
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

        // 出货数据列表
        ArrayList<TakeOutProductBean> outList = new ArrayList<>();
        // 商品数量
        int productSize = mBangmarProcPicks.size();
        for (int i = 0; i < productSize; i++) {
            // 同一个商品 出货货道数量
            int productRoads = mBangmarProcPicks.get(i).getmBangmarProcPickRoads().size();
            for (int k = 0; k < productRoads; k++) {
                int productID = mBangmarProcPicks.get(i).getProductID();
                // 同一个商品 同一个货道 出货数量
                int roadCount = mBangmarProcPicks.get(i).getmBangmarProcPickRoads().get(k).getOutNum();
                for (int j = 0; j < roadCount; j++) {
                    TakeOutProductBean road = new TakeOutProductBean();
                    road.setRoadID(mBangmarProcPicks.get(i).getmBangmarProcPickRoads().get(k).getRoadID());
                    road.setArea(mBangmarProcPicks.get(i).getmBangmarProcPickRoads().get(k).getArea());
                    road.setFloor(mBangmarProcPicks.get(i).getmBangmarProcPickRoads().get(k).getFloor());
                    road.setColumn(mBangmarProcPicks.get(i).getmBangmarProcPickRoads().get(k).getColumn());
                    road.setProductID(productID);
                    // TODO 本地货道扫描数据
                    road.setWidth(SeekerSoftConstant.list.get(mBangmarProcPicks.get(i).getmBangmarProcPickRoads().get(k).getArea())
                            .get(mBangmarProcPicks.get(i).getmBangmarProcPickRoads().get(k).getFloor())
                            .get(mBangmarProcPicks.get(i).getmBangmarProcPickRoads().get(k).getColumn()).getWidth()
                    );
                    outList.add(road);
                }
            }
        }
        machine = BmtVendingMachineUtil.getInstance().getMachine();


        int sumWidth = 0;
        ArrayList<TakeOutProductBean> oneList = new ArrayList<>();
        for (int i = 0; i < outList.size(); i++) {
            sumWidth = sumWidth + outList.get(i).getWidth();
            if (sumWidth <= SeekerSoftConstant.MACHINE_FACT_WIDTH && oneList.size() <= 3) {
                oneList.add(outList.get(i));
            } else {
                groupList.add((ArrayList<TakeOutProductBean>) oneList.clone());

                oneList = new ArrayList<>();
                sumWidth = 0;
                sumWidth = sumWidth + outList.get(i).getWidth();
                if (sumWidth <= SeekerSoftConstant.MACHINE_FACT_WIDTH) {
                    oneList.add(outList.get(i));
                }
            }
        }

        setllOut();

    }

    private void setllOut() {
        /********************************start: 出货（0x04）(异步)********************************/
        /**
         * 1.生成出货业务数据(一组出货最多出三个货道,如果单组出货超过一个货道,必须遵循智能出货规则,如果单组出货不超过一个则无需遵循智能出 货规则)
         */

        ArrayList<TakeOutProductBean> outlist = groupList.get(currentSellOut);

        List<com.bangmart.nt.common.model.Location> locations = new ArrayList<>();
        for (int i = 0; i < outlist.size(); i++) {
            com.bangmart.nt.common.model.Location location = new com.bangmart.nt.common.model.Location(outlist.get(i).getArea(), outlist.get(i).getFloor(), outlist.get(i).getColumn());
            locations.add(location);
        }

        final ReqOutGoods reqOutGoods = new ReqOutGoods(locations);
        final byte[] data = ReqOutGoods.valueOf(reqOutGoods);
        /**
         * 2.构造出货命令(异步)
         */
        Command cmd = Command.create(CommandDef.ID_SELLOUT, data, Command.TYPE_ASYNC);
        cmd.setDesc("分组出货");
        /**
         * 3.监听相关回调
         */
        cmd.setOnReplyListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                String log = null;
                Attention attention = cmd.getLastAttentionData();
                // TODO 出货商品
                // SingleOutGoodsInfo singleOutGoodsInfo = null;
                //出货失败，需要补发一条命令。
                byte[] attData = attention.getData();
                switch (attData[0]) {
                    case ATT_SELLOUT_NOT_TAKEN_AWAY:
                        log = "上个用户没有把货取走，请先取走商品再继续出货";
                        Log.i(TAG, log);
                        appendUILogAsync(log);
                        break;
                    case ATT_SELLOUT_PICK_UP_COMPLETE:
                        log = "第" + attData[1] + "个商品取货成功。";
                        Log.i(TAG, log);
                        appendUILogAsync(log);
                        MBangmarSuccessGood successGood = new MBangmarSuccessGood();
                        successGood.setRoadID(groupList.get(currentSellOut).get(attData[1]).getRoadID());
                        successGood.setPickNum(1);
                        mBangmarSuccessGoods.add(successGood);
                        break;
                    case ATT_SELLOUT_PICK_UP_FAILED:
                        ByteBuffer bb = new ByteBuffer(5);
                        log = "第" + attData[1] + "个商品取货失败。";
                        Log.i(TAG, log);
                        int outGoodsFailedIndex = attData[1] & BmtMsgConstant.BYTE_MASK;
                        /*Location retryLocation = onOutGoods.getRetryLocation(outGoodsFailedIndex);
                        if (null == retryLocation) {
                            bb.append(ATT_SELLOUT_PICK_UP_NO_OPTION);
                            byte[] retry = cmd.sendAttention(bb.copy());
                            log = "无重试货道";
                            Log.i(TAG, log);
                        } else {
                            String nextLocation = StringUtil.getLocation(retryLocation);
                            if (false == TextUtils.isEmpty(nextLocation)) {
                                Logger.sLogger4Machine.info(TAG, "重试位置:" + JsonUtil.toJson(retryLocation));
                                Logger.sLogger4Machine.info(TAG, "重试位置:" + nextLocation);
                                bb.append(ATT_SELLOUT_PICK_UP_RETRY).append(attData[1]).append(nextLocation);
                                byte[] retry = cmd.sendAttention(bb.copy());
                                if (null != retry) {
                                    Logger.sLogger4Machine.info(TAG, "重试指令:" + DataUtil.bytesToHexString(retry));
                                }
                            } else {
                                bb.append(ATT_SELLOUT_PICK_UP_NO_OPTION);
                                byte[] retry = cmd.sendAttention(bb.copy());
                                log = "无重试货道";
                                Log.i(TAG, log);
                            }
                        }*/
                        appendUILogAsync(log);
                        break;
                    case ATT_SELLOUT_WAIT_TO_GET_AWAY:
                        log = "取货口已打开，等待用户取走。";
                        appendUILogAsync(log);
                        break;
                    default:
                }
                return true;
            }
        });
        cmd.setOnCompleteListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                com.bangmart.nt.command.Response response = cmd.getResult();
                if (null != response) {
                    RspOutGoods rspOutGoods = RspOutGoods.valueOf(response.getData());
                    //TODO 根据出货结果,进行其它业务处理
                    String log = "complete = " + rspOutGoods.getFailedCountValue() + rspOutGoods.getEnErrorCodeDesc();
                    appendUILogAsync(log);

                    if (currentSellOut == groupList.size() - 1) {
                        appendUILogAsync("分组出货全部完成.");
                        DoPickSuccess();
                    } else {
                        currentSellOut++;
                        setllOut();
                    }

                }
                return true;
            }
        });
        cmd.setOnErrorListener(new ICallbackListener() {
            @Override
            public boolean callback(Command cmd) {
                com.bangmart.nt.command.Response response = cmd.getResult();
                if (null != response) {
                    RspOutGoods rspOutGoods = RspOutGoods.valueOf(response.getData());
                    //TODO 根据出货结果,进行其它业务处理
                    String log = "error = " + rspOutGoods.getFailedCountValue() + rspOutGoods.getEnErrorCodeDesc();
                    appendUILogAsync(log);
                }
                return true;
            }
        });
        /**
         * 4.执行出货命令
         */
        machine.executeCommand(cmd);
        /********************************end: 出货（0x04）(异步)********************************/
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
                    case ATT_SELLOUT_NOT_TAKEN_AWAY:
                        appendUILogAsync("上个用户没有把货取走，请先取走商品再继续出货");
                        break;
                    case ATT_SELLOUT_PICK_UP_COMPLETE:
                        appendUILogAsync("当前商品取货成功");
                        break;
                    case ATT_SELLOUT_PICK_UP_FAILED:
                        appendUILogAsync("第" + attData[1] + "个商品取货失败。");
                        //String nextLocation = TestSellOut.getNextLocation();
                        String nextLocation = null;
                        ByteBuffer bb = new ByteBuffer(5);

                        if (nextLocation == null) {
                            bb.append(ATT_SELLOUT_PICK_UP_NO_OPTION);
                            byte[] attCode = cmd.sendAttention(bb.copy());
                            appendUILogAsync("没有其他的货道可以出货了。取下一个或结束: " + Tools.bytesToHexString(attCode));
                            // TODO 發送取貨數據
                            DoPickSuccess();
                        } else {
                            bb.append(ATT_SELLOUT_PICK_UP_RETRY).append(attData[1]).append(nextLocation);
                            byte[] attCode = cmd.sendAttention(bb.copy());
                            appendUILogAsync("重试: " + Tools.bytesToHexString(attCode));
                        }
                        break;
                    case ATT_SELLOUT_WAIT_TO_GET_AWAY:
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
        request.setmBangmarSuccessGoods(mBangmarSuccessGoods);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> updateAction = service.DoPickSuccess(request);
        LogCat.e("url = " + updateAction.request().url().toString());
        Gson gson = new Gson();
        String json = gson.toJson(request);
        LogCat.e("url = " + json);

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
