package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmarPickRoadDetailResponse implements Serializable {

    public List<MBangmarProcPick> getmBangmarProcPicks() {
        return mBangmarProcPicks;
    }

    public void setmBangmarProcPicks(List<MBangmarProcPick> mBangmarProcPicks) {
        this.mBangmarProcPicks = mBangmarProcPicks;
    }

    /**
     * 出货商品列表
     */
    private List<MBangmarProcPick> mBangmarProcPicks;

}
