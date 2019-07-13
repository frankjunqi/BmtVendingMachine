package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;

public class MBangmarNeedGood implements Serializable {

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public int getPickNum() {
        return PickNum;
    }

    public void setPickNum(int pickNum) {
        PickNum = pickNum;
    }

    /**
     * 商品ID
     */
    private int ProductID;

    /**
     * 领取数量
     */
    private int PickNum;

}
