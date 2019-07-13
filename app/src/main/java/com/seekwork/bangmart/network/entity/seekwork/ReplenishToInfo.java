package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;

public class ReplenishToInfo implements Serializable {

    public int getRoadID() {
        return RoadID;
    }

    public void setRoadID(int roadID) {
        RoadID = roadID;
    }

    public int getToQty() {
        return ToQty;
    }

    public void setToQty(int toQty) {
        ToQty = toQty;
    }

    /**
     * 货道ID
     */
    private int RoadID;

    /**
     * 补货 补到多少
     */
    private int ToQty;

}
