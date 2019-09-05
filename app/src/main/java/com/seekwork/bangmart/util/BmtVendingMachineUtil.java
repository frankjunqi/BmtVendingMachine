package com.seekwork.bangmart.util;


import com.seekwork.bangmart.NTApplication;
import com.seekwork.bangmart.androidboard.constant.ComUse;
import com.seekwork.bangmart.androidboard.util.AndroidBoardModelUtil;
import com.seekwork.bangmart.helper.BmtVendingMachineHelper;
import com.seekwork.bangmart.helper.SettingsHelper;
import com.seekwork.bangmart.model.SerialPortInfo;

/**
 * Created by zhoujq on 2018/1/10.
 * BangMart自贩机工具类
 */

public class BmtVendingMachineUtil {

    public static BmtVendingMachineHelper getInstance() {
        SerialPortInfo serialPortInfo = AndroidBoardModelUtil.getSerialPortInfo(ComUse.VENDING_MACHINE);
        String deviceNo = SettingsHelper.getInstance().getDeviceNo();
        return BmtVendingMachineHelper.getInstance(NTApplication.getInstance(), deviceNo, serialPortInfo);
    }


}
