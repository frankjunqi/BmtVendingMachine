package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmarPickSuccessRequest implements Serializable {

    public String getCardNo() {
        return CardNo;
    }

    public void setCardNo(String cardNo) {
        CardNo = cardNo;
    }

    public String getMachineCode() {
        return MachineCode;
    }

    public void setMachineCode(String machineCode) {
        MachineCode = machineCode;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public List<MBangmarSuccessGood> getmBangmarSuccessGoods() {
        return mBangmarSuccessGoods;
    }

    public void setmBangmarSuccessGoods(List<MBangmarSuccessGood> mBangmarSuccessGoods) {
        this.mBangmarSuccessGoods = mBangmarSuccessGoods;
    }

    /**
     * 卡号
     */
    private String CardNo;

    /**
     * 设备编号
     */
    private String MachineCode;

    /**
     * 订单ID
     */
    private int OrderID;

    /**
     * 成功出货详情
     */
    private List<MBangmarSuccessGood> mBangmarSuccessGoods;
}
