package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MBangmarPickSuccessRequest implements Serializable {

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
