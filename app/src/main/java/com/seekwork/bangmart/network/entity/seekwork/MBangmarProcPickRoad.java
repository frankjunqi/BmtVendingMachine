package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;

/**
 * 商品出货详情
 */
public class MBangmarProcPickRoad implements Serializable {

    public int getRoadID() {
        return RoadID;
    }

    public void setRoadID(int roadID) {
        RoadID = roadID;
    }

    public int getOutNum() {
        return OutNum;
    }

    public void setOutNum(int outNum) {
        OutNum = outNum;
    }

    /**
     * 货道ID
     */
    private int RoadID;

    /**
     * 出货数量
     */
    private int OutNum;
}
