package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;

public class MBangmarSuccessGood implements Serializable {

    public int getRoadID() {
        return RoadID;
    }

    public void setRoadID(int roadID) {
        RoadID = roadID;
    }

    public int getPickNum() {
        return PickNum;
    }

    public void setPickNum(int pickNum) {
        PickNum = pickNum;
    }

    /**
     * 货道ID
     */
    private int RoadID;

    /**
     * 领取数量
     */
    private int PickNum;

}
