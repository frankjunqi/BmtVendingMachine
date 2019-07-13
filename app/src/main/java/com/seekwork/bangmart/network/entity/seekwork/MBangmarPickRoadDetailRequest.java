package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmarPickRoadDetailRequest implements Serializable {

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public List<MBangmarNeedGood> getmBangmarNeedGoods() {
        return mBangmarNeedGoods;
    }

    public void setmBangmarNeedGoods(List<MBangmarNeedGood> mBangmarNeedGoods) {
        this.mBangmarNeedGoods = mBangmarNeedGoods;
    }

    /**
     * 订单ID
     */
    private int OrderID;

    /**
     * 设备编号
     */
    private String machineCode;

    /**
     * 商品需求数组
     */
    private List<MBangmarNeedGood> mBangmarNeedGoods;

}
