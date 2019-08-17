package com.seekwork.bangmart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.seekwork.bangmart.network.api.Host;
import com.seekwork.bangmart.network.api.SeekWorkService;
import com.seekwork.bangmart.network.api.SrvResult;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickRoadDetailResponse;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarPickSuccessRequest;
import com.seekwork.bangmart.network.entity.seekwork.MBangmarProcPick;
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

        DoPickSuccess();

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
