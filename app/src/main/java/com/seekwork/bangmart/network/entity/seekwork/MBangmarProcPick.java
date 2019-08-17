package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmarProcPick implements Serializable {

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public List<MBangmarProcPickRoad> getmBangmarProcPickRoads() {
        return mBangmarProcPickRoads;
    }

    public void setmBangmarProcPickRoads(List<MBangmarProcPickRoad> mBangmarProcPickRoads) {
        this.mBangmarProcPickRoads = mBangmarProcPickRoads;
    }

    /**
     * 商品ID
     */
    private int ProductID;

    /**
     * 商品出货数组
     */
    private List<MBangmarProcPickRoad> mBangmarProcPickRoads;

}
