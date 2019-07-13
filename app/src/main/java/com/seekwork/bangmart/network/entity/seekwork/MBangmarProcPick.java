package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmarProcPick implements Serializable {

    /**
     * 商品ID
     */
    private int ProductID;

    /**
     * 商品出货数组
     */
    private List<MBangmarProcPickRoad> mBangmarProcPickRoads;

}
