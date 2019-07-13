package com.seekwork.bangmart.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MReplenishToRequest implements Serializable {

    /**
     * 卡号
     */
    private String CardNo;

    /**
     * 机器Code
     */
    private String MachineCode;

    /**
     * 补货详情
     */
    private List<ReplenishToInfo> ReplenishToList;

}
