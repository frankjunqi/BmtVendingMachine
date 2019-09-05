package com.bangmart.bmtvendingmachinedemo.util;


import com.bangmart.bmtvendingmachinedemo.NTApplication;
import com.bangmart.bmtvendingmachinedemo.androidboard.constant.ComUse;
import com.bangmart.bmtvendingmachinedemo.androidboard.util.AndroidBoardModelUtil;
import com.bangmart.bmtvendingmachinedemo.helper.BmtVendingMachineHelper;
import com.bangmart.bmtvendingmachinedemo.helper.SettingsHelper;
import com.bangmart.bmtvendingmachinedemo.model.SerialPortInfo;


/**
 * Created by zhoujq on 2018/1/10.
 * BangMart自贩机工具类
 */

public class BmtVendingMachineUtil {

    public static BmtVendingMachineHelper getInstance(){
        SerialPortInfo serialPortInfo= AndroidBoardModelUtil.getSerialPortInfo(ComUse.VENDING_MACHINE);
        String deviceNo= SettingsHelper.getInstance().getDeviceNo();
        return BmtVendingMachineHelper.getInstance(NTApplication.getContext(),deviceNo,serialPortInfo);
    }


}
